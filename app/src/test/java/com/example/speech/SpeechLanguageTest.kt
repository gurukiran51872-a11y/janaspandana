package com.example.speech

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.example.data.model.AppLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.ArrayList

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SpeechLanguageTest {

  @Test
  fun testRegionalLanguageCodes() {
    assertEquals("kn-IN", SpeechLanguage.KANNADA.localeTag)
    assertEquals("en-IN", SpeechLanguage.ENGLISH.localeTag)
    assertEquals("kn", SpeechLanguage.KANNADA.languageCode)
    assertEquals("en", SpeechLanguage.ENGLISH.languageCode)
  }

  @Test
  fun testAppLanguageMapping() {
    assertEquals(SpeechLanguage.KANNADA, SpeechLanguage.fromAppLanguage(AppLanguage.KANNADA))
    assertEquals(SpeechLanguage.ENGLISH, SpeechLanguage.fromAppLanguage(AppLanguage.ENGLISH))
  }

  @Test
  fun testListeningPromptsBilingual() {
    val knPromptInKannada = SpeechLanguage.KANNADA.listeningPrompt(AppLanguage.KANNADA)
    assertTrue("Should contain Kannada text", knPromptInKannada.contains("ಕನ್ನಡ"))

    val knPromptInEnglish = SpeechLanguage.KANNADA.listeningPrompt(AppLanguage.ENGLISH)
    assertTrue("Should mention Kannada in English", knPromptInEnglish.contains("Kannada"))

    val enPromptInEnglish = SpeechLanguage.ENGLISH.listeningPrompt(AppLanguage.ENGLISH)
    assertTrue("Should mention English", enPromptInEnglish.contains("English"))
  }

  @Test
  fun testCreateRecognizerIntent() {
    val intent = SpeechLanguage.KANNADA.createRecognizerIntent("Speak in Kannada")
    assertEquals(RecognizerIntent.ACTION_RECOGNIZE_SPEECH, intent.action)
    assertEquals("kn-IN", intent.getStringExtra(RecognizerIntent.EXTRA_LANGUAGE))
    assertEquals(RecognizerIntent.LANGUAGE_MODEL_FREE_FORM, intent.getStringExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL))
    assertEquals("Speak in Kannada", intent.getStringExtra(RecognizerIntent.EXTRA_PROMPT))
  }

  @Test
  fun testExtractSpeechResultFromIntent() {
    val intent = Intent().apply {
      val list = ArrayList<String>()
      list.add("ಕುಡಿಯುವ ನೀರಿನ ಪೈಪ್ಲೈನ್ ಒಡೆದಿದೆ")
      putStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS, list)
    }

    val extracted = SpeechLanguage.extractSpeechResult(intent)
    assertNotNull(extracted)
    assertEquals("ಕುಡಿಯುವ ನೀರಿನ ಪೈಪ್ಲೈನ್ ಒಡೆದಿದೆ", extracted)
  }

  @Test
  fun testExtractSpeechResultFromBundle() {
    val bundle = Bundle().apply {
      val list = ArrayList<String>()
      list.add("Streetlight is broken near temple")
      putStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION, list)
    }

    val extracted = SpeechLanguage.extractFromBundle(bundle)
    assertNotNull(extracted)
    assertEquals("Streetlight is broken near temple", extracted)
  }

  @Test
  fun testErrorMessageMappingForRegionalLanguages() {
    val audioErrorKn = VoiceRecognitionHelper.mapErrorCodeToMessage(
      SpeechRecognizer.ERROR_AUDIO,
      SpeechLanguage.KANNADA,
      AppLanguage.KANNADA
    )
    assertTrue("Should contain Kannada error description", audioErrorKn.contains("ಆಡಿಯೋ") || audioErrorKn.contains("ಮೈಕ್ರೊಫೋನ್"))

    val noMatchErrorEn = VoiceRecognitionHelper.mapErrorCodeToMessage(
      SpeechRecognizer.ERROR_NO_MATCH,
      SpeechLanguage.KANNADA,
      AppLanguage.ENGLISH
    )
    assertTrue("Should mention language in English message", noMatchErrorEn.contains("Kannada") || noMatchErrorEn.contains("recognized"))
  }
}
