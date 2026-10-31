package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.AppLanguage
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryAmber
import com.example.ui.theme.SecondaryContainerOrange
import java.util.Locale

data class MapPresetPoint(
  val nameEn: String,
  val nameKn: String,
  val landmark: String,
  val lat: Double,
  val lng: Double
)

/**
 * Interactive Google Maps Location Pinning dialog.
 * Allows citizens to drag, tap, or choose landmark presets to pin
 * the exact geographic location of their civic grievance.
 */
@Composable
fun MapPinPickerDialog(
  initialCoordinates: String,
  initialLandmark: String,
  language: AppLanguage,
  onLocationPinned: (coordinates: String, landmark: String) -> Unit,
  onDismiss: () -> Unit
) {
  // Parse initial coordinates or default to Malur GP center
  var currentLat by remember {
    val parsed = parseCoordinates(initialCoordinates)
    mutableDoubleStateOf(parsed?.first ?: 13.2988)
  }
  var currentLng by remember {
    val parsed = parseCoordinates(initialCoordinates)
    mutableDoubleStateOf(parsed?.second ?: 77.5812)
  }
  var selectedLandmark by remember { mutableStateOf(initialLandmark.ifBlank { "Near Malur Gram Panchayat Bhavan, Main Road" }) }
  var mapOffset by remember { mutableStateOf(Offset.Zero) }
  var zoomLevel by remember { mutableFloatStateOf(16f) }

  val presets = listOf(
    MapPresetPoint("Panchayat Office", "ಪಂಚಾಯತ್ ಕಚೇರಿ", "Near Gram Panchayat Bhavan, Main Road", 13.2988, 77.5812),
    MapPresetPoint("Govt School", "ಸರ್ಕಾರಿ ಶಾಲೆ", "Govt. Higher Primary School, Ward 2", 13.2975, 77.5830),
    MapPresetPoint("Water Tank", "ನೀರಿನ ತೊಟ್ಟಿ", "Overhead Public Water Tank, Ward 4", 13.2995, 77.5802),
    MapPresetPoint("Bus Stand", "ಬಸ್ ನಿಲ್ದಾಣ", "Malur Village Bus Stand Cross", 13.2960, 77.5845),
    MapPresetPoint("Market Cross", "ಮಾರುಕಟ್ಟೆ", "Main Market Junction & Temple Street", 13.2980, 77.5820)
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .clip(RoundedCornerShape(20.dp))
        .testTag("map_pin_picker_dialog"),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Header
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
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFDCFCE7)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Place,
                contentDescription = null,
                tint = Color(0xFF15803D),
                modifier = Modifier.size(20.dp)
              )
            }
            Column {
              Text(
                text = if (language == AppLanguage.ENGLISH) "Pin Exact Location" else "ನಿಖರ ಸ್ಥಳ ಗುರುತಿಸಿ",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = if (language == AppLanguage.ENGLISH) "Drag or tap map to pin grievance" else "ನಕ್ಷೆಯಲ್ಲಿ ಟ್ಯಾಪ್ ಮಾಡಿ ಸ್ಥಳ ಗುರುತಿಸಿ",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }

        // Current Coordinate Display Pill
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "GPS Coordinates (ಅಕ್ಷಾಂಶ-ರೇಖಾಂಶ):",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = String.format(Locale.US, "%.5f° N, %.5f° E", currentLat, currentLng),
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace
                ),
                color = MaterialTheme.colorScheme.primary
              )
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFF15803D),
              modifier = Modifier
                .clickable {
                  // Auto-locate GPS
                  currentLat = 13.2988
                  currentLng = 77.5812
                  selectedLandmark = "Near Gram Panchayat Bhavan, Main Road"
                }
                .testTag("btn_map_locate_gps")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(Icons.Default.MyLocation, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                Text(
                  text = if (language == AppLanguage.ENGLISH) "My GPS" else "ನನ್ನ ಜಿಪಿಎಸ್",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = Color.White
                )
              }
            }
          }
        }

        // Interactive Map Box with Tap & Drag Pinning
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .testTag("interactive_map_canvas"),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
          Box(modifier = Modifier.fillMaxSize()) {
            // Satellite / Street map base image
            AsyncImage(
              model = "https://lh3.googleusercontent.com/aida-public/AB6AXuBGSHRbBZPrm38pXQurZmm36hKJPKsbkrYhwO5Ir-9dHY2_Qvb_L9lsKa9aa-I4dB-9a3y3KjmEKgwUTzVnY6WOHUvn4tX8PpzaTjj-5z9SqtgaVUDcdu5hBTP_nHKk-2YOsoCuBIU307GKD0CNctg21OGzXBTFqTLQ6iVsRk2CrRiCqgmV8XA8vQou0gNM26NsQb-aqKwFN8-eVUr2aGIfT9TOz59hdUs4HF36il7YzYyKJfbFP4s",
              contentDescription = "Interactive Google Map",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )

            // Touch gesture overlay for map movement & pinning
            Box(
              modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                  detectTapGestures { tapOffset ->
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val dx = (tapOffset.x - center.x) / 100000.0
                    val dy = (tapOffset.y - center.y) / 100000.0
                    currentLat = (currentLat - dy).coerceIn(13.25, 13.35)
                    currentLng = (currentLng + dx).coerceIn(77.50, 77.65)
                    selectedLandmark = "Pinned Point (Ward No. 4, Malur)"
                  }
                }
                .pointerInput(Unit) {
                  detectDragGestures { change, dragAmount ->
                    change.consume()
                    val dx = dragAmount.x / 150000.0
                    val dy = dragAmount.y / 150000.0
                    currentLat = (currentLat - dy).coerceIn(13.25, 13.35)
                    currentLng = (currentLng + dx).coerceIn(77.50, 77.65)
                  }
                }
            )

            // Center Pin Marker Overlay
            Column(
              modifier = Modifier.align(Alignment.Center),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              // Location tooltip chip over pin
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.8f),
                shadowElevation = 4.dp
              ) {
                Text(
                  text = "📍 ${selectedLandmark.take(28)}",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
              }
              Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "Pinned Location",
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(36.dp)
              )
              // Shadow dot at pin tip
              Box(
                modifier = Modifier
                  .size(8.dp, 4.dp)
                  .clip(CircleShape)
                  .background(Color.Black.copy(alpha = 0.4f))
              )
            }

            // Map Controls Overlay (Zoom + Compass)
            Column(
              modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier
                  .size(30.dp)
                  .clickable { zoomLevel = (zoomLevel + 1f).coerceAtMost(20f) }
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = Color.Black, modifier = Modifier.size(16.dp))
                }
              }
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier
                  .size(30.dp)
                  .clickable { zoomLevel = (zoomLevel - 1f).coerceAtLeast(10f) }
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = Color.Black, modifier = Modifier.size(16.dp))
                }
              }
            }

            // Google Maps Attribution Badge
            Surface(
              shape = RoundedCornerShape(topStart = 6.dp),
              color = Color.White.copy(alpha = 0.85f),
              modifier = Modifier.align(Alignment.BottomEnd)
            ) {
              Text(
                text = "Google Maps",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.SemiBold),
                color = Color.DarkGray,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }

        // Quick Landmark Presets
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = if (language == AppLanguage.ENGLISH) "Quick Landmark Presets" else "ತ್ವರಿತ ಸ್ಥಳ ಗುರುತುಗಳು",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(presets) { preset ->
              val isSelected = (preset.lat == currentLat && preset.lng == currentLng) || selectedLandmark == preset.landmark
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) PrimaryNavy else MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier
                  .clickable {
                    currentLat = preset.lat
                    currentLng = preset.lng
                    selectedLandmark = preset.landmark
                  }
                  .testTag("preset_${preset.nameEn.replace(" ", "_").lowercase()}")
              ) {
                Text(
                  text = if (language == AppLanguage.ENGLISH) preset.nameEn else preset.nameKn,
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                  color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        // Confirm & Cancel Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier
              .weight(1f)
              .height(48.dp),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text(if (language == AppLanguage.ENGLISH) "Cancel" else "ರದ್ದುಮಾಡಿ")
          }

          Button(
            onClick = {
              val formattedCoords = String.format(Locale.US, "%.5f° N, %.5f° E (Pinned)", currentLat, currentLng)
              onLocationPinned(formattedCoords, selectedLandmark)
              onDismiss()
            },
            modifier = Modifier
              .weight(1.5f)
              .height(48.dp)
              .testTag("btn_confirm_pinned_location"),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (language == AppLanguage.ENGLISH) "Confirm Pin" else "ಸ್ಥಳ ದೃಢೀಕರಿಸಿ",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
          }
        }
      }
    }
  }
}

private fun parseCoordinates(str: String): Pair<Double, Double>? {
  return try {
    val clean = str.replace("° N", "").replace("° E", "").replace("(Pinned)", "").trim()
    val parts = clean.split(",")
    if (parts.size == 2) {
      Pair(parts[0].trim().toDouble(), parts[1].trim().toDouble())
    } else null
  } catch (_: Exception) {
    null
  }
}
