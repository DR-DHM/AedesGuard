package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "neighborhood_alerts")
data class NeighborhoodAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val neighborhood: String,
    val alertType: String, // CLUSTER_OUTBREAK, SCHEDULED_MAINTENANCE, WEATHER_POST_RAIN, PREVENTION_REMINDER
    val severity: String, // CRITICAL, HIGH, INFO
    val description: String,
    val scheduledTime: String? = null,
    val actionPrompt: String,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
