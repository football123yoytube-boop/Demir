package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.util.AppFontFamily
import com.example.util.ClockAlignment
import com.example.util.DayNumberFormat
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserDashboardPreferences(
    val themeId: String = "pure_white",
    val customTheme: DashboardThemeConfig = ThemePresets.PureWhite,
    val isCustomThemeActive: Boolean = false,
    
    // Clock Settings
    val clockFont: AppFontFamily = AppFontFamily.INTER,
    val is24Hour: Boolean = true,
    val showSeconds: Boolean = true,
    val clockScale: Float = 1.0f,
    val clockColorHex: Long = 0xFF111827,
    val clockGlowEnabled: Boolean = true,
    val clockShadowEnabled: Boolean = true,
    val clockOpacity: Float = 1.0f,
    val clockLetterSpacing: Float = 0.0f,
    val clockAlignment: ClockAlignment = ClockAlignment.CENTER,
    val dayNumberFormat: DayNumberFormat = DayNumberFormat.BIG_DAY_LABEL,

    // Background & Card Visuals
    val backgroundType: String = "THEME_GRADIENT", // THEME_GRADIENT, ANIMATED_GRADIENT, SOLID, USER_IMAGE, LUXURY_WALLPAPER
    val userImageUri: String? = null,
    val wallpaperPresetIndex: Int = 0,
    val backgroundBlur: Float = 0f,
    val backgroundOpacity: Float = 1.0f,
    val backgroundDarkeningScrim: Float = 0.0f,
    val cardOpacity: Float = 0.90f,
    val cardCornerRadius: Int = 24,
    val cardShadowElevation: Int = 8,

    // Widget Visibility Toggles & Selected Interactive Home Screen Widgets (Max 3 Side-by-Side)
    val showClockWidget: Boolean = true,
    val showDateWidget: Boolean = true,
    val showDayNumberWidget: Boolean = false,
    val showLocationWidget: Boolean = true,
    val showWeatherWidget: Boolean = true, // Weather widget cannot be removed
    val showWeatherDetailsWidget: Boolean = true,
    val showSunWidget: Boolean = true,
    val showMoonWidget: Boolean = true,
    val showTelemetryWidget: Boolean = true,
    val showCompassWidget: Boolean = true,
    val showQuickToolsWidget: Boolean = true,
    val activeHomeScreenWidgetIds: List<String> = listOf("focus_pomodoro", "hydration_tracker"),

    // Display & Power Settings
    val keepScreenOn: Boolean = true,
    val autoNightDim: Boolean = false,
    val autoWarmNightTint: Boolean = false,
    val dayNightAutoTheme: Boolean = false,
    val screenSaverMode: Boolean = true,
    val animationIntensity: String = "HIGH", // OFF, LOW, MEDIUM, HIGH
    val hapticFeedbackEnabled: Boolean = true,
    
    // Units
    val temperatureUnit: String = "C", // C, F
    val windSpeedUnit: String = "kmh", // kmh, mph, ms
    val weatherRefreshIntervalMinutes: Int = 15,

    // Cache
    val lastKnownCityName: String = "İstanbul, Türkiye",
    val lastKnownLat: Double = 41.0082,
    val lastKnownLng: Double = 28.9784,
    val lastUpdatedTimeFormatted: String = "--:--"
)

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("live_dashboard_prefs", Context.MODE_PRIVATE)
    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val themeAdapter = moshi.adapter(DashboardThemeConfig::class.java)

    private val _preferences = MutableStateFlow(loadPreferences())
    val preferences: StateFlow<UserDashboardPreferences> = _preferences.asStateFlow()

    private fun loadPreferences(): UserDashboardPreferences {
        val themeId = prefs.getString("themeId", "pure_white") ?: "pure_white"
        val customThemeJson = prefs.getString("customThemeJson", null)
        val customTheme = if (customThemeJson != null) {
            try {
                themeAdapter.fromJson(customThemeJson) ?: ThemePresets.PureWhite
            } catch (e: Exception) {
                ThemePresets.PureWhite
            }
        } else {
            ThemePresets.PureWhite
        }

        val defaultWidgets = "focus_pomodoro,hydration_tracker"
        val activeWidgetsString = prefs.getString("activeHomeScreenWidgetIds", defaultWidgets) ?: defaultWidgets
        val activeWidgetsList = activeWidgetsString.split(",").filter { it.isNotBlank() }

        return UserDashboardPreferences(
            themeId = themeId,
            customTheme = customTheme,
            isCustomThemeActive = prefs.getBoolean("isCustomThemeActive", false),
            clockFont = AppFontFamily.fromName(prefs.getString("clockFont", AppFontFamily.INTER.name)),
            is24Hour = prefs.getBoolean("is24Hour", true),
            showSeconds = prefs.getBoolean("showSeconds", true),
            clockScale = prefs.getFloat("clockScale", 1.0f),
            clockColorHex = prefs.getLong("clockColorHex", 0xFF111827),
            clockGlowEnabled = prefs.getBoolean("clockGlowEnabled", true),
            clockShadowEnabled = prefs.getBoolean("clockShadowEnabled", true),
            clockOpacity = prefs.getFloat("clockOpacity", 1.0f),
            clockLetterSpacing = prefs.getFloat("clockLetterSpacing", 0.0f),
            clockAlignment = ClockAlignment.valueOf(prefs.getString("clockAlignment", ClockAlignment.CENTER.name) ?: ClockAlignment.CENTER.name),
            dayNumberFormat = DayNumberFormat.valueOf(prefs.getString("dayNumberFormat", DayNumberFormat.BIG_DAY_LABEL.name) ?: DayNumberFormat.BIG_DAY_LABEL.name),
            backgroundType = prefs.getString("backgroundType", "THEME_GRADIENT") ?: "THEME_GRADIENT",
            userImageUri = prefs.getString("userImageUri", null),
            wallpaperPresetIndex = prefs.getInt("wallpaperPresetIndex", 0),
            backgroundBlur = prefs.getFloat("backgroundBlur", 0f),
            backgroundOpacity = prefs.getFloat("backgroundOpacity", 1.0f),
            backgroundDarkeningScrim = prefs.getFloat("backgroundDarkeningScrim", 0.0f),
            cardOpacity = prefs.getFloat("cardOpacity", 0.90f),
            cardCornerRadius = prefs.getInt("cardCornerRadius", 24),
            cardShadowElevation = prefs.getInt("cardShadowElevation", 8),
            showClockWidget = prefs.getBoolean("showClockWidget", true),
            showDateWidget = prefs.getBoolean("showDateWidget", true),
            showDayNumberWidget = prefs.getBoolean("showDayNumberWidget", false),
            showLocationWidget = prefs.getBoolean("showLocationWidget", true),
            showWeatherWidget = true, // Weather widget cannot be removed
            showWeatherDetailsWidget = prefs.getBoolean("showWeatherDetailsWidget", true),
            showSunWidget = prefs.getBoolean("showSunWidget", true),
            showMoonWidget = prefs.getBoolean("showMoonWidget", true),
            showTelemetryWidget = prefs.getBoolean("showTelemetryWidget", true),
            showCompassWidget = prefs.getBoolean("showCompassWidget", true),
            showQuickToolsWidget = prefs.getBoolean("showQuickToolsWidget", true),
            activeHomeScreenWidgetIds = activeWidgetsList,
            keepScreenOn = prefs.getBoolean("keepScreenOn", true),
            autoNightDim = prefs.getBoolean("autoNightDim", false),
            autoWarmNightTint = prefs.getBoolean("autoWarmNightTint", false),
            dayNightAutoTheme = prefs.getBoolean("dayNightAutoTheme", false),
            screenSaverMode = prefs.getBoolean("screenSaverMode", true),
            animationIntensity = prefs.getString("animationIntensity", "HIGH") ?: "HIGH",
            hapticFeedbackEnabled = prefs.getBoolean("hapticFeedbackEnabled", true),
            temperatureUnit = prefs.getString("temperatureUnit", "C") ?: "C",
            windSpeedUnit = prefs.getString("windSpeedUnit", "kmh") ?: "kmh",
            weatherRefreshIntervalMinutes = prefs.getInt("weatherRefreshIntervalMinutes", 15),
            lastKnownCityName = prefs.getString("lastKnownCityName", "İstanbul, Türkiye") ?: "İstanbul, Türkiye",
            lastKnownLat = prefs.getFloat("lastKnownLat", 41.0082f).toDouble(),
            lastKnownLng = prefs.getFloat("lastKnownLng", 28.9784f).toDouble(),
            lastUpdatedTimeFormatted = prefs.getString("lastUpdatedTimeFormatted", "--:--") ?: "--:--"
        )
    }

    fun updatePreferences(transform: (UserDashboardPreferences) -> UserDashboardPreferences) {
        val current = _preferences.value
        val updated = transform(current)
        _preferences.value = updated

        prefs.edit().apply {
            putString("themeId", updated.themeId)
            putBoolean("isCustomThemeActive", updated.isCustomThemeActive)
            putString("customThemeJson", themeAdapter.toJson(updated.customTheme))
            putString("clockFont", updated.clockFont.name)
            putBoolean("is24Hour", updated.is24Hour)
            putBoolean("showSeconds", updated.showSeconds)
            putFloat("clockScale", updated.clockScale)
            putLong("clockColorHex", updated.clockColorHex)
            putBoolean("clockGlowEnabled", updated.clockGlowEnabled)
            putBoolean("clockShadowEnabled", updated.clockShadowEnabled)
            putFloat("clockOpacity", updated.clockOpacity)
            putFloat("clockLetterSpacing", updated.clockLetterSpacing)
            putString("clockAlignment", updated.clockAlignment.name)
            putString("dayNumberFormat", updated.dayNumberFormat.name)
            putString("backgroundType", updated.backgroundType)
            putString("userImageUri", updated.userImageUri)
            putInt("wallpaperPresetIndex", updated.wallpaperPresetIndex)
            putFloat("backgroundBlur", updated.backgroundBlur)
            putFloat("backgroundOpacity", updated.backgroundOpacity)
            putFloat("backgroundDarkeningScrim", updated.backgroundDarkeningScrim)
            putFloat("cardOpacity", updated.cardOpacity)
            putInt("cardCornerRadius", updated.cardCornerRadius)
            putInt("cardShadowElevation", updated.cardShadowElevation)
            putBoolean("showClockWidget", updated.showClockWidget)
            putBoolean("showDateWidget", updated.showDateWidget)
            putBoolean("showDayNumberWidget", updated.showDayNumberWidget)
            putBoolean("showLocationWidget", updated.showLocationWidget)
            putBoolean("showWeatherWidget", updated.showWeatherWidget)
            putBoolean("showWeatherDetailsWidget", updated.showWeatherDetailsWidget)
            putBoolean("showSunWidget", updated.showSunWidget)
            putBoolean("showMoonWidget", updated.showMoonWidget)
            putBoolean("showTelemetryWidget", updated.showTelemetryWidget)
            putBoolean("showCompassWidget", updated.showCompassWidget)
            putBoolean("showQuickToolsWidget", updated.showQuickToolsWidget)
            putString("activeHomeScreenWidgetIds", updated.activeHomeScreenWidgetIds.joinToString(","))
            putBoolean("keepScreenOn", updated.keepScreenOn)
            putBoolean("autoNightDim", updated.autoNightDim)
            putBoolean("autoWarmNightTint", updated.autoWarmNightTint)
            putBoolean("dayNightAutoTheme", updated.dayNightAutoTheme)
            putBoolean("screenSaverMode", updated.screenSaverMode)
            putString("animationIntensity", updated.animationIntensity)
            putBoolean("hapticFeedbackEnabled", updated.hapticFeedbackEnabled)
            putString("temperatureUnit", updated.temperatureUnit)
            putString("windSpeedUnit", updated.windSpeedUnit)
            putInt("weatherRefreshIntervalMinutes", updated.weatherRefreshIntervalMinutes)
            putString("lastKnownCityName", updated.lastKnownCityName)
            putFloat("lastKnownLat", updated.lastKnownLat.toFloat())
            putFloat("lastKnownLng", updated.lastKnownLng.toFloat())
            putString("lastUpdatedTimeFormatted", updated.lastUpdatedTimeFormatted)
            apply()
        }
    }

    fun exportThemeToJson(): String {
        return themeAdapter.toJson(_preferences.value.customTheme)
    }

    fun importThemeFromJson(json: String): Boolean {
        return try {
            val imported = themeAdapter.fromJson(json) ?: return false
            updatePreferences { it.copy(customTheme = imported, isCustomThemeActive = true) }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun resetToDefaults() {
        prefs.edit().clear().apply()
        _preferences.value = UserDashboardPreferences()
    }
}
