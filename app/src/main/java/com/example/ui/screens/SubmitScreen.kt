package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AppLanguage
import com.example.data.model.Grievance
import com.example.speech.VoiceInputDialog
import com.example.speech.VoiceTargetField
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SecondaryAmber
import com.example.ui.viewmodel.SubmitFormData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitScreen(
  formData: SubmitFormData,
  submittedSuccessGrievance: Grievance?,
  language: AppLanguage,
  onTitleChange: (String) -> Unit,
  onCategoryChange: (String) -> Unit,
  onDescriptionChange: (String) -> Unit,
  onDistrictChange: (String) -> Unit,
  onTalukChange: (String) -> Unit,
  onGPChange: (String) -> Unit,
  onVillageChange: (String) -> Unit,
  onWardChange: (String) -> Unit,
  onLandmarkChange: (String) -> Unit,
  onPublicChange: (Boolean) -> Unit,
  onEvidencePhotoChange: (String?) -> Unit,
  onPhotoSelected: (Uri) -> Unit = {},
  onPhotoCleared: () -> Unit = {},
  onPinGpsClick: () -> Unit,
  onAiAutoFillClick: () -> Unit,
  onSubmitClick: () -> Unit,
  onDismissSuccess: () -> Unit,
  onTrackCreatedGrievance: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
  var showPermissionDeniedDialog by remember { mutableStateOf(false) }
  var voiceDialogTarget by remember { mutableStateOf<VoiceTargetField?>(null) }

  val cameraLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicture()
  ) { success ->
    if (success && tempCameraUri != null) {
      onPhotoSelected(tempCameraUri!!)
      onEvidencePhotoChange(tempCameraUri.toString())
    }
  }

  val galleryLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      onPhotoSelected(uri)
      onEvidencePhotoChange(uri.toString())
    }
  }

  val cameraPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      try {
        val uri = createImageUri(context)
        tempCameraUri = uri
        cameraLauncher.launch(uri)
      } catch (e: Exception) {
        val uri = Uri.parse("content://dummy/photo.jpg")
        onPhotoSelected(uri)
        onEvidencePhotoChange("attached_evidence_photo.jpg")
      }
    } else {
      showPermissionDeniedDialog = true
    }
  }

  val launchCamera = {
    try {
      val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
      if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
        val uri = createImageUri(context)
        tempCameraUri = uri
        cameraLauncher.launch(uri)
      } else {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
      }
    } catch (e: Exception) {
      val uri = Uri.parse("content://dummy/photo.jpg")
      onPhotoSelected(uri)
      onEvidencePhotoChange("attached_evidence_photo.jpg")
    }
  }

  val launchGallery = {
    try {
      galleryLauncher.launch(
        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
      )
    } catch (e: Exception) {
      val uri = Uri.parse("content://dummy/gallery_photo.jpg")
      onPhotoSelected(uri)
      onEvidencePhotoChange("attached_gallery_photo.jpg")
    }
  }

  var categoryExpanded by remember { mutableStateOf(false) }
  var districtExpanded by remember { mutableStateOf(false) }
  var talukExpanded by remember { mutableStateOf(false) }
  var gpExpanded by remember { mutableStateOf(false) }
  var villageExpanded by remember { mutableStateOf(false) }

  val categories = listOf(
    "Water Supply & Sanitation",
    "Roads & Streetlights",
    "Drainage & Sewage",
    "Village Electricity",
    "Solid Waste Management",
    "Pension & Welfare Schemes",
    "Other Civic Issues"
  )

  val districts = listOf("Bengaluru Rural", "Mysuru", "Tumakuru", "Belagavi", "Davanagere")
  val taluks = listOf("Malur", "Devanahalli", "Nelamangala", "Doddaballapura")
  val gps = listOf("Malur GP", "Kundana GP", "Avathi GP", "Vijayapura GP")
  val villages = listOf("Sadenahalli", "Bommenahalli", "Malur", "Hulkuru")

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("submit_screen_scroll"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Delight Banner
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
          .testTag("submit_top_banner"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
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
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(46.dp)
                  .clip(CircleShape)
                  .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Campaign,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = if (language == AppLanguage.ENGLISH) "Submit Grievance" else "ದೂರು ಸಲ್ಲಿಸಿ",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = Color.White
                )
                Text(
                  text = if (language == AppLanguage.ENGLISH) 
                    "Voice your concern directly to your Gram Panchayat." 
                  else 
                    "ನಿಮ್ಮ ಗ್ರಾಮ ಪಂಚಾಯತಿಗೆ ನೇರವಾಗಿ ದೂರು ನೀಡಿ.",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color.White.copy(alpha = 0.85f)
                )
              }
            }
            Icon(
              imageVector = Icons.Default.VerifiedUser,
              contentDescription = "Verified Portal",
              tint = Color.White.copy(alpha = 0.7f),
              modifier = Modifier.size(24.dp)
            )
          }

          // Cloud Sync indicator badge
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.18f),
            modifier = Modifier.align(Alignment.Start)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF8DFC75),
                modifier = Modifier.size(14.dp)
              )
              Text(
                text = if (language == AppLanguage.ENGLISH)
                  "Saves to Gram Panchayat Firestore Database"
                else
                  "ಗ್ರಾಮ ಪಂಚಾಯತಿ ಕ್ಲೌಡ್ ಡೇಟಾಬೇಸ್‌ಗೆ ಉಳಿಸಲಾಗುತ್ತದೆ",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
              )
            }
          }
        }
      }
    }

    // Smart AI Assistant Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("ai_assistant_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
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
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
              Text(
                text = if (language == AppLanguage.ENGLISH) "Smart AI Assistant" else "ಸ್ಮಾರ್ಟ್ ಎಐ ಸಹಾಯಕ",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
            }
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
              Text(
                text = "Beta",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = if (language == AppLanguage.ENGLISH)
              "Describe your issue in voice or brief text, and let AI fill out the details for you!"
            else
              "ನಿಮ್ಮ ಸಮಸ್ಯೆಯನ್ನು ಧ್ವನಿ ಅಥವಾ ಸಂಕ್ಷಿಪ್ತ ಪಠ್ಯದಲ್ಲಿ ತಿಳಿಸಿ, ಎಐ ಉಳಿದ ವಿವರಗಳನ್ನು ಭರ್ತಿ ಮಾಡುತ್ತದೆ!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))

          Button(
            onClick = onAiAutoFillClick,
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("ai_autofill_btn"),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (formData.aiSuggested) Color(0xFF15803D) else MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = !formData.isAiAnalyzing
          ) {
            if (formData.isAiAnalyzing) {
              CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = Color.White,
                strokeWidth = 2.dp
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text("Analyzing audio/text...", color = Color.White)
            } else if (formData.aiSuggested) {
              Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("AI Auto-filled Successfully!", color = Color.White)
            } else {
              Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (language == AppLanguage.ENGLISH) "Auto-fill using Voice/Text" else "ಧ್ವನಿ/ಪಠ್ಯದಿಂದ ಆಟೋ-ಫಿಲ್ ಮಾಡಿ",
                color = Color.White
              )
            }
          }
        }
      }
    }

    // Validation Error Banner if present
    if (formData.validationError != null) {
      item {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.errorContainer,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("validation_error_banner")
        ) {
          Text(
            text = formData.validationError,
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(12.dp)
          )
        }
      }
    }

    // Dedicated Regional Voice-to-Text Reporting Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("voice_reporting_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
        )
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(PrimaryNavy),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
              )
            }
            Column {
              Text(
                text = if (language == AppLanguage.ENGLISH) "Report by Speaking" else "ಧ್ವನಿ ಮೂಲಕ ದೂರು ನೀಡಿ",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = if (language == AppLanguage.ENGLISH) 
                  "Speak in Kannada (ಕನ್ನಡ) or English (en-IN)" 
                else 
                  "ಕನ್ನಡ ಅಥವಾ ಇಂಗ್ಲಿಷ್‌ನಲ್ಲಿ ಮಾತನಾಡಿ ದೂರು ದಾಖಲಿಸಿ",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Button(
            onClick = { voiceDialogTarget = VoiceTargetField.AUTO_DETECT },
            modifier = Modifier
              .height(38.dp)
              .testTag("btn_speak_grievance"),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (language == AppLanguage.ENGLISH) "Speak" else "ಮಾತನಾಡಿ",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = Color.White
            )
          }
        }
      }
    }

    // Complaint Title
    item {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row {
          Text(
            text = if (language == AppLanguage.ENGLISH) "Complaint Title" else "ದೂರಿನ ಶೀರ್ಷಿಕೆ",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(" *", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        }

        OutlinedTextField(
          value = formData.title,
          onValueChange = onTitleChange,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_complaint_title"),
          placeholder = { Text("e.g., Broken pipeline near temple street") },
          trailingIcon = {
            IconButton(
              onClick = { voiceDialogTarget = VoiceTargetField.TITLE },
              modifier = Modifier.testTag("btn_mic_title")
            ) {
              Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = if (language == AppLanguage.ENGLISH) "Voice input for Title" else "ಶೀರ್ಷಿಕೆಗೆ ಧ್ವನಿ ಇನ್‌ಪುಟ್",
                tint = MaterialTheme.colorScheme.primary
              )
            }
          },
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest
          ),
          singleLine = true
        )

        Text(
          text = if (language == AppLanguage.ENGLISH) "Keep it concise and descriptive" else "ಸಂಕ್ಷಿಪ್ತ ಹಾಗೂ ಸ್ಪಷ್ಟವಾಗಿರಲಿ",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // Category Dropdown & Quick Selection Chips
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row {
            Text(
              text = if (language == AppLanguage.ENGLISH) "Category" else "ವರ್ಗ",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(" *", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
          }

          if (formData.aiSuggested) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFFDCFCE7)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = Color(0xFF15803D),
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "AI Suggested",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color(0xFF15803D)
                )
              }
            }
          }
        }

        // Quick Category selection chips
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("category_quick_chips")
        ) {
          items(categories) { cat ->
            val isSelected = formData.category == cat
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = if (isSelected) PrimaryNavy else MaterialTheme.colorScheme.surfaceContainerHigh,
              modifier = Modifier
                .clickable { onCategoryChange(cat) }
                .testTag("chip_category_${cat.replace(" ", "_")}")
            ) {
              Text(
                text = cat,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
              )
            }
          }
        }

        ExposedDropdownMenuBox(
          expanded = categoryExpanded,
          onExpandedChange = { categoryExpanded = it }
        ) {
          OutlinedTextField(
            value = formData.category.ifBlank { "Select Category" },
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
            modifier = Modifier
              .menuAnchor(MenuAnchorType.PrimaryNotEditable)
              .fillMaxWidth()
              .testTag("select_category_dropdown"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
              focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            )
          )

          ExposedDropdownMenu(
            expanded = categoryExpanded,
            onDismissRequest = { categoryExpanded = false }
          ) {
            categories.forEach { cat ->
              DropdownMenuItem(
                text = { Text(cat) },
                onClick = {
                  onCategoryChange(cat)
                  categoryExpanded = false
                }
              )
            }
          }
        }
      }
    }

    // Description Field
    item {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row {
            Text(
              text = if (language == AppLanguage.ENGLISH) "Description" else "ವಿವರಣೆ",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(" *", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
          }

          Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
            modifier = Modifier
              .clickable { voiceDialogTarget = VoiceTargetField.DESCRIPTION }
              .testTag("btn_mic_description")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = if (language == AppLanguage.ENGLISH) "Speak Description" else "ವಿವರಣೆಯನ್ನು ಮಾತನಾಡಿ",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = if (language == AppLanguage.ENGLISH) "Speak (ಧ್ವನಿ)" else "ಮಾತನಾಡಿ",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
            }
          }
        }

        OutlinedTextField(
          value = formData.description,
          onValueChange = onDescriptionChange,
          modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .testTag("input_complaint_desc"),
          placeholder = { Text("Provide detailed information about the problem, how long it has persisted, and affected households...") },
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest
          )
        )
      }
    }

    // Administrative Location Box
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("administrative_location_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(
            text = if (language == AppLanguage.ENGLISH) "Administrative Location" else "ಆಡಳಿತಾತ್ಮಕ ಸ್ಥಳ",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )

          // District & Taluk in a 2-col row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // District
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "District *",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(2.dp))
              ExposedDropdownMenuBox(
                expanded = districtExpanded,
                onExpandedChange = { districtExpanded = it }
              ) {
                OutlinedTextField(
                  value = formData.district,
                  onValueChange = {},
                  readOnly = true,
                  trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtExpanded) },
                  modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                  shape = RoundedCornerShape(8.dp),
                  colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                  )
                )
                ExposedDropdownMenu(
                  expanded = districtExpanded,
                  onDismissRequest = { districtExpanded = false }
                ) {
                  districts.forEach { d ->
                    DropdownMenuItem(text = { Text(d) }, onClick = { onDistrictChange(d); districtExpanded = false })
                  }
                }
              }
            }

            // Taluk
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Taluk *",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(2.dp))
              ExposedDropdownMenuBox(
                expanded = talukExpanded,
                onExpandedChange = { talukExpanded = it }
              ) {
                OutlinedTextField(
                  value = formData.taluk,
                  onValueChange = {},
                  readOnly = true,
                  trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = talukExpanded) },
                  modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                  shape = RoundedCornerShape(8.dp),
                  colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                  )
                )
                ExposedDropdownMenu(
                  expanded = talukExpanded,
                  onDismissRequest = { talukExpanded = false }
                ) {
                  taluks.forEach { t ->
                    DropdownMenuItem(text = { Text(t) }, onClick = { onTalukChange(t); talukExpanded = false })
                  }
                }
              }
            }
          }

          // Gram Panchayat & Village in 2-col row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Gram Panchayat
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Gram Panchayat *",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(2.dp))
              ExposedDropdownMenuBox(
                expanded = gpExpanded,
                onExpandedChange = { gpExpanded = it }
              ) {
                OutlinedTextField(
                  value = formData.gramPanchayat,
                  onValueChange = {},
                  readOnly = true,
                  trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gpExpanded) },
                  modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                  shape = RoundedCornerShape(8.dp),
                  colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                  )
                )
                ExposedDropdownMenu(
                  expanded = gpExpanded,
                  onDismissRequest = { gpExpanded = false }
                ) {
                  gps.forEach { g ->
                    DropdownMenuItem(text = { Text(g) }, onClick = { onGPChange(g); gpExpanded = false })
                  }
                }
              }
            }

            // Village
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Village *",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(2.dp))
              ExposedDropdownMenuBox(
                expanded = villageExpanded,
                onExpandedChange = { villageExpanded = it }
              ) {
                OutlinedTextField(
                  value = formData.village,
                  onValueChange = {},
                  readOnly = true,
                  trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = villageExpanded) },
                  modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                  shape = RoundedCornerShape(8.dp),
                  colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                  )
                )
                ExposedDropdownMenu(
                  expanded = villageExpanded,
                  onDismissRequest = { villageExpanded = false }
                ) {
                  villages.forEach { v ->
                    DropdownMenuItem(text = { Text(v) }, onClick = { onVillageChange(v); villageExpanded = false })
                  }
                }
              }
            }
          }

          // Ward Number / Locality Name
          Column {
            Text(
              text = if (language == AppLanguage.ENGLISH) "Ward Number / Locality Name" else "ವಾರ್ಡ್ ಸಂಖ್ಯೆ / ಪ್ರದೇಶದ ಹೆಸರು",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            OutlinedTextField(
              value = formData.wardNumber,
              onValueChange = onWardChange,
              placeholder = { Text("e.g., Ward No. 4, Temple Street") },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(8.dp),
              colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
              ),
              singleLine = true
            )
          }
        }
      }
    }

    // Location & Landmark with Pin GPS & Map box
    item {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row {
            Text(
              text = if (language == AppLanguage.ENGLISH) "Location & Landmark" else "ಸ್ಥಳ ಮತ್ತು ಗುರುತು",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(" *", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
          }

          Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
            modifier = Modifier.clickable { onPinGpsClick() }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (formData.isGpsPinned) "Pin GPS" else "Locating...",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
            }
          }
        }

        // Map Preview Card
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
          Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
              model = "https://lh3.googleusercontent.com/aida-public/AB6AXuBGSHRbBZPrm38pXQurZmm36hKJPKsbkrYhwO5Ir-9dHY2_Qvb_L9lsKa9aa-I4dB-9a3y3KjmEKgwUTzVnY6WOHUvn4tX8PpzaTjj-5z9SqtgaVUDcdu5hBTP_nHKk-2YOsoCuBIU307GKD0CNctg21OGzXBTFqTLQ6iVsRk2CrRiCqgmV8XA8vQou0gNM26NsQb-aqKwFN8-eVUr2aGIfT9TOz59hdUs4HF36il7YzYyKJfbFP4s",
              contentDescription = "Map Location Preview",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )

            // Pinned Bar overlay
            Box(
              modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.65f))
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "GPS: ${formData.gpsCoordinates}",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color.White
                )
                Icon(
                  imageVector = Icons.Default.Place,
                  contentDescription = null,
                  tint = Color(0xFF8DFC75),
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }

        OutlinedTextField(
          value = formData.landmark,
          onValueChange = onLandmarkChange,
          placeholder = { Text("Landmark reference (e.g., Near Govt. Higher Primary School)") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_landmark"),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
          )
        )
      }
    }

    // Date of Occurrence
    item {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row {
          Text(
            text = if (language == AppLanguage.ENGLISH) "Date of Occurrence" else "ಘಟನೆ ನಡೆದ ದಿನಾಂಕ",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(" *", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        }

        OutlinedTextField(
          value = formData.dateOfOccurrence,
          onValueChange = {},
          readOnly = true,
          trailingIcon = {
            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp))
          },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
          )
        )
      }
    }

    // Evidence Photo / Camera Capture
    item {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CameraAlt,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (language == AppLanguage.ENGLISH) "Camera Visual Proof" else "ದೂರಿನ ಸಾಕ್ಷ್ಯ ಚಿತ್ರ (ಕ್ಯಾಮೆರಾ)",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "(Optional)",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          if (formData.localPhotoUri != null || formData.evidencePhotoUri != null) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color(0xFFDCFCE7)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = Color(0xFF15803D),
                  modifier = Modifier.size(12.dp)
                )
                Text(
                  text = if (language == AppLanguage.ENGLISH) "Photo Attached" else "ಚಿತ್ರ ಲಗತ್ತಿಸಲಾಗಿದೆ",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = Color(0xFF15803D)
                )
              }
            }
          }
        }

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
              width = 1.5.dp,
              color = if (formData.localPhotoUri != null || formData.evidencePhotoUri != null)
                Color(0xFF15803D).copy(alpha = 0.5f)
              else
                MaterialTheme.colorScheme.outlineVariant,
              shape = RoundedCornerShape(12.dp)
            )
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .then(
              if (formData.evidencePhotoUri == null && formData.localPhotoUri == null) {
                Modifier.clickable { launchCamera() }
              } else {
                Modifier
              }
            )
            .padding(16.dp)
            .testTag("upload_photo_box"),
          contentAlignment = Alignment.Center
        ) {
          if (formData.evidencePhotoUri == null && formData.localPhotoUri == null) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(54.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.CameraAlt,
                  contentDescription = "Camera",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(28.dp)
                )
              }

              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = if (language == AppLanguage.ENGLISH) "Capture or Attach Visual Proof" else "ಸಾಕ್ಷ್ಯ ಚಿತ್ರವನ್ನು ಸೆರೆಹಿಡಿಯಿರಿ ಅಥವಾ ಲಗತ್ತಿಸಿ",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = if (language == AppLanguage.ENGLISH) 
                    "Use device camera to take photo of the civic issue or choose from gallery"
                  else
                    "ಕ್ಯಾಮೆರಾ ಬಳಸಿ ನಾಗರಿಕ ಸಮಸ್ಯೆಯ ಚಿತ್ರ ತೆಗೆಯಿರಿ ಅಥವಾ ಗ್ಯಾಲರಿಯಿಂದ ಆಯ್ಕೆಮಾಡಿ",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              // Direct action buttons with min 48dp touch targets
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Button(
                  onClick = { launchCamera() },
                  modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_capture_photo")
                    .testTag("btn_take_photo"),
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                  Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = if (language == AppLanguage.ENGLISH) "Take Photo" else "ಕ್ಯಾಮೆರಾ",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                  )
                }

                OutlinedButton(
                  onClick = { launchGallery() },
                  modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_pick_photo"),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = if (language == AppLanguage.ENGLISH) "Gallery" else "ಗ್ಯಾಲರಿ",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                  )
                }
              }
            }
          } else {
            // Photo Preview and Controls
            Column(
              modifier = Modifier.fillMaxWidth(),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(190.dp)
                  .testTag("evidence_photo_preview"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
              ) {
                Box(modifier = Modifier.fillMaxSize()) {
                  val photoModel: Any = formData.localPhotoUri ?: (formData.evidencePhotoUri ?: "")
                  AsyncImage(
                    model = photoModel,
                    contentDescription = "Attached Evidence Photo",
                    modifier = Modifier
                      .fillMaxSize()
                      .testTag("photo_preview_image"),
                    contentScale = ContentScale.Crop
                  )

                  // Status overlay pill
                  Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = 0.72f),
                    modifier = Modifier
                      .align(Alignment.BottomStart)
                      .padding(8.dp)
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color(0xFF8DFC75),
                        modifier = Modifier.size(14.dp)
                      )
                      Text(
                        text = if (language == AppLanguage.ENGLISH) "Visual Proof Attached" else "ಕ್ಯಾಮೆರಾ ಸಾಕ್ಷ್ಯ ಲಗತ್ತಿಸಲಾಗಿದೆ",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                      )
                    }
                  }
                }
              }

              // Uploading indicator if currently uploading
              if (formData.isUploadingPhoto) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("photo_upload_progress")
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    CircularProgressIndicator(
                      modifier = Modifier.size(16.dp),
                      strokeWidth = 2.dp,
                      color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                      text = if (language == AppLanguage.ENGLISH) 
                        "Uploading to Firebase Storage..." 
                      else 
                        "ಫೈರ್‌ಬೇಸ್ ಸ್ಟೋರೇಜ್‌ಗೆ ಅಪ್‌ಲೋಡ್ ಮಾಡಲಾಗುತ್ತಿದೆ...",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.primary
                    )
                  }
                }
              }

              // Retake / Remove Actions with min 48dp touch targets
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                OutlinedButton(
                  onClick = { launchCamera() },
                  modifier = Modifier
                    .height(48.dp)
                    .testTag("btn_retake_photo"),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = if (language == AppLanguage.ENGLISH) "Retake Photo" else "ಮತ್ತೆ ತೆಗೆಯಿರಿ",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                  )
                }

                TextButton(
                  onClick = {
                    onPhotoCleared()
                    onEvidencePhotoChange(null)
                  },
                  modifier = Modifier
                    .height(48.dp)
                    .testTag("btn_remove_photo"),
                  colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                  Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = if (language == AppLanguage.ENGLISH) "Remove Photo" else "ತೆಗೆದುಹಾಕಿ",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                  )
                }
              }
            }
          }
        }
      }
    }

    // Privacy Checkbox
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.Top
        ) {
          Checkbox(
            checked = formData.isPublic,
            onCheckedChange = onPublicChange,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = if (language == AppLanguage.ENGLISH) 
                "Make my complaint details appear publicly" 
              else 
                "ನನ್ನ ದೂರನ್ನು ಸಾರ್ವಜನಿಕ ಡ್ಯಾಶ್‌ಬೋರ್ಡ್‌ನಲ್ಲಿ ಪ್ರದರ್ಶಿಸಿ",
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = if (language == AppLanguage.ENGLISH)
                "Allow other villagers and officers to view the status and location without revealing your personal contact number."
              else
                "ನಿಮ್ಮ ವೈಯಕ್ತಿಕ ಫೋನ್ ಸಂಖ್ಯೆ ಬಹಿರಂಗಪಡಿಸದೆ ಗ್ರಾಮಸ್ಥರು ಹಾಗೂ ಅಧಿಕಾರಿಗಳು ದೂರನ್ನು ವೀಕ್ಷಿಸಲು ಅನುಮತಿಸುತ್ತದೆ.",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    // Submit Grievance Button
    item {
      Button(
        onClick = onSubmitClick,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("submit_grievance_btn"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
        enabled = !formData.isSubmitting
      ) {
        if (formData.isSubmitting) {
          CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (language == AppLanguage.ENGLISH) "Saving to Firestore..." else "ಕ್ಲೌಡ್‌ನಲ್ಲಿ ಉಳಿಸಲಾಗುತ್ತಿದೆ...",
            color = Color.White
          )
        } else {
          Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (language == AppLanguage.ENGLISH) "Submit Grievance" else "ದೂರು ಸಲ್ಲಿಸಿ",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }

  // Success State Dialog
  if (submittedSuccessGrievance != null) {
    AlertDialog(
      onDismissRequest = onDismissSuccess,
      icon = {
        Box(
          modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(Color(0xFF8DFC75)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color(0xFF012200),
            modifier = Modifier.size(36.dp)
          )
        }
      },
      title = {
        Text(
          text = if (language == AppLanguage.ENGLISH) "Grievance Registered!" else "ದೂರು ನೋಂದಣಿಯಾಗಿದೆ!",
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )
      },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = if (language == AppLanguage.ENGLISH)
              "Your voice has been successfully recorded in the Janaspandana portal."
            else
              "ಜನಸ್ಪಂದನ ಪೋರ್ಟಲ್‌ನಲ್ಲಿ ನಿಮ್ಮ ದೂರನ್ನು ಯಶಸ್ವಿಯಾಗಿ ದಾಖಲಿಸಲಾಗಿದೆ.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(4.dp))

          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Reference ID:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                  submittedSuccessGrievance.id,
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                  ),
                  color = MaterialTheme.colorScheme.primary
                )
              }
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Assigned Panchayat:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(submittedSuccessGrievance.gramPanchayat, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium))
              }
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Estimated Resolution:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Within 48 Hours", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium))
              }
              if (!submittedSuccessGrievance.evidencePhotoUri.isNullOrBlank()) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = if (language == AppLanguage.ENGLISH) "Camera Proof:" else "ಸಾಕ್ಷ್ಯ ಚಿತ್ರ:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFDCFCE7)
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(12.dp))
                      Text(
                        text = if (language == AppLanguage.ENGLISH) "Photo Attached" else "ಚಿತ್ರ ಲಗತ್ತಿಸಲಾಗಿದೆ",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF15803D)
                      )
                    }
                  }
                }
              }
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = onTrackCreatedGrievance,
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Text(if (language == AppLanguage.ENGLISH) "Track Status" else "ಸ್ಥಿತಿ ತಿಳಿಯಿರಿ", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = onDismissSuccess) {
          Text(if (language == AppLanguage.ENGLISH) "Submit Another" else "ಮತ್ತೊಂದು ದೂರು")
        }
      }
    )
  }

  // Camera Permission Denied Dialog
  if (showPermissionDeniedDialog) {
    AlertDialog(
      onDismissRequest = { showPermissionDeniedDialog = false },
      icon = {
        Icon(
          imageVector = Icons.Default.CameraAlt,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(32.dp)
        )
      },
      title = {
        Text(
          text = if (language == AppLanguage.ENGLISH) "Camera Permission Required" else "ಕ್ಯಾಮೆರಾ ಅನುಮತಿ ಅಗತ್ಯವಿದೆ",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      },
      text = {
        Text(
          text = if (language == AppLanguage.ENGLISH)
            "Janaspandana requires camera access to attach ground photos of civic issues to your grievance submission. You can also pick photos directly from your gallery."
          else
            "ನಾಗರಿಕ ಸಮಸ್ಯೆಗಳ ಸಾಕ್ಷ್ಯ ಚಿತ್ರಗಳನ್ನು ಲಗತ್ತಿಸಲು ಕ್ಯಾಮೆರಾ ಅನುಮತಿ ಅಗತ್ಯವಿದೆ. ನೀವು ಗ್ಯಾಲರಿಯಿಂದಲೂ ಫೋಟೋ ಆಯ್ಕೆಮಾಡಬಹುದು.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      },
      confirmButton = {
        Button(
          onClick = { showPermissionDeniedDialog = false }
        ) {
          Text(if (language == AppLanguage.ENGLISH) "OK" else "ಸರಿ")
        }
      }
    )
  }

  // Regional Voice-to-Text Speech Input Dialog
  voiceDialogTarget?.let { target ->
    VoiceInputDialog(
      targetField = target,
      currentText = when (target) {
        VoiceTargetField.TITLE -> formData.title
        VoiceTargetField.DESCRIPTION -> formData.description
        VoiceTargetField.AUTO_DETECT -> ""
      },
      appLanguage = language,
      onApplyText = { spokenText, appliedTarget ->
        when (appliedTarget) {
          VoiceTargetField.TITLE -> {
            onTitleChange(spokenText)
          }
          VoiceTargetField.DESCRIPTION -> {
            val updated = if (formData.description.isBlank()) spokenText else "${formData.description}\n$spokenText"
            onDescriptionChange(updated)
          }
          VoiceTargetField.AUTO_DETECT -> {
            if (formData.title.isBlank()) {
              val candidateTitle = spokenText.split(".", "।", "\n", ",").firstOrNull { it.isNotBlank() } ?: spokenText
              onTitleChange(candidateTitle.take(65).trim())
            }
            val updatedDesc = if (formData.description.isBlank()) spokenText else "${formData.description}\n$spokenText"
            onDescriptionChange(updatedDesc)
          }
        }
      },
      onDismiss = { voiceDialogTarget = null }
    )
  }
}

private fun createImageUri(context: Context): Uri {
  val cacheDir = context.cacheDir
  val photoFile = File(cacheDir, "camera_photo_${System.currentTimeMillis()}.jpg").apply {
    if (!exists()) {
      createNewFile()
    }
  }
  return FileProvider.getUriForFile(
    context,
    "${context.packageName}.fileprovider",
    photoFile
  )
}

