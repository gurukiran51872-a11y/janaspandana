package com.example.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.example.data.model.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class VoiceInputState {
  object Idle : VoiceInputState()
  data class Listening(val rmsLevel: Float = 0f) : VoiceInputState()
  data class Recognized(val text: String, val isPartial: Boolean = false) : VoiceInputState()
  data class Error(val errorMessage: String, val errorCode: Int? = null) : VoiceInputState()
}

class VoiceRecognitionHelper(
  private val context: Context,
  private val onSpeechResult: (String) -> Unit = {}
) {
  private val tag = "VoiceRecognitionHelper"
  private var speechRecognizer: SpeechRecognizer? = null

  private val _state = MutableStateFlow<VoiceInputState>(VoiceInputState.Idle)
  val state: StateFlow<VoiceInputState> = _state.asStateFlow()

  private val _currentLanguage = MutableStateFlow(SpeechLanguage.KANNADA)
  val currentLanguage: StateFlow<SpeechLanguage> = _currentLanguage.asStateFlow()

  fun setLanguage(language: SpeechLanguage) {
    _currentLanguage.value = language
  }

  fun isRecognitionAvailable(): Boolean {
    return try {
      SpeechRecognizer.isRecognitionAvailable(context)
    } catch (e: Exception) {
      false
    }
  }

  fun startListening(
    language: SpeechLanguage = _currentLanguage.value,
    appLanguage: AppLanguage = AppLanguage.KANNADA
  ) {
    _currentLanguage.value = language

    try {
      stopListening()

      if (!isRecognitionAvailable()) {
        _state.value = VoiceInputState.Error(
          if (appLanguage == AppLanguage.ENGLISH) 
            "Speech recognition service not found on device. Using system voice intent."
          else 
            "ಸಾಧನದಲ್ಲಿ ಧ್ವನಿ ಗುರುತಿಸುವಿಕೆ ಸೇವೆ ಲಭ್ಯವಿಲ್ಲ."
        )
        return
      }

      speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
        setRecognitionListener(createListener(language, appLanguage))
      }

      val intent = language.createRecognizerIntent(
        customPrompt = language.listeningPrompt(appLanguage)
      )

      _state.value = VoiceInputState.Listening()
      speechRecognizer?.startListening(intent)
    } catch (e: Exception) {
      Log.e(tag, "Failed to start speech recognition: ${e.message}", e)
      _state.value = VoiceInputState.Error(
        if (appLanguage == AppLanguage.ENGLISH) 
          "Could not initialize microphone: ${e.message}"
        else 
          "ಮೈಕ್ರೊಫೋನ್ ಪ್ರಾರಂಭಿಸಲು ಸಾಧ್ಯವಾಗಲಿಲ್ಲ: ${e.message}"
      )
    }
  }

  fun stopListening() {
    try {
      speechRecognizer?.stopListening()
      speechRecognizer?.cancel()
      speechRecognizer?.destroy()
    } catch (e: Exception) {
      Log.w(tag, "Error stopping recognizer: ${e.message}")
    } finally {
      speechRecognizer = null
      if (_state.value is VoiceInputState.Listening) {
        _state.value = VoiceInputState.Idle
      }
    }
  }

  fun reset() {
    stopListening()
    _state.value = VoiceInputState.Idle
  }

  fun updateRecognizedText(text: String) {
    _state.value = VoiceInputState.Recognized(text = text, isPartial = false)
    onSpeechResult(text)
  }

  private fun createListener(
    speechLanguage: SpeechLanguage,
    appLanguage: AppLanguage
  ): RecognitionListener {
    return object : RecognitionListener {
      override fun onReadyForSpeech(params: Bundle?) {
        _state.value = VoiceInputState.Listening(rmsLevel = 0.1f)
      }

      override fun onBeginningOfSpeech() {
        _state.value = VoiceInputState.Listening(rmsLevel = 0.5f)
      }

      override fun onRmsChanged(rmsdB: Float) {
        if (_state.value is VoiceInputState.Listening) {
          // Normalize rmsdB (typically -2 to 10 dB) to 0.0 .. 1.0
          val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
          _state.value = VoiceInputState.Listening(rmsLevel = normalized)
        }
      }

      override fun onBufferReceived(buffer: ByteArray?) {}

      override fun onEndOfSpeech() {
        // Voice stream finished, processing final results
      }

      override fun onError(error: Int) {
        val errorMessage = mapErrorCodeToMessage(error, speechLanguage, appLanguage)
        Log.w(tag, "Speech recognition error: $error -> $errorMessage")
        _state.value = VoiceInputState.Error(errorMessage = errorMessage, errorCode = error)
        stopListening()
      }

      override fun onResults(results: Bundle?) {
        val recognizedText = SpeechLanguage.extractFromBundle(results)
        if (!recognizedText.isNullOrBlank()) {
          _state.value = VoiceInputState.Recognized(text = recognizedText, isPartial = false)
          onSpeechResult(recognizedText)
        } else {
          _state.value = VoiceInputState.Error(
            if (appLanguage == AppLanguage.ENGLISH) 
              "No speech detected. Please tap mic and speak clearly."
            else 
              "ಯಾವುದೇ ಧ್ವನಿ ಪತ್ತೆಯಾಗಿಲ್ಲ. ದಯವಿಟ್ಟು ಮೈಕ್ ಒತ್ತಿ ಸ್ಪಷ್ಟವಾಗಿ ಮಾತನಾಡಿ."
          )
        }
        stopListening()
      }

      override fun onPartialResults(partialResults: Bundle?) {
        val partialText = SpeechLanguage.extractFromBundle(partialResults)
        if (!partialText.isNullOrBlank()) {
          _state.value = VoiceInputState.Recognized(text = partialText, isPartial = true)
        }
      }

      override fun onEvent(eventType: Int, params: Bundle?) {}
    }
  }

  companion object {
    fun mapErrorCodeToMessage(
      error: Int,
      speechLanguage: SpeechLanguage,
      appLanguage: AppLanguage
    ): String {
      return when (error) {
        SpeechRecognizer.ERROR_AUDIO -> if (appLanguage == AppLanguage.ENGLISH)
          "Audio recording error. Check device microphone."
        else
          "ಆಡಿಯೋ ರೆಕಾರ್ಡಿಂಗ್ ದೋಷ. ಮೈಕ್ರೊಫೋನ್ ಪರಿಶೀಲಿಸಿ."

        SpeechRecognizer.ERROR_CLIENT -> if (appLanguage == AppLanguage.ENGLISH)
          "Client error. Please try again."
        else
          "ಅಪ್ಲಿಕೇಶನ್ ದೋಷ. ದಯವಿಟ್ಟು ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ."

        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> if (appLanguage == AppLanguage.ENGLISH)
          "Microphone permission is required."
        else
          "ಮೈಕ್ರೊಫೋನ್ ಅನುಮತಿ ಅಗತ್ಯವಿದೆ."

        SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> if (appLanguage == AppLanguage.ENGLISH)
          "Network issue. Regional voice requires active connectivity."
        else
          "ನೆಟ್‌ವರ್ಕ್ ಸಮಸ್ಯೆ. ಧ್ವನಿ ಇನ್‌ಪುಟ್‌ಗಾಗಿ ಇಂಟರ್ನೆಟ್ ಸಂಪರ್ಕ ಪರಿಶೀಲಿಸಿ."

        SpeechRecognizer.ERROR_NO_MATCH -> if (appLanguage == AppLanguage.ENGLISH)
          "No words recognized. Please speak closer to the microphone in ${speechLanguage.nativeLabel}."
        else
          "ಮಾತುಗಳು ಸ್ಪಷ್ಟವಾಗಿ ಕೇಳಲಿಲ್ಲ. ದಯವಿಟ್ಟು ${speechLanguage.nativeLabel}ದಲ್ಲಿ ಸ್ಪಷ್ಟವಾಗಿ ಮಾತನಾಡಿ."

        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> if (appLanguage == AppLanguage.ENGLISH)
          "Voice service is busy. Please try again."
        else
          "ಧ್ವನಿ ಸೇವೆ ಕಾರ್ಯನಿರತವಾಗಿದೆ. ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ."

        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> if (appLanguage == AppLanguage.ENGLISH)
          "Listening timed out without speech. Tap mic to try again."
        else
          "ಯಾವುದೇ ಮಾತು ಕೇಳಲಿಲ್ಲ. ಮತ್ತೆ ಮಾತನಾಡಲು ಮೈಕ್ ಒತ್ತಿ."

        else -> if (appLanguage == AppLanguage.ENGLISH)
          "Speech recognition unavailable ($error). Try again."
        else
          "ಧ್ವನಿ ಗುರುತಿಸುವಿಕೆ ಲಭ್ಯವಿಲ್ಲ ($error). ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ."
      }
    }
  }
}
