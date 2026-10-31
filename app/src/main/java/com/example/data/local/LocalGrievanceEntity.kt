package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Grievance
import com.example.data.model.GrievanceStatus

@Entity(tableName = "cached_grievances")
data class LocalGrievanceEntity(
  @PrimaryKey val id: String,
  val title: String,
  val titleKn: String = "",
  val category: String,
  val categoryKn: String = "",
  val description: String,
  val descriptionKn: String = "",
  val status: String,
  val submittedDate: String,
  val district: String = "Bengaluru Rural",
  val taluk: String = "Malur",
  val gramPanchayat: String = "Malur GP",
  val village: String = "Sadenahalli",
  val wardNumber: String = "Ward No. 4",
  val locationName: String = "Ward 4 Panchayat Tank, Malur Taluk",
  val gpsCoordinates: String = "13.2988° N, 77.5812° E",
  val mapImageUrl: String = "",
  val assignedOfficer: String = "Ramesh Kumar",
  val department: String = "Jal Nigam Dept",
  val expectedResolutionDate: String = "Within 48 Hours",
  val isPublic: Boolean = true,
  val rating: Int? = null,
  val feedbackComment: String? = null,
  val evidencePhotoUri: String? = null,
  val cachedAt: Long = System.currentTimeMillis()
) {
  fun toDomain(): Grievance {
    return Grievance(
      id = id,
      title = title,
      titleKn = titleKn,
      category = category,
      categoryKn = categoryKn,
      description = description,
      descriptionKn = descriptionKn,
      status = GrievanceStatus.fromString(status),
      submittedDate = submittedDate,
      district = district,
      taluk = taluk,
      gramPanchayat = gramPanchayat,
      village = village,
      wardNumber = wardNumber,
      locationName = locationName,
      gpsCoordinates = gpsCoordinates,
      mapImageUrl = mapImageUrl.ifBlank {
        "https://lh3.googleusercontent.com/aida-public/AB6AXuBGSHRbBZPrm38pXQurZmm36hKJPKsbkrYhwO5Ir-9dHY2_Qvb_L9lsKa9aa-I4dB-9a3y3KjmEKgwUTzVnY6WOHUvn4tX8PpzaTjj-5z9SqtgaVUDcdu5hBTP_nHKk-2YOsoCuBIU307GKD0CNctg21OGzXBTFqTLQ6iVsRk2CrRiCqgmV8XA8vQou0gNM26NsQb-aqKwFN8-eVUr2aGIfT9TOz59hdUs4HF36il7YzYyKJfbFP4s"
      },
      assignedOfficer = assignedOfficer,
      department = department,
      expectedResolutionDate = expectedResolutionDate,
      isPublic = isPublic,
      rating = rating,
      feedbackComment = feedbackComment,
      evidencePhotoUri = evidencePhotoUri
    )
  }

  companion object {
    fun fromDomain(g: Grievance): LocalGrievanceEntity {
      return LocalGrievanceEntity(
        id = g.id,
        title = g.title,
        titleKn = g.titleKn,
        category = g.category,
        categoryKn = g.categoryKn,
        description = g.description,
        descriptionKn = g.descriptionKn,
        status = g.status.name,
        submittedDate = g.submittedDate,
        district = g.district,
        taluk = g.taluk,
        gramPanchayat = g.gramPanchayat,
        village = g.village,
        wardNumber = g.wardNumber,
        locationName = g.locationName,
        gpsCoordinates = g.gpsCoordinates,
        mapImageUrl = g.mapImageUrl,
        assignedOfficer = g.assignedOfficer,
        department = g.department,
        expectedResolutionDate = g.expectedResolutionDate,
        isPublic = g.isPublic,
        rating = g.rating,
        feedbackComment = g.feedbackComment,
        evidencePhotoUri = g.evidencePhotoUri
      )
    }
  }
}
