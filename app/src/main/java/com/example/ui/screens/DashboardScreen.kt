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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.Grievance
import com.example.data.model.GrievanceStatus
import com.example.ui.components.ChartType
import com.example.ui.components.GrievanceSearchBar
import com.example.ui.components.GrievanceStatusBadge
import com.example.ui.components.GrievanceStatusChart
import com.example.ui.components.getStatusBadgeColors
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryAmber
import com.example.ui.theme.SecondaryContainerOrange

@Composable
fun DashboardScreen(
  grievances: List<Grievance>,
  searchQuery: String,
  categoryFilter: String,
  language: AppLanguage,
  onSearchChange: (String) -> Unit,
  onCategoryFilterChange: (String) -> Unit,
  onNewGrievanceClick: () -> Unit,
  onTrackGrievanceClick: (Grievance) -> Unit,
  onRateGrievance: (String, Int, String) -> Unit,
  modifier: Modifier = Modifier,
  statusFilter: String = "All",
  onStatusFilterChange: (String) -> Unit = {}
) {
  val submittedCount = grievances.size
  val pendingCount = grievances.count { it.status == GrievanceStatus.PENDING }
  val inProgressCount = grievances.count { it.status == GrievanceStatus.IN_PROGRESS }
  val resolvedCount = grievances.count { it.status == GrievanceStatus.RESOLVED }

  val filterCategories = listOf("All", "Water Supply", "Streetlights", "Drainage", "Roads")

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("dashboard_screen_scroll"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Search Bar at the Top of the Grievance List View
    item {
      GrievanceSearchBar(
        query = searchQuery,
        onQueryChange = onSearchChange,
        language = language,
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
      )
    }

    // Active Search Query Banner
    if (searchQuery.isNotBlank()) {
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
          modifier = Modifier.fillMaxWidth().testTag("active_search_info")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (language == AppLanguage.ENGLISH) 
                "Results for \"$searchQuery\" (${grievances.size})"
              else 
                "\"$searchQuery\" ಗಾಗಿ ಫಲಿತಾಂಶಗಳು (${grievances.size})",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              text = if (language == AppLanguage.ENGLISH) "Clear search" else "ತೆರವು",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.error,
              modifier = Modifier
                .clickable { onSearchChange("") }
                .padding(4.dp)
                .testTag("clear_filter_text_btn")
            )
          }
        }
      }
    }

    // 2. Welcome Banner with Warm Greeting & Quick Actions
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("dashboard_welcome_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryNavy)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
          ) {
            Column {
              Text(
                text = "GRAM PANCHAYAT: MALUR",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                ),
                color = Color.White.copy(alpha = 0.75f)
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = if (language == AppLanguage.ENGLISH) "Namaskara, Rajesh!" else "ನಮಸ್ಕಾರ, ರಾಜೇಶ್!",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
              )
            }

            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = if (language == AppLanguage.ENGLISH)
              "Your active applications and grievances are being monitored by village administration."
            else
              "ನಿಮ್ಮ ಸಕ್ರಿಯ ಅರ್ಜಿಗಳು ಮತ್ತು ದೂರುಗಳನ್ನು ಗ್ರಾಮ ಆಡಳಿತ ಮೇಲ್ವಿಚಾರಣೆ ನಡೆಸುತ್ತಿದೆ.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.85f)
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Quick Action Bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = onNewGrievanceClick,
              modifier = Modifier
                .weight(1f)
                .height(46.dp)
                .testTag("btn_new_grievance"),
              colors = ButtonDefaults.buttonColors(containerColor = SecondaryAmber),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (language == AppLanguage.ENGLISH) "New Grievance" else "ಹೊಸ ದೂರು",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
              )
            }

            Button(
              onClick = { },
              modifier = Modifier
                .weight(1f)
                .height(46.dp)
                .testTag("btn_search_id"),
              colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (language == AppLanguage.ENGLISH) "Search ID" else "ಐಡಿ ಹುಡುಕಿ",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
              )
            }
          }
        }
      }
    }

    // 2. Summary Metric Cards (2x2 Grid)
    item {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          DashboardMetricCard(
            title = if (language == AppLanguage.ENGLISH) "Submitted" else "ಸಲ್ಲಿಸಲಾಗಿದೆ",
            value = submittedCount.toString(),
            label = "Total",
            icon = Icons.Default.Send,
            iconTint = PrimaryBlue,
            iconBg = Color(0xFFE0E7FF),
            modifier = Modifier.weight(1f),
            onClick = { onStatusFilterChange("All") }
          )
          DashboardMetricCard(
            title = if (language == AppLanguage.ENGLISH) "Pending" else "ಬಾಕಿ ಉಳಿದಿದೆ",
            value = pendingCount.toString(),
            label = "Needs review",
            valueColor = SecondaryAmber,
            icon = Icons.Default.HourglassEmpty,
            iconTint = SecondaryAmber,
            iconBg = Color(0xFFFFEDD5),
            modifier = Modifier.weight(1f),
            onClick = { onStatusFilterChange("Pending") }
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          DashboardMetricCard(
            title = if (language == AppLanguage.ENGLISH) "In Progress" else "ಪ್ರಗತಿಯಲ್ಲಿದೆ",
            value = inProgressCount.toString(),
            label = "Active work",
            valueColor = PrimaryBlue,
            icon = Icons.Default.Engineering,
            iconTint = PrimaryBlue,
            iconBg = Color(0xFFDBEAFE),
            modifier = Modifier.weight(1f),
            onClick = { onStatusFilterChange("In Progress") }
          )
          DashboardMetricCard(
            title = if (language == AppLanguage.ENGLISH) "Resolved" else "ಪರಿಹರಿಸಲಾಗಿದೆ",
            value = resolvedCount.toString(),
            label = "Completed",
            valueColor = Color(0xFF15803D),
            icon = Icons.Default.TaskAlt,
            iconTint = Color(0xFF15803D),
            iconBg = Color(0xFFDCFCE7),
            modifier = Modifier.weight(1f),
            onClick = { onStatusFilterChange("Resolved") }
          )
        }
      }
    }

    // 2.5 Visual Status Summary Section (Interactive Recharts-Style Pie & Bar Component)
    item {
      GrievanceStatusChart(
        grievances = grievances,
        language = language,
        communityPending = pendingCount,
        communityInProgress = inProgressCount,
        communityResolved = resolvedCount,
        initialChartType = ChartType.PIE,
        onFilterSelect = { status ->
          val filterName = when (status) {
            GrievanceStatus.PENDING -> "Pending"
            GrievanceStatus.IN_PROGRESS -> "In Progress"
            GrievanceStatus.RESOLVED -> "Resolved"
            GrievanceStatus.SUBMITTED -> "All"
          }
          onStatusFilterChange(filterName)
        }
      )
    }

    // 3. Status & Category Filter Chips
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (language == AppLanguage.ENGLISH) "Quick Filters" else "ತ್ವರಿತ ಫಿಲ್ಟರ್‌ಗಳು",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          if (searchQuery.isNotEmpty() || categoryFilter != "All" || statusFilter != "All") {
            Text(
              text = if (language == AppLanguage.ENGLISH) "Reset filters" else "ಫಿಲ್ಟರ್ ತೆರವು",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier
                .clickable {
                  onSearchChange("")
                  onCategoryFilterChange("All")
                  onStatusFilterChange("All")
                }
                .padding(4.dp)
                .testTag("reset_all_filters_btn")
            )
          }
        }

        // Status Filter Chips Row with visual status indicators
        val statusOptions = listOf(
          "All" to submittedCount,
          "Pending" to pendingCount,
          "In Progress" to inProgressCount,
          "Resolved" to resolvedCount
        )

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.testTag("dashboard_status_filter_row")
        ) {
          items(statusOptions) { (statusLabel, count) ->
            val isSelected = statusFilter.equals(statusLabel, ignoreCase = true)
            val indicatorColor = when (statusLabel) {
              "Pending" -> Color(0xFFB45309)
              "In Progress" -> Color(0xFF0369A1)
              "Resolved" -> Color(0xFF15803D)
              else -> PrimaryNavy
            }

            FilterChip(
              selected = isSelected,
              onClick = { onStatusFilterChange(statusLabel) },
              modifier = Modifier.testTag("filter_status_${statusLabel.lowercase().replace(" ", "_")}"),
              leadingIcon = {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.White else indicatorColor)
                )
              },
              label = {
                Text(
                  text = "$statusLabel ($count)",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = when (statusLabel) {
                  "Pending" -> Color(0xFFB45309)
                  "In Progress" -> Color(0xFF0369A1)
                  "Resolved" -> Color(0xFF15803D)
                  else -> PrimaryNavy
                },
                selectedLabelColor = Color.White,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                labelColor = MaterialTheme.colorScheme.onSurface
              ),
              shape = RoundedCornerShape(20.dp)
            )
          }
        }

        // Filter Category Chips
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(filterCategories) { cat ->
            val isSelected = categoryFilter.equals(cat, ignoreCase = true)
            FilterChip(
              selected = isSelected,
              onClick = { onCategoryFilterChange(cat) },
              label = {
                val labelText = when (cat) {
                  "All" -> "All ($submittedCount)"
                  "Water Supply" -> "Water Supply (4)"
                  "Streetlights" -> "Streetlights (3)"
                  "Drainage" -> "Drainage (3)"
                  "Roads" -> "Roads (2)"
                  else -> cat
                }
                Text(
                  text = labelText,
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = Color.White,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
              ),
              shape = RoundedCornerShape(20.dp)
            )
          }
        }
      }
    }

    // 4. Grievance List Section Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (searchQuery.isNotBlank()) {
            if (language == AppLanguage.ENGLISH) "Search Results (${grievances.size})" else "ಹುಡುಕಾಟದ ಫಲಿತಾಂಶಗಳು (${grievances.size})"
          } else {
            if (language == AppLanguage.ENGLISH) "Recent Submissions (${grievances.size})" else "ಇತ್ತೀಚಿನ ಸಲ್ಲಿಕೆಗಳು (${grievances.size})"
          },
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        if (categoryFilter != "All" || statusFilter != "All") {
          Text(
            text = if (language == AppLanguage.ENGLISH) "View All" else "ಎಲ್ಲ ನೋಡಿ",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { 
              onCategoryFilterChange("All")
              onStatusFilterChange("All")
            }
          )
        }
      }
    }

    // Empty state when search or filter returns no results
    if (grievances.isEmpty()) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("empty_grievances_search_card"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
              )
            }
            Text(
              text = if (language == AppLanguage.ENGLISH) "No grievances found" else "ಯಾವುದೇ ದೂರುಗಳು ಕಂಡುಬಂದಿಲ್ಲ",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = if (language == AppLanguage.ENGLISH)
                "No grievances match \"$searchQuery\". Try searching by another title or category like 'Water', 'Roads', 'Streetlights', or clearing filters."
              else
                "ನಿಮ್ಮ ಹುಡುಕಾಟಕ್ಕೆ ಯಾವುದೇ ಫಲಿತಾಂಶ ಲಭ್ಯವಿಲ್ಲ. ದಯವಿಟ್ಟು ಬೇರೆ ಶೀರ್ಷಿಕೆ ಅಥವಾ ವರ್ಗವನ್ನು ಹುಡುಕಿ.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center
            )
            Button(
              onClick = {
                onSearchChange("")
                onCategoryFilterChange("All")
                onStatusFilterChange("All")
              },
              colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("btn_reset_empty_search")
            ) {
              Text(
                text = if (language == AppLanguage.ENGLISH) "Reset Search & Filters" else "ಫಿಲ್ಟರ್ ತೆರವುಗೊಳಿಸಿ",
                color = Color.White
              )
            }
          }
        }
      }
    }

    // Grievance Cards List
    items(grievances) { grievance ->
      DashboardGrievanceCard(
        grievance = grievance,
        language = language,
        onTrackClick = { onTrackGrievanceClick(grievance) },
        onRateSubmit = { rating, comment -> onRateGrievance(grievance.id, rating, comment) }
      )
    }

    // 5. Delightful Tip Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(PrimaryBlue.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Lightbulb,
              contentDescription = null,
              tint = PrimaryBlue,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(14.dp))
          Column {
            Text(
              text = if (language == AppLanguage.ENGLISH) "Did you know?" else "ನಿಮಗೆ ತಿಳಿದಿದೆಯೇ?",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = if (language == AppLanguage.ENGLISH)
                "Gram Sabha meetings are held every quarter. Submit your village development suggestions early!"
              else
                "ಗ್ರಾಮ ಸಭೆಗಳು ಪ್ರತಿ ತ್ರೈಮಾಸಿಕದಲ್ಲಿ ನಡೆಯುತ್ತವೆ. ನಿಮ್ಮ ಗ್ರಾಮ ಅಭಿವೃದ್ಧಿ ಸಲಹೆಗಳನ್ನು ಮುಂಚಿತವಾಗಿಯೇ ಸಲ್ಲಿಸಿ!",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

@Composable
private fun DashboardMetricCard(
  title: String,
  value: String,
  label: String,
  icon: ImageVector,
  iconTint: Color,
  iconBg: Color,
  modifier: Modifier = Modifier,
  valueColor: Color = MaterialTheme.colorScheme.onSurface,
  onClick: () -> Unit = {}
) {
  Card(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .clickable { onClick() },
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(iconBg),
          contentAlignment = Alignment.Center
        ) {
          Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(
          text = value,
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
          color = valueColor
        )
        Text(
          text = label,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(bottom = 2.dp)
        )
      }
    }
  }
}

@Composable
private fun DashboardGrievanceCard(
  grievance: Grievance,
  language: AppLanguage,
  onTrackClick: () -> Unit,
  onRateSubmit: (Int, String) -> Unit
) {
  var selectedRating by remember(grievance.id) { mutableIntStateOf(grievance.rating ?: 4) }
  var feedbackSubmitted by remember(grievance.id) { mutableStateOf(grievance.rating != null) }

  val categoryIcon = when {
    grievance.category.contains("Water", ignoreCase = true) -> Icons.Default.WaterDrop
    grievance.category.contains("Street", ignoreCase = true) -> Icons.Default.Lightbulb
    grievance.category.contains("Drain", ignoreCase = true) -> Icons.Default.CleaningServices
    else -> Icons.Default.Engineering
  }

  val isResolved = grievance.status == GrievanceStatus.RESOLVED

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("dashboard_card_${grievance.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Header: Icon + ID + Title + Color-Coded Status Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.Top
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = categoryIcon,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "#${grievance.id}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = grievance.title,
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Color-coded Status Badge
        GrievanceStatusBadge(
          status = grievance.status,
          language = language
        )
      }

      // Description Snippet
      Text(
        text = grievance.description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2
      )

      // If resolved, show rating interactive section!
      if (isResolved) {
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (language == AppLanguage.ENGLISH) "Rate this resolution:" else "ಪರಿಹಾರವನ್ನು ರೇಟ್ ಮಾಡಿ:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
              )
              Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                (1..5).forEach { star ->
                  Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "$star stars",
                    tint = if (star <= selectedRating) SecondaryContainerOrange else MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier
                      .size(20.dp)
                      .clickable {
                        selectedRating = star
                        feedbackSubmitted = false
                      }
                  )
                }
              }
            }

            if (!feedbackSubmitted) {
              Button(
                onClick = {
                  feedbackSubmitted = true
                  onRateSubmit(selectedRating, "Resolved satisfactorily")
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = if (language == AppLanguage.ENGLISH) "Submit Feedback" else "ಪ್ರತಿಕ್ರಿಯೆ ಸಲ್ಲಿಸಿ",
                  style = MaterialTheme.typography.labelMedium,
                  color = Color.White
                )
              }
            } else {
              Text(
                text = "✓ Thank you for rating this resolution!",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = Color(0xFF15803D)
              )
            }
          }
        }
      }

      HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f))

      // Footer: Date & Track Progress Action
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = if (isResolved) "Resolved: ${grievance.submittedDate}" else "Submitted: ${grievance.submittedDate}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          if (!grievance.evidencePhotoUri.isNullOrBlank()) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.CameraAlt,
                  contentDescription = "Visual Evidence Attached",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(11.dp)
                )
                Text(
                  text = if (language == AppLanguage.ENGLISH) "Photo Proof" else "ಸಾಕ್ಷ್ಯ ಚಿತ್ರ",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }
        }

        Row(
          modifier = Modifier
            .clickable { onTrackClick() }
            .padding(vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (isResolved) 
              (if (language == AppLanguage.ENGLISH) "View Details" else "ವಿವರ ನೋಡಿ")
            else 
              (if (language == AppLanguage.ENGLISH) "Track Progress" else "ಪ್ರಗತಿ ತಿಳಿಯಿರಿ"),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp)
          )
        }
      }
    }
  }
}

@Composable
fun StatusPill(status: GrievanceStatus, language: AppLanguage = AppLanguage.ENGLISH) {
  GrievanceStatusBadge(status = status, language = language)
}
