package com.example.data.model

enum class AppLanguage(val code: String, val displayName: String) {
  ENGLISH("EN", "English"),
  KANNADA("KN", "ಕನ್ನಡ")
}

enum class GrievanceStatus(val titleEn: String, val titleKn: String) {
  SUBMITTED("Submitted", "ಸಲ್ಲಿಸಲಾಗಿದೆ"),
  PENDING("Pending", "ಬಾಕಿ ಉಳಿದಿದೆ"),
  IN_PROGRESS("In Progress", "ಪ್ರಗತಿಯಲ್ಲಿದೆ"),
  RESOLVED("Resolved", "ಪರಿಹರಿಸಲಾಗಿದೆ");

  fun title(language: AppLanguage): String {
    return if (language == AppLanguage.ENGLISH) titleEn else titleKn
  }

  companion object {
    fun fromString(value: String?): GrievanceStatus {
      return when (value?.trim()?.lowercase()) {
        "resolved" -> RESOLVED
        "in progress", "inprogress", "in_progress" -> IN_PROGRESS
        "submitted" -> SUBMITTED
        "pending" -> PENDING
        else -> PENDING
      }
    }
  }
}

data class TimelineStep(
  val title: String,
  val titleKn: String = "",
  val timestamp: String,
  val description: String,
  val descriptionKn: String = "",
  val isCompleted: Boolean = true,
  val isCurrent: Boolean = false
)

data class Grievance(
  val id: String,
  val title: String,
  val titleKn: String = "",
  val category: String,
  val categoryKn: String = "",
  val description: String,
  val descriptionKn: String = "",
  val status: GrievanceStatus = GrievanceStatus.PENDING,
  val submittedDate: String,
  val district: String = "Bengaluru Rural",
  val taluk: String = "Malur",
  val gramPanchayat: String = "Malur GP",
  val village: String = "Sadenahalli",
  val wardNumber: String = "Ward No. 4",
  val locationName: String = "Ward 4 Panchayat Tank, Malur Taluk",
  val gpsCoordinates: String = "13.2988° N, 77.5812° E",
  val mapImageUrl: String = "https://lh3.googleusercontent.com/aida-public/AB6AXuBGSHRbBZPrm38pXQurZmm36hKJPKsbkrYhwO5Ir-9dHY2_Qvb_L9lsKa9aa-I4dB-9a3y3KjmEKgwUTzVnY6WOHUvn4tX8PpzaTjj-5z9SqtgaVUDcdu5hBTP_nHKk-2YOsoCuBIU307GKD0CNctg21OGzXBTFqTLQ6iVsRk2CrRiCqgmV8XA8vQou0gNM26NsQb-aqKwFN8-eVUr2aGIfT9TOz59hdUs4HF36il7YzYyKJfbFP4s",
  val assignedOfficer: String = "Ramesh Kumar",
  val department: String = "Jal Nigam Dept",
  val expectedResolutionDate: String = "19 Oct 2026",
  val isPublic: Boolean = true,
  val rating: Int? = null,
  val feedbackComment: String? = null,
  val timeline: List<TimelineStep> = emptyList(),
  val evidencePhotoUri: String? = null
)

data class NoticeItem(
  val id: String,
  val tag: String,
  val tagKn: String,
  val timeAgo: String,
  val title: String,
  val titleKn: String,
  val snippet: String,
  val snippetKn: String,
  val isAlert: Boolean = false
)

data class VillageStat(
  val initials: String,
  val name: String,
  val district: String,
  val resolvedPercent: Int,
  val totalCases: Int
)

data class MonthlyTrendItem(
  val month: String,
  val received: Int,
  val resolved: Int
)
