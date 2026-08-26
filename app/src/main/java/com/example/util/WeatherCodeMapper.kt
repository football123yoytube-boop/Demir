package com.example.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.SevereCold
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class WeatherInfo(
    val description: String,
    val icon: ImageVector,
    val emoji: String,
    val accentColor: Color
)

object WeatherCodeMapper {

    fun getWeatherInfo(code: Int?, isDay: Boolean = true): WeatherInfo {
        return when (code) {
            0 -> WeatherInfo(
                description = if (isDay) "Açık" else "Açık Gece",
                icon = if (isDay) Icons.Default.WbSunny else Icons.Default.NightsStay,
                emoji = if (isDay) "☀️" else "🌙",
                accentColor = if (isDay) Color(0xFFFFB300) else Color(0xFF90CAF9)
            )
            1 -> WeatherInfo(
                description = "Çoğunlukla Açık",
                icon = if (isDay) Icons.Default.WbSunny else Icons.Default.NightsStay,
                emoji = if (isDay) "🌤️" else "🌑",
                accentColor = Color(0xFFFFCA28)
            )
            2 -> WeatherInfo(
                description = "Parçalı Bulutlu",
                icon = Icons.Default.WbCloudy,
                emoji = "⛅",
                accentColor = Color(0xFF81D4FA)
            )
            3 -> WeatherInfo(
                description = "Kapalı",
                icon = Icons.Default.Cloud,
                emoji = "☁️",
                accentColor = Color(0xFFB0BEC5)
            )
            45, 48 -> WeatherInfo(
                description = "Sisli",
                icon = Icons.Default.Air,
                emoji = "🌫️",
                accentColor = Color(0xFFCFD8DC)
            )
            51, 53, 55 -> WeatherInfo(
                description = "Çiseleme",
                icon = Icons.Default.WaterDrop,
                emoji = "🌦️",
                accentColor = Color(0xFF4FC3F7)
            )
            56, 57 -> WeatherInfo(
                description = "Dondurucu Çisenti",
                icon = Icons.Default.SevereCold,
                emoji = "🌨️",
                accentColor = Color(0xFF80DEEA)
            )
            61, 63, 65 -> WeatherInfo(
                description = "Yağmurlu",
                icon = Icons.Default.WaterDrop,
                emoji = "🌧️",
                accentColor = Color(0xFF29B6F6)
            )
            66, 67 -> WeatherInfo(
                description = "Dondurucu Yağmur",
                icon = Icons.Default.SevereCold,
                emoji = "🌨️",
                accentColor = Color(0xFF4DD0E1)
            )
            71, 73, 75 -> WeatherInfo(
                description = "Karlı",
                icon = Icons.Default.Grain,
                emoji = "❄️",
                accentColor = Color(0xFFE0F7FA)
            )
            77 -> WeatherInfo(
                description = "Kar Taneleri",
                icon = Icons.Default.Grain,
                emoji = "🌨️",
                accentColor = Color(0xFFB2EBF2)
            )
            80, 81, 82 -> WeatherInfo(
                description = "Sağanak Yağış",
                icon = Icons.Default.WaterDrop,
                emoji = "⛈️",
                accentColor = Color(0xFF0288D1)
            )
            85, 86 -> WeatherInfo(
                description = "Kar Sağanağı",
                icon = Icons.Default.Grain,
                emoji = "🌨️",
                accentColor = Color(0xFFB3E5FC)
            )
            95 -> WeatherInfo(
                description = "Gök Gürültülü Fırtına",
                icon = Icons.Default.Thunderstorm,
                emoji = "⚡",
                accentColor = Color(0xFFFF7043)
            )
            96, 99 -> WeatherInfo(
                description = "Dolu İhtimalli Fırtına",
                icon = Icons.Default.Thunderstorm,
                emoji = "⛈️",
                accentColor = Color(0xFFFF5722)
            )
            else -> WeatherInfo(
                description = "Açık",
                icon = if (isDay) Icons.Default.WbSunny else Icons.Default.NightsStay,
                emoji = if (isDay) "☀️" else "🌙",
                accentColor = Color(0xFFFFB300)
            )
        }
    }
}
