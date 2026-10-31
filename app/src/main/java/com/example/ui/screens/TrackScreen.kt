package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.EditRoad
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AppLanguage
import com.example.data.model.Grievance
import com.example.data.model.GrievanceStatus
import com.example.data.model.TimelineStep
import com.example.ui.components.GrievanceStatusBadge
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryAmber
import com.example.ui.theme.SecondaryContainerOrange

@Composable
fun TrackScreen(
  activeGrievance: Grievance?,
  allGrievances: List<Grievance>,
  searchQuery: String,
  language: AppLanguage,
  onSearchChange: (String) -> Unit,
  onSearchSubmit: () -> Unit,
  onSelectGrievance: (Grievance) -> Unit,
  onProvideFeedbackClick: (Grievance) -> Unit,
  onReopenClick: (String) -> Unit,
  onStatusChange: (GrievanceStatus) -> Unit = {},
  modifier: Modifier = Modifier
) {
  var showFeedbackDialog by remember { mutableStateOf(false) }
  var feedbackRating by remember { mutableIntStateOf(5) }
  var feedbackComments by remember { mutableStateOf("") }
  var showReopenedToast by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("track_screen_scroll"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Header with Title & Live Status Pill
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (language == AppLanguage.ENGLISH) "Track Grievance" else "ದೂರಿನ ಸ್ಥಿತಿ ತಿಳಿಯಿರಿ",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
          )
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
          ) {
            Text(
              text = if (language == AppLanguage.ENGLISH) "Live Status" else "ನೈಜ ಸ್ಥಿತಿ",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        }

        // Search Input & Track Button
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
              .weight(1f)
              .testTag("track_id_search_input"),
            placeholder = { Text("Enter Complaint ID (e.g. GP202610160001)") },
            leadingIcon = {
              Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            singleLine = true
          )

          Button(
            onClick = onSearchSubmit,
            modifier = Modifier
              .height(52.dp)
              .testTag("btn_track_search"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
          ) {
            Text(
              text = if (language == AppLanguage.ENGLISH) "Track" else "ಪರಿಶೀಲಿಸಿ",
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }
    }

    // 2. Recent Submissions Quick Access Pills
    item {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = if (language == AppLanguage.ENGLISH) "Your Recent Submissions" else "ನಿಮ್ಮ ಇತ್ತೀಚಿನ ದೂರುಗಳು",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(allGrievances) { g ->
            val isSelected = activeGrievance?.id == g.id
            val icon = when {
              g.category.contains("Water", ignoreCase = true) -> Icons.Default.WaterDrop
              g.category.contains("Street", ignoreCase = true) -> Icons.Default.Lightbulb
              else -> Icons.Default.EditRoad
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (isSelected) PrimaryNavy else MaterialTheme.colorScheme.surfaceContainerHigh,
              modifier = Modifier
                .clickable { onSelectGrievance(g) }
                .testTag("quick_track_pill_${g.id}")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = icon,
                  contentDescription = null,
                  tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = "${g.id} (${g.category.take(7)})",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  ),
                  color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                )
                GrievanceStatusBadge(
                  status = g.status,
                  language = language,
                  isCompact = true
                )
              }
            }
          }
        }
      }
    }

    // 3. Complaint Summary Card
    if (activeGrievance != null) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("track_summary_card"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // Header: ID and Status Badge
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Top
            ) {
              Column {
                Text(
                  text = if (language == AppLanguage.ENGLISH) "Complaint ID" else "ದೂರು ಐಡಿ",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = activeGrievance.id,
                  style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                  ),
                  color = MaterialTheme.colorScheme.primary
                )
              }

              // Status badge
              GrievanceStatusBadge(
                status = activeGrievance.status,
                language = language
              )
            }

            // Category & Submitted Date Strip
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text(
                    text = if (language == AppLanguage.ENGLISH) "Category" else "ವರ್ಗ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.WaterDrop,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(16.dp)
                    )
                    Text(
                      text = activeGrievance.category,
                      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                  }
                }

                Column {
                  Text(
                    text = if (language == AppLanguage.ENGLISH) "Submitted On" else "ಸಲ್ಲಿಸಿದ ದಿನಾಂಕ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Text(
                    text = activeGrievance.submittedDate,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }

            // Description
            Column {
              Text(
                text = if (language == AppLanguage.ENGLISH) "Description" else "ವಿವರಣೆ",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = activeGrievance.description,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                  .fillMaxWidth()
                  .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
                  .padding(10.dp)
              )
            }

            // Officer & ETA Grid
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              // Officer
              Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(34.dp)
                      .clip(CircleShape)
                      .background(PrimaryBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.Badge, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                  }
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(
                      text = "Assigned Officer",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                      text = activeGrievance.assignedOfficer,
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurface,
                      maxLines = 1
                    )
                    Text(
                      text = activeGrievance.department,
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.primary,
                      maxLines = 1
                    )
                  }
                }
              }

              // Expected Resolution
              Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(34.dp)
                      .clip(CircleShape)
                      .background(Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.Event, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(18.dp))
                  }
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(
                      text = "Expected Resol...",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                      text = activeGrievance.expectedResolutionDate,
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurface,
                      maxLines = 1
                    )
                    Text(
                      text = "Within 48 hrs",
                      style = MaterialTheme.typography.labelSmall,
                      color = Color(0xFF15803D),
                      maxLines = 1
                    )
                  }
                }
              }
            }

            // Location Preview Map Card
            Column {
              Text(
                text = if (language == AppLanguage.ENGLISH) "Location" else "ಸ್ಥಳ",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(4.dp))
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(120.dp),
                shape = RoundedCornerShape(10.dp)
              ) {
                Box(modifier = Modifier.fillMaxSize()) {
                  AsyncImage(
                    model = activeGrievance.mapImageUrl,
                    contentDescription = "Map Location",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                  )
                  Box(
                    modifier = Modifier
                      .align(Alignment.BottomCenter)
                      .fillMaxWidth()
                      .background(Color.Black.copy(alpha = 0.65f))
                      .padding(horizontal = 10.dp, vertical = 6.dp)
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      Icon(Icons.Default.LocationOn, contentDescription = null, tint = SecondaryContainerOrange, modifier = Modifier.size(16.dp))
                      Text(
                        text = activeGrievance.locationName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                      )
                    }
                  }
                }
              }
            }

            // Attached Evidence Photo Preview
            if (!activeGrievance.evidencePhotoUri.isNullOrBlank()) {
              Column {
                Text(
                  text = if (language == AppLanguage.ENGLISH) "Attached Evidence Photo" else "ಲಗತ್ತಿಸಲಾದ ಸಾಕ್ಷ್ಯ ಚಿತ್ರ",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                      model = activeGrievance.evidencePhotoUri,
                      contentDescription = "Evidence Photo",
                      modifier = Modifier
                        .fillMaxSize()
                        .testTag("track_evidence_photo"),
                      contentScale = ContentScale.Crop
                    )
                  }
                }
              }
            }
          }
        }
      }

      // 4. Progress Timeline Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("progress_timeline_card"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Text(
              text = if (language == AppLanguage.ENGLISH) "Progress Timeline" else "ಪ್ರಗತಿ ವೇಳಾಪಟ್ಟಿ",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )

            // Timeline Items
            val timelineSteps = if (activeGrievance.timeline.isNotEmpty()) {
              activeGrievance.timeline
            } else {
              listOf(
                TimelineStep(
                  title = "Complaint Registered",
                  timestamp = activeGrievance.submittedDate,
                  description = "Successfully registered via Janaspandana Mobile App."
                )
              )
            }

            timelineSteps.forEachIndexed { index, step ->
              TimelineRow(
                step = step,
                isFirst = index == 0,
                isLast = index == timelineSteps.size - 1
              )
            }
          }
        }
      }

      // 4b. Status Action & Notification Test (Officer/Panchayat Action)
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("status_action_card"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (language == AppLanguage.ENGLISH) "Officer Action / Status Update" else "ಅಧಿಕಾರಿಗಳ ಕ್ರಮ / ಸ್ಥಿತಿ ಬದಲಾವಣೆ",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
              ) {
                Text(
                  text = if (language == AppLanguage.ENGLISH) "FCM Alert" else "ಅಧಿಸೂಚನೆ",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
              }
            }

            Text(
              text = if (language == AppLanguage.ENGLISH)
                "Simulate Panchayat Development Officer (PDO) workflow. Changing status immediately triggers automatic Firebase Cloud Messaging & system notifications."
              else
                "ಸ್ಥಿತಿ ಬದಲಾವಣೆಯು ಸ್ವಯಂಚಾಲಿತ ಅಧಿಸೂಚನೆಯನ್ನು ಕಳುಹಿಸುತ್ತದೆ.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              val isPending = activeGrievance.status == GrievanceStatus.PENDING
              val isInProgress = activeGrievance.status == GrievanceStatus.IN_PROGRESS
              val isResolved = activeGrievance.status == GrievanceStatus.RESOLVED

              Button(
                onClick = { onStatusChange(GrievanceStatus.PENDING) },
                modifier = Modifier
                  .weight(1f)
                  .testTag("btn_status_pending"),
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (isPending) Color(0xFFB45309) else MaterialTheme.colorScheme.surfaceContainerHigh,
                  contentColor = if (isPending) Color.White else MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = !isPending
              ) {
                Text(
                  text = if (language == AppLanguage.ENGLISH) "Pending" else "ಬಾಕಿ",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
              }

              Button(
                onClick = { onStatusChange(GrievanceStatus.IN_PROGRESS) },
                modifier = Modifier
                  .weight(1f)
                  .testTag("btn_status_in_progress"),
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (isInProgress) Color(0xFF0284C7) else MaterialTheme.colorScheme.surfaceContainerHigh,
                  contentColor = if (isInProgress) Color.White else MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = !isInProgress
              ) {
                Text(
                  text = if (language == AppLanguage.ENGLISH) "In Progress" else "ಪ್ರಗತಿಯಲ್ಲಿದೆ",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
              }

              Button(
                onClick = { onStatusChange(GrievanceStatus.RESOLVED) },
                modifier = Modifier
                  .weight(1f)
                  .testTag("btn_status_resolved"),
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (isResolved) Color(0xFF16A34A) else MaterialTheme.colorScheme.surfaceContainerHigh,
                  contentColor = if (isResolved) Color.White else MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = !isResolved
              ) {
                Text(
                  text = if (language == AppLanguage.ENGLISH) "Resolved" else "ಪರಿಹರಿಸಲಾಗಿದೆ",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
              }
            }
          }
        }
      }

      // 5. Action Footer: Provide Feedback & Reopen
      item {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Button(
            onClick = { showFeedbackDialog = true },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("btn_provide_feedback"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
          ) {
            Icon(Icons.Default.RateReview, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (language == AppLanguage.ENGLISH) "Provide Service Feedback" else "ಸೇವಾ ಪ್ರತಿಕ್ರಿಯೆ ನೀಡಿ",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
              color = Color.White
            )
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = if (language == AppLanguage.ENGLISH) "Unsatisfied with the status?" else "ಸ್ಥಿತಿಯಿಂದ ಅತೃಪ್ತರಾಗಿದ್ದೀರಾ?",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = if (language == AppLanguage.ENGLISH) "Reopen Complaint" else "ದೂರನ್ನು ಮರುತೆರೆಯಿರಿ",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier
                .clickable {
                  onReopenClick(activeGrievance.id)
                  showReopenedToast = true
                }
                .testTag("btn_reopen_complaint")
            )
          }

          if (showReopenedToast) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFDCFCE7),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "✓ Complaint reopened for re-inspection by higher officers.",
                color = Color(0xFF15803D),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(10.dp)
              )
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }

  // Provide Feedback Dialog
  if (showFeedbackDialog && activeGrievance != null) {
    AlertDialog(
      onDismissRequest = { showFeedbackDialog = false },
      title = {
        Text("Provide Service Feedback", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Rate the response and resolution quality for #${activeGrievance.id}:",
            style = MaterialTheme.typography.bodySmall
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
          ) {
            (1..5).forEach { star ->
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (star <= feedbackRating) SecondaryContainerOrange else MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier
                  .size(32.dp)
                  .clickable { feedbackRating = star }
                  .padding(2.dp)
              )
            }
          }

          OutlinedTextField(
            value = feedbackComments,
            onValueChange = { feedbackComments = it },
            placeholder = { Text("Write additional comments or observations...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            maxLines = 3
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onProvideFeedbackClick(activeGrievance.copy(rating = feedbackRating, feedbackComment = feedbackComments))
            showFeedbackDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
        ) {
          Text("Submit", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { showFeedbackDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
private fun TimelineRow(
  step: TimelineStep,
  isFirst: Boolean,
  isLast: Boolean
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Node indicator & vertical line
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.width(24.dp)
    ) {
      Box(
        modifier = Modifier
          .size(24.dp)
          .clip(CircleShape)
          .background(
            if (isFirst) PrimaryNavy else if (step.isCompleted) SecondaryAmber else Color.LightGray
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (isFirst) Icons.Default.Check else if (isLast) Icons.Default.DoneAll else Icons.Default.Engineering,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(14.dp)
        )
      }

      if (!isLast) {
        Box(
          modifier = Modifier
            .width(2.dp)
            .height(54.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
        )
      }
    }

    // Step Details
    Column(
      modifier = Modifier
        .weight(1f)
        .padding(bottom = if (isLast) 0.dp else 12.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = step.title,
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            color = if (isFirst) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
          )
        )
        Text(
          text = step.timestamp,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Text(
        text = step.description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
          .padding(8.dp)
      )
    }
  }
}
