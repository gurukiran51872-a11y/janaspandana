package com.example.service

import android.util.Log
import com.example.data.model.AppLanguage
import com.example.data.model.GrievanceStatus
import com.example.notification.NotificationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class JanaspandanaMessagingService : FirebaseMessagingService() {

  companion object {
    private const val TAG = "JanaspandanaFCM"
  }

  override fun onNewToken(token: String) {
    super.onNewToken(token)
    Log.d(TAG, "New FCM Token received: $token")
    val currentUser = FirebaseAuth.getInstance().currentUser
    if (currentUser != null) {
      val userDoc = FirebaseFirestore.getInstance().collection("users").document(currentUser.uid)
      userDoc.update(
        mapOf(
          "fcmToken" to token,
          "fcmUpdatedAt" to FieldValue.serverTimestamp()
        )
      ).addOnFailureListener {
        // If user profile document doesn't exist yet, merge create
        userDoc.set(
          mapOf(
            "userId" to currentUser.uid,
            "fcmToken" to token,
            "fcmUpdatedAt" to FieldValue.serverTimestamp()
          ),
          com.google.firebase.firestore.SetOptions.merge()
        )
      }

      try {
        com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic("user_${currentUser.uid}")
        com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic("grievance_status_updates")
      } catch (e: Exception) {
        Log.w(TAG, "Topic subscription deferred: ${e.message}")
      }
    }
  }

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    super.onMessageReceived(remoteMessage)
    Log.d(TAG, "FCM message received from: ${remoteMessage.from}")

    // Extract payload from data map or notification payload
    val data = remoteMessage.data
    val grievanceId = data["grievanceId"] ?: data["id"] ?: "GP202610160001"
    val title = data["title"] ?: remoteMessage.notification?.title ?: "Civic Grievance"
    val prevStatusStr = data["previousStatus"] ?: data["oldStatus"]
    val newStatusStr = data["newStatus"] ?: data["status"] ?: remoteMessage.notification?.body ?: "Resolved"
    val langStr = data["language"] ?: "EN"

    val prevStatus = if (!prevStatusStr.isNullOrBlank()) GrievanceStatus.fromString(prevStatusStr) else null
    val newStatus = GrievanceStatus.fromString(newStatusStr)
    val language = if (langStr.equals("KN", ignoreCase = true)) AppLanguage.KANNADA else AppLanguage.ENGLISH

    NotificationHelper.showStatusChangeNotification(
      context = applicationContext,
      grievanceId = grievanceId,
      title = title,
      previousStatus = prevStatus,
      newStatus = newStatus,
      language = language
    )
  }
}
