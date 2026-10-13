package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "breeding_reports")
data class BreedingReport(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val containerType: String,
    val severity: String, // CRITICAL, HIGH, MEDIUM, LOW
    val neighborhood: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val isAnonymous: Boolean = true,
    val reporterName: String = "Anonymous Citizen",
    val notes: String = "",
    val larvaCountEstimate: String = "10-50 (Moderate)",
    val status: String = "Pending Inspection", // Pending Inspection, Officer Dispatched, Treated & Cleared
    val photoUri: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
