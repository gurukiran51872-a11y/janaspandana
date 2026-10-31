package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.Grievance
import com.example.data.model.GrievanceStatus
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryAmber
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.sqrt

enum class ChartType {
  PIE,
  BAR
}

data class StatusChartData(
  val pendingCount: Int,
  val inProgressCount: Int,
  val resolvedCount: Int
) {
  val total: Int get() = pendingCount + inProgressCount + resolvedCount
  val resolutionRate: Int get() = if (total > 0) ((resolvedCount.toFloat() / total) * 100).toInt() else 0
}

data class PieSlice(
  val status: GrievanceStatus,
  val labelEn: String,
  val labelKn: String,
  val count: Int,
  val color: Color,
  val gradient: Brush
)

/**
 * Interactive visual summary dashboard component inspired by Recharts.
 * Displays a breakdown of grievance statuses (Pending vs In Progress vs Resolved)
 * with an interactive Pie/Donut Chart, slice touch inspection, Recharts-style tooltip,
 * and a Bar Chart view toggle.
 */
@Composable
fun GrievanceStatusChart(
  grievances: List<Grievance>,
  language: AppLanguage,
  modifier: Modifier = Modifier,
  communityPending: Int = 142,
  communityInProgress: Int = 96,
  communityResolved: Int = 1010,
  initialChartType: ChartType = ChartType.PIE,
  onFilterSelect: ((GrievanceStatus) -> Unit)? = null
) {
  var chartType by remember { mutableStateOf(initialChartType) }
  var selectedSlice by remember { mutableStateOf<GrievanceStatus?>(null) }

  // Compute live user grievance counts; fall back to baseline if empty
  val livePending = grievances.count { it.status == GrievanceStatus.PENDING }
  val liveInProgress = grievances.count { it.status == GrievanceStatus.IN_PROGRESS }
  val liveResolved = grievances.count { it.status == GrievanceStatus.RESOLVED }

  val data = StatusChartData(
    pendingCount = if (grievances.isNotEmpty()) livePending else communityPending,
    inProgressCount = if (grievances.isNotEmpty()) liveInProgress else communityInProgress,
    resolvedCount = if (grievances.isNotEmpty()) liveResolved else communityResolved
  )

  val pendingColor = Color(0xFFD97706) // Amber-600
  val pendingGradient = Brush.verticalGradient(listOf(Color(0xFFFBBF24), Color(0xFFD97706)))
  val inProgressColor = Color(0xFF0284C7) // Sky-600
  val inProgressGradient = Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7)))
  val resolvedColor = Color(0xFF16A34A) // Green-600
  val resolvedGradient = Brush.verticalGradient(listOf(Color(0xFF4ADE80), Color(0xFF16A34A)))

  val slices = listOf(
    PieSlice(
      status = GrievanceStatus.PENDING,
      labelEn = "Pending",
      labelKn = "ಬಾಕಿ",
      count = data.pendingCount,
      color = pendingColor,
      gradient = pendingGradient
    ),
    PieSlice(
      status = GrievanceStatus.IN_PROGRESS,
      labelEn = "In Progress",
      labelKn = "ಪ್ರಗತಿಯಲ್ಲಿ",
      count = data.inProgressCount,
      color = inProgressColor,
      gradient = inProgressGradient
    ),
    PieSlice(
      status = GrievanceStatus.RESOLVED,
      labelEn = "Resolved",
      labelKn = "ಪರಿಹರಿಸಲಾಗಿದೆ",
      count = data.resolvedCount,
      color = resolvedColor,
      gradient = resolvedGradient
    )
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("status_summary_chart_card"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Header: Title, Live Rate Pill & Interactive Recharts View Switcher
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(PrimaryNavy.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (chartType == ChartType.PIE) Icons.Default.PieChart else Icons.Default.BarChart,
              contentDescription = null,
              tint = PrimaryNavy,
              modifier = Modifier.size(22.dp)
            )
          }
          Column {
            Text(
              text = if (language == AppLanguage.ENGLISH) "Grievance Status Breakdown" else "ದೂರುಗಳ ಸ್ಥಿತಿ ಸಾರಾಂಶ",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = if (language == AppLanguage.ENGLISH) "Pending vs. Resolved items" else "ಬಾಕಿ ಮತ್ತು ಪರಿಹರಿಸಿದ ದೂರುಗಳ ಹೋಲಿಕೆ",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Live Resolution Rate Pill
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFDCFCE7)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.TrendingUp,
              contentDescription = null,
              tint = Color(0xFF15803D),
              modifier = Modifier.size(13.dp)
            )
            Text(
              text = "${data.resolutionRate}% Rate",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = Color(0xFF15803D)
            )
          }
        }
      }

      // Recharts-Style Chart Mode Toggle (Pie vs Bar)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(MaterialTheme.colorScheme.surfaceContainerHigh)
          .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        // Pie Chart Tab
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (chartType == ChartType.PIE) PrimaryNavy else Color.Transparent,
          modifier = Modifier
            .weight(1f)
            .height(36.dp)
            .clickable { chartType = ChartType.PIE }
            .testTag("btn_chart_mode_pie")
        ) {
          Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.PieChart,
              contentDescription = null,
              tint = if (chartType == ChartType.PIE) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (language == AppLanguage.ENGLISH) "Pie Chart" else "ಪೈ ಚಾರ್ಟ್",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = if (chartType == ChartType.PIE) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Bar Chart Tab
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (chartType == ChartType.BAR) PrimaryNavy else Color.Transparent,
          modifier = Modifier
            .weight(1f)
            .height(36.dp)
            .clickable { chartType = ChartType.BAR }
            .testTag("btn_chart_mode_bar")
        ) {
          Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.BarChart,
              contentDescription = null,
              tint = if (chartType == ChartType.BAR) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (language == AppLanguage.ENGLISH) "Bar Chart" else "ಬಾರ್ ಚಾರ್ಟ್",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = if (chartType == ChartType.BAR) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // Visual Chart Area (Interactive Pie or Bar)
      if (chartType == ChartType.PIE) {
        StatusPieChartCanvas(
          slices = slices,
          total = data.total,
          selectedSlice = selectedSlice,
          onSliceSelected = { selected -> selectedSlice = selected },
          language = language,
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .testTag("status_pie_chart_canvas")
        )
      } else {
        StatusBarChartCanvas(
          pendingCount = data.pendingCount,
          inProgressCount = data.inProgressCount,
          resolvedCount = data.resolvedCount,
          language = language,
          modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .testTag("status_bar_chart_canvas")
        )
      }

      // Recharts-Style Interactive Tooltip / Detail Callout
      AnimatedVisibility(
        visible = selectedSlice != null,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        selectedSlice?.let { status ->
          val slice = slices.firstOrNull { it.status == status }
          val percentage = if (data.total > 0 && slice != null) {
            ((slice.count.toFloat() / data.total) * 100).toInt()
          } else 0

          val explanation = when (status) {
            GrievanceStatus.PENDING -> if (language == AppLanguage.ENGLISH)
              "Citizen complaints awaiting initial verification by Panchayat officer."
            else
              "ಪಂಚಾಯಿತಿ ಅಧಿಕಾರಿಯ ಪರಿಶೀಲನೆಗಾಗಿ ಬಾಕಿ ಉಳಿದಿರುವ ದೂರುಗಳು."
            GrievanceStatus.IN_PROGRESS -> if (language == AppLanguage.ENGLISH)
              "Civic works actively underway on the ground by assigned field teams."
            else
              "ನಿಯೋಜಿತ ಕ್ಷೇತ್ರ ತಂಡಗಳಿಂದ ಪ್ರಗತಿಯಲ್ಲಿರುವ ಸಾರ್ವಜನಿಕ ಕಾಮಗಾರಿಗಳು."
            GrievanceStatus.RESOLVED -> if (language == AppLanguage.ENGLISH)
              "Civic issues inspected and resolved satisfactorily."
            else
              "ಪರಿಶೀಲಿಸಿ ಯಶಸ್ವಿಯಾಗಿ ಪರಿಹರಿಸಲಾದ ನಾಗರಿಕ ಸಮಸ್ಯೆಗಳು."
            GrievanceStatus.SUBMITTED -> ""
          }

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("status_chart_tooltip"),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
            ),
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              slice?.color?.copy(alpha = 0.5f) ?: Color.Transparent
            )
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(12.dp)
                      .clip(CircleShape)
                      .background(slice?.color ?: PrimaryNavy)
                  )
                  Text(
                    text = "${if (language == AppLanguage.ENGLISH) slice?.labelEn else slice?.labelKn} Grievances",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = (slice?.color ?: PrimaryNavy).copy(alpha = 0.15f)
                  ) {
                    Text(
                      text = "$percentage% of total",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = slice?.color ?: PrimaryNavy,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }

                IconButton(
                  onClick = { selectedSlice = null },
                  modifier = Modifier.size(24.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close tooltip",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }

              Text(
                text = explanation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Total Count: ${slice?.count ?: 0} items",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )

                if (onFilterSelect != null) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryNavy,
                    modifier = Modifier
                      .clickable { onFilterSelect(status) }
                      .testTag("btn_apply_chart_filter")
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                      )
                      Text(
                        text = if (language == AppLanguage.ENGLISH) "Filter List" else "ಫಿಲ್ಟರ್ ಮಾಡಿ",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }

      // Resolution Rate Progress Bar
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (language == AppLanguage.ENGLISH) "Overall Resolution Rate" else "ಒಟ್ಟು ಪರಿಹಾರ ದರ",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${data.resolvedCount} of ${data.total} resolved (${data.resolutionRate}%)",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF15803D)
          )
        }

        LinearProgressIndicator(
          progress = { if (data.total > 0) data.resolvedCount.toFloat() / data.total else 0f },
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
          color = Color(0xFF16A34A),
          trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
      }

      HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f))

      // Interactive Legend Row (Tapping selects/highlights the slice)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Pending Legend Item
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (selectedSlice == GrievanceStatus.PENDING) pendingColor.copy(alpha = 0.15f) else Color.Transparent,
          border = if (selectedSlice == GrievanceStatus.PENDING) androidx.compose.foundation.BorderStroke(1.dp, pendingColor) else null,
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
              selectedSlice = if (selectedSlice == GrievanceStatus.PENDING) null else GrievanceStatus.PENDING
            }
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag("legend_pending")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(pendingColor)
            )
            Text(
              text = "${if (language == AppLanguage.ENGLISH) "Pending" else "ಬಾಕಿ"}: ${data.pendingCount}",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (selectedSlice == GrievanceStatus.PENDING) FontWeight.Bold else FontWeight.Medium
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }

        // In Progress Legend Item
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (selectedSlice == GrievanceStatus.IN_PROGRESS) inProgressColor.copy(alpha = 0.15f) else Color.Transparent,
          border = if (selectedSlice == GrievanceStatus.IN_PROGRESS) androidx.compose.foundation.BorderStroke(1.dp, inProgressColor) else null,
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
              selectedSlice = if (selectedSlice == GrievanceStatus.IN_PROGRESS) null else GrievanceStatus.IN_PROGRESS
            }
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag("legend_in_progress")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(inProgressColor)
            )
            Text(
              text = "${if (language == AppLanguage.ENGLISH) "In Progress" else "ಪ್ರಗತಿಯಲ್ಲಿ"}: ${data.inProgressCount}",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (selectedSlice == GrievanceStatus.IN_PROGRESS) FontWeight.Bold else FontWeight.Medium
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }

        // Resolved Legend Item
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (selectedSlice == GrievanceStatus.RESOLVED) resolvedColor.copy(alpha = 0.15f) else Color.Transparent,
          border = if (selectedSlice == GrievanceStatus.RESOLVED) androidx.compose.foundation.BorderStroke(1.dp, resolvedColor) else null,
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
              selectedSlice = if (selectedSlice == GrievanceStatus.RESOLVED) null else GrievanceStatus.RESOLVED
            }
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag("legend_resolved")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(resolvedColor)
            )
            Text(
              text = "${if (language == AppLanguage.ENGLISH) "Resolved" else "ಪರಿಹಾರ"}: ${data.resolvedCount}",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = Color(0xFF15803D)
            )
          }
        }
      }
    }
  }
}

/**
 * Interactive Donut/Pie Chart with touch slice detection and center readout
 */
@Composable
private fun StatusPieChartCanvas(
  slices: List<PieSlice>,
  total: Int,
  selectedSlice: GrievanceStatus?,
  onSliceSelected: (GrievanceStatus?) -> Unit,
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier,
    contentAlignment = Alignment.Center
  ) {
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(slices, total, selectedSlice) {
          detectTapGestures { tapOffset ->
            val w = size.width.toFloat()
            val h = size.height.toFloat()
            val center = Offset(w / 2f, h / 2f)
            val dx = tapOffset.x - center.x
            val dy = tapOffset.y - center.y
            val dist = sqrt(dx * dx + dy * dy)
            val outerRadius = min(w, h) / 2f
            val innerRadius = outerRadius * 0.45f

            if (dist in innerRadius..outerRadius && total > 0) {
              // Calculate angle with 12 o'clock (-90 degrees) as 0
              var deg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
              if (deg < 0) deg += 360f
              val normalizedDeg = (deg + 90f) % 360f

              var currentAngle = 0f
              var tappedSlice: GrievanceStatus? = null
              for (slice in slices) {
                val sweep = (slice.count.toFloat() / total) * 360f
                if (normalizedDeg >= currentAngle && normalizedDeg < currentAngle + sweep) {
                  tappedSlice = slice.status
                  break
                }
                currentAngle += sweep
              }
              onSliceSelected(if (selectedSlice == tappedSlice) null else tappedSlice)
            } else if (dist < innerRadius) {
              // Tap in center resets selection
              onSliceSelected(null)
            }
          }
        }
    ) {
      val w = size.width
      val h = size.height
      val center = Offset(w / 2f, h / 2f)
      val baseStrokeWidth = 32.dp.toPx()
      val selectedStrokeWidth = 40.dp.toPx()
      val radius = (min(w, h) / 2f) - (selectedStrokeWidth / 2f) - 4.dp.toPx()

      if (total == 0) {
        // Draw empty state circle
        drawCircle(
          color = Color.LightGray.copy(alpha = 0.3f),
          radius = radius,
          center = center,
          style = Stroke(width = baseStrokeWidth)
        )
      } else {
        var startAngle = -90f // Start from 12 o'clock
        val nonZeroCount = slices.count { it.count > 0 }
        val gap = if (nonZeroCount > 1) 3f else 0f

        slices.forEach { slice ->
          val fraction = slice.count.toFloat() / total
          val sweepAngle = (fraction * 360f)

          if (sweepAngle > 0f) {
            val isSelected = selectedSlice == slice.status
            val strokeW = if (isSelected) selectedStrokeWidth else baseStrokeWidth

            drawArc(
              color = slice.color,
              startAngle = startAngle + (gap / 2f),
              sweepAngle = (sweepAngle - gap).coerceAtLeast(0.5f),
              useCenter = false,
              topLeft = Offset(center.x - radius, center.y - radius),
              size = Size(radius * 2f, radius * 2f),
              style = Stroke(width = strokeW, cap = StrokeCap.Round)
            )

            // If selected, draw an outer subtle highlight ring
            if (isSelected) {
              val highlightRadius = radius + (selectedStrokeWidth / 2f) + 3.dp.toPx()
              drawCircle(
                color = slice.color.copy(alpha = 0.35f),
                radius = highlightRadius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
              )
            }
          }
          startAngle += sweepAngle
        }
      }
    }

    // Center Readout Inside the Donut
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      if (selectedSlice != null) {
        val selected = slices.firstOrNull { it.status == selectedSlice }
        val pct = if (total > 0 && selected != null) ((selected.count.toFloat() / total) * 100).toInt() else 0

        Text(
          text = "${selected?.count ?: 0}",
          style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
          color = selected?.color ?: MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = if (language == AppLanguage.ENGLISH) selected?.labelEn ?: "" else selected?.labelKn ?: "",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = selected?.color ?: MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "$pct%",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      } else {
        Text(
          text = total.toString(),
          style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = if (language == AppLanguage.ENGLISH) "Total Items" else "ಒಟ್ಟು ದೂರುಗಳು",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = if (language == AppLanguage.ENGLISH) "Tap slice" else "ಟ್ಯಾಪ್ ಮಾಡಿ",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
          color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        )
      }
    }
  }
}

/**
 * Native Canvas-based bar chart rendering comparison bars for Pending, In Progress, and Resolved
 */
@Composable
private fun StatusBarChartCanvas(
  pendingCount: Int,
  inProgressCount: Int,
  resolvedCount: Int,
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  val maxCount = maxOf(pendingCount, inProgressCount, resolvedCount, 1)

  val barItems = listOf(
    Triple(
      if (language == AppLanguage.ENGLISH) "Pending" else "ಬಾಕಿ",
      pendingCount,
      Brush.verticalGradient(listOf(Color(0xFFFBBF24), Color(0xFFD97706)))
    ),
    Triple(
      if (language == AppLanguage.ENGLISH) "In Progress" else "ಪ್ರಗತಿಯಲ್ಲಿ",
      inProgressCount,
      Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7)))
    ),
    Triple(
      if (language == AppLanguage.ENGLISH) "Resolved" else "ಪರಿಹರಿಸಲಾಗಿದೆ",
      resolvedCount,
      Brush.verticalGradient(listOf(Color(0xFF4ADE80), Color(0xFF16A34A)))
    )
  )

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(MaterialTheme.colorScheme.surface)
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    // 1. Canvas drawing background horizontal grid lines and vertical comparison bars
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height
      val bottomMargin = 22.dp.toPx()
      val topMargin = 22.dp.toPx()
      val chartHeight = h - bottomMargin - topMargin

      // Horizontal subtle gridlines
      drawLine(
        color = Color.LightGray.copy(alpha = 0.35f),
        start = Offset(0f, topMargin),
        end = Offset(w, topMargin),
        strokeWidth = 1.dp.toPx()
      )
      drawLine(
        color = Color.LightGray.copy(alpha = 0.25f),
        start = Offset(0f, topMargin + (chartHeight * 0.5f)),
        end = Offset(w, topMargin + (chartHeight * 0.5f)),
        strokeWidth = 1.dp.toPx()
      )
      drawLine(
        color = Color.LightGray.copy(alpha = 0.45f),
        start = Offset(0f, h - bottomMargin),
        end = Offset(w, h - bottomMargin),
        strokeWidth = 1.5.dp.toPx()
      )

      val barCount = barItems.size
      val slotWidth = w / barCount
      val barWidth = slotWidth * 0.45f

      barItems.forEachIndexed { index, (_, count, brush) ->
        val centerX = index * slotWidth + (slotWidth / 2)
        val barFraction = (count.toFloat() / maxCount).coerceIn(0.08f, 1.0f)
        val barHeight = chartHeight * barFraction
        val barLeft = centerX - (barWidth / 2)
        val barTop = (h - bottomMargin) - barHeight

        drawRoundRect(
          brush = brush,
          topLeft = Offset(barLeft, barTop),
          size = Size(barWidth, barHeight),
          cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
        )
      }
    }

    // 2. Value counts above each bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.TopCenter),
      horizontalArrangement = Arrangement.SpaceAround
    ) {
      barItems.forEach { (_, count, _) ->
        Text(
          text = count.toString(),
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          ),
          color = MaterialTheme.colorScheme.onSurface,
          textAlign = TextAlign.Center,
          modifier = Modifier.width(60.dp)
        )
      }
    }

    // 3. Category labels below each bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter),
      horizontalArrangement = Arrangement.SpaceAround
    ) {
      barItems.forEach { (label, _, _) ->
        Text(
          text = label,
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
          ),
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          modifier = Modifier.width(72.dp)
        )
      }
    }
  }
}
