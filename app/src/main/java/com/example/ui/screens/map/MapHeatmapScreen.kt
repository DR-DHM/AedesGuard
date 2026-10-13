package com.example.ui.screens.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BreedingReport
import com.example.ui.viewmodel.AedesViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MapHeatmapScreen(
    viewModel: AedesViewModel
) {
    val allReports by viewModel.allReports.collectAsState()
    val severityFilter by viewModel.severityFilter.collectAsState()
    val dateRangeFilter by viewModel.dateRangeFilter.collectAsState()
    val selectedReport by viewModel.selectedReportDetail.collectAsState()

    // Filter reports according to selected severity and date range
    val now = System.currentTimeMillis()
    val filteredReports = allReports.filter { report ->
        val matchesSeverity = if (severityFilter == "ALL") true else report.severity.equals(severityFilter, ignoreCase = true)
        val matchesDate = when (dateRangeFilter) {
            "TODAY" -> (now - report.timestamp) <= 86400000L
            "WEEK" -> (now - report.timestamp) <= (7 * 86400000L)
            "MONTH" -> (now - report.timestamp) <= (30 * 86400000L)
            else -> true
        }
        matchesSeverity && matchesDate
    }

    val totalCount = allReports.size
    val criticalCount = allReports.count { it.severity == "CRITICAL" }
    val clearedCount = allReports.count { it.status == "Treated & Cleared" }
    val pendingCount = totalCount - clearedCount

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Citywide Vector Surveillance Map",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Real-time municipal breeding hotspots, cluster heatmaps & report tracking",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Statistics Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "Total Reports",
                    value = totalCount.toString(),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Critical Sites",
                    value = criticalCount.toString(),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Neutralized",
                    value = clearedCount.toString(),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Filters: Severity & Date Range
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Severity Filter:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                val severities = listOf("ALL", "CRITICAL", "HIGH", "MEDIUM", "LOW")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(severities) { sev ->
                        val isSelected = severityFilter == sev
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable { viewModel.setSeverityFilter(sev) }
                                .testTag("filter_severity_$sev")
                        ) {
                            Text(
                                text = sev,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Date Range:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                val dateRanges = listOf(
                    "ALL" to "All Time",
                    "TODAY" to "Today",
                    "WEEK" to "Past 7 Days",
                    "MONTH" to "Past 30 Days"
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(dateRanges) { (key, label) ->
                        val isSelected = dateRangeFilter == key
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable { viewModel.setDateRangeFilter(key) }
                                .testTag("filter_date_$key")
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Interactive Canvas Heatmap & Vector Map
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
                    .testTag("interactive_map_canvas")
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(filteredReports) {
                                detectTapGestures { tapOffset ->
                                    // Check if user tapped near any report pin
                                    val width = size.width.toFloat()
                                    val height = size.height.toFloat()
                                    val tappedReport = filteredReports.minByOrNull { report ->
                                        val pinX = ((report.longitude - 103.8050) / 0.0350 * width).toFloat()
                                        val pinY = ((1.3650 - report.latitude) / 0.0300 * height).toFloat()
                                        val dx = tapOffset.x - pinX
                                        val dy = tapOffset.y - pinY
                                        dx * dx + dy * dy
                                    }
                                    if (tappedReport != null) {
                                        viewModel.selectReportDetail(tappedReport)
                                    }
                                }
                            }
                    ) {
                        val w = size.width
                        val h = size.height

                        // 1. Draw Map Background grid
                        drawRect(Color(0xFFE8ECE9))
                        // River / waterway
                        drawLine(
                            color = Color(0xFFB3E5FC),
                            start = Offset(0f, h * 0.45f),
                            end = Offset(w, h * 0.7f),
                            strokeWidth = 24f
                        )
                        // Roads
                        drawLine(Color(0xFFD0D7D1), Offset(w * 0.25f, 0f), Offset(w * 0.25f, h), strokeWidth = 8f)
                        drawLine(Color(0xFFD0D7D1), Offset(w * 0.65f, 0f), Offset(w * 0.65f, h), strokeWidth = 8f)
                        drawLine(Color(0xFFD0D7D1), Offset(0f, h * 0.3f), Offset(w, h * 0.3f), strokeWidth = 8f)
                        drawLine(Color(0xFFD0D7D1), Offset(0f, h * 0.8f), Offset(w, h * 0.8f), strokeWidth = 8f)

                        // 2. Draw Heatmap Blooms around critical and high density spots
                        filteredReports.filter { it.severity == "CRITICAL" || it.severity == "HIGH" }.forEach { report ->
                            val px = ((report.longitude - 103.8050) / 0.0350 * w).toFloat().coerceIn(20f, w - 20f)
                            val py = ((1.3650 - report.latitude) / 0.0300 * h).toFloat().coerceIn(20f, h - 20f)
                            val heatRadius = if (report.severity == "CRITICAL") 90f else 65f
                            val centerColor = if (report.severity == "CRITICAL") Color(0x77FF2A2A) else Color(0x66FFA000)

                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(centerColor, Color(0x00FF2A2A)),
                                    center = Offset(px, py),
                                    radius = heatRadius
                                ),
                                radius = heatRadius,
                                center = Offset(px, py)
                            )
                        }

                        // 3. Draw Report Pins
                        filteredReports.forEach { report ->
                            val px = ((report.longitude - 103.8050) / 0.0350 * w).toFloat().coerceIn(20f, w - 20f)
                            val py = ((1.3650 - report.latitude) / 0.0300 * h).toFloat().coerceIn(20f, h - 20f)

                            val pinColor = when (report.severity) {
                                "CRITICAL" -> Color(0xFFD32F2F)
                                "HIGH" -> Color(0xFFF57C00)
                                "MEDIUM" -> Color(0xFFFBC02D)
                                else -> Color(0xFF388E3C)
                            }

                            // Halo for cleared status
                            if (report.status == "Treated & Cleared") {
                                drawCircle(
                                    color = Color(0xFF4CAF50),
                                    radius = 18f,
                                    center = Offset(px, py),
                                    style = Stroke(width = 4f)
                                )
                            }

                            // Pin body
                            drawCircle(
                                color = Color.White,
                                radius = 13f,
                                center = Offset(px, py)
                            )
                            drawCircle(
                                color = pinColor,
                                radius = 10f,
                                center = Offset(px, py)
                            )
                        }
                    }

                    // Map overlay labels
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xCCFFFFFF),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFF2A2A)))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Heatmap Glow: Dengue Cluster", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xCCFFFFFF),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Tap pin to inspect breeding site",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Selected Report Detail Card
        selectedReport?.let { report ->
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("selected_report_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = report.containerType,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${report.address} (${report.neighborhood})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            SeverityBadge(report.severity)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Reported by: ${report.reporterName} • Larva Est: ${report.larvaCountEstimate}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (report.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Citizen Note: \"${report.notes}\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Status: ",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = report.status,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (report.status == "Treated & Cleared") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (report.status != "Treated & Cleared") {
                            Button(
                                onClick = { viewModel.markReportCleared(report.id) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("mark_cleared_button")
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Mark as Inspected & Cleared")
                            }
                        }
                    }
                }
            }
        }

        // List of Reported Incidents matching filter
        item {
            Text(
                text = "Reported Sites List (${filteredReports.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        items(filteredReports) { report ->
            BreedingReportItem(
                report = report,
                isSelected = selectedReport?.id == report.id,
                onClick = { viewModel.selectReportDetail(report) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.12f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SeverityBadge(severity: String) {
    val (bgColor, textColor) = when (severity.uppercase()) {
        "CRITICAL" -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
        "HIGH" -> Color(0xFFF57C00) to Color.White
        "MEDIUM" -> Color(0xFFFBC02D) to Color.Black
        else -> Color(0xFF388E3C) to Color.White
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Text(
            text = severity,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun BreedingReportItem(
    report: BreedingReport,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val dateStr = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(report.timestamp))
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = if (report.status == "Treated & Cleared") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = report.containerType,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${report.neighborhood} • $dateStr",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            SeverityBadge(report.severity)
        }
    }
}
