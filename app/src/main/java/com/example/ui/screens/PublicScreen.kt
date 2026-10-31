package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.EditRoad
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.MonthlyTrendItem
import com.example.data.model.VillageStat
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryAmber

@Composable
fun PublicScreen(
  villageStats: List<VillageStat>,
  monthlyTrends: List<MonthlyTrendItem>,
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("public_screen_scroll"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Top Header Strip
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "KARNATAKA GRAM PANCHAYAT TRANSPARENCY",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.8.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFDCFCE7)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(12.dp))
              Text(
                text = "Live Data",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF15803D)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = if (language == AppLanguage.ENGLISH) 
            "Public Governance Dashboard" 
          else 
            "ಸಾರ್ವಜನಿಕ ಆಡಳಿತ ಡ್ಯಾಶ್‌ಬೋರ್ಡ್",
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )

        Text(
          text = if (language == AppLanguage.ENGLISH)
            "Real-time aggregated metrics ensuring transparency, accountability, and citizen trust across all regional districts."
          else
            "ಎಲ್ಲ ಪ್ರಾದೇಶಿಕ ಜಿಲ್ಲೆಗಳಲ್ಲಿ ಪಾರದರ್ಶಕತೆ, ಹೊಣೆಗಾರಿಕೆ ಮತ್ತು ನಾಗರಿಕ ವಿಶ್ವಾಸವನ್ನು ಖಾತರಿಪಡಿಸುವ ನೈಜ ಸಮಯದ ಅಂಕಿಅಂಶಗಳು.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // 2. Key Metrics Bento Grid
    item {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Total Grievances
          Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = if (language == AppLanguage.ENGLISH) "Total Grievances" else "ಒಟ್ಟು ದೂರುಗಳು",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(PrimaryBlue.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Assignment, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = "24,852",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )

              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
              ) {
                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(14.dp))
                Text(
                  text = "+12% this month",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color(0xFF15803D)
                )
              }
            }
          }

          // Resolution Rate
          Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = if (language == AppLanguage.ENGLISH) "Resolved Rate" else "ಪರಿಹಾರ ದರ",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(SecondaryAmber.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.TaskAlt, contentDescription = null, tint = SecondaryAmber, modifier = Modifier.size(16.dp))
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = "91.4%",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = SecondaryAmber
              )

              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
              ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(14.dp))
                Text(
                  text = "22,714 closed",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color(0xFF15803D)
                )
              }
            }
          }
        }

        // Avg Resolution Time (Full width)
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
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
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = PrimaryNavy, modifier = Modifier.size(20.dp))
              }
              Column {
                Text(
                  text = "Avg. Resolution Time",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = "3.8 Days",
                  style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }

            Column(horizontalAlignment = Alignment.End) {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFDCFCE7)
              ) {
                Text(
                  text = "SLA Target: 5 Days",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = Color(0xFF15803D),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "1.2 days faster than avg",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }

    // 3. Category Breakdown (Live Aggregates with Progress Bars)
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (language == AppLanguage.ENGLISH) "Category Breakdown" else "ವರ್ಗವಾರು ವಿಂಗಡಣೆ",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              text = "Live Aggregates",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Text(
            text = "Distribution of civic complaints categorized by infrastructure domain.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          // 1: Water
          CategoryProgressRow(
            label = "Water Supply & Pipelines",
            count = "8,420 (34%)",
            progress = 0.34f,
            color = PrimaryNavy,
            icon = Icons.Default.WaterDrop
          )

          // 2: Roads
          CategoryProgressRow(
            label = "Village Roads & Paving",
            count = "6,210 (25%)",
            progress = 0.25f,
            color = SecondaryAmber,
            icon = Icons.Default.EditRoad
          )

          // 3: Streetlights
          CategoryProgressRow(
            label = "Streetlights & Electrical",
            count = "4,970 (20%)",
            progress = 0.20f,
            color = PrimaryBlue,
            icon = Icons.Default.Lightbulb
          )

          // 4: Sanitation
          CategoryProgressRow(
            label = "Sanitation & Waste",
            count = "5,252 (21%)",
            progress = 0.21f,
            color = Color(0xFF15803D),
            icon = Icons.Default.Eco
          )
        }
      }
    }

    // 4. Monthly Trend (2024-25) Bar Chart
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Monthly Trend (2024-25)",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
              Text(
                text = "Last 6 Months",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Text(
            text = "Comparison of incoming vs. resolved public grievances per month.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(4.dp))

          // Native Canvas Bar Chart
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(160.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(MaterialTheme.colorScheme.surface)
              .padding(horizontal = 12.dp, vertical = 10.dp)
          ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
              val w = size.width
              val h = size.height
              val maxVal = 5500f
              val barGroupCount = monthlyTrends.size
              val groupWidth = w / barGroupCount
              val singleBarWidth = groupWidth * 0.28f

              // Baseline and grid line
              drawLine(
                color = Color.LightGray.copy(alpha = 0.4f),
                start = Offset(0f, h * 0.33f),
                end = Offset(w, h * 0.33f),
                strokeWidth = 1f
              )
              drawLine(
                color = Color.LightGray.copy(alpha = 0.4f),
                start = Offset(0f, h * 0.66f),
                end = Offset(w, h * 0.66f),
                strokeWidth = 1f
              )

              monthlyTrends.forEachIndexed { i, item ->
                val groupCenterX = i * groupWidth + (groupWidth / 2)
                val recHeight = (item.received / maxVal) * (h - 24.dp.toPx())
                val resHeight = (item.resolved / maxVal) * (h - 24.dp.toPx())

                val recX = groupCenterX - singleBarWidth - 2.dp.toPx()
                val resX = groupCenterX + 2.dp.toPx()
                val bottomY = h - 20.dp.toPx()

                // Received bar (Navy)
                drawRoundRect(
                  color = PrimaryBlue,
                  topLeft = Offset(recX, bottomY - recHeight),
                  size = Size(singleBarWidth, recHeight),
                  cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )

                // Resolved bar (Amber)
                drawRoundRect(
                  color = SecondaryAmber,
                  topLeft = Offset(resX, bottomY - resHeight),
                  size = Size(singleBarWidth, resHeight),
                  cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
              }
            }

            // Month Labels Row below canvas
            Row(
              modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceAround
            ) {
              monthlyTrends.forEach {
                Text(
                  text = it.month,
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          // Legend
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(PrimaryBlue))
              Text("Received Grievances", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(SecondaryAmber))
              Text("Resolved Grievances", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
        }
      }
    }

    // 5. Village-wise Statistics Table
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (language == AppLanguage.ENGLISH) "Village-wise Statistics" else "ಗ್ರಾಮವಾರು ಅಂಕಿಅಂಶ",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              text = "Top Panchayats",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Text(
            text = "Performance metrics per Gram Panchayat jurisdiction. Personal citizen data is fully anonymized.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            villageStats.forEach { stat ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                  ) {
                    Box(
                      modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.15f)),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = stat.initials,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryNavy
                      )
                    }

                    Column {
                      Text(
                        text = stat.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                      )
                      Text(
                        text = stat.district,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }

                  Column(horizontalAlignment = Alignment.End) {
                    Text(
                      text = "${stat.resolvedPercent}% Resolved",
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                      color = SecondaryAmber
                    )
                    Text(
                      text = "${stat.totalCases} total",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    // 6. Data Privacy Assurance Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.Top,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Icon(
            imageVector = Icons.Default.VerifiedUser,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = if (language == AppLanguage.ENGLISH) "Data Privacy Assurance" else "ದತ್ತಾಂಶ ಗೌಪ್ಯತೆಯ ಭರವಸೆ",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              text = if (language == AppLanguage.ENGLISH)
                "In accordance with national digital governance frameworks, all citizen identifiers, contact numbers, and personal details are strictly masked and stored with end-to-end encryption. Only statistical aggregates are displayed publicly."
              else
                "ರಾಷ್ಟ್ರೀಯ ಡಿಜಿಟಲ್ ಆಡಳಿತ ನಿಯಮಗಳಂತೆ, ನಾಗರಿಕರ ಗುರುತುಗಳು, ಮೊಬೈಲ್ ಸಂಖ್ಯೆಗಳು ಹಾಗೂ ವೈಯಕ್ತಿಕ ವಿವರಗಳನ್ನು ಸಂಪೂರ್ಣ ಎನ್‌ಕ್ರಿಪ್ಶನ್ ಮೂಲಕ ಸುರಕ್ಷಿತಗೊಳಿಸಲಾಗಿದ್ದು, ಕೇವಲ ಅಂಕಿಅಂಶಗಳನ್ನು ಮಾತ್ರ ಪ್ರದರ್ಶಿಸಲಾಗುತ್ತದೆ.",
              style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
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
private fun CategoryProgressRow(
  label: String,
  count: String,
  progress: Float,
  color: Color,
  icon: androidx.compose.ui.graphics.vector.ImageVector
) {
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Text(
          text = label,
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
          color = MaterialTheme.colorScheme.onSurface
        )
      }
      Text(
        text = count,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    LinearProgressIndicator(
      progress = { progress },
      modifier = Modifier
        .fillMaxWidth()
        .height(8.dp)
        .clip(RoundedCornerShape(4.dp)),
      color = color,
      trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )
  }
}
