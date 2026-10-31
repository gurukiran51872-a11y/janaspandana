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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.EditRoad
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MiscellaneousServices
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Soap
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.Grievance
import com.example.data.model.NoticeItem
import com.example.ui.components.GrievanceStatusBadge
import com.example.ui.components.GrievanceStatusChart
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryAmber
import com.example.ui.theme.SecondaryContainerOrange

data class ServiceCategoryItem(
  val nameEn: String,
  val nameKn: String,
  val icon: ImageVector,
  val iconColor: Color,
  val iconBgColor: Color
)

@Composable
fun HomeScreen(
  language: AppLanguage,
  notices: List<NoticeItem>,
  onLanguageSelect: (AppLanguage) -> Unit,
  onSubmitComplaintClick: () -> Unit,
  onTrackComplaintClick: () -> Unit,
  onCategorySelect: (String) -> Unit,
  modifier: Modifier = Modifier,
  recentGrievances: List<Grievance> = emptyList(),
  onGrievanceClick: (Grievance) -> Unit = {}
) {
  val categories = listOf(
    ServiceCategoryItem("Water Supply", "ಕುಡಿಯುವ ನೀರು", Icons.Default.WaterDrop, Color(0xFF1E3A8A), Color(0xFFE0E7FF)),
    ServiceCategoryItem("Roads", "ರಸ್ತೆಗಳು", Icons.Default.EditRoad, Color(0xFF8F4E00), Color(0xFFFFEDD5)),
    ServiceCategoryItem("Streetlights", "ಬೀದಿದೀಪಗಳು", Icons.Default.Lightbulb, Color(0xFF2563EB), Color(0xFFDBEAFE)),
    ServiceCategoryItem("Drainage", "ಚರಂಡಿ", Icons.Default.Plumbing, Color(0xFF4338CA), Color(0xFFE0E7FF)),
    ServiceCategoryItem("Garbage", "ಕಸ ವಿಲೇವಾರಿ", Icons.Default.Delete, Color(0xFF475569), Color(0xFFF1F5F9)),
    ServiceCategoryItem("Sanitation", "ಸ್ವಚ್ಛತೆ", Icons.Default.Soap, Color(0xFF0F766E), Color(0xFFCCFBF1)),
    ServiceCategoryItem("Infrastructure", "ಮೂಲಸೌಕರ್ಯ", Icons.Default.Domain, Color(0xFF7C3AED), Color(0xFFEDE9FE)),
    ServiceCategoryItem("Health", "ಆರೋಗ್ಯ", Icons.Default.LocalHospital, Color(0xFFBE123C), Color(0xFFFFE4E6)),
    ServiceCategoryItem("Services", "ಸೇವೆಗಳು", Icons.Default.MiscellaneousServices, Color(0xFF1D4ED8), Color(0xFFDBEAFE)),
    ServiceCategoryItem("Other Issues", "ಇತರ ಸಮಸ್ಯೆಗಳು", Icons.Default.MoreHoriz, Color(0xFF475569), Color(0xFFF1F5F9))
  )

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("home_screen_scroll"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Language Switch Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
          .testTag("lang_switch_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Translate,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "ಬದಲಾಯಿಸಿ / Switch Language",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
              )
              Text(
                text = "View portal in Kannada or English",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f)
              )
            }
          }

          // Toggle Buttons
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(Color.White.copy(alpha = 0.15f))
              .padding(2.dp)
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (language == AppLanguage.ENGLISH) Color.White else Color.Transparent)
                .clickable { onLanguageSelect(AppLanguage.ENGLISH) }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = "EN",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (language == AppLanguage.ENGLISH) PrimaryBlue else Color.White
              )
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (language == AppLanguage.KANNADA) Color.White else Color.Transparent)
                .clickable { onLanguageSelect(AppLanguage.KANNADA) }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = "ಕನ್ನಡ",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (language == AppLanguage.KANNADA) PrimaryBlue else Color.White
              )
            }
          }
        }
      }
    }

    // 2. Hero Banner Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("hero_banner_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryNavy)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.verticalGradient(
                colors = listOf(PrimaryNavy, PrimaryBlue)
              )
            )
            .padding(20.dp)
        ) {
          // Tag
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.15f)
          ) {
            Text(
              text = if (language == AppLanguage.ENGLISH) 
                "Karnataka Gram Panchayat Portal" 
              else 
                "ಕರ್ನಾಟಕ ಗ್ರಾಮ ಪಂಚಾಯತಿ ಪೋರ್ಟಲ್",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
              color = Color.White.copy(alpha = 0.9f),
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = if (language == AppLanguage.ENGLISH) 
              "Raise Your Voice. Improve Your Village." 
            else 
              "ನಿಮ್ಮ ಧ್ವನಿ ಎತ್ತಿ. ನಿಮ್ಮ ಗ್ರಾಮ ಸುಧಾರಿಸಿ.",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Bold,
              lineHeight = 32.sp
            ),
            color = Color.White
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = if (language == AppLanguage.ENGLISH)
              "Your direct link to local administration. Report civic issues, track progress in real-time, and build a better community together."
            else
              "ಸ್ಥಳೀಯ ಆಡಳಿತದೊಂದಿಗೆ ನಿಮ್ಮ ನೇರ ಸಂಪರ್ಕ. ನಾಗರಿಕ ಸಮಸ್ಯೆಗಳನ್ನು ವರದಿ ಮಾಡಿ, ನೈಜ ಸಮಯದಲ್ಲಿ ಪ್ರಗತಿ ಪರಿಶೀಲಿಸಿ.",
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
            color = Color.White.copy(alpha = 0.85f)
          )

          Spacer(modifier = Modifier.height(20.dp))

          // Primary Actions
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
              onClick = onSubmitComplaintClick,
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("hero_submit_complaint_btn"),
              colors = ButtonDefaults.buttonColors(containerColor = SecondaryAmber),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (language == AppLanguage.ENGLISH) "Submit a Complaint" else "ದೂರು ಸಲ್ಲಿಸಿ",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White
              )
            }

            OutlinedButton(
              onClick = onTrackComplaintClick,
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("hero_track_complaint_btn"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White.copy(alpha = 0.1f)
              ),
              border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.4f), Color.White.copy(alpha = 0.4f)))
              )
            ) {
              Icon(Icons.Default.Sync, contentDescription = null, tint = Color.White)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (language == AppLanguage.ENGLISH) "Track Your Complaint" else "ದೂರಿನ ಸ್ಥಿತಿ ತಿಳಿಯಿರಿ",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White
              )
            }

            // Voice Accessibility Feature Banner
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color.White.copy(alpha = 0.12f),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onSubmitComplaintClick() }
                .testTag("home_voice_banner")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Mic,
                  contentDescription = null,
                  tint = SecondaryAmber,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = if (language == AppLanguage.ENGLISH)
                    "Voice Reporting Available: Speak in Kannada (ಕನ್ನಡ) or English"
                  else
                    "ಧ್ವನಿ ದೂರು ಲಭ್ಯ: ಕನ್ನಡ ಅಥವಾ ಇಂಗ್ಲಿಷ್‌ನಲ್ಲಿ ಮಾತನಾಡಿ ದೂರು ದಾಖಲಿಸಿ",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color.White.copy(alpha = 0.95f)
                )
              }
            }
          }
        }
      }
    }

    // 3. Visual Summary Section: Grievance Status Bar Chart (Pending vs. Resolved items)
    item {
      GrievanceStatusChart(
        grievances = recentGrievances,
        language = language
      )
    }

    // 4. Live Panchayat Stats Header & 2x2 Bento Grid
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (language == AppLanguage.ENGLISH) "Live Panchayat Stats" else "ನೈಜ ಪಂಚಾಯತ್ ಅಂಕಿಅಂಶ",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFDCFCE7)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF16A34A))
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (language == AppLanguage.ENGLISH) "Live Updates" else "ನೈಜ ಅಪ್‌ಡೇಟ್",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = Color(0xFF15803D)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2x2 Bento Grid
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          BentoStatCard(
            title = if (language == AppLanguage.ENGLISH) "Total Filed" else "ಒಟ್ಟು ದಾಖಲು",
            value = "1,248",
            subtitle = "+12 today",
            subtitleColor = Color(0xFF15803D),
            icon = Icons.Default.Folder,
            iconColor = Color(0xFF1E3A8A),
            modifier = Modifier.weight(1f)
          )
          BentoStatCard(
            title = if (language == AppLanguage.ENGLISH) "Pending" else "ಬಾಕಿ ಉಳಿದಿದೆ",
            value = "142",
            subtitle = "Action required",
            subtitleColor = SecondaryAmber,
            icon = Icons.Default.HourglassEmpty,
            iconColor = SecondaryAmber,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          BentoStatCard(
            title = if (language == AppLanguage.ENGLISH) "Resolved" else "ಪರಿಹರಿಸಲಾಗಿದೆ",
            value = "1,010",
            subtitle = "81% success rate",
            subtitleColor = Color(0xFF15803D),
            icon = Icons.Default.CheckCircle,
            iconColor = Color(0xFF15803D),
            modifier = Modifier.weight(1f)
          )
          BentoStatCard(
            title = if (language == AppLanguage.ENGLISH) "In Review" else "ಪರಿಶೀಲನೆಯಲ್ಲಿದೆ",
            value = "96",
            subtitle = "Being inspected",
            subtitleColor = Color(0xFF2563EB),
            icon = Icons.Default.QuestionAnswer,
            iconColor = Color(0xFF2563EB),
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // 4. Service Categories Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (language == AppLanguage.ENGLISH) "Service Categories" else "ಸೇವಾ ವಿಭಾಗಗಳು",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = if (language == AppLanguage.ENGLISH) "View All" else "ಎಲ್ಲ ನೋಡಿ",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.clickable { }
        )
      }
    }

    // Grid of Categories (2 per row)
    items(categories.chunked(2)) { rowCategories ->
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        rowCategories.forEach { category ->
          Card(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(14.dp))
              .clickable { onCategorySelect(category.nameEn) }
              .testTag("category_card_${category.nameEn.replace(" ", "_")}"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            shape = RoundedCornerShape(14.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 12.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Box(
                modifier = Modifier
                  .size(46.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(category.iconBgColor),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = category.icon,
                  contentDescription = category.nameEn,
                  tint = category.iconColor,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = if (language == AppLanguage.ENGLISH) category.nameEn else category.nameKn,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
        if (rowCategories.size == 1) {
          Spacer(modifier = Modifier.weight(1f))
        }
      }
    }

    // 5. Recent Resident Grievances (Live list with Color-Coded Status Badges)
    if (recentGrievances.isNotEmpty()) {
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (language == AppLanguage.ENGLISH) "Recent Grievances" else "ಇತ್ತೀಚಿನ ದೂರುಗಳು",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = if (language == AppLanguage.ENGLISH) "Track All" else "ಎಲ್ಲ ಟ್ರ್ಯಾಕ್ ಮಾಡಿ",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onTrackComplaintClick() }
          )
        }
      }

      items(recentGrievances.take(3)) { grievance ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onGrievanceClick(grievance) }
            .testTag("home_grievance_card_${grievance.id}"),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
          shape = RoundedCornerShape(14.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "#${grievance.id} • ${grievance.category}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              // Prominent Color-coded Status Badge
              GrievanceStatusBadge(
                status = grievance.status,
                language = language,
                isCompact = true
              )
            }

            Text(
              text = grievance.title,
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = grievance.locationName.ifBlank { grievance.village },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                modifier = Modifier.weight(1f)
              )
              Text(
                text = grievance.submittedDate,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }

    // 6. Notices & Announcements Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = if (language == AppLanguage.ENGLISH) "Panchayat Notices &" else "ಪಂಚಾಯತಿ ಪ್ರಕಟಣೆಗಳು ಮತ್ತು",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = if (language == AppLanguage.ENGLISH) "Updates" else "ಮಾಹಿತಿಗಳು",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }
        Text(
          text = if (language == AppLanguage.ENGLISH) "Latest Announcements" else "ಇತ್ತೀಚಿನ ಪ್ರಕಟಣೆಗಳು",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    items(notices) { notice ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("notice_card_${notice.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        shape = RoundedCornerShape(14.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.Top
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(if (notice.isAlert) SecondaryContainerOrange else PrimaryBlue),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (notice.isAlert) Icons.Default.Campaign else Icons.Default.WaterDrop,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (language == AppLanguage.ENGLISH) notice.tag else notice.tagKn,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.secondary
              )
              Text(
                text = notice.timeAgo,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
              text = if (language == AppLanguage.ENGLISH) notice.title else notice.titleKn,
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = if (language == AppLanguage.ENGLISH) notice.snippet else notice.snippetKn,
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
private fun BentoStatCard(
  title: String,
  value: String,
  subtitle: String,
  subtitleColor: Color,
  icon: ImageVector,
  iconColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
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
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconColor,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = value,
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall,
        color = subtitleColor
      )
    }
  }
}
