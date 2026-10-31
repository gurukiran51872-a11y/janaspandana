package com.example.data.repository

import com.example.data.model.Grievance
import com.example.data.model.GrievanceStatus
import com.example.data.model.MonthlyTrendItem
import com.example.data.model.NoticeItem
import com.example.data.model.TimelineStep
import com.example.data.model.VillageStat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GrievanceRepository {

  private val _grievances = MutableStateFlow<List<Grievance>>(getInitialGrievances())
  val grievances: StateFlow<List<Grievance>> = _grievances.asStateFlow()

  val notices: List<NoticeItem> = listOf(
    NoticeItem(
      id = "N1",
      tag = "Gram Sabha Meeting",
      tagKn = "ಗ್ರಾಮ ಸಭೆ ಸಭೆ",
      timeAgo = "2 hours ago",
      title = "Annual budget planning meeting scheduled for fiscal 2026-27",
      titleKn = "2026-27ನೇ ಸಾಲಿನ ವಾರ್ಷಿಕ ಬಜೆಟ್ ಯೋಜನಾ ಸಭೆ ನಿಗದಿಪಡಿಸಲಾಗಿದೆ",
      snippet = "All villagers are requested to attend and share priorities for village road and drinking water infrastructure projects.",
      snippetKn = "ಎಲ್ಲ ಗ್ರಾಮಸ್ಥರು ಭಾಗವಹಿಸಿ ರಸ್ತೆ ಮತ್ತು ಕುಡಿಯುವ ನೀರಿನ ಅಭಿವೃದ್ಧಿ ಕಾಮಗಾರಿಗಳ ಸಲಹೆಗಳನ್ನು ನೀಡಬೇಕಾಗಿ ಕೋರಲಾಗಿದೆ.",
      isAlert = true
    ),
    NoticeItem(
      id = "N2",
      tag = "Water Supply Advisory",
      tagKn = "ಕುಡಿಯುವ ನೀರಿನ ಪ್ರಕಟಣೆ",
      timeAgo = "Yesterday",
      title = "Scheduled pipeline maintenance in Ward 3 & 4",
      titleKn = "ವಾರ್ಡ್ 3 ಮತ್ತು 4 ರಲ್ಲಿ ಪೈಪ್‌ಲೈನ್ ನಿರ್ವಹಣಾ ಕಾರ್ಯ",
      snippet = "Water supply will be temporarily halted between 10:00 AM and 02:00 PM tomorrow due to valve replacement.",
      snippetKn = "ವಾಲ್ವ್ ಬದಲಾವಣೆ ಕಾಮಗಾರಿಯ ಕಾರಣ ನಾಳೆ ಬೆಳಿಗ್ಗೆ 10:00 ರಿಂದ ಮಧ್ಯಾಹ್ನ 02:00 ರವರೆಗೆ ನೀರು ಸರಬರಾಜು ಸ್ಥಗಿತಗೊಳ್ಳಲಿದೆ.",
      isAlert = false
    )
  )

  val villageStats: List<VillageStat> = listOf(
    VillageStat("VP", "Malavalli Gram Panchayat", "Mandya District", 96, 1420),
    VillageStat("HP", "Hunsur Rural Panchayat", "Mysuru District", 92, 1180),
    VillageStat("SP", "Sira Taluk Panchayat", "Tumakuru District", 89, 950),
    VillageStat("GP", "Gundlupet Panchayat", "Chamarajanagar", 94, 830)
  )

  val monthlyTrends: List<MonthlyTrendItem> = listOf(
    MonthlyTrendItem("Nov", 3500, 3200),
    MonthlyTrendItem("Dec", 4200, 3900),
    MonthlyTrendItem("Jan", 3000, 2900),
    MonthlyTrendItem("Feb", 4500, 4300),
    MonthlyTrendItem("Mar", 3800, 3700),
    MonthlyTrendItem("Apr", 5000, 4700)
  )

  fun getGrievanceById(id: String): Grievance? {
    val cleanId = id.trim().uppercase()
    return _grievances.value.firstOrNull { 
      it.id.equals(cleanId, ignoreCase = true) || it.id.contains(cleanId, ignoreCase = true) 
    }
  }

  fun submitGrievance(
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
  ): Grievance {
    val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
    val randomDigits = (1000..9999).random()
    val newId = "GP202610$randomDigits"

    val newGrievance = Grievance(
      id = newId,
      title = title,
      category = category,
      description = description,
      status = GrievanceStatus.PENDING,
      submittedDate = dateStr,
      district = district,
      taluk = taluk,
      gramPanchayat = gramPanchayat,
      village = village,
      wardNumber = wardNumber,
      locationName = locationName.ifBlank { "$village, $taluk Taluk" },
      gpsCoordinates = gpsCoordinates,
      assignedOfficer = "Panchayat Development Officer (PDO)",
      department = "Gram Panchayat Admin",
      expectedResolutionDate = "Within 48 hours",
      isPublic = isPublic,
      timeline = listOf(
        TimelineStep(
          title = "Complaint Registered",
          titleKn = "ದೂರು ನೋಂದಾಯಿಸಲಾಗಿದೆ",
          timestamp = "Today, Just now",
          description = "Successfully registered via Janaspandana Mobile App. Acknowledgement SMS sent to registered mobile number.",
          descriptionKn = "ಜನಸ್ಪಂದನ ಮೊಬೈಲ್ ಆ್ಯಪ್ ಮೂಲಕ ಯಶಸ್ವಿಯಾಗಿ ನೋಂದಾಯಿಸಲಾಗಿದೆ. ಸ್ವೀಕೃತಿ ಎಸ್ಎಂಎಸ್ ರವಾನಿಸಲಾಗಿದೆ.",
          isCompleted = true,
          isCurrent = true
        )
      ),
      evidencePhotoUri = evidencePhotoUri
    )

    _grievances.update { listOf(newGrievance) + it }
    return newGrievance
  }

  fun updateRatingAndFeedback(id: String, rating: Int, feedback: String?) {
    _grievances.update { list ->
      list.map { g ->
        if (g.id == id) g.copy(rating = rating, feedbackComment = feedback) else g
      }
    }
  }

  fun reopenGrievance(id: String): Boolean {
    var found = false
    _grievances.update { list ->
      list.map { g ->
        if (g.id == id) {
          found = true
          val updatedTimeline = listOf(
            TimelineStep(
              title = "Complaint Reopened",
              titleKn = "ದೂರನ್ನು ಮರುತೆರೆಯಲಾಗಿದೆ",
              timestamp = "Today, Just now",
              description = "Citizen reported dissatisfaction with resolution. Sent back for field re-inspection.",
              descriptionKn = "ಪರಿಹಾರದಿಂದ ಅತೃಪ್ತಿ ವ್ಯಕ್ತಪಡಿಸಲಾಗಿದೆ. ಮರುಪರಿಶೀಲನೆಗಾಗಿ ಕಳುಹಿಸಲಾಗಿದೆ.",
              isCompleted = true,
              isCurrent = true
            )
          ) + g.timeline
          g.copy(status = GrievanceStatus.IN_PROGRESS, timeline = updatedTimeline)
        } else g
      }
    }
    return found
  }

  companion object {
    private fun getInitialGrievances(): List<Grievance> {
      return listOf(
        Grievance(
          id = "GP202610160001",
          title = "Low water pressure in Ward 4",
          titleKn = "ವಾರ್ಡ್ 4 ರಲ್ಲಿ ಕಡಿಮೆ ನೀರಿನ ಒತ್ತಡ",
          category = "Water Supply",
          categoryKn = "ಕುಡಿಯುವ ನೀರು ಸರಬರಾಜು",
          description = "Main pipeline valve leaking heavily near the public overhead tank in Ward No. 4, causing water wastage and low pressure.",
          descriptionKn = "ವಾರ್ಡ್ 4 ರ ಸಾರ್ವಜನಿಕ ಓವರ್‌ಹೆಡ್ ಟ್ಯಾಂಕ್ ಬಳಿ ಮುಖ್ಯ ಪೈಪ್‌ಲೈನ್ ವಾಲ್ವ್ ಸೋರಿಕೆಯಾಗುತ್ತಿದ್ದು, ನೀರಿನ ವ್ಯರ್ಥ ಮತ್ತು ಕಡಿಮೆ ಒತ್ತಡ ಉಂಟಾಗುತ್ತಿದೆ.",
          status = GrievanceStatus.IN_PROGRESS,
          submittedDate = "16 Oct 2026",
          district = "Bengaluru Rural",
          taluk = "Malur",
          gramPanchayat = "Malur GP",
          village = "Malur",
          wardNumber = "Ward No. 4",
          locationName = "Ward 4 Panchayat Tank, Malur Taluk",
          gpsCoordinates = "13.2988° N, 77.5812° E",
          assignedOfficer = "Ramesh Kumar",
          department = "Jal Nigam Dept",
          expectedResolutionDate = "19 Oct 2026",
          timeline = listOf(
            TimelineStep(
              title = "Field Inspection Started",
              titleKn = "ಸ್ಥಳ ಪರಿಶೀಲನೆ ಪ್ರಾರಂಭವಾಗಿದೆ",
              timestamp = "17 Oct, 10:30 AM",
              description = "Officer Ramesh Kumar visited the site with technical team to assess valve damage. Replacement parts requisitioned.",
              descriptionKn = "ಅಧಿಕಾರಿ ರಮೇಶ್ ಕುಮಾರ್ ತಾಂತ್ರಿಕ ತಂಡದೊಂದಿಗೆ ಸ್ಥಳ ಪರಿಶೀಲನೆ ನಡೆಸಿ ವಾಲ್ವ್ ಹಾನಿಯನ್ನು ಅಂದಾಜಿಸಿದ್ದಾರೆ. ಬಿಡಿಭಾಗಗಳನ್ನು ತರಿಸಲಾಗುತ್ತಿದೆ.",
              isCompleted = true,
              isCurrent = true
            ),
            TimelineStep(
              title = "Assigned to Department",
              titleKn = "ಇಲಾಖೆಗೆ ನಿಯೋಜಿಸಲಾಗಿದೆ",
              timestamp = "16 Oct, 03:15 PM",
              description = "Complaint forwarded to Jal Nigam Water Section head. Technician allocated for site visit.",
              descriptionKn = "ದೂರನ್ನು ಜಲ ನಿಗಮ ನೀರಿನ ವಿಭಾಗದ ಮುಖ್ಯಸ್ಥರಿಗೆ ಕಳುಹಿಸಲಾಗಿದೆ. ಸ್ಥಳ ಪರಿಶೀಲನೆಗೆ ತಂತ್ರಜ್ಞರನ್ನು ನಿಯೋಜಿಸಲಾಗಿದೆ.",
              isCompleted = true
            ),
            TimelineStep(
              title = "Complaint Registered",
              titleKn = "ದೂರು ನೋಂದಾಯಿಸಲಾಗಿದೆ",
              timestamp = "16 Oct, 09:00 AM",
              description = "Successfully registered via Janaspandana Mobile App. Acknowledgement SMS sent to registered mobile number.",
              descriptionKn = "ಜನಸ್ಪಂದನ ಮೊಬೈಲ್ ಆ್ಯಪ್ ಮೂಲಕ ಯಶಸ್ವಿಯಾಗಿ ನೋಂದಾಯಿಸಲಾಗಿದೆ. ಸ್ವೀಕೃತಿ ಸಂದೇಶ ರವಾನಿಸಲಾಗಿದೆ.",
              isCompleted = true
            )
          )
        ),
        Grievance(
          id = "GP202609280412",
          title = "Non-working streetlamp near temple",
          titleKn = "ದೇವಸ್ಥಾನದ ಬಳಿ ಕೆಟ್ಟಿರುವ ಬೀದಿದೀಪ",
          category = "Streetlights",
          categoryKn = "ಬೀದಿದೀಪಗಳು",
          description = "Awaiting verification from Panchayat Development Officer (PDO) for bulb replacement dispatch.",
          descriptionKn = "ಬಲ್ಬ್ ಬದಲಾವಣೆಗಾಗಿ ಪಂಚಾಯತ್ ಅಭಿವೃದ್ಧಿ ಅಧಿಕಾರಿಯಿಂದ ಪರಿಶೀಲನೆ ಬಾಕಿ ಉಳಿದಿದೆ.",
          status = GrievanceStatus.PENDING,
          submittedDate = "Yesterday",
          district = "Bengaluru Rural",
          taluk = "Malur",
          gramPanchayat = "Malur GP",
          village = "Sadenahalli",
          wardNumber = "Ward No. 2",
          locationName = "Sri Anjaneya Temple Cross, Sadenahalli",
          gpsCoordinates = "13.2945° N, 77.5850° E",
          assignedOfficer = "Manjunath S.",
          department = "Electrical Section",
          expectedResolutionDate = "20 Oct 2026",
          timeline = listOf(
            TimelineStep(
              title = "Officer Review Pending",
              titleKn = "ಅಧಿಕಾರಿ ಪರಿಶೀಲನೆ ಬಾಕಿ",
              timestamp = "Yesterday, 04:00 PM",
              description = "Assigned to PDO desk for electrical contract dispatch.",
              descriptionKn = "ವಿದ್ಯುತ್ ಗುತ್ತಿಗೆದಾರರ ರವಾನೆಗಾಗಿ ಪಿಡಿಒ ಮೇಜಿಗೆ ಕಳುಹಿಸಲಾಗಿದೆ.",
              isCompleted = true,
              isCurrent = true
            ),
            TimelineStep(
              title = "Complaint Registered",
              titleKn = "ದೂರು ನೋಂದಾಯಿಸಲಾಗಿದೆ",
              timestamp = "Yesterday, 11:30 AM",
              description = "Citizen grievance logged through Janaspandana portal.",
              descriptionKn = "ಜನಸ್ಪಂದನ ಪೋರ್ಟಲ್ ಮೂಲಕ ದೂರು ದಾಖಲಾಗಿದೆ.",
              isCompleted = true
            )
          )
        ),
        Grievance(
          id = "GP202608140195",
          title = "Blocked drainage clearing near school",
          titleKn = "ಶಾಲೆಯ ಬಳಿ ಮುಚ್ಚಿಹೋಗಿರುವ ಚರಂಡಿ ಸ್ವಚ್ಛತೆ",
          category = "Drainage",
          categoryKn = "ಚರಂಡಿ ಮತ್ತು ಒಳಚರಂಡಿ",
          description = "Silt and plastic waste removed successfully by sanitation workers. Verified by ward member.",
          descriptionKn = "ಸ್ವಚ್ಛತಾ ಕಾರ್ಮಿಕರಿಂದ ಹೂಳು ಮತ್ತು ಪ್ಲಾಸ್ಟಿಕ್ ತ್ಯಾಜ್ಯವನ್ನು ಯಶಸ್ವಿಯಾಗಿ ತೆಗೆದುಹಾಕಲಾಗಿದೆ. ವಾರ್ಡ್ ಸದಸ್ಯರಿಂದ ಪರಿಶೀಲಿಸಲಾಗಿದೆ.",
          status = GrievanceStatus.RESOLVED,
          submittedDate = "3 days ago",
          district = "Bengaluru Rural",
          taluk = "Malur",
          gramPanchayat = "Malur GP",
          village = "Bommenahalli",
          wardNumber = "Ward No. 1",
          locationName = "Govt. Higher Primary School, Bommenahalli",
          gpsCoordinates = "13.2890° N, 77.5790° E",
          assignedOfficer = "Shivappa G.",
          department = "Health & Sanitation",
          expectedResolutionDate = "Completed",
          rating = 4,
          timeline = listOf(
            TimelineStep(
              title = "Issue Resolved",
              titleKn = "ಸಮಸ್ಯೆ ಪರಿಹರಿಸಲಾಗಿದೆ",
              timestamp = "3 days ago, 04:30 PM",
              description = "Desilting work completed and sanitized with bleaching powder. Verified by ward member.",
              descriptionKn = "ಹೂಳು ತೆಗೆಯುವ ಕಾರ್ಯ ಪೂರ್ಣಗೊಂಡಿದ್ದು ಬ್ಲೀಚಿಂಗ್ ಪೌಡರ್ ಸಿಂಪಡಿಸಲಾಗಿದೆ. ವಾರ್ಡ್ ಸದಸ್ಯರಿಂದ ಪರಿಶೀಲನೆ.",
              isCompleted = true,
              isCurrent = true
            ),
            TimelineStep(
              title = "Field Sanitation Team Deployed",
              titleKn = "ಸ್ವಚ್ಛತಾ ತಂಡ ನಿಯೋಜನೆ",
              timestamp = "4 days ago, 09:00 AM",
              description = "Sanitation tractor and 4 workers dispatched to school surroundings.",
              descriptionKn = "ಶಾಲಾ ಪರಿಸರಕ್ಕೆ ಟ್ರ್ಯಾಕ್ಟರ್ ಮತ್ತು 4 ಕಾರ್ಮಿಕರನ್ನು ಕಳುಹಿಸಲಾಗಿದೆ.",
              isCompleted = true
            ),
            TimelineStep(
              title = "Complaint Registered",
              titleKn = "ದೂರು ನೋಂದಾಯಿಸಲಾಗಿದೆ",
              timestamp = "5 days ago, 10:15 AM",
              description = "Registered via web portal.",
              descriptionKn = "ವೆಬ್ ಪೋರ್ಟಲ್ ಮೂಲಕ ನೋಂದಾಯಿಸಲಾಗಿದೆ.",
              isCompleted = true
            )
          )
        ),
        Grievance(
          id = "JP-2023-8891",
          title = "Low water pressure in Ward 4",
          titleKn = "ವಾರ್ಡ್ 4 ರಲ್ಲಿ ಕಡಿಮೆ ನೀರಿನ ಒತ್ತಡ",
          category = "Water Supply",
          categoryKn = "ಕುಡಿಯುವ ನೀರು",
          description = "Main pipeline valve repair scheduled with the local plumber. Expected completion by tomorrow evening.",
          descriptionKn = "ಸ್ಥಳೀಯ ಪ್ಲಂಬರ್ ಅವರೊಂದಿಗೆ ಮುಖ್ಯ ಪೈಪ್‌ಲೈನ್ ವಾಲ್ವ್ ದುರಸ್ತಿ ನಿಗದಿಪಡಿಸಲಾಗಿದೆ.",
          status = GrievanceStatus.IN_PROGRESS,
          submittedDate = "14 Oct 2026",
          district = "Bengaluru Rural",
          taluk = "Malur",
          gramPanchayat = "Malur GP",
          village = "Malur",
          wardNumber = "Ward No. 4",
          locationName = "Near Market Road, Malur",
          gpsCoordinates = "13.2988° N, 77.5812° E",
          assignedOfficer = "Ramesh Kumar",
          department = "Jal Nigam Dept",
          expectedResolutionDate = "18 Oct 2026"
        )
      )
    }
  }
}
