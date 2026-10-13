package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.data.api.GeminiLarvaService
import com.example.data.local.AppDatabase
import com.example.data.model.BreedingReport
import com.example.data.model.HouseholdHotspot
import com.example.data.model.LarvaScanResult
import com.example.data.model.NeighborhoodAlert
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AedesRepository(
    private val context: Context,
    private val database: AppDatabase = AppDatabase.getInstance(context),
    private val geminiService: GeminiLarvaService = GeminiLarvaService()
) {
    private val reportDao = database.reportDao()
    private val alertDao = database.alertDao()
    private val scanDao = database.scanDao()

    init {
        // Populate initial realistic community breeding reports and municipal alerts if empty
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfEmpty()
        }
    }

    // Reports Flow
    fun getAllReports(): Flow<List<BreedingReport>> = reportDao.getAllReports()

    suspend fun submitReport(report: BreedingReport): Long {
        return reportDao.insertReport(report)
    }

    suspend fun updateReportStatus(reportId: Long, status: String) {
        reportDao.updateStatus(reportId, status)
    }

    // Alerts Flow
    fun getAllAlerts(): Flow<List<NeighborhoodAlert>> = alertDao.getAllAlerts()

    suspend fun markAlertAsRead(alertId: Long) {
        alertDao.markAsRead(alertId)
    }

    suspend fun addAlert(alert: NeighborhoodAlert): Long {
        return alertDao.insertAlert(alert)
    }

    // Scans Flow
    fun getAllScans(): Flow<List<LarvaScanResult>> = scanDao.getAllScans()

    suspend fun saveScan(scan: LarvaScanResult): Long {
        return scanDao.insertScan(scan)
    }

    suspend fun analyzeLarva(bitmap: Bitmap, containerHint: String? = null): LarvaScanResult {
        val result = geminiService.analyzeLarvaImage(bitmap, containerHint)
        scanDao.insertScan(result)
        return result
    }

    suspend fun askHighThinkingVectorIntelligence(query: String, context: String? = null): String {
        return geminiService.queryVectorIntelligenceHighThinking(query, context)
    }

    fun getHouseholdHotspots(): List<HouseholdHotspot> {
        return listOf(
            HouseholdHotspot(
                id = "hotspot_1",
                title = "Flower Pot Plates & Saucers",
                location = "Balcony / Garden",
                riskFactor = "Top domestic source; collects runoff water; Aedes eggs stick to rim.",
                preventionTip = "Empty water plates every alternate day. Scrub bottom and edges vigorously to remove eggs.",
                checkFrequency = "Every 2 days"
            ),
            HouseholdHotspot(
                id = "hotspot_2",
                title = "Air Conditioner Drip Trays",
                location = "External Walls / Windows",
                riskFactor = "Constant steady condensation provides ideal shaded larval breeding.",
                preventionTip = "Direct runoff pipe to proper drain, or add Bti microbial sand to the tray weekly.",
                checkFrequency = "Weekly"
            ),
            HouseholdHotspot(
                id = "hotspot_3",
                title = "Clogged Roof Gutters & Drains",
                location = "Roof & Perimeter",
                riskFactor = "Decaying leaves block water flow, creating undisturbed murky pools.",
                preventionTip = "Clear fallen leaves monthly. Apply Bti granules or insect growth regulator.",
                checkFrequency = "Bi-weekly"
            ),
            HouseholdHotspot(
                id = "hotspot_4",
                title = "Water Storage Barrels & Cisterns",
                location = "Yard / Service Area",
                riskFactor = "Massive volume can produce thousands of adult vectors within 7 days.",
                preventionTip = "Seal tightly with fine mosquito netting (<1mm mesh) or fitted lids.",
                checkFrequency = "Weekly inspection"
            ),
            HouseholdHotspot(
                id = "hotspot_5",
                title = "Pet Bowls & Ornamental Vases",
                location = "Indoor / Living Room",
                riskFactor = "Clean water changed irregularly allows complete 7-day larval maturation.",
                preventionTip = "Change water and brush bowl/vase interior every 2 days.",
                checkFrequency = "Daily / 2 days"
            ),
            HouseholdHotspot(
                id = "hotspot_6",
                title = "Discarded Tires & Plastic Trash",
                location = "Perimeter / Backyard",
                riskFactor = "Tires retain heat and rainwater, creating microclimate optimized for Aedes albopictus.",
                preventionTip = "Drill drainage holes or puncture tires; store under shelter.",
                checkFrequency = "Weekly"
            )
        )
    }

    private suspend fun seedInitialDataIfEmpty() {
        if (reportDao.getCount() == 0) {
            val now = System.currentTimeMillis()
            val dayMillis = 86400000L
            val sampleReports = listOf(
                BreedingReport(
                    containerType = "Discarded Tires in Yard",
                    severity = "CRITICAL",
                    neighborhood = "Greenwood North",
                    address = "42 Elmwood Ave, Greenwood",
                    latitude = 1.3521,
                    longitude = 103.8198,
                    isAnonymous = true,
                    reporterName = "Anonymous Citizen",
                    notes = "Over 50 wigglers found in discarded tires after weekend rain. Aedes stripes observed on dead adults.",
                    larvaCountEstimate = "50+ (Heavy Infestation)",
                    status = "Pending Inspection",
                    timestamp = now - (1 * 3600000L)
                ),
                BreedingReport(
                    containerType = "Flower Pot Plate",
                    severity = "HIGH",
                    neighborhood = "Downtown Riverside",
                    address = "120 Riverfront Terrace, Apt 4B",
                    latitude = 1.3480,
                    longitude = 103.8250,
                    isAnonymous = false,
                    reporterName = "Marcus Tan",
                    notes = "Stagnant saucer under ficus bonsai with ~25 active mosquito larvae hanging at 45 degree angle.",
                    larvaCountEstimate = "10-50 (Moderate)",
                    status = "Officer Dispatched",
                    timestamp = now - (6 * 3600000L)
                ),
                BreedingReport(
                    containerType = "Clogged Perimeter Drain",
                    severity = "CRITICAL",
                    neighborhood = "Sunset Hills",
                    address = "Block 18 Sunset Way",
                    latitude = 1.3590,
                    longitude = 103.8110,
                    isAnonymous = true,
                    reporterName = "Anonymous Citizen",
                    notes = "Severe water ponding caused by construction debris. Heavy swarm of mosquito larvae moving in S-patterns.",
                    larvaCountEstimate = "50+ (Heavy Infestation)",
                    status = "Pending Inspection",
                    timestamp = now - (14 * 3600000L)
                ),
                BreedingReport(
                    containerType = "Uncovered Water Drum",
                    severity = "HIGH",
                    neighborhood = "Eastview Park",
                    address = "89 Eastview Crescent",
                    latitude = 1.3410,
                    longitude = 103.8340,
                    isAnonymous = true,
                    reporterName = "Anonymous Citizen",
                    notes = "Open blue 200L plastic drum collecting rainwater. Active larvae observed near surface.",
                    larvaCountEstimate = "10-50 (Moderate)",
                    status = "Treated & Cleared",
                    timestamp = now - (2 * dayMillis)
                ),
                BreedingReport(
                    containerType = "Aircon Drip Tray",
                    severity = "MEDIUM",
                    neighborhood = "Metro Central",
                    address = "334 Orchard Boulevard, Unit 12",
                    latitude = 1.3550,
                    longitude = 103.8290,
                    isAnonymous = false,
                    reporterName = "Sarah Jenkins",
                    notes = "Condensation drip tray has green algae and small larvae developing.",
                    larvaCountEstimate = "1-10 (Early)",
                    status = "Treated & Cleared",
                    timestamp = now - (4 * dayMillis)
                ),
                BreedingReport(
                    containerType = "Construction Ground Depression",
                    severity = "CRITICAL",
                    neighborhood = "Sunset Hills",
                    address = "Hillview Avenue Site 3",
                    latitude = 1.3620,
                    longitude = 103.8080,
                    isAnonymous = true,
                    reporterName = "Anonymous Citizen",
                    notes = "Water collection in foundation pit with thousands of larvae and pupae.",
                    larvaCountEstimate = "50+ (Heavy Infestation)",
                    status = "Officer Dispatched",
                    timestamp = now - (5 * dayMillis)
                ),
                BreedingReport(
                    containerType = "Roof Gutter Ponding",
                    severity = "LOW",
                    neighborhood = "Greenwood North",
                    address = "15 Greenwood Lane",
                    latitude = 1.3535,
                    longitude = 103.8160,
                    isAnonymous = false,
                    reporterName = "David Lee",
                    notes = "Slight accumulation in gutter corner after autumn leaf fall.",
                    larvaCountEstimate = "1-10 (Early)",
                    status = "Treated & Cleared",
                    timestamp = now - (7 * dayMillis)
                )
            )
            reportDao.insertAllReports(sampleReports)
        }

        if (alertDao.getCount() == 0) {
            val now = System.currentTimeMillis()
            val sampleAlerts = listOf(
                NeighborhoodAlert(
                    title = "Red Cluster Warning: Dengue Outbreak",
                    neighborhood = "Greenwood North",
                    alertType = "CLUSTER_OUTBREAK",
                    severity = "CRITICAL",
                    description = "Health Authority has declared a RED ALERT CLUSTER with 12 confirmed Dengue cases within 200m. Vector surveillance indicates elevated Aedes aegypti larval presence.",
                    scheduledTime = "Active immediately",
                    actionPrompt = "Inspect all household containers twice daily; apply insect repellent (DEET/Picaridin).",
                    isRead = false,
                    timestamp = now - (2 * 3600000L)
                ),
                NeighborhoodAlert(
                    title = "Scheduled Thermal Fogging & Bti Misting",
                    neighborhood = "Sunset Hills Sector 2 & 4",
                    alertType = "SCHEDULED_MAINTENANCE",
                    severity = "HIGH",
                    description = "Municipal Vector Control crews will conduct Ultra-Low Volume (ULV) space spray and Bti microbial larviciding to disrupt adult mosquitoes and aquatic larvae.",
                    scheduledTime = "Tomorrow, 08:30 AM - 11:30 AM",
                    actionPrompt = "Keep external windows closed during misting. Cover food and bird cages.",
                    isRead = false,
                    timestamp = now - (10 * 3600000L)
                ),
                NeighborhoodAlert(
                    title = "Post-Rainfall Larva Wipeout Alert",
                    neighborhood = "All City Districts",
                    alertType = "WEATHER_POST_RAIN",
                    severity = "INFO",
                    description = "Heavy monsoon rainfall recorded over the past 48 hours. Aedes mosquito eggs hatch within hours of submersion. Perform the 5-step Mozzie Wipeout today.",
                    scheduledTime = "Recommended within 24 hours",
                    actionPrompt = "Empty plant pot plates, overturn buckets, loosen hardened flowerbed soil.",
                    isRead = true,
                    timestamp = now - (36 * 3600000L)
                ),
                NeighborhoodAlert(
                    title = "Drainage Jetting & Sump Maintenance",
                    neighborhood = "Downtown Riverside",
                    alertType = "SCHEDULED_MAINTENANCE",
                    severity = "HIGH",
                    description = "Public works team will flush storm water sumps and perimeter scupper drains to eliminate Culex and Aedes breeding pockets.",
                    scheduledTime = "Friday, 02:00 PM - 05:00 PM",
                    actionPrompt = "Report any unserviced clogged roadside drains via the Community Report tab.",
                    isRead = true,
                    timestamp = now - (72 * 3600000L)
                )
            )
            alertDao.insertAllAlerts(sampleAlerts)
        }
    }
}
