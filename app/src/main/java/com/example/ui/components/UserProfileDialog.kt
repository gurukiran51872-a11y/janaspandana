package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.AppLanguage
import com.example.data.model.Grievance
import com.example.data.model.GrievanceStatus
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryAmber

enum class GrievanceStatusFilter(val labelEn: String, val labelKn: String) {
  ALL("All", "ಎಲ್ಲವೂ"),
  PENDING("Pending", "ಬಾಕಿ"),
  IN_PROGRESS("In Progress", "ಪ್ರಗತಿಯಲ್ಲಿದೆ"),
  RESOLVED("Resolved", "ಪರಿಹರಿಸಲಾಗಿದೆ")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CitizenProfileDialog(
  userName: String = "Rajesh K.",
  userEmail: String = "citizen@janaspandana.karnataka.gov.in",
  userGrievances: List<Grievance> = emptyList(),
  onTrackGrievance: (Grievance) -> Unit = {},
  onSignOut: () -> Unit = {},
  onDismiss: () -> Unit,
  language: AppLanguage
) {
  var selectedFilter by remember { mutableStateOf(GrievanceStatusFilter.ALL) }
  var showSignOutConfirmation by remember { mutableStateOf(false) }

  // Filter grievance submissions by status
  val filteredGrievances = remember(userGrievances, selectedFilter) {
    when (selectedFilter) {
      GrievanceStatusFilter.ALL -> userGrievances
      GrievanceStatusFilter.PENDING -> userGrievances.filter { it.status == GrievanceStatus.PENDING || it.status == GrievanceStatus.SUBMITTED }
      GrievanceStatusFilter.IN_PROGRESS -> userGrievances.filter { it.status == GrievanceStatus.IN_PROGRESS }
      GrievanceStatusFilter.RESOLVED -> userGrievances.filter { it.status == GrievanceStatus.RESOLVED }
    }
  }

  val totalCount = userGrievances.size
  val pendingCount = userGrievances.count { it.status == GrievanceStatus.PENDING || it.status == GrievanceStatus.SUBMITTED }
  val inProgressCount = userGrievances.count { it.status == GrievanceStatus.IN_PROGRESS }
  val resolvedCount = userGrievances.count { it.status == GrievanceStatus.RESOLVED }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.92f)
        .testTag("citizen_profile_modal"),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      shadowElevation = 12.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(top = 18.dp, bottom = 12.dp)
      ) {
        // 1. Top Header with Close Action
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(PrimaryNavy),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = userName.firstOrNull()?.uppercase() ?: "R",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
            }
            Column {
              Text(
                text = if (language == AppLanguage.ENGLISH) "Citizen Profile" else "ನಾಗರಿಕ ಪ್ರೊಫೈಲ್",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Verified,
                  contentDescription = null,
                  tint = Color(0xFF16A34A),
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (language == AppLanguage.ENGLISH) "Verified Resident" else "ದೃಢೀಕೃತ ನಾಗರಿಕ",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                  color = Color(0xFF16A34A)
                )
              }
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(36.dp)
              .testTag("profile_close_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        // 2. Scrollable Body
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 20.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          // User Identity Card
          item {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp)
                .testTag("user_profile_info_card"),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                // Name & Email
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.Top
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = userName.ifBlank { "Rajesh K." },
                      style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurface,
                      modifier = Modifier.testTag("profile_user_name")
                    )
                    Text(
                      text = userEmail.ifBlank { "citizen@janaspandana.karnataka.gov.in" },
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      modifier = Modifier.testTag("profile_user_email")
                    )
                  }

                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryBlue.copy(alpha = 0.12f)
                  ) {
                    Text(
                      text = "Citizen",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = PrimaryBlue,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                // Jurisdiction Details
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = if (language == AppLanguage.ENGLISH) "Gram Panchayat" else "ಗ್ರಾಮ ಪಂಚಾಯತಿ",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                      text = "Malur Gram Panchayat",
                      style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                  }
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = if (language == AppLanguage.ENGLISH) "District & Taluk" else "ಜಿಲ್ಲೆ ಮತ್ತು ತಾಲೂಕು",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                      text = "Malur, Bengaluru Rural",
                      style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                  }
                }
              }
            }
          }

          // 3. Quick Grievance Metrics Overview Cards
          item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                text = if (language == AppLanguage.ENGLISH) "My Grievances Overview" else "ನನ್ನ ದೂರುಗಳ ಅವಲೋಕನ",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                ProfileMetricPill(
                  label = if (language == AppLanguage.ENGLISH) "Total" else "ಒಟ್ಟು",
                  count = totalCount,
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.weight(1f)
                )
                ProfileMetricPill(
                  label = if (language == AppLanguage.ENGLISH) "Pending" else "ಬಾಕಿ",
                  count = pendingCount,
                  color = Color(0xFFB45309),
                  modifier = Modifier.weight(1f)
                )
                ProfileMetricPill(
                  label = if (language == AppLanguage.ENGLISH) "In Progress" else "ಪ್ರಗತಿ",
                  count = inProgressCount,
                  color = Color(0xFF0284C7),
                  modifier = Modifier.weight(1f)
                )
                ProfileMetricPill(
                  label = if (language == AppLanguage.ENGLISH) "Resolved" else "ಪರಿಹಾರ",
                  count = resolvedCount,
                  color = Color(0xFF16A34A),
                  modifier = Modifier.weight(1f)
                )
              }
            }
          }

          // 4. Status Filtering System
          item {
            Column(
              modifier = Modifier.fillMaxWidth(),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                  )
                  Text(
                    text = if (language == AppLanguage.ENGLISH) "Filter by Status" else "ಸ್ಥಿತಿಯ ಪ್ರಕಾರ ಫಿಲ್ಟರ್ ಮಾಡಿ",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }

                Text(
                  text = "${filteredGrievances.size} ${if (language == AppLanguage.ENGLISH) "Items" else "ದೂರುಗಳು"}",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              // Interactive Status Filter Chips
              FlowRow(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("profile_status_filter_chips"),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                StatusFilterChip(
                  label = if (language == AppLanguage.ENGLISH) "All" else "ಎಲ್ಲವೂ",
                  count = totalCount,
                  isSelected = selectedFilter == GrievanceStatusFilter.ALL,
                  onClick = { selectedFilter = GrievanceStatusFilter.ALL },
                  tag = "filter_chip_all"
                )

                StatusFilterChip(
                  label = if (language == AppLanguage.ENGLISH) "Pending" else "ಬಾಕಿ",
                  count = pendingCount,
                  badgeColor = Color(0xFFB45309),
                  isSelected = selectedFilter == GrievanceStatusFilter.PENDING,
                  onClick = { selectedFilter = GrievanceStatusFilter.PENDING },
                  tag = "filter_chip_pending"
                )

                StatusFilterChip(
                  label = if (language == AppLanguage.ENGLISH) "In Progress" else "ಪ್ರಗತಿಯಲ್ಲಿದೆ",
                  count = inProgressCount,
                  badgeColor = Color(0xFF0284C7),
                  isSelected = selectedFilter == GrievanceStatusFilter.IN_PROGRESS,
                  onClick = { selectedFilter = GrievanceStatusFilter.IN_PROGRESS },
                  tag = "filter_chip_in_progress"
                )

                StatusFilterChip(
                  label = if (language == AppLanguage.ENGLISH) "Resolved" else "ಪರಿಹರಿಸಲಾಗಿದೆ",
                  count = resolvedCount,
                  badgeColor = Color(0xFF16A34A),
                  isSelected = selectedFilter == GrievanceStatusFilter.RESOLVED,
                  onClick = { selectedFilter = GrievanceStatusFilter.RESOLVED },
                  tag = "filter_chip_resolved"
                )
              }
            }
          }

          // 5. User's Submitted Grievances List
          if (filteredGrievances.isEmpty()) {
            item {
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 16.dp)
                  .testTag("profile_empty_submissions"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Inbox,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(40.dp)
                  )
                  Text(
                    text = when (selectedFilter) {
                      GrievanceStatusFilter.ALL -> if (language == AppLanguage.ENGLISH) "No grievances submitted yet" else "ಇನ್ನೂ ಯಾವುದೇ ದೂರುಗಳನ್ನು ಸಲ್ಲಿಸಿಲ್ಲ"
                      GrievanceStatusFilter.PENDING -> if (language == AppLanguage.ENGLISH) "No pending grievances" else "ಯಾವುದೇ ಬಾಕಿ ದೂರುಗಳಿಲ್ಲ"
                      GrievanceStatusFilter.IN_PROGRESS -> if (language == AppLanguage.ENGLISH) "No grievances currently in progress" else "ಪ್ರಗತಿಯಲ್ಲಿರುವ ಯಾವುದೇ ದೂರುಗಳಿಲ್ಲ"
                      GrievanceStatusFilter.RESOLVED -> if (language == AppLanguage.ENGLISH) "No resolved grievances yet" else "ಇನ್ನೂ ಯಾವುದೇ ಪರಿಹರಿಸಿದ ದೂರುಗಳಿಲ್ಲ"
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  if (selectedFilter != GrievanceStatusFilter.ALL) {
                    TextButton(onClick = { selectedFilter = GrievanceStatusFilter.ALL }) {
                      Text(if (language == AppLanguage.ENGLISH) "Show all submissions" else "ಎಲ್ಲಾ ದೂರುಗಳನ್ನು ವೀಕ್ಷಿಸಿ")
                    }
                  }
                }
              }
            }
          } else {
            items(filteredGrievances, key = { it.id }) { grievance ->
              UserGrievanceSubmissionItem(
                grievance = grievance,
                language = language,
                onTrackClick = {
                  onDismiss()
                  onTrackGrievance(grievance)
                }
              )
            }
          }
        }

        // 6. Dialog Footer: Sign Out & Dismiss Actions
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (!showSignOutConfirmation) {
            TextButton(
              onClick = { showSignOutConfirmation = true },
              modifier = Modifier.testTag("profile_sign_out_btn")
            ) {
              Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (language == AppLanguage.ENGLISH) "Sign Out" else "ಲಾಗ್ ಔಟ್",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
              )
            }
          } else {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(
                text = if (language == AppLanguage.ENGLISH) "Confirm sign out?" else "ದೃಢೀಕರಿಸಿ?",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error
              )
              Button(
                onClick = {
                  onDismiss()
                  onSignOut()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("profile_confirm_sign_out_btn")
              ) {
                Text(
                  text = if (language == AppLanguage.ENGLISH) "Yes, Sign Out" else "ಹೌದು",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
              }
              OutlinedButton(
                onClick = { showSignOutConfirmation = false },
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = if (language == AppLanguage.ENGLISH) "Cancel" else "ರದ್ದು",
                  style = MaterialTheme.typography.labelSmall
                )
              }
            }
          }

          Button(
            onClick = onDismiss,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
            modifier = Modifier.testTag("profile_dismiss_button")
          ) {
            Text(
              text = if (language == AppLanguage.ENGLISH) "Close" else "ಮುಚ್ಚಿ",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ProfileMetricPill(
  label: String,
  count: Int,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    color = color.copy(alpha = 0.08f),
    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.25f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp, horizontal = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = count.toString(),
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = color
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
private fun StatusFilterChip(
  label: String,
  count: Int,
  badgeColor: Color? = null,
  isSelected: Boolean,
  onClick: () -> Unit,
  tag: String
) {
  FilterChip(
    selected = isSelected,
    onClick = onClick,
    label = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(text = label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = if (isSelected) {
            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.25f)
          } else {
            (badgeColor ?: MaterialTheme.colorScheme.primary).copy(alpha = 0.15f)
          }
        ) {
          Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else (badgeColor ?: MaterialTheme.colorScheme.primary),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }
    },
    colors = FilterChipDefaults.filterChipColors(
      selectedContainerColor = badgeColor ?: MaterialTheme.colorScheme.primary,
      selectedLabelColor = MaterialTheme.colorScheme.onPrimary
    ),
    shape = RoundedCornerShape(12.dp),
    modifier = Modifier.testTag(tag)
  )
}

@Composable
private fun UserGrievanceSubmissionItem(
  grievance: Grievance,
  language: AppLanguage,
  onTrackClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("user_grievance_item_${grievance.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Top Row: ID + Status Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Text(
            text = "#${grievance.id}",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        GrievanceStatusBadge(
          status = grievance.status,
          language = language
        )
      }

      // Title & Category
      Text(
        text = if (language == AppLanguage.KANNADA && grievance.titleKn.isNotBlank()) grievance.titleKn else grievance.title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = "${grievance.wardNumber}, ${grievance.village}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Text(
          text = grievance.submittedDate,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      // Track Action Button
      Button(
        onClick = onTrackClick,
        modifier = Modifier
          .fillMaxWidth()
          .height(38.dp)
          .testTag("track_submission_btn_${grievance.id}"),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
          contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Text(
            text = if (language == AppLanguage.ENGLISH) "Track this Grievance" else "ಈ ದೂರನ್ನು ಪರಿಶೀಲಿಸಿ",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
