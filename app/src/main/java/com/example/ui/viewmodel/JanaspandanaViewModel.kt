package com.example.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppLanguage
import com.example.data.model.FirestoreGrievance
import com.example.data.model.Grievance
import com.example.data.model.GrievanceStatus
import com.example.data.model.NoticeItem
import com.example.data.model.TimelineStep
import com.example.data.model.VillageStat
import com.example.data.repository.FirestoreGrievanceRepository
import com.example.data.repository.GrievanceRepository
import com.example.notification.NotificationHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppTab(val route: String) {
  HOME("home"),
  SUBMIT("submit"),
  TRACK("track"),
  DASHBOARD("dashboard"),
  PUBLIC("public")
}

data class SubmitFormData(
  val title: String = "",
  val category: String = "",
  val description: String = "",
  val district: String = "Bengaluru Rural",
  val taluk: String = "Malur",
  val gramPanchayat: String = "Malur GP",
  val village: String = "Sadenahalli",
  val wardNumber: String = "",
  val landmark: String = "",
  val gpsCoordinates: String = "13.2988° N, 77.5812° E",
  val isGpsPinned: Boolean = true,
  val dateOfOccurrence: String = "16/10/2026",
  val isPublic: Boolean = true,
  val evidencePhotoUri: String? = null,
  val localPhotoUri: Uri? = null,
  val isUploadingPhoto: Boolean = false,
  val isAiAnalyzing: Boolean = false,
  val aiSuggested: Boolean = false,
  val isSubmitting: Boolean = false,
  val validationError: String? = null
)

class JanaspandanaViewModel(
  private val firestoreRepository: FirestoreGrievanceRepository? = null,
  private val localFallbackRepo: GrievanceRepository = GrievanceRepository()
) : ViewModel() {

  private val _activeTab = MutableStateFlow(AppTab.HOME)
  val activeTab: StateFlow<AppTab> = _activeTab.asStateFlow()

  private val _language = MutableStateFlow(AppLanguage.ENGLISH)
  val language: StateFlow<AppLanguage> = _language.asStateFlow()

  private val _isDarkMode = MutableStateFlow(false)
  val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

  private val _showProfileDialog = MutableStateFlow(false)
  val showProfileDialog: StateFlow<Boolean> = _showProfileDialog.asStateFlow()

  private val _trackSearchId = MutableStateFlow("GP202610160001")
  val trackSearchId: StateFlow<String> = _trackSearchId.asStateFlow()

  private val _activeTrackGrievance = MutableStateFlow<Grievance?>(null)
  val activeTrackGrievance: StateFlow<Grievance?> = _activeTrackGrievance.asStateFlow()

  private val _dashboardSearchQuery = MutableStateFlow("")
  val dashboardSearchQuery: StateFlow<String> = _dashboardSearchQuery.asStateFlow()

  private val _dashboardCategoryFilter = MutableStateFlow("All")
  val dashboardCategoryFilter: StateFlow<String> = _dashboardCategoryFilter.asStateFlow()

  private val _dashboardStatusFilter = MutableStateFlow("All")
  val dashboardStatusFilter: StateFlow<String> = _dashboardStatusFilter.asStateFlow()

  private val _submitForm = MutableStateFlow(SubmitFormData())
  val submitForm: StateFlow<SubmitFormData> = _submitForm.asStateFlow()

  private val _submittedSuccessGrievance = MutableStateFlow<Grievance?>(null)
  val submittedSuccessGrievance: StateFlow<Grievance?> = _submittedSuccessGrievance.asStateFlow()

  private val _statusNotificationAlert = MutableStateFlow<String?>(null)
  val statusNotificationAlert: StateFlow<String?> = _statusNotificationAlert.asStateFlow()

  private val _allGrievances = MutableStateFlow<List<Grievance>>(emptyList())
  val allGrievances: StateFlow<List<Grievance>> = _allGrievances.asStateFlow()

  val notices: List<NoticeItem> = localFallbackRepo.notices
  val villageStats: List<VillageStat> = localFallbackRepo.villageStats
  val monthlyTrends = localFallbackRepo.monthlyTrends

  val filteredDashboardGrievances: StateFlow<List<Grievance>> = combine(
    allGrievances,
    _dashboardSearchQuery,
    _dashboardCategoryFilter,
    _dashboardStatusFilter
  ) { list, query, catFilter, statFilter ->
    list.filter { g ->
      val cleanQuery = query.trim()
      val matchesQuery = cleanQuery.isBlank() || 
        g.title.contains(cleanQuery, ignoreCase = true) ||
        g.titleKn.contains(cleanQuery, ignoreCase = true) ||
        g.category.contains(cleanQuery, ignoreCase = true) ||
        g.categoryKn.contains(cleanQuery, ignoreCase = true) ||
        g.id.contains(cleanQuery, ignoreCase = true) ||
        g.description.contains(cleanQuery, ignoreCase = true) ||
        g.status.titleEn.contains(cleanQuery, ignoreCase = true) ||
        g.status.titleKn.contains(cleanQuery, ignoreCase = true)

      val matchesCategory = when (catFilter) {
        "All", "All (12)" -> true
        "Water Supply", "Water Supply (4)" -> g.category.contains("Water", ignoreCase = true)
        "Streetlights", "Streetlights (3)" -> g.category.contains("Street", ignoreCase = true)
        "Drainage", "Drainage (3)" -> g.category.contains("Drainage", ignoreCase = true)
        "Roads", "Roads (2)" -> g.category.contains("Road", ignoreCase = true)
        else -> true
      }

      val matchesStatus = when (statFilter) {
        "All" -> true
        "Pending" -> g.status == GrievanceStatus.PENDING
        "In Progress" -> g.status == GrievanceStatus.IN_PROGRESS
        "Resolved" -> g.status == GrievanceStatus.RESOLVED
        "Submitted" -> g.status == GrievanceStatus.SUBMITTED
        else -> true
      }

      matchesQuery && matchesCategory && matchesStatus
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  init {
    // Initialize with local fallback grievances first
    _allGrievances.value = localFallbackRepo.grievances.value
    if (_allGrievances.value.isNotEmpty()) {
      _activeTrackGrievance.value = _allGrievances.value.first()
    }
  }

  fun onUserAuthenticated(userId: String, context: Context? = null) {
    if (firestoreRepository != null) {
      viewModelScope.launch {
        // Retrieve and register FCM token to Firestore for automatic push notifications
        try {
          FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            Log.d("JanaspandanaVM", "FCM token retrieved: $token")
            viewModelScope.launch {
              firestoreRepository.saveFcmToken(token)
            }
          }.addOnFailureListener { e ->
            Log.w("JanaspandanaVM", "FCM token registration deferred: ${e.message}")
          }
          // Subscribe to user and grievance status topic streams
          FirebaseMessaging.getInstance().subscribeToTopic("user_$userId")
          FirebaseMessaging.getInstance().subscribeToTopic("grievance_status_updates")
        } catch (e: Exception) {
          Log.w("JanaspandanaVM", "FCM not initialized: ${e.message}")
        }

        // Pre-seed initial sample grievances into Firestore if collection is clean
        firestoreRepository.seedInitialUserGrievances(userId)

        // Observe real-time Firestore grievances and notify on status changes
        firestoreRepository.observeUserGrievances(userId).collect { firestoreList ->
          if (firestoreList.isNotEmpty()) {
            val domainList = firestoreList.map { it.toDomain() }

            // Automatic Status Change Detection: notify when grievance status changes (e.g. Pending -> Resolved)
            val previousMap = _allGrievances.value.associate { it.id to it.status }
            if (previousMap.isNotEmpty()) {
              for (item in domainList) {
                val oldStatus = previousMap[item.id]
                if (oldStatus != null && oldStatus != item.status) {
                  // Trigger system notification
                  if (context != null) {
                    NotificationHelper.showStatusChangeNotification(
                      context = context,
                      grievanceId = item.id,
                      title = item.title,
                      previousStatus = oldStatus,
                      newStatus = item.status,
                      language = _language.value
                    )
                  }
                  // Set in-app notification banner
                  _statusNotificationAlert.value = "Status updated: #${item.id} is now ${item.status.title(_language.value)}"
                }
              }
            }

            _allGrievances.value = domainList
            if (_activeTrackGrievance.value == null || !_allGrievances.value.any { it.id == _activeTrackGrievance.value?.id }) {
              _activeTrackGrievance.value = domainList.firstOrNull()
            }
          }
        }
      }
    }
  }

  fun dismissStatusNotificationAlert() {
    _statusNotificationAlert.value = null
  }

  // Allows citizens and Panchayat officers to change grievance status (e.g. Pending -> In Progress -> Resolved)
  // and triggers the automatic notification pipeline!
  fun simulateStatusUpdate(grievanceId: String, newStatus: GrievanceStatus, context: Context?) {
    viewModelScope.launch {
      val grievance = _allGrievances.value.firstOrNull { it.id == grievanceId }
      val oldStatus = grievance?.status ?: GrievanceStatus.PENDING

      if (firestoreRepository != null && Firebase.auth.currentUser != null) {
        val statusName = when (newStatus) {
          GrievanceStatus.RESOLVED -> "Resolved"
          GrievanceStatus.IN_PROGRESS -> "In Progress"
          GrievanceStatus.PENDING -> "Pending"
          GrievanceStatus.SUBMITTED -> "Submitted"
        }
        firestoreRepository.updateGrievanceStatus(grievanceId, statusName)
      }

      // Update in-memory state
      _allGrievances.update { list ->
        list.map { if (it.id == grievanceId) it.copy(status = newStatus) else it }
      }
      if (_activeTrackGrievance.value?.id == grievanceId) {
        _activeTrackGrievance.value = _activeTrackGrievance.value?.copy(status = newStatus)
      }

      // Dispatch system notification via NotificationHelper
      if (context != null && grievance != null) {
        NotificationHelper.showStatusChangeNotification(
          context = context,
          grievanceId = grievanceId,
          title = grievance.title,
          previousStatus = oldStatus,
          newStatus = newStatus,
          language = _language.value
        )
      }

      _statusNotificationAlert.value = "Status updated: #${grievanceId} is now ${newStatus.title(_language.value)} (Notification delivered)"
    }
  }

  fun setTab(tab: AppTab) {
    _activeTab.value = tab
  }

  fun toggleLanguage() {
    _language.value = if (_language.value == AppLanguage.ENGLISH) AppLanguage.KANNADA else AppLanguage.ENGLISH
  }

  fun setLanguage(lang: AppLanguage) {
    _language.value = lang
  }

  fun toggleDarkMode() {
    _isDarkMode.value = !_isDarkMode.value
  }

  fun setProfileDialogVisible(visible: Boolean) {
    _showProfileDialog.value = visible
  }

  fun setTrackSearchId(id: String) {
    _trackSearchId.value = id
  }

  fun searchAndTrack(id: String = _trackSearchId.value) {
    val cleanId = id.trim().uppercase()
    val found = _allGrievances.value.firstOrNull { 
      it.id.equals(cleanId, ignoreCase = true) || it.id.contains(cleanId, ignoreCase = true)
    }
    if (found != null) {
      _activeTrackGrievance.value = found
      _trackSearchId.value = found.id
    }
  }

  fun selectGrievanceForTracking(grievance: Grievance) {
    _activeTrackGrievance.value = grievance
    _trackSearchId.value = grievance.id
    _activeTab.value = AppTab.TRACK
  }

  fun selectGrievanceById(id: String) {
    val found = _allGrievances.value.firstOrNull { it.id.equals(id, ignoreCase = true) }
    if (found != null) {
      _activeTrackGrievance.value = found
      _trackSearchId.value = found.id
    } else {
      _trackSearchId.value = id
    }
    _activeTab.value = AppTab.TRACK
  }

  fun selectCategoryForNewComplaint(categoryName: String) {
    _submitForm.update { it.copy(category = categoryName) }
    _activeTab.value = AppTab.SUBMIT
  }

  fun setDashboardSearch(query: String) {
    _dashboardSearchQuery.value = query
  }

  fun setDashboardCategoryFilter(filter: String) {
    _dashboardCategoryFilter.value = filter
  }

  fun setDashboardStatusFilter(status: String) {
    _dashboardStatusFilter.value = status
  }

  fun updateSubmitTitle(title: String) {
    _submitForm.update { it.copy(title = title, validationError = null) }
  }

  fun updateSubmitCategory(category: String) {
    _submitForm.update { it.copy(category = category, validationError = null) }
  }

  fun updateSubmitDescription(description: String) {
    _submitForm.update { it.copy(description = description, validationError = null) }
  }

  fun updateSubmitDistrict(district: String) {
    _submitForm.update { it.copy(district = district) }
  }

  fun updateSubmitTaluk(taluk: String) {
    _submitForm.update { it.copy(taluk = taluk) }
  }

  fun updateSubmitGP(gp: String) {
    _submitForm.update { it.copy(gramPanchayat = gp) }
  }

  fun updateSubmitVillage(village: String) {
    _submitForm.update { it.copy(village = village) }
  }

  fun updateSubmitWard(ward: String) {
    _submitForm.update { it.copy(wardNumber = ward) }
  }

  fun updateSubmitLandmark(landmark: String) {
    _submitForm.update { it.copy(landmark = landmark) }
  }

  fun updateSubmitPublic(isPublic: Boolean) {
    _submitForm.update { it.copy(isPublic = isPublic) }
  }

  fun updateEvidencePhoto(uri: String?) {
    _submitForm.update { it.copy(evidencePhotoUri = uri) }
  }

  fun setPhotoUri(uri: Uri?) {
    _submitForm.update { it.copy(localPhotoUri = uri, evidencePhotoUri = uri?.toString()) }
  }

  fun clearPhoto() {
    _submitForm.update { it.copy(localPhotoUri = null, evidencePhotoUri = null) }
  }

  fun pinCurrentGps() {
    viewModelScope.launch {
      _submitForm.update { it.copy(isGpsPinned = false) }
      delay(700)
      _submitForm.update { 
        it.copy(
          isGpsPinned = true,
          gpsCoordinates = "13.2988° N, 77.5812° E (Pinned)",
          landmark = if (it.landmark.isBlank()) "Near Gram Panchayat Bhavan, Main Road" else it.landmark
        )
      }
    }
  }

  fun triggerAiAutoFill() {
    viewModelScope.launch {
      _submitForm.update { it.copy(isAiAnalyzing = true) }
      delay(1100)
      _submitForm.update {
        it.copy(
          isAiAnalyzing = false,
          aiSuggested = true,
          title = "Main water pipeline leakage near temple junction",
          category = "Water Supply & Sanitation",
          description = "Water is gushing out from the main supply valve since yesterday morning, leading to water wastage and low pressure in surrounding households."
        )
      }
    }
  }

  fun submitCurrentGrievance() {
    val form = _submitForm.value
    if (form.title.trim().length < 3) {
      _submitForm.update { it.copy(validationError = "Please enter a complaint title (at least 3 characters)") }
      return
    }
    if (form.category.isBlank()) {
      _submitForm.update { it.copy(validationError = "Please select a grievance category") }
      return
    }
    if (form.description.trim().length < 5) {
      _submitForm.update { it.copy(validationError = "Please provide a detailed description (at least 5 characters)") }
      return
    }

    _submitForm.update { it.copy(isSubmitting = true, isUploadingPhoto = form.localPhotoUri != null, validationError = null) }
    viewModelScope.launch {
      var photoUrl: String? = form.evidencePhotoUri
      val localUri = form.localPhotoUri

      // Attempt upload to Firebase Storage if a local photo was captured/picked
      if (localUri != null && firestoreRepository != null && Firebase.auth.currentUser != null) {
        val uploadResult = firestoreRepository.uploadEvidencePhoto(localUri, "photo_${System.currentTimeMillis()}")
        if (uploadResult.isSuccess) {
          photoUrl = uploadResult.getOrNull()
        } else {
          // If storage bucket is unprovisioned or network error occurs, fall back gracefully to local uri string
          photoUrl = localUri.toString()
        }
      }
      _submitForm.update { it.copy(isUploadingPhoto = false) }

      if (firestoreRepository != null && Firebase.auth.currentUser != null) {
        val result = firestoreRepository.createGrievance(
          title = form.title.trim(),
          category = form.category.trim(),
          description = form.description.trim(),
          district = form.district,
          taluk = form.taluk,
          gramPanchayat = form.gramPanchayat,
          village = form.village,
          wardNumber = form.wardNumber.ifBlank { "Ward No. 4" },
          locationName = form.landmark.ifBlank { "${form.village}, ${form.gramPanchayat}" },
          gpsCoordinates = form.gpsCoordinates,
          isPublic = form.isPublic,
          evidencePhotoUri = photoUrl
        )

        if (result.isSuccess) {
          val newId = result.getOrThrow()
          val created = Grievance(
            id = newId,
            title = form.title.trim(),
            category = form.category.trim(),
            description = form.description.trim(),
            status = GrievanceStatus.PENDING,
            submittedDate = "Today",
            district = form.district,
            taluk = form.taluk,
            gramPanchayat = form.gramPanchayat,
            village = form.village,
            wardNumber = form.wardNumber.ifBlank { "Ward No. 4" },
            locationName = form.landmark.ifBlank { "${form.village}, ${form.gramPanchayat}" },
            gpsCoordinates = form.gpsCoordinates,
            isPublic = form.isPublic,
            evidencePhotoUri = photoUrl
          )
          _submittedSuccessGrievance.value = created
          _activeTrackGrievance.value = created
          _trackSearchId.value = created.id
          _submitForm.value = SubmitFormData()
        } else {
          val err = result.exceptionOrNull()?.localizedMessage ?: "Failed to save grievance to Firestore. Please check connection and try again."
          _submitForm.update { it.copy(isSubmitting = false, validationError = err) }
        }
      } else {
        val created = localFallbackRepo.submitGrievance(
          title = form.title.trim(),
          category = form.category.trim(),
          description = form.description.trim(),
          district = form.district,
          taluk = form.taluk,
          gramPanchayat = form.gramPanchayat,
          village = form.village,
          wardNumber = form.wardNumber.ifBlank { "Ward No. 4" },
          locationName = form.landmark.ifBlank { "${form.village}, ${form.gramPanchayat}" },
          gpsCoordinates = form.gpsCoordinates,
          isPublic = form.isPublic,
          evidencePhotoUri = photoUrl
        )
        _allGrievances.value = localFallbackRepo.grievances.value
        _submittedSuccessGrievance.value = created
        _activeTrackGrievance.value = created
        _trackSearchId.value = created.id
        _submitForm.value = SubmitFormData()
      }
    }
  }

  fun dismissSuccessDialog() {
    _submittedSuccessGrievance.value = null
  }

  fun trackCreatedGrievance() {
    val created = _submittedSuccessGrievance.value
    _submittedSuccessGrievance.value = null
    if (created != null) {
      _activeTrackGrievance.value = created
      _trackSearchId.value = created.id
    }
    _activeTab.value = AppTab.TRACK
  }

  fun submitFeedback(grievanceId: String, rating: Int, comment: String) {
    viewModelScope.launch {
      if (firestoreRepository != null && Firebase.auth.currentUser != null) {
        firestoreRepository.updateRatingAndFeedback(grievanceId, rating.toLong(), comment)
      }
      localFallbackRepo.updateRatingAndFeedback(grievanceId, rating, comment)
      _allGrievances.update { list ->
        list.map { if (it.id == grievanceId) it.copy(rating = rating, feedbackComment = comment) else it }
      }
      if (_activeTrackGrievance.value?.id == grievanceId) {
        _activeTrackGrievance.value = _activeTrackGrievance.value?.copy(rating = rating, feedbackComment = comment)
      }
    }
  }

  fun reopenGrievance(grievanceId: String) {
    viewModelScope.launch {
      if (firestoreRepository != null && Firebase.auth.currentUser != null) {
        firestoreRepository.reopenGrievance(grievanceId)
      }
      localFallbackRepo.reopenGrievance(grievanceId)
      _allGrievances.update { list ->
        list.map { if (it.id == grievanceId) it.copy(status = GrievanceStatus.IN_PROGRESS) else it }
      }
      if (_activeTrackGrievance.value?.id == grievanceId) {
        _activeTrackGrievance.value = _activeTrackGrievance.value?.copy(status = GrievanceStatus.IN_PROGRESS)
      }
    }
  }
}

private fun FirestoreGrievance.toDomain(): Grievance {
  return Grievance(
    id = id,
    title = title,
    category = category,
    description = description,
    status = GrievanceStatus.fromString(status),
    submittedDate = submittedDate.ifBlank { "Recently" },
    district = district,
    taluk = taluk,
    gramPanchayat = gramPanchayat,
    village = village,
    wardNumber = wardNumber,
    locationName = locationName,
    gpsCoordinates = gpsCoordinates,
    isPublic = isPublic,
    rating = rating?.toInt(),
    feedbackComment = feedbackComment,
    assignedOfficer = assignedOfficer,
    department = department,
    expectedResolutionDate = expectedResolutionDate,
    evidencePhotoUri = evidencePhotoUri,
    timeline = listOf(
      TimelineStep(
        title = "Grievance Recorded in Cloud",
        timestamp = submittedDate.ifBlank { "Synced" },
        description = "Verified in Karnataka Gram Panchayat Cloud Firestore database."
      )
    )
  )
}
