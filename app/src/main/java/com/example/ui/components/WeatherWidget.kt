package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CurrentWeather
import com.example.data.DashboardThemeConfig
import com.example.data.UserDashboardPreferences
import com.example.manager.LocationData
import com.example.util.WeatherCodeMapper
import kotlin.math.roundToInt

@Composable
fun WeatherWidget(
    currentWeather: CurrentWeather?,
    location: LocationData,
    preferences: UserDashboardPreferences,
    theme: DashboardThemeConfig,
    weatherStatus: String,
    isOffline: Boolean,
    modifier: Modifier = Modifier
) {
    val fontConfig = preferences.clockFont
    val isDay = currentWeather?.isDay == 1
    val weatherInfo = WeatherCodeMapper.getWeatherInfo(currentWeather?.weatherCode, isDay)

    val tempRaw = currentWeather?.temperature2m ?: 24.0
    val feelsLikeRaw = currentWeather?.apparentTemperature ?: 25.0
    val isFahrenheit = preferences.temperatureUnit == "F"

    val tempValue = if (isFahrenheit) (tempRaw * 9 / 5 + 32).roundToInt() else tempRaw.roundToInt()
    val feelsLikeValue = if (isFahrenheit) (feelsLikeRaw * 9 / 5 + 32).roundToInt() else feelsLikeRaw.roundToInt()
    val unitSymbol = if (isFahrenheit) "°F" else "°C"

    val windSpeedRaw = currentWeather?.windSpeed10m ?: 12.0
    val (windSpeedFormatted, windUnit) = when (preferences.windSpeedUnit) {
        "mph" -> "${(windSpeedRaw * 0.621371).roundToInt()}" to "mph"
        "ms" -> "${(windSpeedRaw / 3.6).roundToInt()}" to "m/s"
        else -> "${windSpeedRaw.roundToInt()}" to "km/h"
    }

    val windDegree = currentWeather?.windDirection10m?.toFloat() ?: 0f
    val humidity = currentWeather?.relativeHumidity2m?.toInt() ?: 45
    val pressure = currentWeather?.pressureMsl?.toInt() ?: 1013

    GlassCard(
        modifier = modifier,
        cornerRadius = preferences.cardCornerRadius.dp,
        backgroundColor = Color(theme.cardBackground).copy(alpha = preferences.cardOpacity),
        borderColor = Color(theme.cardBorder),
        shadowElevation = preferences.cardShadowElevation.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Location and Status Row
            if (preferences.showLocationWidget) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TwoPointFiveIcon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Konum",
                            tint = Color(theme.accentColor),
                            size = 18.dp,
                            depthOffset = 1.5.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = location.cityName.uppercase(),
                            style = TextStyle(
                                fontFamily = fontConfig.family,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                letterSpacing = 1.sp,
                                color = Color(theme.textPrimary)
                            )
                        )
                    }

                    // Live / Offline Status Indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                color = if (isOffline) Color(0x33FF5252) else Color(0x334CAF50),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    color = if (isOffline) Color(0xFFFF5252) else Color(0xFF4CAF50),
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isOffline) "Çevrimdışı" else "Canlı",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(theme.textPrimary).copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Temp & Condition Row
            if (preferences.showWeatherWidget) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TwoPointFiveWeatherIcon(
                            weatherCode = currentWeather?.weatherCode,
                            isDay = isDay,
                            size = 48.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "$tempValue$unitSymbol",
                                style = TextStyle(
                                    fontFamily = fontConfig.family,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 38.sp,
                                    color = Color(theme.textPrimary)
                                )
                            )
                            Text(
                                text = weatherInfo.description,
                                style = TextStyle(
                                    fontFamily = fontConfig.family,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = Color(theme.textSecondary)
                                )
                            )
                        }
                    }

                    // Feels like & Rain info
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Hissedilen $feelsLikeValue$unitSymbol",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(theme.textPrimary).copy(alpha = 0.85f)
                        )
                        Text(
                            text = "Yağış ${currentWeather?.rain ?: 0.0} mm",
                            fontSize = 12.sp,
                            color = Color(theme.textSecondary)
                        )
                    }
                }
            }

            // Weather Details Grid (Humidity, Wind, Pressure)
            if (preferences.showWeatherDetailsWidget) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Humidity
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TwoPointFiveIcon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "Nem",
                            tint = Color(0xFF29B6F6),
                            size = 16.dp,
                            depthOffset = 1.dp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Nem %$humidity",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(theme.textSecondary)
                        )
                    }

                    // Wind
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TwoPointFiveIcon(
                            imageVector = Icons.Default.Air,
                            contentDescription = "Rüzgar",
                            tint = Color(0xFF80CBC4),
                            size = 16.dp,
                            depthOffset = 1.dp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$windSpeedFormatted $windUnit",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(theme.textSecondary)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        TwoPointFiveIcon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = "Rüzgar Yönü",
                            tint = Color(theme.accentColor),
                            size = 12.dp,
                            depthOffset = 1.dp,
                            modifier = Modifier.rotate(windDegree)
                        )
                    }

                    // Pressure
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TwoPointFiveIcon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Basınç",
                            tint = Color(0xFFFFB74D),
                            size = 16.dp,
                            depthOffset = 1.dp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$pressure hPa",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(theme.textSecondary)
                        )
                    }
                }
            }
        }
    }
}
