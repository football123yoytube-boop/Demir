package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CurrentWeather
import com.example.data.DailyWeather
import com.example.data.DashboardThemeConfig
import com.example.data.UserDashboardPreferences
import com.example.manager.DeviceTelemetry
import com.example.manager.LocationData
import com.example.util.WeatherCodeMapper
import kotlin.math.roundToInt

@Composable
fun DynamicIslandWidget(
    location: LocationData,
    currentWeather: CurrentWeather?,
    dailyWeather: DailyWeather?,
    telemetry: DeviceTelemetry,
    isOffline: Boolean,
    weatherStatus: String,
    theme: DashboardThemeConfig,
    preferences: UserDashboardPreferences,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    val isDay = currentWeather?.isDay == 1
    val weatherInfo = WeatherCodeMapper.getWeatherInfo(currentWeather?.weatherCode, isDay)
    val tempRaw = currentWeather?.temperature2m ?: 24.0
    val isFahrenheit = preferences.temperatureUnit == "F"
    val tempVal = if (isFahrenheit) (tempRaw * 9 / 5 + 32).roundToInt() else tempRaw.roundToInt()
    val unitSymbol = if (isFahrenheit) "°F" else "°C"

    val batteryPercent = telemetry.batteryPercent
    val isCharging = telemetry.isCharging
    val isWifi = telemetry.networkType == "Wi-Fi" || telemetry.isOnline

    // Dynamic Island is always pitch-black (True OLED Black) for authentic luxury pill look
    val islandBg = Color(0xFF000000)
    val islandBorder = Color(0x33FFFFFF)

    Box(
        modifier = modifier
            .shadow(
                elevation = if (isExpanded) 20.dp else 10.dp,
                shape = RoundedCornerShape(if (isExpanded) 26.dp else 32.dp),
                ambientColor = Color.Black.copy(alpha = 0.7f),
                spotColor = Color.Black.copy(alpha = 0.8f)
            )
            .clip(RoundedCornerShape(if (isExpanded) 26.dp else 32.dp))
            .background(islandBg)
            .border(
                width = 1.dp,
                color = islandBorder,
                shape = RoundedCornerShape(if (isExpanded) 26.dp else 32.dp)
            )
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = 0.75f,
                    stiffness = 320f
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                isExpanded = !isExpanded
            }
            .testTag("dynamic_island")
            .padding(horizontal = 16.dp, vertical = if (isExpanded) 12.dp else 7.dp)
    ) {
        if (!isExpanded) {
            // Compact Pill State
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left: Location & Status Dot
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(
                                if (isOffline) Color(0xFFFF5252) else Color(0xFF4CAF50)
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = location.cityName.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.8.sp,
                        color = Color.White.copy(alpha = 0.92f)
                    )
                }

                // Center Divider Dot
                Text(
                    text = "•",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.35f)
                )

                // Quick Weather Pill
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TwoPointFiveIcon(
                        imageVector = weatherInfo.icon,
                        contentDescription = weatherInfo.description,
                        tint = weatherInfo.accentColor,
                        size = 15.dp,
                        depthOffset = 1.dp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$tempVal$unitSymbol",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Right Divider Dot
                Text(
                    text = "•",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.35f)
                )

                // Right: Battery & Network
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TwoPointFiveIcon(
                        imageVector = if (isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                        contentDescription = "Pil",
                        tint = if (batteryPercent <= 20) Color(0xFFFF5252) else Color(0xFF81C784),
                        size = 14.dp,
                        depthOffset = 1.dp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$batteryPercent%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TwoPointFiveIcon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "Wi-Fi",
                        tint = if (isWifi) Color(theme.accentColor) else Color.White.copy(alpha = 0.4f),
                        size = 13.dp,
                        depthOffset = 1.dp
                    )
                }
            }
        } else {
            // Expanded Detailed Island Dropdown
            Column(
                modifier = Modifier.widthIn(min = 340.dp, max = 460.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header: Location + Status
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TwoPointFiveIcon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Konum",
                            tint = Color(theme.accentColor),
                            size = 16.dp,
                            depthOffset = 1.5.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${location.cityName}, ${location.countryName}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Text(
                        text = if (isOffline) "Çevrimdışı Bellek" else "Canlı Senkronize",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isOffline) Color(0xFFFF8A80) else Color(0xFF81C784)
                    )
                }

                // Weather Summary Row
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.06f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TwoPointFiveWeatherIcon(
                            weatherCode = currentWeather?.weatherCode,
                            isDay = currentWeather?.isDay == 1,
                            size = 28.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "$tempVal$unitSymbol • ${weatherInfo.description}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                text = "Hissedilen: ${((currentWeather?.apparentTemperature ?: 24.0)).roundToInt()}$unitSymbol",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.65f)
                            )
                        }
                    }

                    // Sun times glance
                    val sunriseStr = dailyWeather?.sunrise?.firstOrNull()?.takeLast(5) ?: "--:--"
                    val sunsetStr = dailyWeather?.sunset?.firstOrNull()?.takeLast(5) ?: "--:--"
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TwoPointFiveIcon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = "Güneş",
                            tint = Color(0xFFFFD54F),
                            size = 14.dp,
                            depthOffset = 1.dp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$sunriseStr / $sunsetStr",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                // Telemetry Quick Row
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Battery
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TwoPointFiveIcon(
                            imageVector = if (isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                            contentDescription = "Pil",
                            tint = if (batteryPercent <= 20) Color(0xFFFF5252) else Color(0xFF81C784),
                            size = 14.dp,
                            depthOffset = 1.dp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$batteryPercent% ${if (isCharging) "(Şarj Oluyor)" else ""}",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    // Humidity & Wind
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TwoPointFiveIcon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = "Nem",
                                tint = Color(0xFF4FC3F7),
                                size = 13.dp,
                                depthOffset = 1.dp
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "%${currentWeather?.relativeHumidity2m?.toInt() ?: 45}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TwoPointFiveIcon(
                                imageVector = Icons.Default.Air,
                                contentDescription = "Rüzgar",
                                tint = Color(0xFF80CBC4),
                                size = 13.dp,
                                depthOffset = 1.dp
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${currentWeather?.windSpeed10m?.roundToInt() ?: 10} km/h",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }
                }
            }
        }
    }
}
