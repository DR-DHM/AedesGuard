package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.NeighborhoodAlert
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Query("SELECT * FROM neighborhood_alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<NeighborhoodAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: NeighborhoodAlert): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAlerts(alerts: List<NeighborhoodAlert>)

    @Update
    suspend fun updateAlert(alert: NeighborhoodAlert)

    @Query("UPDATE neighborhood_alerts SET isRead = 1 WHERE id = :alertId")
    suspend fun markAsRead(alertId: Long)

    @Query("SELECT COUNT(*) FROM neighborhood_alerts WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM neighborhood_alerts")
    suspend fun getCount(): Int
}
