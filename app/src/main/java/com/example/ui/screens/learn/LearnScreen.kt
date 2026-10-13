package com.example.ui.screens.learn

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.HouseholdHotspot
import com.example.ui.viewmodel.AedesViewModel

@Composable
fun LearnScreen(
    viewModel: AedesViewModel
) {
    val hotspots by viewModel.hotspots.collectAsState()
    val thinkingQuery by viewModel.thinkingQuery.collectAsState()
    val isThinking by viewModel.isThinking.collectAsState()
    val thinkingResult by viewModel.thinkingResult.collectAsState()

    var activeTab by remember { mutableStateOf("WIPEOUT") } // WIPEOUT, HOTSPOTS, ANATOMY, HIGH_THINKING
    var customQueryText by remember { mutableStateOf(thinkingQuery) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Educational Resources & Science Lab",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Infestation prevention guides, anatomical taxonomic keys & High Thinking vector intelligence",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section Tabs
        item {
            val tabs = listOf(
                "WIPEOUT" to "5-Step Wipeout",
                "HOTSPOTS" to "Hotspot Checklist",
                "ANATOMY" to "Larva Anatomy Guide",
                "HIGH_THINKING" to "Vector Thinking Lab"
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tabs) { (key, label) ->
                    val isSelected = activeTab == key
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clickable { activeTab = key }
                            .testTag("tab_$key")
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        when (activeTab) {
            "WIPEOUT" -> {
                item {
                    FiveStepWipeoutSection()
                }
            }

            "HOTSPOTS" -> {
                item {
                    Text(
                        text = "Household Inspection Checklist (${hotspots.count { it.isCompleted }}/${hotspots.size} Checked)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(hotspots) { hotspot ->
                    HotspotChecklistItem(
                        hotspot = hotspot,
                        onToggle = { viewModel.toggleHotspot(hotspot.id) }
                    )
                }
            }

            "ANATOMY" -> {
                item {
                    LarvaAnatomyGuideSection()
                }
            }

            "HIGH_THINKING" -> {
                item {
                    HighThinkingLabSection(
                        query = customQueryText,
                        onQueryChange = { customQueryText = it },
                        isThinking = isThinking,
                        result = thinkingResult,
                        onRunQuery = { viewModel.runHighThinkingQuery(customQueryText) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun FiveStepWipeoutSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ElevatedCard(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "The 5-Step Mozzie Wipeout Protocol",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Aedes mosquitoes breed primarily in clean stagnant domestic water. Follow these 5 proven steps every alternate day to break the 7-day aquatic mosquito life cycle.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        WipeoutStepCard(
            stepNumber = "1",
            title = "Turn Over All Water Storage Pails",
            description = "Empty buckets, watering cans, and basins. Store them inverted so they cannot collect water from rain or garden hoses."
        )

        WipeoutStepCard(
            stepNumber = "2",
            title = "Change Water in Flower Vases Every 2 Days",
            description = "Wash and brush the inner sides of vases with a scrub cloth to dislodge sticky mosquito eggs before refilling."
        )

        WipeoutStepCard(
            stepNumber = "3",
            title = "Empty and Scrub Plant Pot Saucers",
            description = "Drain saucers every alternate day. Do not leave water standing under bonsai, ficus, or money plants."
        )

        WipeoutStepCard(
            stepNumber = "4",
            title = "Loosen Hardened Soil in Potted Plants",
            description = "Compacted soil prevents drainage and forms small surface puddles where Aedes lay single eggs."
        )

        WipeoutStepCard(
            stepNumber = "5",
            title = "Clear Roof Gutters & Add Bti Larvicide",
            description = "Remove fallen leaves from gutters monthly. Add Bacillus thuringiensis israelensis (Bti) granules to stagnant sumps."
        )
    }
}

@Composable
private fun WipeoutStepCard(stepNumber: String, title: String, description: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun HotspotChecklistItem(
    hotspot: HouseholdHotspot,
    onToggle: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (hotspot.isCompleted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = hotspot.isCompleted,
                onCheckedChange = { onToggle() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = hotspot.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = hotspot.location,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Risk: ${hotspot.riskFactor}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Prevention: ${hotspot.preventionTip}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Frequency: ${hotspot.checkFrequency}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun LarvaAnatomyGuideSection() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Image(
                    painter = painterResource(id = R.drawable.img_larva_guide),
                    contentDescription = "Mosquito Larva Anatomy Comparison Diagram",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Taxonomic Larval Identification Keys",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Mosquito larvae can be rapidly differentiated by the presence, length, and resting angle of the posterior respiratory siphon.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "1. Aedes (Dengue & Yellow Fever Vector):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• Siphon: Short, dark, barrel-shaped (Siphon Index ~2.0)\n• Position: Suspended obliquely at ~45° from the water surface tension film\n• Motion: Active serpentine S-shaped swimming; photophobic (dives away from light)\n• Habitat: Clean domestic containers, flower pots, drums",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "2. Culex (Southern House Mosquito):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• Siphon: Long, slender, tube-like (Siphon Index ~4.0 to 5.0)\n• Position: Hangs nearly vertical at ~90° straight down from the surface\n• Habitat: Organically enriched, muddy or stagnant drain water",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "3. Anopheles (Malaria Vector):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• Siphon: Completely absent (breaths through palmate hairs directly on body)\n• Position: Lies horizontally flat and parallel right beneath the water surface\n• Habitat: Sunlit pools, slow streams, vegetated wetlands",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

/**
 * High Thinking Vector Science Lab
 * Integrates gemini-3.1-pro-preview with ThinkingLevel.HIGH
 * As strictly instructed in system feature:
 * "You MUST add thinking mode to the app where relevant to handle users' most complex queries.
 * You MUST use the gemini-3.1-pro-preview model and set thinkingLevel to ThinkingLevel.HIGH. Do not set maxOutputTokens."
 */
@Composable
private fun HighThinkingLabSection(
    query: String,
    onQueryChange: (String) -> Unit,
    isThinking: Boolean,
    result: String?,
    onRunQuery: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        ElevatedCard(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Vector Science & High Thinking Lab",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "Powered by gemini-3.1-pro-preview with ThinkingLevel.HIGH",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ask complex questions regarding diapause egg desiccation, insecticide resistance management, transovarial virus transmission, and micro-climate vector modeling.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }

        // Preset inquiries
        Text(
            text = "Select Specialized Entomology Topic:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        val presets = listOf(
            "How do Aedes eggs survive 6 months in dry flower pots without water?",
            "Compare Bti vs Pyriproxyfen resistance and safety in household water tanks",
            "Differentiating Aedes albopictus vs Culex in dark basement sumps",
            "What temperature and humidity triggers rapid Dengue extrinsic incubation?"
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            presets.forEach { preset ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onQueryChange(preset) }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Science,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = preset,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Input Field
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Custom Vector or Entomology Query") },
            placeholder = { Text("e.g. Can Aedes larvae survive in chlorinated swimming pools?") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("high_thinking_query_input"),
            shape = RoundedCornerShape(12.dp),
            minLines = 2
        )

        Button(
            onClick = onRunQuery,
            enabled = !isThinking && query.isNotBlank(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("run_high_thinking_button"),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (isThinking) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Synthesizing with High Thinking...")
            } else {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analyze with High Thinking Mode")
            }
        }

        // Thinking Result Display
        result?.let { analysisText ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("high_thinking_result_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Scientific Reasoning & Vector Strategy",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = analysisText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}
