package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BreedingReport
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Query("SELECT * FROM breeding_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<BreedingReport>>

    @Query("SELECT * FROM breeding_reports WHERE severity = :severity ORDER BY timestamp DESC")
    fun getReportsBySeverity(severity: String): Flow<List<BreedingReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: BreedingReport): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllReports(reports: List<BreedingReport>)

    @Update
    suspend fun updateReport(report: BreedingReport)

    @Query("UPDATE breeding_reports SET status = :newStatus WHERE id = :reportId")
    suspend fun updateStatus(reportId: Long, newStatus: String)

    @Query("SELECT COUNT(*) FROM breeding_reports")
    suspend fun getCount(): Int

    @Query("DELETE FROM breeding_reports WHERE id = :reportId")
    suspend fun deleteReport(reportId: Long)
}
