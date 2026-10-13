package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BreedingReport
import com.example.data.model.HouseholdHotspot
import com.example.data.model.LarvaScanResult
import com.example.data.model.NeighborhoodAlert
import com.example.data.repository.AedesRepository
import com.example.ui.components.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AedesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AedesRepository(application.applicationContext)

    // --- Detection State ---
    private val _selectedBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedBitmap: StateFlow<Bitmap?> = _selectedBitmap.asStateFlow()

    private val _containerType = MutableStateFlow("Flower Pot / Saucer")
    val containerType: StateFlow<String> = _containerType.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _scanResult = MutableStateFlow<LarvaScanResult?>(null)
    val scanResult: StateFlow<LarvaScanResult?> = _scanResult.asStateFlow()

    private val _showPhotoGuide = MutableStateFlow(false)
    val showPhotoGuide: StateFlow<Boolean> = _showPhotoGuide.asStateFlow()

    val scanHistory: StateFlow<List<LarvaScanResult>> = repository.getAllScans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Reports & Map State ---
    val allReports: StateFlow<List<BreedingReport>> = repository.getAllReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _severityFilter = MutableStateFlow("ALL")
    val severityFilter: StateFlow<String> = _severityFilter.asStateFlow()

    private val _dateRangeFilter = MutableStateFlow("ALL")
    val dateRangeFilter: StateFlow<String> = _dateRangeFilter.asStateFlow()

    private val _selectedReportDetail = MutableStateFlow<BreedingReport?>(null)
    val selectedReportDetail: StateFlow<BreedingReport?> = _selectedReportDetail.asStateFlow()

    private val _isSubmittingReport = MutableStateFlow(false)
    val isSubmittingReport: StateFlow<Boolean> = _isSubmittingReport.asStateFlow()

    private val _reportSuccessMessage = MutableStateFlow<String?>(null)
    val reportSuccessMessage: StateFlow<String?> = _reportSuccessMessage.asStateFlow()

    // --- Alerts State ---
    val allAlerts: StateFlow<List<NeighborhoodAlert>> = repository.getAllAlerts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _subscribedNeighborhood = MutableStateFlow("All Neighborhoods")
    val subscribedNeighborhood: StateFlow<String> = _subscribedNeighborhood.asStateFlow()

    private val _pushNotificationsEnabled = MutableStateFlow(true)
    val pushNotificationsEnabled: StateFlow<Boolean> = _pushNotificationsEnabled.asStateFlow()

    private val _rainAlertsEnabled = MutableStateFlow(true)
    val rainAlertsEnabled: StateFlow<Boolean> = _rainAlertsEnabled.asStateFlow()

    private val _foggingAlertsEnabled = MutableStateFlow(true)
    val foggingAlertsEnabled: StateFlow<Boolean> = _foggingAlertsEnabled.asStateFlow()

    // --- High Thinking Vector Science Lab State ---
    private val _thinkingQuery = MutableStateFlow("")
    val thinkingQuery: StateFlow<String> = _thinkingQuery.asStateFlow()

    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking.asStateFlow()

    private val _thinkingResult = MutableStateFlow<String?>(null)
    val thinkingResult: StateFlow<String?> = _thinkingResult.asStateFlow()

    // --- Educational Hotspots State ---
    private val _hotspots = MutableStateFlow<List<HouseholdHotspot>>(repository.getHouseholdHotspots())
    val hotspots: StateFlow<List<HouseholdHotspot>> = _hotspots.asStateFlow()

    init {
        NotificationHelper.createNotificationChannel(application.applicationContext)
    }

    // --- Detection Functions ---
    fun setSelectedBitmap(bitmap: Bitmap?) {
        _selectedBitmap.value = bitmap
        _scanResult.value = null
    }

    fun setContainerType(type: String) {
        _containerType.value = type
    }

    fun togglePhotoGuide(show: Boolean) {
        _showPhotoGuide.value = show
    }

    fun analyzeLarva(bitmap: Bitmap) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            _selectedBitmap.value = bitmap
            try {
                val result = repository.analyzeLarva(bitmap, _containerType.value)
                _scanResult.value = result
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun loadMockSampleLarva(isAedes: Boolean) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            try {
                kotlinx.coroutines.delay(600) // Realistic scanning feedback
                val result = if (isAedes) {
                    LarvaScanResult(
                        speciesName = "Aedes aegypti (Linnaeus)",
                        commonName = "Yellow Fever / Dengue Vector Larva",
                        isAedes = true,
                        confidencePercent = 96,
                        riskLevel = "CRITICAL",
                        siphonType = "Short, dark barrel-shaped siphon (Siphon Index 2.0)",
                        hangingAngle = "~45° oblique angle from water surface tension",
                        keyFeatures = "Dark respiratory siphon with pecten spines, comb scales arranged in single row with lateral teeth, serpentine movement.",
                        actionSteps = "• Tip out standing water immediately onto dry ground\n• Scrub inner container walls with rough sponge or brush to remove eggs\n• Check flowerpot plates, gutters, and perimeter drains\n• Add Bti microbial larvicide if water cannot be drained",
                        detailedExplanation = "High-confidence detection of Aedes aegypti wiggler larva. Aedes mosquitoes lay desiccation-resistant eggs along container walls that hatch upon water immersion. Urgent vector elimination recommended."
                    )
                } else {
                    LarvaScanResult(
                        speciesName = "Culex quinquefasciatus",
                        commonName = "Southern House Mosquito Larva",
                        isAedes = false,
                        confidencePercent = 89,
                        riskLevel = "MODERATE",
                        siphonType = "Long, slender respiratory tube (Siphon Index 4.5)",
                        hangingAngle = "~90° vertical posture suspended downwards from meniscus",
                        keyFeatures = "Elongated breathing siphon tube with multiple subventral hair tufts. Prefers organically contaminated or stagnant drain water.",
                        actionSteps = "• Clear organic sludge and unblock water flow\n• Empty murky water receptacle\n• Treat with larvicidal soap or approved vector agent",
                        detailedExplanation = "Specimen identified as Culex quinquefasciatus larva. Distinguished from Aedes by its distinctly elongated breathing tube and vertical hanging posture."
                    )
                }
                repository.saveScan(result)
                _scanResult.value = result
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    // --- Report & Map Filters ---
    fun setSeverityFilter(severity: String) {
        _severityFilter.value = severity
    }

    fun setDateRangeFilter(range: String) {
        _dateRangeFilter.value = range
    }

    fun selectReportDetail(report: BreedingReport?) {
        _selectedReportDetail.value = report
    }

    fun submitBreedingReport(
        containerType: String,
        severity: String,
        neighborhood: String,
        address: String,
        isAnonymous: Boolean,
        reporterName: String,
        notes: String,
        larvaEstimate: String,
        latitude: Double = 1.3521 + (Math.random() - 0.5) * 0.03,
        longitude: Double = 103.8198 + (Math.random() - 0.5) * 0.03
    ) {
        viewModelScope.launch {
            _isSubmittingReport.value = true
            try {
                val report = BreedingReport(
                    containerType = containerType,
                    severity = severity,
                    neighborhood = neighborhood,
                    address = address.ifBlank { "$neighborhood sector" },
                    latitude = latitude,
                    longitude = longitude,
                    isAnonymous = isAnonymous,
                    reporterName = if (isAnonymous) "Anonymous Citizen" else reporterName.ifBlank { "Concerned Resident" },
                    notes = notes,
                    larvaCountEstimate = larvaEstimate,
                    status = "Pending Inspection",
                    timestamp = System.currentTimeMillis()
                )
                repository.submitReport(report)
                _reportSuccessMessage.value = "Breeding site reported successfully to municipal vector control!"
            } finally {
                _isSubmittingReport.value = false
            }
        }
    }

    fun clearReportSuccessMessage() {
        _reportSuccessMessage.value = null
    }

    fun markReportCleared(reportId: Long) {
        viewModelScope.launch {
            repository.updateReportStatus(reportId, "Treated & Cleared")
            _selectedReportDetail.value = _selectedReportDetail.value?.copy(status = "Treated & Cleared")
        }
    }

    // --- Alert Management ---
    fun markAlertRead(alertId: Long) {
        viewModelScope.launch {
            repository.markAlertAsRead(alertId)
        }
    }

    fun setSubscribedNeighborhood(neighborhood: String) {
        _subscribedNeighborhood.value = neighborhood
    }

    fun togglePushNotifications(enabled: Boolean) {
        _pushNotificationsEnabled.value = enabled
    }

    fun toggleRainAlerts(enabled: Boolean) {
        _rainAlertsEnabled.value = enabled
    }

    fun toggleFoggingAlerts(enabled: Boolean) {
        _foggingAlertsEnabled.value = enabled
    }

    fun sendTestPushNotification() {
        NotificationHelper.sendNeighborhoodAlert(
            getApplication(),
            title = "⚠️ Dengue Cluster Alert: ${_subscribedNeighborhood.value}",
            message = "Municipal Vector Control: High Aedes activity recorded. Inspect plant saucers & drains today!"
        )
    }

    // --- High Thinking Vector Science ---
    fun setThinkingQuery(query: String) {
        _thinkingQuery.value = query
    }

    fun runHighThinkingQuery(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _isThinking.value = true
            _thinkingQuery.value = query
            _thinkingResult.value = null
            try {
                val context = "Subscribed Zone: ${_subscribedNeighborhood.value}. Active severity alerts in city."
                val answer = repository.askHighThinkingVectorIntelligence(query, context)
                _thinkingResult.value = answer
            } finally {
                _isThinking.value = false
            }
        }
    }

    // --- Hotspot Toggle ---
    fun toggleHotspot(id: String) {
        _hotspots.value = _hotspots.value.map {
            if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
        }
    }
}
