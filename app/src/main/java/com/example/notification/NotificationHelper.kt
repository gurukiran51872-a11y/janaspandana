package com.example.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.GrievanceStatus

object NotificationHelper {

  const val CHANNEL_ID = "grievance_status_channel"
  const val EXTRA_GRIEVANCE_ID = "extra_grievance_id"

  fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = "Grievance Status Updates"
      val descriptionText = "Notifications sent when your grievance status changes (e.g. from Pending to Resolved)"
      val importance = NotificationManager.IMPORTANCE_HIGH
      val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
        description = descriptionText
        enableLights(true)
        enableVibration(true)
      }
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }

  fun showStatusChangeNotification(
    context: Context,
    grievanceId: String,
    title: String,
    previousStatus: GrievanceStatus?,
    newStatus: GrievanceStatus,
    language: AppLanguage = AppLanguage.ENGLISH
  ) {
    createNotificationChannel(context)

    // Check runtime notification permission on Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        return
      }
    }

    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      putExtra(EXTRA_GRIEVANCE_ID, grievanceId)
    }

    val pendingIntent = PendingIntent.getActivity(
      context,
      grievanceId.hashCode(),
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val statusTitle = newStatus.title(language)
    val notifTitle = if (language == AppLanguage.ENGLISH) {
      "Grievance #$grievanceId Updated"
    } else {
      "ದೂರು #$grievanceId ನವೀಕರಿಸಲಾಗಿದೆ"
    }

    val notifBody = if (previousStatus != null) {
      if (language == AppLanguage.ENGLISH) {
        "\"$title\" changed from ${previousStatus.titleEn} to $statusTitle"
      } else {
        "\"$title\" ಸ್ಥಿತಿ ${previousStatus.titleKn} ಇಂದ $statusTitle ಗೆ ಬದಲಾಗಿದೆ"
      }
    } else {
      if (language == AppLanguage.ENGLISH) {
        "\"$title\" is now $statusTitle"
      } else {
        "\"$title\" ಈಗ $statusTitle ಆಗಿದೆ"
      }
    }

    val color = when (newStatus) {
      GrievanceStatus.RESOLVED -> 0xFF15803D.toInt()
      GrievanceStatus.IN_PROGRESS -> 0xFF0369A1.toInt()
      GrievanceStatus.PENDING -> 0xFFB45309.toInt()
      GrievanceStatus.SUBMITTED -> 0xFF334155.toInt()
    }

    val actionLabel = if (language == AppLanguage.ENGLISH) "Track Status" else "ಸ್ಥಿತಿ ತಿಳಿಯಿರಿ"
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentTitle(notifTitle)
      .setContentText(notifBody)
      .setStyle(NotificationCompat.BigTextStyle().bigText(notifBody))
      .setColor(color)
      .setAutoCancel(true)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setCategory(NotificationCompat.CATEGORY_STATUS)
      .setContentIntent(pendingIntent)
      .addAction(R.mipmap.ic_launcher, actionLabel, pendingIntent)

    val notificationManager = NotificationManagerCompat.from(context)
    val notificationId = (grievanceId.hashCode() and 0x7FFFFFFF)
    try {
      notificationManager.notify(notificationId, builder.build())
    } catch (_: SecurityException) {
      // Handled if permission revoked concurrently
    }
  }
}
