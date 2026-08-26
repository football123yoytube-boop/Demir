package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CurrentWeather
import com.example.data.DailyWeather
import com.example.data.DashboardThemeConfig
import com.example.data.PreferencesManager
import com.example.data.ThemePresets
import com.example.data.UserDashboardPreferences
import com.example.data.WeatherRepository
import com.example.data.WeatherResponse
import com.example.data.WeatherResult
import com.example.manager.DeviceInfoManager
import com.example.manager.DeviceTelemetry
import com.example.manager.LocationData
import com.example.manager.LocationManager
import com.example.manager.TimerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class TimeState(
    val timeFormatted: String = "12:00:00",
    val hourMinuteFormatted: String = "12:00",
    val secondsFormatted: String = "00",
    val dayName: String = "ÇARŞAMBA",
    val fullDate: String = "26 AĞUSTOS 2026",
    val dayOfMonth: Int = 26,
    val monthName: String = "AĞUSTOS",
    val year: Int = 2026,
    val isNightHour: Boolean = false
)

data class DashboardUiState(
    val time: TimeState = TimeState(),
    val currentWeather: CurrentWeather? = null,
    val dailyWeather: DailyWeather? = null,
    val location: LocationData = LocationData(41.0082, 28.9784, "İstanbul", "Türkiye"),
    val telemetry: DeviceTelemetry = DeviceTelemetry(),
    val preferences: UserDashboardPreferences = UserDashboardPreferences(),
    val effectiveTheme: DashboardThemeConfig = ThemePresets.PureWhite,
    val isWeatherLoading: Boolean = false,
    val weatherStatusMessage: String = "Güncelleniyor...",
    val isOffline: Boolean = false,
    val areControlsVisible: Boolean = false,
    val isSettingsOpen: Boolean = false,
    val isQuickToolsOpen: Boolean = false,
    val isScreensaverActive: Boolean = false,
    val idleSeconds: Int = 0,
    val screensaverOffsetX: Float = 0f,
    val screensaverOffsetY: Float = 0f
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    private val locationManager = LocationManager(application)
    private val weatherRepository = WeatherRepository()
    private val deviceInfoManager = DeviceInfoManager(application)
    val timerManager = TimerManager(application, viewModelScope)

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var timeLoopJob: Job? = null
    private var weatherLoopJob: Job? = null
    private var controlsAutoHideJob: Job? = null
    private var idleJob: Job? = null

    private val turkishLocale = Locale("tr", "TR")

    init {
        deviceInfoManager.startListening()

        // Observe preferences
        viewModelScope.launch {
            preferencesManager.preferences.collectLatest { prefs ->
                val effective = calculateEffectiveTheme(prefs, _uiState.value.time.isNightHour, _uiState.value.currentWeather?.isDay == 1)
                _uiState.value = _uiState.value.copy(
                    preferences = prefs,
                    effectiveTheme = effective
                )
            }
        }

        // Observe telemetry
        viewModelScope.launch {
            deviceInfoManager.telemetry.collectLatest { telem ->
                _uiState.value = _uiState.value.copy(telemetry = telem)
            }
        }

        startTimeTicker()
        startIdleTicker()
        fetchLocationAndWeather()
        startPeriodicWeatherRefresh()
    }

    private fun startTimeTicker() {
        timeLoopJob?.cancel()
        timeLoopJob = viewModelScope.launch(Dispatchers.Default) {
            val fullDateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", turkishLocale)
            val dayNameFormatter = DateTimeFormatter.ofPattern("EEEE", turkishLocale)
            val time24Formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            val time12Formatter = DateTimeFormatter.ofPattern("hh:mm:ss a")
            val hm24Formatter = DateTimeFormatter.ofPattern("HH:mm")
            val hm12Formatter = DateTimeFormatter.ofPattern("hh:mm a")
            val secFormatter = DateTimeFormatter.ofPattern("ss")
            val monthFormatter = DateTimeFormatter.ofPattern("MMMM", turkishLocale)

            while (isActive) {
                val now = LocalDateTime.now()
                val is24h = _uiState.value.preferences.is24Hour
                val isNight = now.hour >= 21 || now.hour < 6

                val timeStr = if (is24h) now.format(time24Formatter) else now.format(time12Formatter)
                val hmStr = if (is24h) now.format(hm24Formatter) else now.format(hm12Formatter)
                val secStr = now.format(secFormatter)
                val dayName = now.format(dayNameFormatter).uppercase(turkishLocale)
                val fullDate = now.format(fullDateFormatter).uppercase(turkishLocale)
                val monthName = now.format(monthFormatter).uppercase(turkishLocale)

                val newTimeState = TimeState(
                    timeFormatted = timeStr,
                    hourMinuteFormatted = hmStr,
                    secondsFormatted = secStr,
                    dayName = dayName,
                    fullDate = fullDate,
                    dayOfMonth = now.dayOfMonth,
                    monthName = monthName,
                    year = now.year,
                    isNightHour = isNight
                )

                val currentEffectiveTheme = calculateEffectiveTheme(
                    _uiState.value.preferences,
                    isNight,
                    _uiState.value.currentWeather?.isDay == 1
                )

                _uiState.value = _uiState.value.copy(
                    time = newTimeState,
                    effectiveTheme = currentEffectiveTheme
                )

                delay(500)
            }
        }
    }

    private fun startIdleTicker() {
        idleJob?.cancel()
        idleJob = viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                delay(1000)
                val currentIdle = _uiState.value.idleSeconds + 1
                val isScreensaver = _uiState.value.preferences.screenSaverMode && currentIdle >= 60

                var offX = _uiState.value.screensaverOffsetX
                var offY = _uiState.value.screensaverOffsetY

                if (isScreensaver && currentIdle % 10 == 0) {
                    offX = ((-15..15).random()).toFloat()
                    offY = ((-10..10).random()).toFloat()
                }

                _uiState.value = _uiState.value.copy(
                    idleSeconds = currentIdle,
                    isScreensaverActive = isScreensaver,
                    screensaverOffsetX = offX,
                    screensaverOffsetY = offY
                )
            }
        }
    }

    fun onUserInteraction() {
        _uiState.value = _uiState.value.copy(
            idleSeconds = 0,
            isScreensaverActive = false,
            screensaverOffsetX = 0f,
            screensaverOffsetY = 0f
        )
    }

    fun toggleControlsVisibility() {
        onUserInteraction()
        if (_uiState.value.preferences.hapticFeedbackEnabled) {
            timerManager.triggerHaptic(false)
        }
        val nextState = !_uiState.value.areControlsVisible
        _uiState.value = _uiState.value.copy(areControlsVisible = nextState)

        if (nextState) {
            scheduleControlsAutoHide()
        } else {
            controlsAutoHideJob?.cancel()
        }
    }

    private fun scheduleControlsAutoHide() {
        controlsAutoHideJob?.cancel()
        controlsAutoHideJob = viewModelScope.launch {
            delay(5000)
            _uiState.value = _uiState.value.copy(areControlsVisible = false)
        }
    }

    fun openSettings() {
        onUserInteraction()
        if (_uiState.value.preferences.hapticFeedbackEnabled) {
            timerManager.triggerHaptic(true)
        }
        _uiState.value = _uiState.value.copy(isSettingsOpen = true, areControlsVisible = false)
    }

    fun closeSettings() {
        onUserInteraction()
        _uiState.value = _uiState.value.copy(isSettingsOpen = false)
    }

    fun toggleQuickTools() {
        onUserInteraction()
        if (_uiState.value.preferences.hapticFeedbackEnabled) {
            timerManager.triggerHaptic(false)
        }
        _uiState.value = _uiState.value.copy(isQuickToolsOpen = !_uiState.value.isQuickToolsOpen)
    }

    fun fetchLocationAndWeather() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isWeatherLoading = true)

            val prefs = _uiState.value.preferences
            val loc = locationManager.getCurrentLocation(prefs.lastKnownLat, prefs.lastKnownLng)
            
            _uiState.value = _uiState.value.copy(location = loc)
            preferencesManager.updatePreferences { 
                it.copy(
                    lastKnownCityName = loc.cityName,
                    lastKnownLat = loc.latitude,
                    lastKnownLng = loc.longitude
                )
            }

            fetchWeatherData(loc.latitude, loc.longitude)
        }
    }

    fun refreshWeatherManually() {
        onUserInteraction()
        if (_uiState.value.preferences.hapticFeedbackEnabled) {
            timerManager.triggerHaptic(false)
        }
        viewModelScope.launch {
            deviceInfoManager.refreshStaticAndNetworkInfo()
            fetchLocationAndWeather()
        }
    }

    private suspend fun fetchWeatherData(latitude: Double, longitude: Double) {
        _uiState.value = _uiState.value.copy(isWeatherLoading = true)
        val result = weatherRepository.getWeatherData(latitude, longitude)
        val nowHm = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

        when (result) {
            is WeatherResult.Success -> {
                val resp = result.response
                val isCached = result.isFromCache
                val statusMsg = if (isCached) "Son güncelleme: $nowHm (Çevrimdışı)" else "Güncel ($nowHm)"
                
                _uiState.value = _uiState.value.copy(
                    currentWeather = resp.current,
                    dailyWeather = resp.daily,
                    isWeatherLoading = false,
                    weatherStatusMessage = statusMsg,
                    isOffline = isCached
                )
                preferencesManager.updatePreferences {
                    it.copy(lastUpdatedTimeFormatted = nowHm)
                }
            }
            is WeatherResult.Error -> {
                _uiState.value = _uiState.value.copy(
                    isWeatherLoading = false,
                    weatherStatusMessage = "Çevrimdışı — veri alınamadı",
                    isOffline = true
                )
            }
        }
    }

    private fun startPeriodicWeatherRefresh() {
        weatherLoopJob?.cancel()
        weatherLoopJob = viewModelScope.launch {
            while (isActive) {
                val intervalMins = _uiState.value.preferences.weatherRefreshIntervalMinutes.coerceAtLeast(5)
                delay(intervalMins * 60 * 1000L)
                val loc = _uiState.value.location
                fetchWeatherData(loc.latitude, loc.longitude)
            }
        }
    }

    private fun calculateEffectiveTheme(
        prefs: UserDashboardPreferences,
        isNightHour: Boolean,
        isDayFromWeather: Boolean
    ): DashboardThemeConfig {
        if (prefs.isCustomThemeActive) {
            return prefs.customTheme
        }

        var baseTheme = ThemePresets.getThemeById(prefs.themeId)

        if (prefs.dayNightAutoTheme) {
            baseTheme = if (isDayFromWeather || !isNightHour) {
                ThemePresets.PureWhite
            } else {
                ThemePresets.Midnight
            }
        }

        if (prefs.autoNightDim && isNightHour) {
            baseTheme = baseTheme.copy(
                glassOpacity = (baseTheme.glassOpacity * 0.7f).coerceAtLeast(0.2f),
                textPrimary = 0xFFCCCCCC,
                textSecondary = 0xFF777777
            )
        }

        return baseTheme
    }

    fun updatePreferences(transform: (UserDashboardPreferences) -> UserDashboardPreferences) {
        preferencesManager.updatePreferences(transform)
    }

    fun resetPreferences() {
        preferencesManager.resetToDefaults()
        fetchLocationAndWeather()
    }

    fun exportTheme(): String {
        return preferencesManager.exportThemeToJson()
    }

    fun importTheme(json: String): Boolean {
        return preferencesManager.importThemeFromJson(json)
    }

    override fun onCleared() {
        super.onCleared()
        deviceInfoManager.stopListening()
        timeLoopJob?.cancel()
        weatherLoopJob?.cancel()
        idleJob?.cancel()
        controlsAutoHideJob?.cancel()
    }
}
