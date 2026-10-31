package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage

@Composable
fun GrievanceSearchBar(
  query: String,
  onQueryChange: (String) -> Unit,
  language: AppLanguage,
  modifier: Modifier = Modifier,
  placeholderText: String? = null
) {
  val defaultPlaceholder = if (language == AppLanguage.ENGLISH) {
    "Search grievances by title or category..."
  } else {
    "ಶೀರ್ಷಿಕೆ ಅಥವಾ ವರ್ಗದ ಮೂಲಕ ಹುಡುಕಿ..."
  }

  OutlinedTextField(
    value = query,
    onValueChange = onQueryChange,
    modifier = modifier
      .fillMaxWidth()
      .testTag("grievance_search_bar")
      .testTag("dashboard_search_input"),
    placeholder = {
      Text(
        text = placeholderText ?: defaultPlaceholder,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    },
    leadingIcon = {
      Icon(
        imageVector = Icons.Default.Search,
        contentDescription = if (language == AppLanguage.ENGLISH) "Search" else "ಹುಡುಕಿ",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(22.dp)
      )
    },
    trailingIcon = {
      AnimatedVisibility(
        visible = query.isNotEmpty(),
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        IconButton(
          onClick = { onQueryChange("") },
          modifier = Modifier.testTag("clear_search_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = if (language == AppLanguage.ENGLISH) "Clear search" else "ತೆರವುಗೊಳಿಸಿ",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    },
    shape = RoundedCornerShape(16.dp),
    colors = OutlinedTextFieldDefaults.colors(
      focusedContainerColor = MaterialTheme.colorScheme.surface,
      unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
      focusedBorderColor = MaterialTheme.colorScheme.primary,
      unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
    ),
    singleLine = true
  )
}
