package com.example.data.model

data class HouseholdHotspot(
    val id: String,
    val title: String,
    val location: String, // Indoor, Outdoor, Balcony, Garden, Perimeter
    val riskFactor: String,
    val preventionTip: String,
    val checkFrequency: String,
    val isCompleted: Boolean = false
)
