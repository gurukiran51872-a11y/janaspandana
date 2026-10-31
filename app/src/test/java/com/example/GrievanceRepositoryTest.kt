package com.example

import com.example.data.model.GrievanceStatus
import com.example.data.repository.GrievanceRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GrievanceRepositoryTest {

  private lateinit var repository: GrievanceRepository

  @Before
  fun setUp() {
    repository = GrievanceRepository()
  }

  @Test
  fun testInitialGrievancesLoaded() {
    val initial = repository.grievances.value
    assertTrue(initial.isNotEmpty())
    val waterGrievance = repository.getGrievanceById("GP202610160001")
    assertNotNull(waterGrievance)
    assertEquals(GrievanceStatus.IN_PROGRESS, waterGrievance?.status)
    assertEquals("Water Supply", waterGrievance?.category)
  }

  @Test
  fun testSubmitGrievance() {
    val initialCount = repository.grievances.value.size
    val created = repository.submitGrievance(
      title = "Pothole on Main Road",
      category = "Roads & Streetlights",
      description = "Large pothole causing vehicle damage",
      district = "Bengaluru Rural",
      taluk = "Malur",
      gramPanchayat = "Malur GP",
      village = "Malur",
      wardNumber = "Ward No. 3",
      locationName = "Main Road, Malur",
      gpsCoordinates = "13.2988° N, 77.5812° E",
      isPublic = true
    )

    assertNotNull(created.id)
    assertEquals("Pothole on Main Road", created.title)
    assertEquals(initialCount + 1, repository.grievances.value.size)
  }

  @Test
  fun testReopenGrievance() {
    val reopened = repository.reopenGrievance("GP202608140195")
    assertTrue(reopened)
    val grievance = repository.getGrievanceById("GP202608140195")
    assertEquals(GrievanceStatus.IN_PROGRESS, grievance?.status)
  }

  @Test
  fun testUpdateRating() {
    repository.updateRatingAndFeedback("GP202610160001", 5, "Fast resolution")
    val updated = repository.getGrievanceById("GP202610160001")
    assertEquals(5, updated?.rating)
    assertEquals("Fast resolution", updated?.feedbackComment)
  }

  @Test
  fun testSubmitGrievanceWithEvidencePhoto() {
    val created = repository.submitGrievance(
      title = "Overflowing drain near market",
      category = "Drainage & Sewage",
      description = "Severe overflow flooding pavement",
      district = "Bengaluru Rural",
      taluk = "Malur",
      gramPanchayat = "Malur GP",
      village = "Malur",
      wardNumber = "Ward No. 2",
      locationName = "Market Cross",
      gpsCoordinates = "13.2988° N, 77.5812° E",
      isPublic = true,
      evidencePhotoUri = "https://firebasestorage.googleapis.com/v0/b/app/o/grievances%2Fphoto.jpg"
    )

    assertNotNull(created.evidencePhotoUri)
    assertEquals("https://firebasestorage.googleapis.com/v0/b/app/o/grievances%2Fphoto.jpg", created.evidencePhotoUri)
  }

  @Test
  fun testGrievanceStatusFromString() {
    assertEquals(GrievanceStatus.PENDING, GrievanceStatus.fromString("Pending"))
    assertEquals(GrievanceStatus.PENDING, GrievanceStatus.fromString("pending"))
    assertEquals(GrievanceStatus.IN_PROGRESS, GrievanceStatus.fromString("In Progress"))
    assertEquals(GrievanceStatus.IN_PROGRESS, GrievanceStatus.fromString("in_progress"))
    assertEquals(GrievanceStatus.RESOLVED, GrievanceStatus.fromString("Resolved"))
    assertEquals(GrievanceStatus.RESOLVED, GrievanceStatus.fromString("resolved"))
    assertEquals(GrievanceStatus.SUBMITTED, GrievanceStatus.fromString("Submitted"))
    assertEquals(GrievanceStatus.PENDING, GrievanceStatus.fromString(null))
    assertEquals(GrievanceStatus.PENDING, GrievanceStatus.fromString("unknown_status"))
  }

  @Test
  fun testGrievanceStatusBilingualTitles() {
    assertEquals("Pending", GrievanceStatus.PENDING.title(com.example.data.model.AppLanguage.ENGLISH))
    assertEquals("ಬಾಕಿ ಉಳಿದಿದೆ", GrievanceStatus.PENDING.title(com.example.data.model.AppLanguage.KANNADA))
    assertEquals("In Progress", GrievanceStatus.IN_PROGRESS.title(com.example.data.model.AppLanguage.ENGLISH))
    assertEquals("ಪ್ರಗತಿಯಲ್ಲಿದೆ", GrievanceStatus.IN_PROGRESS.title(com.example.data.model.AppLanguage.KANNADA))
    assertEquals("Resolved", GrievanceStatus.RESOLVED.title(com.example.data.model.AppLanguage.ENGLISH))
    assertEquals("ಪರಿಹರಿಸಲಾಗಿದೆ", GrievanceStatus.RESOLVED.title(com.example.data.model.AppLanguage.KANNADA))
  }

  @Test
  fun testStatusBadgeColorsConfig() {
    val pendingColors = com.example.ui.components.getStatusBadgeColors(GrievanceStatus.PENDING)
    assertNotNull(pendingColors.containerColor)
    assertNotNull(pendingColors.contentColor)
    assertNotNull(pendingColors.icon)

    val inProgressColors = com.example.ui.components.getStatusBadgeColors(GrievanceStatus.IN_PROGRESS)
    assertNotNull(inProgressColors.containerColor)
    assertNotNull(inProgressColors.contentColor)
    assertNotNull(inProgressColors.icon)

    val resolvedColors = com.example.ui.components.getStatusBadgeColors(GrievanceStatus.RESOLVED)
    assertNotNull(resolvedColors.containerColor)
    assertNotNull(resolvedColors.contentColor)
    assertNotNull(resolvedColors.icon)
  }

  @Test
  fun testSubmitGrievanceDefaultStatusIsPending() {
    val created = repository.submitGrievance(
      title = "New complaint",
      category = "Streetlights",
      description = "Streetlight flickering",
      district = "Bengaluru Rural",
      taluk = "Malur",
      gramPanchayat = "Malur GP",
      village = "Malur",
      wardNumber = "Ward No. 1",
      locationName = "Cross Road",
      gpsCoordinates = "13.2988° N, 77.5812° E",
      isPublic = true
    )
    assertEquals(GrievanceStatus.PENDING, created.status)
  }

  @Test
  fun testFilterGrievancesByTitle() {
    val grievances = repository.grievances.value
    val titleQuery = "water pressure"
    val results = grievances.filter { it.title.contains(titleQuery, ignoreCase = true) }
    assertTrue(results.isNotEmpty())
    assertTrue(results.all { it.title.contains(titleQuery, ignoreCase = true) })
  }

  @Test
  fun testFilterGrievancesByCategory() {
    val grievances = repository.grievances.value
    val categoryQuery = "Streetlights"
    val results = grievances.filter { it.category.contains(categoryQuery, ignoreCase = true) }
    assertTrue(results.isNotEmpty())
    assertTrue(results.all { it.category.contains(categoryQuery, ignoreCase = true) })
  }

  @Test
  fun testStatusChartDataCalculations() {
    val chartData = com.example.ui.components.StatusChartData(
      pendingCount = 20,
      inProgressCount = 10,
      resolvedCount = 70
    )
    assertEquals(100, chartData.total)
    assertEquals(70, chartData.resolutionRate)
  }

  @Test
  fun testNotificationHelperConstants() {
    assertEquals("grievance_status_channel", com.example.notification.NotificationHelper.CHANNEL_ID)
    assertEquals("extra_grievance_id", com.example.notification.NotificationHelper.EXTRA_GRIEVANCE_ID)
  }

  @Test
  fun testStatusTransitionsAndStateUpdate() {
    val initial = repository.grievances.value.first()
    val updatedToInProgress = initial.copy(status = GrievanceStatus.IN_PROGRESS)
    assertEquals(GrievanceStatus.IN_PROGRESS, updatedToInProgress.status)
    assertEquals(initial.id, updatedToInProgress.id)

    val updatedToResolved = updatedToInProgress.copy(status = GrievanceStatus.RESOLVED)
    assertEquals(GrievanceStatus.RESOLVED, updatedToResolved.status)
    assertEquals(initial.title, updatedToResolved.title)
  }

  @Test
  fun testFcmPayloadStatusParsing() {
    val samplePayload = mapOf(
      "grievanceId" to "GP202610160001",
      "title" to "Low water pressure in Ward 4",
      "previousStatus" to "Pending",
      "newStatus" to "In Progress"
    )

    val prevStatus = GrievanceStatus.fromString(samplePayload["previousStatus"])
    val newStatus = GrievanceStatus.fromString(samplePayload["newStatus"])

    assertEquals(GrievanceStatus.PENDING, prevStatus)
    assertEquals(GrievanceStatus.IN_PROGRESS, newStatus)
    assertTrue(prevStatus != newStatus)
  }

  @Test
  fun testProfileGrievanceStatusFiltering() {
    val grievances = repository.grievances.value
    assertTrue("Repository should have sample grievances", grievances.isNotEmpty())

    val pendingList = grievances.filter { it.status == GrievanceStatus.PENDING || it.status == GrievanceStatus.SUBMITTED }
    val inProgressList = grievances.filter { it.status == GrievanceStatus.IN_PROGRESS }
    val resolvedList = grievances.filter { it.status == GrievanceStatus.RESOLVED }

    // Assert that status filtering correctly partitions or matches grievances
    assertTrue(pendingList.all { it.status == GrievanceStatus.PENDING || it.status == GrievanceStatus.SUBMITTED })
    assertTrue(inProgressList.all { it.status == GrievanceStatus.IN_PROGRESS })
    assertTrue(resolvedList.all { it.status == GrievanceStatus.RESOLVED })

    val totalCategorized = pendingList.size + inProgressList.size + resolvedList.size
    assertEquals(grievances.size, totalCategorized)
  }

  @Test
  fun testGrievanceStatusFilterLabels() {
    assertEquals("All", com.example.ui.components.GrievanceStatusFilter.ALL.labelEn)
    assertEquals("ಎಲ್ಲವೂ", com.example.ui.components.GrievanceStatusFilter.ALL.labelKn)
    assertEquals("Pending", com.example.ui.components.GrievanceStatusFilter.PENDING.labelEn)
    assertEquals("ಬಾಕಿ", com.example.ui.components.GrievanceStatusFilter.PENDING.labelKn)
    assertEquals("In Progress", com.example.ui.components.GrievanceStatusFilter.IN_PROGRESS.labelEn)
    assertEquals("ಪ್ರಗತಿಯಲ್ಲಿದೆ", com.example.ui.components.GrievanceStatusFilter.IN_PROGRESS.labelKn)
    assertEquals("Resolved", com.example.ui.components.GrievanceStatusFilter.RESOLVED.labelEn)
    assertEquals("ಪರಿಹರಿಸಲಾಗಿದೆ", com.example.ui.components.GrievanceStatusFilter.RESOLVED.labelKn)
  }

  @Test
  fun testCameraCapturedPhotoUriAttachedAndPreservedInGrievance() {
    val cameraFileUri = "content://com.example.fileprovider/camera_photos/camera_photo_1730000000.jpg"
    val created = repository.submitGrievance(
      title = "Broken street light pole after storm",
      category = "Roads & Streetlights",
      description = "Electric pole tilted towards main road, needs urgent repair",
      district = "Bengaluru Rural",
      taluk = "Malur",
      gramPanchayat = "Malur GP",
      village = "Sadenahalli",
      wardNumber = "Ward No. 1",
      locationName = "Near Temple Cross",
      gpsCoordinates = "13.2988° N, 77.5812° E",
      isPublic = true,
      evidencePhotoUri = cameraFileUri
    )

    assertNotNull(created.id)
    assertEquals(cameraFileUri, created.evidencePhotoUri)
    val fetched = repository.getGrievanceById(created.id)
    assertNotNull(fetched)
    assertEquals(cameraFileUri, fetched?.evidencePhotoUri)
  }

  @Test
  fun testGrievanceWithoutCameraPhotoHasNullPhotoUri() {
    val created = repository.submitGrievance(
      title = "Delay in old age pension disbursement",
      category = "Pension & Welfare Schemes",
      description = "Pension for September and October has not been credited to bank account",
      district = "Bengaluru Rural",
      taluk = "Malur",
      gramPanchayat = "Malur GP",
      village = "Bommenahalli",
      wardNumber = "Ward No. 4",
      locationName = "Main Village",
      gpsCoordinates = "13.2988° N, 77.5812° E",
      isPublic = false,
      evidencePhotoUri = null
    )

    assertEquals(null, created.evidencePhotoUri)
  }

  @Test
  fun testInteractiveChartTypeEnumValues() {
    val pieType = com.example.ui.components.ChartType.PIE
    val barType = com.example.ui.components.ChartType.BAR
    assertEquals("PIE", pieType.name)
    assertEquals("BAR", barType.name)
    assertEquals(2, com.example.ui.components.ChartType.values().size)
  }

  @Test
  fun testInteractivePieChartBreakdownPendingVsResolved() {
    val data = com.example.ui.components.StatusChartData(
      pendingCount = 15,
      inProgressCount = 5,
      resolvedCount = 30
    )
    assertEquals(50, data.total)
    assertEquals(60, data.resolutionRate) // 30 / 50 = 60%

    // Proportions
    val pendingFraction = data.pendingCount.toFloat() / data.total
    val resolvedFraction = data.resolvedCount.toFloat() / data.total
    assertEquals(0.30f, pendingFraction, 0.001f)
    assertEquals(0.60f, resolvedFraction, 0.001f)
  }

  @Test
  fun testStatusChartDataZeroTotalHandledGracefully() {
    val emptyData = com.example.ui.components.StatusChartData(0, 0, 0)
    assertEquals(0, emptyData.total)
    assertEquals(0, emptyData.resolutionRate)
  }
}

