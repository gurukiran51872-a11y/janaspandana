package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.auth.attemptAutoSignIn
import com.example.auth.signOutUser
import com.example.data.model.AppLanguage
import com.example.data.model.GrievanceStatus
import com.example.data.repository.FirestoreGrievanceRepository
import com.example.notification.NotificationHelper
import com.example.ui.components.CitizenProfileDialog
import com.example.ui.components.JanaspandanaBottomNavBar
import com.example.ui.components.JanaspandanaTopHeader
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PublicScreen
import com.example.ui.screens.SignInScreen
import com.example.ui.screens.SubmitScreen
import com.example.ui.screens.TrackScreen
import com.example.ui.theme.JanaspandanaTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.JanaspandanaViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
  private var targetGrievanceId by mutableStateOf<String?>(null)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    handleIntent(intent)
    enableEdgeToEdge()
    setContent {
      AppContent(targetGrievanceId = targetGrievanceId)
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    handleIntent(intent)
  }

  private fun handleIntent(intent: Intent?) {
    val id = intent?.getStringExtra(NotificationHelper.EXTRA_GRIEVANCE_ID)
    if (!id.isNullOrBlank()) {
      targetGrievanceId = id
    }
  }
}

@Composable
fun AppContent(targetGrievanceId: String? = null) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val credentialManager = remember { CredentialManager.create(context) }
  var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

  // Request POST_NOTIFICATIONS permission on Android 13+ (API 33+)
  val notificationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { _ -> }

  LaunchedEffect(Unit) {
    NotificationHelper.createNotificationChannel(context)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    }
  }

  DisposableEffect(Unit) {
    val listener = FirebaseAuth.AuthStateListener { auth ->
      currentUser = auth.currentUser
    }
    Firebase.auth.addAuthStateListener(listener)
    onDispose {
      Firebase.auth.removeAuthStateListener(listener)
    }
  }

  // Attempt auto sign in on startup if not already authenticated
  LaunchedEffect(Unit) {
    if (currentUser == null) {
      attemptAutoSignIn(
        context = context,
        credentialManager = credentialManager,
        onAuthSuccess = { currentUser = Firebase.auth.currentUser },
        onUnauthenticated = {},
        scope = coroutineScope
      )
    }
  }

  var isDarkMode by remember { mutableStateOf(false) }
  var language by remember { mutableStateOf(AppLanguage.ENGLISH) }

  JanaspandanaTheme(darkTheme = isDarkMode) {
    if (currentUser == null) {
      // Auth Gate: Render Login UI with visible "Sign in with Google" button
      SignInScreen(
        language = language,
        onLanguageToggle = {
          language = if (language == AppLanguage.ENGLISH) AppLanguage.KANNADA else AppLanguage.ENGLISH
        },
        onAuthSuccess = {
          currentUser = Firebase.auth.currentUser
        }
      )
    } else {
      // Authenticated session: create ViewModel with Firestore repository and render feature screens
      val user = currentUser!!
      val viewModel: JanaspandanaViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val firestoreRepo = FirestoreGrievanceRepository(context)
            @Suppress("UNCHECKED_CAST")
            return JanaspandanaViewModel(firestoreRepo) as T
          }
        }
      )

      LaunchedEffect(user.uid) {
        viewModel.onUserAuthenticated(user.uid, context)
      }

      LaunchedEffect(targetGrievanceId) {
        targetGrievanceId?.let { id ->
          viewModel.selectGrievanceById(id)
        }
      }

      JanaspandanaApp(
        viewModel = viewModel,
        currentUser = user,
        onSignOut = {
          signOutUser(
            context = context,
            credentialManager = credentialManager,
            onSignOutComplete = { currentUser = null },
            scope = coroutineScope
          )
        }
      )
    }
  }
}

@Composable
fun JanaspandanaApp(
  viewModel: JanaspandanaViewModel,
  currentUser: FirebaseUser,
  onSignOut: () -> Unit
) {
  val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
  val language by viewModel.language.collectAsStateWithLifecycle()
  val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
  val showProfile by viewModel.showProfileDialog.collectAsStateWithLifecycle()

  val allGrievances by viewModel.allGrievances.collectAsStateWithLifecycle()
  val filteredDashboardGrievances by viewModel.filteredDashboardGrievances.collectAsStateWithLifecycle()
  val activeTrackGrievance by viewModel.activeTrackGrievance.collectAsStateWithLifecycle()
  val statusNotificationAlert by viewModel.statusNotificationAlert.collectAsStateWithLifecycle()
  val trackSearchId by viewModel.trackSearchId.collectAsStateWithLifecycle()
  val dashboardSearchQuery by viewModel.dashboardSearchQuery.collectAsStateWithLifecycle()
  val dashboardCategoryFilter by viewModel.dashboardCategoryFilter.collectAsStateWithLifecycle()
  val dashboardStatusFilter by viewModel.dashboardStatusFilter.collectAsStateWithLifecycle()
  val submitFormData by viewModel.submitForm.collectAsStateWithLifecycle()
  val submittedSuccessGrievance by viewModel.submittedSuccessGrievance.collectAsStateWithLifecycle()
  val context = LocalContext.current

  // Handle back button to return to Home when on another tab
  BackHandler(enabled = activeTab != AppTab.HOME) {
    viewModel.setTab(AppTab.HOME)
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .testTag("main_scaffold"),
    topBar = {
      Box(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)) {
        JanaspandanaTopHeader(
          language = language,
          isDarkMode = isDarkMode,
          onLanguageChange = { viewModel.setLanguage(it) },
          onToggleDarkMode = { viewModel.toggleDarkMode() },
          onProfileClick = { viewModel.setProfileDialogVisible(true) }
        )
      }
    },
    bottomBar = {
      JanaspandanaBottomNavBar(
        activeTab = activeTab,
        onTabSelected = { viewModel.setTab(it) },
        language = language
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      if (statusNotificationAlert != null) {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("status_notification_banner"),
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFEFF6FF),
          shadowElevation = 2.dp
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.NotificationsActive,
              contentDescription = "Notification Alert",
              tint = Color(0xFF1D4ED8),
              modifier = Modifier.size(20.dp)
            )
            Text(
              text = statusNotificationAlert ?: "",
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
              color = Color(0xFF1E3A8A),
              modifier = Modifier.weight(1f)
            )
            IconButton(
              onClick = { viewModel.dismissStatusNotificationAlert() },
              modifier = Modifier
                .size(28.dp)
                .testTag("btn_dismiss_notification_banner")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Dismiss",
                tint = Color(0xFF1E3A8A),
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }

      Box(modifier = Modifier.weight(1f)) {
        Crossfade(targetState = activeTab, label = "tab_transition") { tab ->
          when (tab) {
            AppTab.HOME -> {
              HomeScreen(
                language = language,
                notices = viewModel.notices,
                onLanguageSelect = { viewModel.setLanguage(it) },
                onSubmitComplaintClick = { viewModel.setTab(AppTab.SUBMIT) },
                onTrackComplaintClick = { viewModel.setTab(AppTab.TRACK) },
                onCategorySelect = { viewModel.selectCategoryForNewComplaint(it) },
                recentGrievances = allGrievances,
                onGrievanceClick = { viewModel.selectGrievanceForTracking(it) }
              )
            }

            AppTab.SUBMIT -> {
              SubmitScreen(
                formData = submitFormData,
                submittedSuccessGrievance = submittedSuccessGrievance,
                language = language,
                onTitleChange = { viewModel.updateSubmitTitle(it) },
                onCategoryChange = { viewModel.updateSubmitCategory(it) },
                onDescriptionChange = { viewModel.updateSubmitDescription(it) },
                onDistrictChange = { viewModel.updateSubmitDistrict(it) },
                onTalukChange = { viewModel.updateSubmitTaluk(it) },
                onGPChange = { viewModel.updateSubmitGP(it) },
                onVillageChange = { viewModel.updateSubmitVillage(it) },
                onWardChange = { viewModel.updateSubmitWard(it) },
                onLandmarkChange = { viewModel.updateSubmitLandmark(it) },
                onPublicChange = { viewModel.updateSubmitPublic(it) },
                onEvidencePhotoChange = { viewModel.updateEvidencePhoto(it) },
                onPhotoSelected = { viewModel.setPhotoUri(it) },
                onPhotoCleared = { viewModel.clearPhoto() },
                onPinGpsClick = { viewModel.pinCurrentGps() },
                onAiAutoFillClick = { viewModel.triggerAiAutoFill() },
                onSubmitClick = { viewModel.submitCurrentGrievance() },
                onDismissSuccess = { viewModel.dismissSuccessDialog() },
                onTrackCreatedGrievance = { viewModel.trackCreatedGrievance() }
              )
            }

            AppTab.TRACK -> {
              TrackScreen(
                activeGrievance = activeTrackGrievance,
                allGrievances = allGrievances,
                searchQuery = trackSearchId,
                language = language,
                onSearchChange = { viewModel.setTrackSearchId(it) },
                onSearchSubmit = { viewModel.searchAndTrack() },
                onSelectGrievance = { viewModel.selectGrievanceForTracking(it) },
                onProvideFeedbackClick = { viewModel.submitFeedback(it.id, it.rating ?: 5, it.feedbackComment ?: "") },
                onReopenClick = { viewModel.reopenGrievance(it) },
                onStatusChange = { newStatus ->
                  activeTrackGrievance?.let { g ->
                    viewModel.simulateStatusUpdate(g.id, newStatus, context)
                  }
                }
              )
            }

          AppTab.DASHBOARD -> {
            DashboardScreen(
              grievances = filteredDashboardGrievances,
              searchQuery = dashboardSearchQuery,
              categoryFilter = dashboardCategoryFilter,
              language = language,
              onSearchChange = { viewModel.setDashboardSearch(it) },
              onCategoryFilterChange = { viewModel.setDashboardCategoryFilter(it) },
              statusFilter = dashboardStatusFilter,
              onStatusFilterChange = { viewModel.setDashboardStatusFilter(it) },
              onNewGrievanceClick = { viewModel.setTab(AppTab.SUBMIT) },
              onTrackGrievanceClick = { viewModel.selectGrievanceForTracking(it) },
              onRateGrievance = { id, rating, comment -> viewModel.submitFeedback(id, rating, comment) }
            )
          }

          AppTab.PUBLIC -> {
            PublicScreen(
              villageStats = viewModel.villageStats,
              monthlyTrends = viewModel.monthlyTrends,
              language = language
            )
          }
        }
      }
    }
  }
  }

  if (showProfile) {
    CitizenProfileDialog(
      userName = currentUser.displayName ?: "Rajesh K.",
      userEmail = currentUser.email ?: "citizen@janaspandana.karnataka.gov.in",
      userGrievances = allGrievances,
      onTrackGrievance = { grievance ->
        viewModel.selectGrievanceForTracking(grievance)
        viewModel.setProfileDialogVisible(false)
      },
      onSignOut = onSignOut,
      onDismiss = { viewModel.setProfileDialogVisible(false) },
      language = language
    )
  }
}
