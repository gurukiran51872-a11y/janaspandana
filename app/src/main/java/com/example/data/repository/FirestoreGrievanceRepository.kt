package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.R
import com.example.data.model.FirestoreGrievance
import com.example.data.model.FirestoreUserProfile
import com.example.data.util.OperationType
import com.example.data.util.handleFirestoreError
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class FirestoreGrievanceRepository(
  private val db: FirebaseFirestore
) {
  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      FirebaseApp.getInstance(),
      context.applicationContext.getString(R.string.firestore_database_id)
    )
  )

  private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()

  private fun requireUserId(): String {
    return auth.currentUser?.uid
      ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
  }

  // Real-time observation of user's own grievances
  fun observeUserGrievances(userId: String): Flow<List<FirestoreGrievance>> {
    val path = "grievances"
    return db.collection(path)
      .whereEqualTo("userId", userId)
      .snapshots()
      .map { snapshot ->
        snapshot.documents.mapNotNull { doc ->
          doc.toObject(FirestoreGrievance::class.java)?.let { item ->
            if (item.id.isBlank()) item.copy(id = doc.id) else item
          }
        }.sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
      }
      .catch { e ->
        if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
        throw e
      }
  }

  // Real-time observation of all public grievances
  fun observePublicGrievances(): Flow<List<FirestoreGrievance>> {
    val path = "grievances"
    return db.collection(path)
      .whereEqualTo("isPublic", true)
      .snapshots()
      .map { snapshot ->
        snapshot.documents.mapNotNull { doc ->
          doc.toObject(FirestoreGrievance::class.java)?.let { item ->
            if (item.id.isBlank()) item.copy(id = doc.id) else item
          }
        }.sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
      }
      .catch { e ->
        if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
        throw e
      }
  }

  // Upload photo to Firebase Storage
  suspend fun uploadEvidencePhoto(uri: Uri, grievanceId: String): Result<String> {
    return try {
      val uid = requireUserId()
      val storageRef = FirebaseStorage.getInstance().reference
      val imageRef = storageRef.child("grievances/$uid/${grievanceId}_${System.currentTimeMillis()}.jpg")
      imageRef.putFile(uri).await()
      val downloadUrl = imageRef.downloadUrl.await().toString()
      Result.success(downloadUrl)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  // Create grievance
  suspend fun createGrievance(
    title: String,
    category: String,
    description: String,
    district: String,
    taluk: String,
    gramPanchayat: String,
    village: String,
    wardNumber: String,
    locationName: String,
    gpsCoordinates: String,
    isPublic: Boolean,
    evidencePhotoUri: String? = null
  ): Result<String> {
    val uid = requireUserId()
    val user = auth.currentUser
    val path = "grievances"
    val docId = "GP" + SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()) + (1000..9999).random()

    val payload = hashMapOf<String, Any>(
      "id" to docId,
      "userId" to uid,
      "authorName" to (user?.displayName ?: "Citizen"),
      "authorEmail" to (user?.email ?: ""),
      "title" to title,
      "category" to category,
      "description" to description,
      "status" to "Pending",
      "submittedDate" to SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
      "district" to district,
      "taluk" to taluk,
      "gramPanchayat" to gramPanchayat,
      "village" to village,
      "wardNumber" to wardNumber,
      "locationName" to locationName.ifBlank { "$village, $taluk Taluk" },
      "gpsCoordinates" to gpsCoordinates,
      "isPublic" to isPublic,
      "assignedOfficer" to "Panchayat Development Officer (PDO)",
      "department" to "Gram Panchayat Admin",
      "expectedResolutionDate" to "Within 48 hours",
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp()
    )

    if (evidencePhotoUri != null) {
      payload["evidencePhotoUri"] = evidencePhotoUri
    }

    return try {
      db.collection(path).document(docId).set(payload).await()
      Result.success(docId)
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, "$path/$docId")
      Result.failure(e)
    }
  }

  // Update citizen feedback rating & comment
  suspend fun updateRatingAndFeedback(
    grievanceId: String,
    rating: Long,
    feedbackComment: String
  ): Result<Unit> {
    val uid = requireUserId()
    val path = "grievances/$grievanceId"
    val updates = hashMapOf<String, Any>(
      "rating" to rating,
      "feedbackComment" to feedbackComment,
      "updatedAt" to FieldValue.serverTimestamp()
    )

    return try {
      db.document(path).update(updates).await()
      Result.success(Unit)
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, path)
      Result.failure(e)
    }
  }

  // Reopen grievance
  suspend fun reopenGrievance(grievanceId: String): Result<Unit> {
    val uid = requireUserId()
    val path = "grievances/$grievanceId"
    val updates = hashMapOf<String, Any>(
      "status" to "In Progress",
      "updatedAt" to FieldValue.serverTimestamp()
    )

    return try {
      db.document(path).update(updates).await()
      Result.success(Unit)
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, path)
      Result.failure(e)
    }
  }

  // Update grievance status (e.g. from Pending to In Progress or Resolved)
  suspend fun updateGrievanceStatus(grievanceId: String, newStatus: String): Result<Unit> {
    val path = "grievances/$grievanceId"
    val updates = hashMapOf<String, Any>(
      "status" to newStatus,
      "updatedAt" to FieldValue.serverTimestamp()
    )

    return try {
      db.document(path).update(updates).await()
      Result.success(Unit)
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, path)
      Result.failure(e)
    }
  }

  // Update FCM registration token for push notifications
  suspend fun saveFcmToken(token: String): Result<Unit> {
    return try {
      val uid = requireUserId()
      val path = "users/$uid"
      val doc = db.document(path).get().await()
      if (doc.exists()) {
        val updates = hashMapOf<String, Any>(
          "fcmToken" to token,
          "fcmUpdatedAt" to FieldValue.serverTimestamp()
        )
        db.document(path).update(updates).await()
      } else {
        val payload = hashMapOf<String, Any>(
          "userId" to uid,
          "displayName" to (auth.currentUser?.displayName?.ifBlank { "Citizen" } ?: "Citizen"),
          "email" to (auth.currentUser?.email ?: ""),
          "phone" to "",
          "gramPanchayat" to "Malur GP",
          "fcmToken" to token,
          "fcmUpdatedAt" to FieldValue.serverTimestamp(),
          "createdAt" to FieldValue.serverTimestamp(),
          "updatedAt" to FieldValue.serverTimestamp()
        )
        db.document(path).set(payload).await()
      }
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  // Save or sync citizen user profile
  suspend fun saveUserProfile(displayName: String, email: String, phone: String, gramPanchayat: String): Result<Unit> {
    val uid = requireUserId()
    val path = "users/$uid"
    val payload = hashMapOf<String, Any>(
      "userId" to uid,
      "displayName" to displayName,
      "email" to email,
      "phone" to phone,
      "gramPanchayat" to gramPanchayat,
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp()
    )

    return try {
      db.document(path).set(payload).await()
      Result.success(Unit)
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, path)
      Result.failure(e)
    }
  }

  // Pre-seed initial sample grievances for a user if they don't have any yet
  suspend fun seedInitialUserGrievances(userId: String) {
    try {
      val existing = db.collection("grievances").whereEqualTo("userId", userId).limit(1).get().await()
      if (existing.isEmpty) {
        val samples = listOf(
          hashMapOf<String, Any>(
            "id" to "GP202610160001",
            "userId" to userId,
            "authorName" to (auth.currentUser?.displayName ?: "Citizen"),
            "authorEmail" to (auth.currentUser?.email ?: ""),
            "title" to "Low water pressure in Ward 4",
            "category" to "Water Supply",
            "description" to "Main pipeline valve leaking heavily near the public overhead tank in Ward No. 4, causing water wastage and low pressure.",
            "status" to "In Progress",
            "submittedDate" to "16 Oct 2026",
            "district" to "Bengaluru Rural",
            "taluk" to "Malur",
            "gramPanchayat" to "Malur GP",
            "village" to "Malur",
            "wardNumber" to "Ward No. 4",
            "locationName" to "Ward 4 Panchayat Tank, Malur Taluk",
            "gpsCoordinates" to "13.2988° N, 77.5812° E",
            "isPublic" to true,
            "assignedOfficer" to "Ramesh Kumar",
            "department" to "Jal Nigam Dept",
            "expectedResolutionDate" to "19 Oct 2026",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
          ),
          hashMapOf<String, Any>(
            "id" to "GP202609280412",
            "userId" to userId,
            "authorName" to (auth.currentUser?.displayName ?: "Citizen"),
            "authorEmail" to (auth.currentUser?.email ?: ""),
            "title" to "Non-working streetlamp near temple",
            "category" to "Streetlights",
            "description" to "Awaiting verification from Panchayat Development Officer (PDO) for bulb replacement dispatch.",
            "status" to "Pending",
            "submittedDate" to "Yesterday",
            "district" to "Bengaluru Rural",
            "taluk" to "Malur",
            "gramPanchayat" to "Malur GP",
            "village" to "Sadenahalli",
            "wardNumber" to "Ward No. 2",
            "locationName" to "Sri Anjaneya Temple Cross, Sadenahalli",
            "gpsCoordinates" to "13.2945° N, 77.5850° E",
            "isPublic" to true,
            "assignedOfficer" to "Manjunath S.",
            "department" to "Electrical Section",
            "expectedResolutionDate" to "20 Oct 2026",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
          ),
          hashMapOf<String, Any>(
            "id" to "GP202608140195",
            "userId" to userId,
            "authorName" to (auth.currentUser?.displayName ?: "Citizen"),
            "authorEmail" to (auth.currentUser?.email ?: ""),
            "title" to "Blocked drainage clearing near school",
            "category" to "Drainage",
            "description" to "Silt and plastic waste removed successfully by sanitation workers. Verified by ward member.",
            "status" to "Resolved",
            "submittedDate" to "3 days ago",
            "district" to "Bengaluru Rural",
            "taluk" to "Malur",
            "gramPanchayat" to "Malur GP",
            "village" to "Bommenahalli",
            "wardNumber" to "Ward No. 1",
            "locationName" to "Govt. Higher Primary School, Bommenahalli",
            "gpsCoordinates" to "13.2890° N, 77.5790° E",
            "isPublic" to true,
            "rating" to 4L,
            "assignedOfficer" to "Shivappa G.",
            "department" to "Health & Sanitation",
            "expectedResolutionDate" to "Completed",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
          )
        )

        for (sample in samples) {
          val id = sample["id"] as String
          db.collection("grievances").document(id).set(sample).await()
        }
      }
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.LIST, "grievances")
    }
  }
}
