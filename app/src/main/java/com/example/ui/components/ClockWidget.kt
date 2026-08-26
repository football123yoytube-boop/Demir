package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DashboardThemeConfig
import com.example.data.UserDashboardPreferences
import com.example.util.ClockAlignment
import com.example.util.DayNumberFormat
import com.example.viewmodel.TimeState

@Composable
fun ClockWidget(
    timeState: TimeState,
    preferences: UserDashboardPreferences,
    theme: DashboardThemeConfig,
    modifier: Modifier = Modifier
) {
    val fontConfig = preferences.clockFont
    val baseColor = if (preferences.isCustomThemeActive) {
        Color(preferences.customTheme.clockColor)
    } else {
        Color(theme.clockColor)
    }

    val glowColor = Color(theme.clockGlowColor)
    val alignment = preferences.clockAlignment
    val horizontalAlign = when (alignment) {
        ClockAlignment.LEFT -> Alignment.Start
        ClockAlignment.CENTER -> Alignment.CenterHorizontally
        ClockAlignment.RIGHT -> Alignment.End
    }
    val textAlign = when (alignment) {
        ClockAlignment.LEFT -> TextAlign.Start
        ClockAlignment.CENTER -> TextAlign.Center
        ClockAlignment.RIGHT -> TextAlign.End
    }

    val shadowStyle = if (preferences.clockShadowEnabled && theme.isDark) {
        Shadow(
            color = Color.Black.copy(alpha = 0.75f),
            offset = Offset(0f, 10f),
            blurRadius = 24f
        )
    } else {
        Shadow(
            color = Color.Black.copy(alpha = if (theme.isDark) 0.35f else 0.08f),
            offset = Offset(0f, 4f),
            blurRadius = 10f
        )
    }

    // Grand Enlarge Clock calculation
    val baseRawSize = if (preferences.showSeconds) 124 else 142
    val clockBaseFontSize = (baseRawSize * preferences.clockScale).sp

    // Colon Breathing Pulse Animation
    val infiniteTransition = rememberInfiniteTransition(label = "clock_colon_breath")
    val colonAlpha by infiniteTransition.animateFloat(
        initialValue = 0.40f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "colon_alpha"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = horizontalAlign,
        verticalArrangement = Arrangement.Center
    ) {
        // Date & Day Header (Apple Style Tracked Caps)
        if (preferences.showDateWidget) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = timeState.dayName,
                    style = TextStyle(
                        fontFamily = fontConfig.family,
                        fontWeight = FontWeight.Bold,
                        fontStyle = fontConfig.fontStyle,
                        fontSize = 18.sp,
                        letterSpacing = 2.4.sp,
                        color = Color(theme.accentColor),
                        shadow = shadowStyle
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timeState.fullDate,
                    style = TextStyle(
                        fontFamily = fontConfig.family,
                        fontWeight = FontWeight.SemiBold,
                        fontStyle = fontConfig.fontStyle,
                        fontSize = 17.sp,
                        letterSpacing = 1.6.sp,
                        color = Color(theme.textPrimary).copy(alpha = 0.95f),
                        shadow = shadowStyle
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Giant Digital Clock with Fluid Animated Digits
        if (preferences.showClockWidget) {
            Box(
                contentAlignment = when (alignment) {
                    ClockAlignment.LEFT -> Alignment.CenterStart
                    ClockAlignment.CENTER -> Alignment.Center
                    ClockAlignment.RIGHT -> Alignment.CenterEnd
                },
                modifier = Modifier.testTag("giant_clock_digits")
            ) {
                // Subtle Ambient Glow Layer (only for dark themes if enabled)
                if (preferences.clockGlowEnabled && theme.isDark && theme.clockGlowColor != 0x00000000L) {
                    Text(
                        text = if (preferences.showSeconds) timeState.timeFormatted else timeState.hourMinuteFormatted,
                        style = TextStyle(
                            fontFamily = fontConfig.family,
                            fontWeight = fontConfig.defaultWeight,
                            fontStyle = fontConfig.fontStyle,
                            fontSize = clockBaseFontSize,
                            letterSpacing = preferences.clockLetterSpacing.sp,
                            textAlign = textAlign,
                            color = glowColor.copy(alpha = 0.55f)
                        ),
                        modifier = Modifier
                            .blur(22.dp)
                            .alpha(0.65f)
                    )
                }

                // Sharp Foreground Hero Digits with Animated Spring Number Rolls
                val timeString = if (preferences.showSeconds) timeState.timeFormatted else timeState.hourMinuteFormatted
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = when (alignment) {
                        ClockAlignment.LEFT -> Arrangement.Start
                        ClockAlignment.CENTER -> Arrangement.Center
                        ClockAlignment.RIGHT -> Arrangement.End
                    }
                ) {
                    timeString.forEachIndexed { index, char ->
                        if (char == ':') {
                            Text(
                                text = ":",
                                style = TextStyle(
                                    fontFamily = fontConfig.family,
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = fontConfig.fontStyle,
                                    fontSize = clockBaseFontSize,
                                    letterSpacing = preferences.clockLetterSpacing.sp,
                                    color = baseColor.copy(alpha = preferences.clockOpacity * colonAlpha),
                                    shadow = shadowStyle
                                ),
                                modifier = Modifier.padding(horizontal = 1.dp)
                            )
                        } else {
                            AnimatedContent(
                                targetState = char,
                                transitionSpec = {
                                    (slideInVertically(
                                        animationSpec = spring(dampingRatio = 0.8f, stiffness = 320f)
                                    ) { height -> height / 3 } + fadeIn(
                                        animationSpec = tween(180)
                                    )).togetherWith(
                                        slideOutVertically(
                                            animationSpec = spring(dampingRatio = 0.8f, stiffness = 320f)
                                        ) { height -> -height / 3 } + fadeOut(
                                            animationSpec = tween(180)
                                        )
                                    )
                                },
                                label = "clock_digit_$index"
                            ) { targetChar ->
                                Text(
                                    text = targetChar.toString(),
                                    style = TextStyle(
                                        fontFamily = fontConfig.family,
                                        fontWeight = fontConfig.defaultWeight,
                                        fontStyle = fontConfig.fontStyle,
                                        fontSize = clockBaseFontSize,
                                        letterSpacing = preferences.clockLetterSpacing.sp,
                                        textAlign = textAlign,
                                        color = baseColor.copy(alpha = preferences.clockOpacity),
                                        shadow = shadowStyle
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Optional Day Number Badge (e.g. "26 GÜN")
        if (preferences.showDayNumberWidget) {
            Spacer(modifier = Modifier.height(2.dp))
            val dayText = when (preferences.dayNumberFormat) {
                DayNumberFormat.BIG_DAY_LABEL -> "${timeState.dayOfMonth} GÜN"
                DayNumberFormat.DAY_DOT -> "${timeState.dayOfMonth}. Gün"
                DayNumberFormat.PLAIN_NUMBER -> "${timeState.dayOfMonth}"
                DayNumberFormat.MONTH_DAY_FULL -> "AYIN ${timeState.dayOfMonth}. GÜNÜ"
            }
            Text(
                text = dayText,
                style = TextStyle(
                    fontFamily = fontConfig.family,
                    fontWeight = FontWeight.Bold,
                    fontStyle = fontConfig.fontStyle,
                    fontSize = 16.sp,
                    letterSpacing = 2.sp,
                    textAlign = textAlign,
                    color = Color(theme.accentColor).copy(alpha = 0.9f),
                    shadow = shadowStyle
                )
            )
        }
    }
}

