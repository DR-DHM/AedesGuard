package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(val route: String, val title: String, val icon: ImageVector) {
    DETECT("detect", "Detect Larva", Icons.Filled.DocumentScanner),
    MAP("map", "Map & Heatmap", Icons.Filled.Map),
    REPORT("report", "Report Site", Icons.Filled.ReportProblem),
    ALERTS("alerts", "Alerts", Icons.Filled.Campaign),
    LEARN("learn", "Learn & Lab", Icons.AutoMirrored.Filled.MenuBook)
}
