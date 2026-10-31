package com.example.data.local

import com.example.data.model.Grievance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalGrievanceRepository(private val grievanceDao: GrievanceDao) {

  val cachedGrievances: Flow<List<Grievance>> = grievanceDao.getAllCachedGrievances().map { list ->
    list.map { it.toDomain() }
  }

  suspend fun cacheGrievances(grievances: List<Grievance>) {
    val entities = grievances.map { LocalGrievanceEntity.fromDomain(it) }
    grievanceDao.insertAll(entities)
  }

  suspend fun cacheGrievance(grievance: Grievance) {
    grievanceDao.insertGrievance(LocalGrievanceEntity.fromDomain(grievance))
  }

  suspend fun getGrievanceById(id: String): Grievance? {
    return grievanceDao.getCachedGrievanceById(id)?.toDomain()
  }

  suspend fun updateStatus(id: String, status: String) {
    grievanceDao.updateStatus(id, status)
  }

  suspend fun updateRating(id: String, rating: Int, comment: String) {
    grievanceDao.updateRating(id, rating, comment)
  }

  suspend fun deleteById(id: String) {
    grievanceDao.deleteById(id)
  }

  suspend fun clearCache() {
    grievanceDao.clearAll()
  }
}
