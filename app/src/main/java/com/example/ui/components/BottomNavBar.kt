package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage
import com.example.ui.viewmodel.AppTab

private data class NavItemConfig(
  val tab: AppTab,
  val labelEn: String,
  val labelKn: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String
)

@Composable
fun JanaspandanaBottomNavBar(
  activeTab: AppTab,
  onTabSelected: (AppTab) -> Unit,
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  val items = listOf(
    NavItemConfig(
      tab = AppTab.HOME,
      labelEn = "Home",
      labelKn = "ಮುಖಪುಟ",
      selectedIcon = Icons.Filled.Home,
      unselectedIcon = Icons.Outlined.Home,
      testTag = "nav_tab_home"
    ),
    NavItemConfig(
      tab = AppTab.SUBMIT,
      labelEn = "Submit",
      labelKn = "ಸಲ್ಲಿಸಿ",
      selectedIcon = Icons.Filled.AddCircle,
      unselectedIcon = Icons.Outlined.AddCircleOutline,
      testTag = "nav_tab_submit"
    ),
    NavItemConfig(
      tab = AppTab.TRACK,
      labelEn = "Track",
      labelKn = "ಟ್ರ್ಯಾಕ್",
      selectedIcon = Icons.Filled.TrackChanges,
      unselectedIcon = Icons.Outlined.TrackChanges,
      testTag = "nav_tab_track"
    ),
    NavItemConfig(
      tab = AppTab.DASHBOARD,
      labelEn = "Dashboard",
      labelKn = "ಡ್ಯಾಶ್‌ಬೋರ್ಡ್",
      selectedIcon = Icons.Filled.Dashboard,
      unselectedIcon = Icons.Outlined.Dashboard,
      testTag = "nav_tab_dashboard"
    ),
    NavItemConfig(
      tab = AppTab.PUBLIC,
      labelEn = "Public",
      labelKn = "ಸಾರ್ವಜನಿಕ",
      selectedIcon = Icons.Filled.Public,
      unselectedIcon = Icons.Outlined.Public,
      testTag = "nav_tab_public"
    )
  )

  NavigationBar(
    modifier = modifier
      .windowInsetsPadding(WindowInsets.navigationBars)
      .testTag("janaspandana_bottom_nav"),
    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
    tonalElevation = 6.dp
  ) {
    items.forEach { item ->
      val selected = activeTab == item.tab
      NavigationBarItem(
        selected = selected,
        onClick = { onTabSelected(item.tab) },
        icon = {
          Icon(
            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.labelEn,
            modifier = Modifier.size(24.dp)
          )
        },
        label = {
          Text(
            text = if (language == AppLanguage.ENGLISH) item.labelEn else item.labelKn,
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
          )
        },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = MaterialTheme.colorScheme.primary,
          selectedTextColor = MaterialTheme.colorScheme.primary,
          indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
          unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
          unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.testTag(item.testTag)
      )
    }
  }
}
