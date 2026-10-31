package com.example.speech

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.data.model.AppLanguage
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy

enum class VoiceTargetField {
  TITLE,
  DESCRIPTION,
  AUTO_DETECT
}

@Composable
fun VoiceInputDialog(
  targetField: VoiceTargetField,
  currentText: String = "",
  appLanguage: AppLanguage,
  onApplyText: (text: String, target: VoiceTargetField) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  var selectedSpeechLang by remember {
    mutableStateOf(SpeechLanguage.fromAppLanguage(appLanguage))
  }
  var transcribedText by remember { mutableStateOf("") }
  var hasPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
      ) == PackageManager.PERMISSION_GRANTED
    )
  }

  val helper = remember {
    VoiceRecognitionHelper(context) { recognized ->
      transcribedText = recognized
    }
  }

  val voiceState by helper.state.collectAsState()

  // Fallback system speech launcher (e.g. if SpeechRecognizer service is not running directly)
  val systemSpeechLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result ->
    val spoken = SpeechLanguage.extractSpeechResult(result.data)
    if (!spoken.isNullOrBlank()) {
      transcribedText = spoken
      helper.updateRecognizedText(spoken)
    }
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { granted ->
    hasPermission = granted
    if (granted) {
      if (helper.isRecognitionAvailable()) {
        helper.startListening(selectedSpeechLang, appLanguage)
      } else {
        val intent = selectedSpeechLang.createRecognizerIntent(
          selectedSpeechLang.listeningPrompt(appLanguage)
        )
        try {
          systemSpeechLauncher.launch(intent)
        } catch (e: Exception) {
          helper.startListening(selectedSpeechLang, appLanguage)
        }
      }
    }
  }

  // Cleanup helper on close
  DisposableEffect(Unit) {
    onDispose {
      helper.reset()
    }
  }

  // Start listening automatically on mount if permission granted
  LaunchedEffect(hasPermission, selectedSpeechLang) {
    if (hasPermission) {
      if (helper.isRecognitionAvailable()) {
        helper.startListening(selectedSpeechLang, appLanguage)
      } else {
        try {
          systemSpeechLauncher.launch(
            selectedSpeechLang.createRecognizerIntent(
              selectedSpeechLang.listeningPrompt(appLanguage)
            )
          )
        } catch (e: Exception) {
          helper.startListening(selectedSpeechLang, appLanguage)
        }
      }
    }
  }

  val isListening = voiceState is VoiceInputState.Listening

  // Infinite ripple pulse animation for mic
  val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (isListening) 1.25f else 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 850, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  Dialog(
    onDismissRequest = {
      helper.reset()
      onDismiss()
    },
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .padding(vertical = 24.dp)
        .testTag("voice_input_dialog"),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Dialog Header
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
                .background(MaterialTheme.colorScheme.primaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.RecordVoiceOver,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
            }
            Column {
              Text(
                text = if (appLanguage == AppLanguage.ENGLISH) 
                  "Voice-to-Text Input" 
                else 
                  "ಧ್ವನಿ ಮೂಲಕ ದೂರು ನಮೂದು",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = when (targetField) {
                  VoiceTargetField.TITLE -> if (appLanguage == AppLanguage.ENGLISH) "For Complaint Title" else "ದೂರಿನ ಶೀರ್ಷಿಕೆಗಾಗಿ"
                  VoiceTargetField.DESCRIPTION -> if (appLanguage == AppLanguage.ENGLISH) "For Description" else "ದೂರಿನ ವಿವರಣೆಗಾಗಿ"
                  VoiceTargetField.AUTO_DETECT -> if (appLanguage == AppLanguage.ENGLISH) "Smart Voice Report" else "ಸಂಪೂರ್ಣ ಧ್ವನಿ ದೂರು"
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }

          IconButton(
            onClick = {
              helper.reset()
              onDismiss()
            },
            modifier = Modifier.testTag("btn_close_voice_dialog")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }

        // Regional Language Switcher Bar
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
          )
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Translate,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = if (appLanguage == AppLanguage.ENGLISH) "Speech Language:" else "ಧ್ವನಿ ಭಾಷೆ:",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              // Kannada Button
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (selectedSpeechLang == SpeechLanguage.KANNADA) 
                  PrimaryNavy 
                else 
                  MaterialTheme.colorScheme.surface,
                modifier = Modifier
                  .clickable {
                    if (selectedSpeechLang != SpeechLanguage.KANNADA) {
                      selectedSpeechLang = SpeechLanguage.KANNADA
                      if (hasPermission && helper.isRecognitionAvailable()) {
                        helper.startListening(SpeechLanguage.KANNADA, appLanguage)
                      }
                    }
                  }
                  .testTag("lang_chip_kannada")
              ) {
                Text(
                  text = "ಕನ್ನಡ (kn-IN)",
                  color = if (selectedSpeechLang == SpeechLanguage.KANNADA) Color.White else MaterialTheme.colorScheme.onSurface,
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }

              // English Button
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (selectedSpeechLang == SpeechLanguage.ENGLISH) 
                  PrimaryNavy 
                else 
                  MaterialTheme.colorScheme.surface,
                modifier = Modifier
                  .clickable {
                    if (selectedSpeechLang != SpeechLanguage.ENGLISH) {
                      selectedSpeechLang = SpeechLanguage.ENGLISH
                      if (hasPermission && helper.isRecognitionAvailable()) {
                        helper.startListening(SpeechLanguage.ENGLISH, appLanguage)
                      }
                    }
                  }
                  .testTag("lang_chip_english")
              ) {
                Text(
                  text = "English (en-IN)",
                  color = if (selectedSpeechLang == SpeechLanguage.ENGLISH) Color.White else MaterialTheme.colorScheme.onSurface,
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }
        }

        // Animated Microphone & Status Area
        Box(
          modifier = Modifier
            .size(120.dp)
            .padding(8.dp),
          contentAlignment = Alignment.Center
        ) {
          // Outer pulsing ring when listening
          if (isListening) {
            Box(
              modifier = Modifier
                .size(100.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            )
          }

          // Main Microphone FAB Button
          Surface(
            shape = CircleShape,
            color = if (isListening) MaterialTheme.colorScheme.primary else PrimaryNavy,
            shadowElevation = 8.dp,
            modifier = Modifier
              .size(72.dp)
              .clickable {
                if (!hasPermission) {
                  permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                } else if (isListening) {
                  helper.stopListening()
                } else {
                  if (helper.isRecognitionAvailable()) {
                    helper.startListening(selectedSpeechLang, appLanguage)
                  } else {
                    try {
                      systemSpeechLauncher.launch(
                        selectedSpeechLang.createRecognizerIntent(
                          selectedSpeechLang.listeningPrompt(appLanguage)
                        )
                      )
                    } catch (e: Exception) {
                      helper.startListening(selectedSpeechLang, appLanguage)
                    }
                  }
                }
              }
              .testTag("voice_dialog_mic_button")
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                contentDescription = if (isListening) "Listening active" else "Tap to speak",
                tint = Color.White,
                modifier = Modifier.size(34.dp)
              )
            }
          }
        }

        // Status Prompt Text
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(
            text = when {
              !hasPermission -> if (appLanguage == AppLanguage.ENGLISH)
                "Microphone permission required to speak"
              else
                "ಧ್ವನಿಗಾಗಿ ಮೈಕ್ರೊಫೋನ್ ಅನುಮತಿ ಅಗತ್ಯವಿದೆ"

              isListening -> selectedSpeechLang.listeningPrompt(appLanguage)

              voiceState is VoiceInputState.Recognized -> if (appLanguage == AppLanguage.ENGLISH)
                "Speech recognized! Review below or tap mic to speak again."
              else
                "ಧ್ವನಿ ಯಶಸ್ವಿಯಾಗಿ ಗುರುತಿಸಲಾಗಿದೆ! ಪರಿಶೀಲಿಸಿ."

              voiceState is VoiceInputState.Error -> (voiceState as VoiceInputState.Error).errorMessage

              else -> if (appLanguage == AppLanguage.ENGLISH)
                "Tap microphone to speak in ${selectedSpeechLang.englishLabel}"
              else
                "${selectedSpeechLang.nativeLabel}ದಲ್ಲಿ ಮಾತನಾಡಲು ಮೈಕ್ ಒತ್ತಿ"
            },
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = if (isListening) FontWeight.Bold else FontWeight.Medium
            ),
            color = when {
              !hasPermission || voiceState is VoiceInputState.Error -> MaterialTheme.colorScheme.error
              isListening -> MaterialTheme.colorScheme.primary
              else -> MaterialTheme.colorScheme.onSurface
            },
            textAlign = TextAlign.Center
          )

          // Hint example
          Text(
            text = selectedSpeechLang.exampleHint(appLanguage),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
          )
        }

        // Live Spoken Transcript Input & Review Box
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (appLanguage == AppLanguage.ENGLISH) "Recognized Text:" else "ಗುರುತಿಸಿದ ಪಠ್ಯ:",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )

            if (transcribedText.isNotBlank()) {
              TextButton(
                onClick = { transcribedText = "" },
                modifier = Modifier.height(28.dp)
              ) {
                Text(
                  text = if (appLanguage == AppLanguage.ENGLISH) "Clear" else "ಅಳಿಸಿ",
                  style = MaterialTheme.typography.labelSmall
                )
              }
            }
          }

          OutlinedTextField(
            value = transcribedText,
            onValueChange = { transcribedText = it },
            modifier = Modifier
              .fillMaxWidth()
              .height(110.dp)
              .testTag("voice_transcript_output"),
            placeholder = {
              Text(
                text = if (isListening) 
                  "..." 
                else if (appLanguage == AppLanguage.ENGLISH)
                  "Your spoken words will appear here. You can also edit before inserting."
                else
                  "ನೀವು ಮಾತನಾಡಿದ ಮಾತುಗಳು ಇಲ್ಲಿ ಕಾಣಿಸುತ್ತವೆ..."
              )
            },
            shape = RoundedCornerShape(12.dp)
          )
        }

        // Action Buttons Row
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Primary Apply Button
          Button(
            onClick = {
              if (transcribedText.isNotBlank()) {
                onApplyText(transcribedText, targetField)
                helper.reset()
                onDismiss()
              }
            },
            enabled = transcribedText.isNotBlank(),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("btn_insert_spoken_text"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (transcribedText.isNotBlank()) Color(0xFF15803D) else MaterialTheme.colorScheme.primary
            )
          ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = when (targetField) {
                VoiceTargetField.TITLE -> if (appLanguage == AppLanguage.ENGLISH) 
                  "Insert into Title" 
                else 
                  "ಶೀರ್ಷಿಕೆಗೆ ಸೇರಿಸಿ"
                VoiceTargetField.DESCRIPTION -> if (appLanguage == AppLanguage.ENGLISH) 
                  "Insert into Description" 
                else 
                  "ವಿವರಣೆಗೆ ಸೇರಿಸಿ"
                VoiceTargetField.AUTO_DETECT -> if (appLanguage == AppLanguage.ENGLISH) 
                  "Apply to Grievance Form" 
                else 
                  "ದೂರಿಗೆ ಅನ್ವಯಿಸಿ"
              },
              fontWeight = FontWeight.Bold
            )
          }

          // Secondary Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = {
                if (!hasPermission) {
                  permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                } else if (helper.isRecognitionAvailable()) {
                  helper.startListening(selectedSpeechLang, appLanguage)
                } else {
                  try {
                    systemSpeechLauncher.launch(
                      selectedSpeechLang.createRecognizerIntent(
                        selectedSpeechLang.listeningPrompt(appLanguage)
                      )
                    )
                  } catch (e: Exception) {
                    helper.startListening(selectedSpeechLang, appLanguage)
                  }
                }
              },
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("btn_try_voice_again"),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (appLanguage == AppLanguage.ENGLISH) "Speak Again" else "ಮತ್ತೆ ಮಾತನಾಡಿ",
                style = MaterialTheme.typography.labelMedium
              )
            }

            TextButton(
              onClick = {
                helper.reset()
                onDismiss()
              },
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("btn_cancel_voice"),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text(
                text = if (appLanguage == AppLanguage.ENGLISH) "Cancel" else "ರದ್ದುಮಾಡಿ",
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }
  }
}
