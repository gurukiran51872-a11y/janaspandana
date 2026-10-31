package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Test

class GrievanceRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun createGrievance_authenticatedUser_success() = runBlocking {
    signInTestUser("rajesh@test.com")
    val repository = FirestoreGrievanceRepository(firestore)

    val result = withTimeout(5000L) {
      repository.createGrievance(
        title = "Pipeline leakage at Ward 4",
        category = "Water Supply & Sanitation",
        description = "Water is gushing out from the main valve",
        district = "Bengaluru Rural",
        taluk = "Malur",
        gramPanchayat = "Malur GP",
        village = "Malur",
        wardNumber = "Ward No. 4",
        locationName = "Ward 4 Tank",
        gpsCoordinates = "13.2988° N, 77.5812° E",
        isPublic = true
      )
    }

    assertTrue(result.isSuccess)
    val docId = result.getOrThrow()
    assertNotNull(docId)
    assertTrue(docId.startsWith("GP"))
  }

  @Test
  fun getUserGrievances_authenticatedUser_returnsResults() = runBlocking {
    val uid = signInTestUser("rajesh@test.com")
    val repository = FirestoreGrievanceRepository(firestore)

    val docId = withTimeout(5000L) {
      repository.createGrievance(
        title = "Non-working streetlamp",
        category = "Roads & Streetlights",
        description = "Streetlamp is off near temple cross",
        district = "Bengaluru Rural",
        taluk = "Malur",
        gramPanchayat = "Malur GP",
        village = "Sadenahalli",
        wardNumber = "Ward No. 2",
        locationName = "Temple Cross",
        gpsCoordinates = "13.2988° N, 77.5812° E",
        isPublic = true
      ).getOrThrow()
    }

    val list = withTimeout(5000L) {
      repository.observeUserGrievances(uid).first()
    }

    assertTrue(list.any { it.id == docId })
  }

  @Test
  fun updateRatingAndFeedback_authenticatedOwner_success() = runBlocking {
    signInTestUser("rajesh@test.com")
    val repository = FirestoreGrievanceRepository(firestore)

    val docId = withTimeout(5000L) {
      repository.createGrievance(
        title = "Cleaned drainage",
        category = "Drainage & Sewage",
        description = "Drainage was cleared",
        district = "Bengaluru Rural",
        taluk = "Malur",
        gramPanchayat = "Malur GP",
        village = "Bommenahalli",
        wardNumber = "Ward No. 1",
        locationName = "School Road",
        gpsCoordinates = "13.2988° N, 77.5812° E",
        isPublic = true
      ).getOrThrow()
    }

    val updateResult = withTimeout(5000L) {
      repository.updateRatingAndFeedback(docId, 5L, "Promptly resolved by sanitation workers")
    }

    assertTrue(updateResult.isSuccess)
  }

  @Test
  fun observeGrievances_unauthenticated_failsWithPermissionDenied(): Unit = runBlocking {
    // Explicitly sign out
    auth.signOut()
    val repository = FirestoreGrievanceRepository(firestore)

    assertThrows(Exception::class.java) {
      runBlocking {
        withTimeout(3000L) {
          repository.observeUserGrievances("unauth_user").first()
        }
      }
    }
    Unit
  }

  @Test
  fun saveFcmToken_authenticatedUser_success() = runBlocking {
    val uid = signInTestUser("rajesh@test.com")
    val repository = FirestoreGrievanceRepository(firestore)

    val testToken = "fcm_token_sample_1234567890_abcdef"
    val result = withTimeout(5000L) {
      repository.saveFcmToken(testToken)
    }

    assertTrue(result.isSuccess)

    // Verify token was persisted in users collection
    val doc = firestore.collection("users").document(uid).get().await()
    assertTrue(doc.exists())
    assertEquals(testToken, doc.getString("fcmToken"))
  }
}
