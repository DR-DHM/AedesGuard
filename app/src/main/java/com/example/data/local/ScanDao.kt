package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.LarvaScanResult
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {
    @Query("SELECT * FROM larva_scans ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<LarvaScanResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: LarvaScanResult): Long

    @Query("SELECT * FROM larva_scans ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestScan(): LarvaScanResult?

    @Query("SELECT COUNT(*) FROM larva_scans")
    suspend fun getCount(): Int
}
