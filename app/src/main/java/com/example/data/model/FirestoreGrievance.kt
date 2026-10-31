package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class FirestoreGrievance(
  val id: String = "",
  val userId: String = "",
  val authorName: String = "",
  val authorEmail: String = "",
  val title: String = "",
  val category: String = "",
  val description: String = "",
  val status: String = "Pending",
  val submittedDate: String = "",
  val district: String = "Bengaluru Rural",
  val taluk: String = "Malur",
  val gramPanchayat: String = "Malur GP",
  val village: String = "Malur",
  val wardNumber: String = "Ward No. 4",
  val locationName: String = "",
  val gpsCoordinates: String = "",
  @get:PropertyName("isPublic") @set:PropertyName("isPublic") var isPublic: Boolean = true,
  val rating: Long? = null,
  val feedbackComment: String? = null,
  val assignedOfficer: String = "Panchayat Development Officer (PDO)",
  val department: String = "Gram Panchayat Admin",
  val expectedResolutionDate: String = "Within 48 hours",
  val evidencePhotoUri: String? = null,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreUserProfile(
  val id: String = "",
  val userId: String = "",
  val displayName: String = "",
  val email: String = "",
  val phone: String = "",
  val gramPanchayat: String = "Malur GP",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)
