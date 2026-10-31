package com.example.speech

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.example.data.model.AppLanguage
import java.util.Locale

/**
 * Regional speech recognition languages supported by Janaspandana
 * for rural citizen accessibility.
 */
enum class SpeechLanguage(
  val localeTag: String,
  val nativeLabel: String,
  val englishLabel: String,
  val languageCode: String
) {
  KANNADA(
    localeTag = "kn-IN",
    nativeLabel = "ಕನ್ನಡ",
    englishLabel = "Kannada",
    languageCode = "kn"
  ),
  ENGLISH(
    localeTag = "en-IN",
    nativeLabel = "English",
    englishLabel = "English (India)",
    languageCode = "en"
  );

  fun getDisplayName(appLanguage: AppLanguage): String {
    return when (this) {
      KANNADA -> if (appLanguage == AppLanguage.ENGLISH) "Kannada (ಕನ್ನಡ)" else "ಕನ್ನಡ"
      ENGLISH -> if (appLanguage == AppLanguage.ENGLISH) "English (India)" else "ಇಂಗ್ಲಿಷ್"
    }
  }

  fun listeningPrompt(appLanguage: AppLanguage): String {
    return when (this) {
      KANNADA -> if (appLanguage == AppLanguage.ENGLISH) 
        "Listening in Kannada... Speak now" 
      else 
        "ಕನ್ನಡದಲ್ಲಿ ಮಾತನಾಡಿ... ಆಲಿಸಲಾಗುತ್ತಿದೆ"
      ENGLISH -> if (appLanguage == AppLanguage.ENGLISH) 
        "Listening in English... Speak now" 
      else 
        "ಇಂಗ್ಲಿಷ್‌ನಲ್ಲಿ ಮಾತನಾಡಿ... ಆಲಿಸಲಾಗುತ್ತಿದೆ"
    }
  }

  fun exampleHint(appLanguage: AppLanguage): String {
    return when (this) {
      KANNADA -> if (appLanguage == AppLanguage.ENGLISH)
        "Example: 'ನಮ್ಮ ಹಳ್ಳಿಯ ಮುಖ್ಯ ರಸ್ತೆಯಲ್ಲಿ ಕುಡಿಯುವ ನೀರಿನ ಪೈಪ್ಲೈನ್ ಒಡೆದಿದೆ'"
      else
        "ಉದಾ: 'ನಮ್ಮ ಹಳ್ಳಿಯ ಮುಖ್ಯ ರಸ್ತೆಯಲ್ಲಿ ಕುಡಿಯುವ ನೀರಿನ ಪೈಪ್ಲೈನ್ ಒಡೆದಿದೆ'"
      ENGLISH -> if (appLanguage == AppLanguage.ENGLISH)
        "Example: 'Water supply pipeline broken near primary school road'"
      else
        "ಉದಾ: 'Water supply pipeline broken near primary school road'"
    }
  }

  fun toLocale(): Locale {
    return when (this) {
      KANNADA -> Locale("kn", "IN")
      ENGLISH -> Locale("en", "IN")
    }
  }

  fun createRecognizerIntent(customPrompt: String? = null): Intent {
    return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
      putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
      putExtra(RecognizerIntent.EXTRA_LANGUAGE, localeTag)
      putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, localeTag)
      putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, localeTag)
      putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
      putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
      if (customPrompt != null) {
        putExtra(RecognizerIntent.EXTRA_PROMPT, customPrompt)
      }
    }
  }

  companion object {
    fun fromAppLanguage(appLanguage: AppLanguage): SpeechLanguage {
      return when (appLanguage) {
        AppLanguage.KANNADA -> KANNADA
        AppLanguage.ENGLISH -> ENGLISH
      }
    }

    fun extractSpeechResult(intent: Intent?): String? {
      if (intent == null) return null
      val results = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
      return results?.firstOrNull { it.isNotBlank() }
    }

    fun extractFromBundle(bundle: Bundle?): String? {
      if (bundle == null) return null
      val results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
      return results?.firstOrNull { it.isNotBlank() }
    }
  }
}
