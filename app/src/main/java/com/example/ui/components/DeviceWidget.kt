package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DashboardThemeConfig
import com.example.data.UserDashboardPreferences
import com.example.manager.DeviceTelemetry
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun DeviceWidget(
    telemetry: DeviceTelemetry,
    preferences: UserDashboardPreferences,
    theme: DashboardThemeConfig,
    modifier: Modifier = Modifier
) {
    val fontConfig = preferences.clockFont

    val batteryColor = when {
        telemetry.batteryPercent > 50 -> Color(0xFF4CAF50)
        telemetry.batteryPercent > 20 -> Color(0xFFFFB300)
        else -> Color(0xFFFF5252)
    }

    GlassCard(
        modifier = modifier,
        cornerRadius = preferences.cardCornerRadius.dp,
        backgroundColor = Color(theme.cardBackground).copy(alpha = preferences.cardOpacity),
        borderColor = Color(theme.cardBorder),
        shadowElevation = preferences.cardShadowElevation.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Row 1: Battery & Network
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Battery
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TwoPointFiveIcon(
                        imageVector = if (telemetry.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                        contentDescription = "Pil",
                        tint = batteryColor,
                        size = 22.dp,
                        depthOffset = 1.5.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "%${telemetry.batteryPercent}",
                                style = TextStyle(
                                    fontFamily = fontConfig.family,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(theme.textPrimary)
                                )
                            )
                            if (telemetry.isCharging) {
                                Text(
                                    text = " (Şarjda)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF81C784)
                                )
                            }
                        }
                        Text(
                            text = "${telemetry.batteryTempCelsius}°C • ${telemetry.batteryHealth}",
                            fontSize = 10.sp,
                            color = Color(theme.textSecondary)
                        )
                    }
                }

                // Network
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TwoPointFiveIcon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "Bağlantı",
                        tint = if (telemetry.isOnline) Color(0xFF4FC3F7) else Color(0xFFFF5252),
                        size = 18.dp,
                        depthOffset = 1.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = telemetry.networkType,
                            style = TextStyle(
                                fontFamily = fontConfig.family,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = Color(theme.textPrimary)
                            )
                        )
                        Text(
                            text = if (telemetry.isOnline) "İnternet Aktif" else "Bağlantı Yok",
                            fontSize = 10.sp,
                            color = if (telemetry.isOnline) Color(theme.textSecondary) else Color(0xFFFF5252)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: RAM & Storage & Compass
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // RAM Info
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TwoPointFiveIcon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = "RAM",
                            tint = Color(theme.accentColor),
                            size = 14.dp,
                            depthOffset = 1.dp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "RAM: %${telemetry.ramUsagePercent}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(theme.textPrimary)
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    LinearProgressIndicator(
                        progress = { telemetry.ramUsagePercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(4.dp),
                        color = Color(theme.accentColor),
                        trackColor = Color(theme.cardBorder).copy(alpha = 0.3f)
                    )
                    Text(
                        text = "${telemetry.usedRamGb} / ${telemetry.totalRamGb} GB",
                        fontSize = 9.sp,
                        color = Color(theme.textSecondary)
                    )
                }

                // Storage Info
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TwoPointFiveIcon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = "Depolama",
                            tint = Color(0xFFFFB74D),
                            size = 14.dp,
                            depthOffset = 1.dp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Depolama",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(theme.textPrimary)
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    val storagePercent = (1f - (telemetry.freeStorageGb / telemetry.totalStorageGb)).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { storagePercent },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(4.dp),
                        color = Color(0xFFFFB74D),
                        trackColor = Color(theme.cardBorder).copy(alpha = 0.3f)
                    )
                    Text(
                        text = "${telemetry.freeStorageGb.roundToInt()} GB Boş / ${telemetry.totalStorageGb.roundToInt()} GB",
                        fontSize = 9.sp,
                        color = Color(theme.textSecondary)
                    )
                }

                // Compass Dial
                if (preferences.showCompassWidget) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.weight(0.9f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.size(22.dp)) {
                                    drawCircle(
                                        color = Color(theme.cardBorder),
                                        radius = size.minDimension / 2,
                                        style = Stroke(width = 1.5.dp.toPx())
                                    )
                                    val angleRad = Math.toRadians((telemetry.compassDegrees - 90.0)).toFloat()
                                    val needleLength = size.minDimension / 2.3f
                                    val endX = center.x + cos(angleRad) * needleLength
                                    val endY = center.y + sin(angleRad) * needleLength
                                    drawLine(
                                        color = Color(0xFFFF5252),
                                        start = center,
                                        end = Offset(endX, endY),
                                        strokeWidth = 2.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${telemetry.compassDegrees.roundToInt()}°",
                                    style = TextStyle(
                                        fontFamily = fontConfig.family,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(theme.textPrimary)
                                    )
                                )
                                Text(
                                    text = telemetry.compassDirection,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(theme.accentColor)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Row 3: Device Model & OS Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = "Cihaz",
                        tint = Color(theme.textSecondary),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = telemetry.deviceModel,
                        fontSize = 10.sp,
                        color = Color(theme.textSecondary)
                    )
                }

                Text(
                    text = telemetry.androidVersion,
                    fontSize = 10.sp,
                    color = Color(theme.textSecondary).copy(alpha = 0.8f)
                )
            }
        }
    }
}
