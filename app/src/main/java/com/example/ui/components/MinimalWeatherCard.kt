package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CurrentWeather
import com.example.data.DashboardThemeConfig
import com.example.data.UserDashboardPreferences
import com.example.util.WeatherCodeMapper
import kotlin.math.roundToInt

@Composable
fun MinimalWeatherCard(
    currentWeather: CurrentWeather?,
    preferences: UserDashboardPreferences,
    theme: DashboardThemeConfig,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f),
        label = "weather_card_scale"
    )

    val isDay = currentWeather?.isDay == 1
    val weatherInfo = WeatherCodeMapper.getWeatherInfo(currentWeather?.weatherCode, isDay)

    val tempRaw = currentWeather?.temperature2m ?: 24.0
    val isFahrenheit = preferences.temperatureUnit == "F"
    val tempVal = if (isFahrenheit) (tempRaw * 9 / 5 + 32).roundToInt() else tempRaw.roundToInt()
    val unitSymbol = if (isFahrenheit) "°F" else "°C"

    val feelsLikeRaw = currentWeather?.apparentTemperature ?: 25.0
    val feelsLikeVal = if (isFahrenheit) (feelsLikeRaw * 9 / 5 + 32).roundToInt() else feelsLikeRaw.roundToInt()

    val cardBg = if (!theme.isDark) Color(0xFFF3F4F6) else Color(theme.cardBackground)
    val borderColor = if (!theme.isDark) Color(0x1F111827) else Color(theme.cardBorder)

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (theme.isDark) 14.dp else 6.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color.Black.copy(alpha = if (theme.isDark) 0.4f else 0.08f),
                spotColor = Color.Black.copy(alpha = if (theme.isDark) 0.5f else 0.12f)
            )
            .clip(RoundedCornerShape(28.dp))
            .background(cardBg)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(28.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("minimal_weather_pill")
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // 2.5D Volumetric Weather Icon
            TwoPointFiveWeatherIcon(
                weatherCode = currentWeather?.weatherCode,
                isDay = isDay,
                size = 32.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Temperature
            Text(
                text = "$tempVal$unitSymbol",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
                color = Color(theme.textPrimary)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "•",
                fontSize = 14.sp,
                color = Color(theme.textSecondary).copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Condition Description
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = weatherInfo.description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(theme.textPrimary)
                )
                Text(
                    text = "Hissedilen $feelsLikeVal$unitSymbol",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(theme.textSecondary)
                )
            }
        }
    }
}

