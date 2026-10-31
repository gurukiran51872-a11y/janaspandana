package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.GrievanceStatus

data class StatusBadgeColors(
  val containerColor: Color,
  val contentColor: Color,
  val borderColor: Color,
  val icon: ImageVector
)

/**
 * Returns color-coded styling for grievance statuses:
 * - Pending: Amber container with amber text and hourglass icon
 * - In Progress: Sky/Blue container with deep blue text and engineering icon
 * - Resolved: Emerald/Green container with deep green text and checkmark icon
 * - Submitted: Slate container with slate text and send icon
 */
fun getStatusBadgeColors(status: GrievanceStatus): StatusBadgeColors {
  return when (status) {
    GrievanceStatus.PENDING -> StatusBadgeColors(
      containerColor = Color(0xFFFEF3C7),
      contentColor = Color(0xFFB45309),
      borderColor = Color(0xFFFDE68A),
      icon = Icons.Default.HourglassEmpty
    )
    GrievanceStatus.IN_PROGRESS -> StatusBadgeColors(
      containerColor = Color(0xFFE0F2FE),
      contentColor = Color(0xFF0369A1),
      borderColor = Color(0xFFBAE6FD),
      icon = Icons.Default.Engineering
    )
    GrievanceStatus.RESOLVED -> StatusBadgeColors(
      containerColor = Color(0xFFDCFCE7),
      contentColor = Color(0xFF15803D),
      borderColor = Color(0xFFBBF7D0),
      icon = Icons.Default.CheckCircle
    )
    GrievanceStatus.SUBMITTED -> StatusBadgeColors(
      containerColor = Color(0xFFF1F5F9),
      contentColor = Color(0xFF334155),
      borderColor = Color(0xFFCBD5E1),
      icon = Icons.Default.Send
    )
  }
}

@Composable
fun GrievanceStatusBadge(
  status: GrievanceStatus,
  modifier: Modifier = Modifier,
  language: AppLanguage = AppLanguage.ENGLISH,
  isCompact: Boolean = false
) {
  val colors = getStatusBadgeColors(status)
  val statusText = status.title(language)

  Surface(
    shape = RoundedCornerShape(20.dp),
    color = colors.containerColor,
    border = BorderStroke(1.dp, colors.borderColor),
    modifier = modifier.testTag("status_badge_${status.name.lowercase()}")
  ) {
    Row(
      modifier = Modifier.padding(
        horizontal = if (isCompact) 8.dp else 10.dp,
        vertical = if (isCompact) 3.dp else 5.dp
      ),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Icon(
        imageVector = colors.icon,
        contentDescription = statusText,
        tint = colors.contentColor,
        modifier = Modifier.size(if (isCompact) 12.dp else 14.dp)
      )
      Text(
        text = statusText,
        style = if (isCompact) {
          MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        } else {
          MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        },
        color = colors.contentColor
      )
    }
  }
}
