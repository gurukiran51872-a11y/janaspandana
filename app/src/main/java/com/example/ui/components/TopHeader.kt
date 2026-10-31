package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage

@Composable
fun JanaspandanaTopHeader(
  language: AppLanguage,
  isDarkMode: Boolean,
  onLanguageChange: (AppLanguage) -> Unit,
  onToggleDarkMode: () -> Unit,
  onProfileClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var langMenuExpanded by remember { mutableStateOf(false) }

  Surface(
    modifier = modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
    tonalElevation = 2.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // App Logo, Title and Tagline
      Row(
        modifier = Modifier
          .weight(1f)
          .testTag("app_header_title_col"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        androidx.compose.foundation.Image(
          painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.janaspandana_logo_1793451111247),
          contentDescription = "Janaspandana Logo",
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .testTag("app_header_logo")
        )

        Column {
          Text(
            text = "janaspandana",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = (-0.5).sp
            ),
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = if (language == AppLanguage.ENGLISH) 
              "Your Voice, Our Responsibility" 
            else 
              "ನಿಮ್ಮ ಧ್ವನಿ, ನಮ್ಮ ಜವಾಬ್ದಾರಿ",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Actions: Language selector, Dark mode toggle, Profile Avatar
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Language Dropdown Selector
        Box {
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(MaterialTheme.colorScheme.surfaceContainerHigh)
              .clickable { langMenuExpanded = true }
              .padding(horizontal = 8.dp, vertical = 5.dp)
              .testTag("language_selector_btn"),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = language.code,
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
              imageVector = Icons.Default.ArrowDropDown,
              contentDescription = "Select Language",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
          }

          DropdownMenu(
            expanded = langMenuExpanded,
            onDismissRequest = { langMenuExpanded = false }
          ) {
            DropdownMenuItem(
              text = {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text("English (EN)", fontWeight = if (language == AppLanguage.ENGLISH) FontWeight.Bold else FontWeight.Normal)
                  if (language == AppLanguage.ENGLISH) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                  }
                }
              },
              onClick = {
                onLanguageChange(AppLanguage.ENGLISH)
                langMenuExpanded = false
              }
            )
            DropdownMenuItem(
              text = {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text("ಕನ್ನಡ (KN)", fontWeight = if (language == AppLanguage.KANNADA) FontWeight.Bold else FontWeight.Normal)
                  if (language == AppLanguage.KANNADA) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                  }
                }
              },
              onClick = {
                onLanguageChange(AppLanguage.KANNADA)
                langMenuExpanded = false
              }
            )
          }
        }

        // Dark Mode Toggle
        IconButton(
          onClick = onToggleDarkMode,
          modifier = Modifier
            .size(40.dp)
            .testTag("dark_mode_toggle_btn")
        ) {
          Icon(
            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
            contentDescription = "Toggle Dark Mode",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Citizen Profile Avatar
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable { onProfileClick() }
            .testTag("profile_avatar_btn"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Citizen Profile",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}


