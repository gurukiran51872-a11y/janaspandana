package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GrievanceDao {
  @Query("SELECT * FROM cached_grievances ORDER BY cachedAt DESC")
  fun getAllCachedGrievances(): Flow<List<LocalGrievanceEntity>>

  @Query("SELECT * FROM cached_grievances WHERE id = :id LIMIT 1")
  suspend fun getCachedGrievanceById(id: String): LocalGrievanceEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGrievance(grievance: LocalGrievanceEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(grievances: List<LocalGrievanceEntity>)

  @Query("UPDATE cached_grievances SET status = :status WHERE id = :id")
  suspend fun updateStatus(id: String, status: String)

  @Query("UPDATE cached_grievances SET rating = :rating, feedbackComment = :comment WHERE id = :id")
  suspend fun updateRating(id: String, rating: Int, comment: String)

  @Query("DELETE FROM cached_grievances WHERE id = :id")
  suspend fun deleteById(id: String)

  @Query("DELETE FROM cached_grievances")
  suspend fun clearAll()
}
