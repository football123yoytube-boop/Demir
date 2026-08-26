package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DailyWeather
import com.example.data.DashboardThemeConfig
import com.example.data.UserDashboardPreferences
import com.example.util.AstronomyFormatter

@Composable
fun AstronomyWidget(
    dailyWeather: DailyWeather?,
    preferences: UserDashboardPreferences,
    theme: DashboardThemeConfig,
    modifier: Modifier = Modifier
) {
    val fontConfig = preferences.clockFont

    val sunriseStr = AstronomyFormatter.formatIsoTime(dailyWeather?.sunrise?.firstOrNull())
    val sunsetStr = AstronomyFormatter.formatIsoTime(dailyWeather?.sunset?.firstOrNull())
    val daylightStr = AstronomyFormatter.formatDaylightDuration(dailyWeather?.daylightDuration?.firstOrNull())

    val moonriseStr = AstronomyFormatter.formatIsoTime(dailyWeather?.moonrise?.firstOrNull())
    val moonsetStr = AstronomyFormatter.formatIsoTime(dailyWeather?.moonset?.firstOrNull())
    val moonPhaseInfo = AstronomyFormatter.getMoonPhaseInfo(dailyWeather?.moonPhase?.firstOrNull())

    GlassCard(
        modifier = modifier,
        cornerRadius = preferences.cardCornerRadius.dp,
        backgroundColor = Color(theme.cardBackground).copy(alpha = preferences.cardOpacity),
        borderColor = Color(theme.cardBorder),
        shadowElevation = preferences.cardShadowElevation.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Sun Section
            if (preferences.showSunWidget) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sunrise
                    Column(horizontalAlignment = Alignment.Start) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🌅", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = sunriseStr,
                                style = TextStyle(
                                    fontFamily = fontConfig.family,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = Color(theme.textPrimary)
                                )
                            )
                        }
                        Text(
                            text = "GÜNEŞ DOĞUŞU",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            color = Color(theme.textSecondary)
                        )
                    }

                    // Daylight Duration
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "☀️", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = daylightStr,
                                style = TextStyle(
                                    fontFamily = fontConfig.family,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFFFFCA28)
                                )
                            )
                        }
                        Text(
                            text = "GÜN IŞIĞI",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            color = Color(theme.textSecondary)
                        )
                    }

                    // Sunset
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🌇", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = sunsetStr,
                                style = TextStyle(
                                    fontFamily = fontConfig.family,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = Color(theme.textPrimary)
                                )
                            )
                        }
                        Text(
                            text = "GÜNEŞ BATIŞI",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            color = Color(theme.textSecondary)
                        )
                    }
                }
            }

            if (preferences.showSunWidget && preferences.showMoonWidget) {
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Moon Section
            if (preferences.showMoonWidget) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Moonrise
                    Column(horizontalAlignment = Alignment.Start) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🌙", fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = moonriseStr,
                                style = TextStyle(
                                    fontFamily = fontConfig.family,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = Color(theme.textPrimary)
                                )
                            )
                        }
                        Text(
                            text = "AY DOĞUŞU",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            color = Color(theme.textSecondary)
                        )
                    }

                    // Moon Phase
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = moonPhaseInfo.emoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = moonPhaseInfo.name.uppercase(),
                                style = TextStyle(
                                    fontFamily = fontConfig.family,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(theme.accentColor)
                                )
                            )
                        }
                        Text(
                            text = "AY FAZI (%${moonPhaseInfo.illuminationPercent})",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            color = Color(theme.textSecondary)
                        )
                    }

                    // Moonset
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🌘", fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = moonsetStr,
                                style = TextStyle(
                                    fontFamily = fontConfig.family,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = Color(theme.textPrimary)
                                )
                            )
                        }
                        Text(
                            text = "AY BATIŞI",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            color = Color(theme.textSecondary)
                        )
                    }
                }
            }
        }
    }
}
