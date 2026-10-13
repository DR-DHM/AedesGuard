package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "larva_scans")
data class LarvaScanResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val speciesName: String,
    val commonName: String,
    val isAedes: Boolean,
    val confidencePercent: Int,
    val riskLevel: String, // CRITICAL, HIGH, MODERATE, LOW
    val siphonType: String,
    val hangingAngle: String,
    val keyFeatures: String,
    val actionSteps: String, // newline separated or json
    val detailedExplanation: String,
    val sampleImageRes: Int? = null,
    val customImageUri: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
