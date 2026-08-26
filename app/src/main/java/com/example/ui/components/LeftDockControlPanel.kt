package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.DashboardThemeConfig
import com.example.data.UserDashboardPreferences

@Composable
fun LeftDockControlPanel(
    theme: DashboardThemeConfig,
    preferences: UserDashboardPreferences,
    isQuickToolsOpen: Boolean,
    isAstronomyOpen: Boolean,
    isTelemetryOpen: Boolean,
    isRefreshing: Boolean,
    onToggleQuickTools: () -> Unit,
    onToggleAstronomy: () -> Unit,
    onToggleTelemetry: () -> Unit,
    onCycleTheme: () -> Unit,
    onRefreshWeather: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val refreshRotation by animateFloatAsState(
        targetValue = if (isRefreshing) 720f else 0f,
        animationSpec = if (isRefreshing) {
            tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        } else {
            spring(dampingRatio = 0.6f, stiffness = 300f)
        },
        label = "refresh_rotate"
    )

    val dockBg = if (!theme.isDark) Color(0xE6FFFFFF) else Color(theme.cardBackground)
    val borderColor = if (!theme.isDark) Color(0x1F111827) else Color(theme.cardBorder)

    Box(
        modifier = modifier
            .shadow(
                elevation = if (theme.isDark) 20.dp else 8.dp,
                shape = RoundedCornerShape(34.dp),
                ambientColor = Color.Black.copy(alpha = if (theme.isDark) 0.5f else 0.1f),
                spotColor = Color.Black.copy(alpha = if (theme.isDark) 0.6f else 0.15f)
            )
            .clip(RoundedCornerShape(34.dp))
            .background(dockBg)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(34.dp)
            )
            .padding(vertical = 12.dp, horizontal = 8.dp)
            .testTag("left_dock_control_panel")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Settings Button (Primary Top) - 2.5D Badge
            TwoPointFiveIconBadge(
                imageVector = Icons.Default.Settings,
                contentDescription = "Ayarlar",
                isSelected = false,
                isDark = theme.isDark,
                primaryColor = Color(theme.accentColor),
                onClick = onOpenSettings,
                testTag = "dock_settings_btn"
            )

            // Quick Tools (Timer / Stopwatch) - 2.5D Badge
            TwoPointFiveIconBadge(
                imageVector = Icons.Default.Timer,
                contentDescription = "Zamanlayıcı & Kronometre",
                isSelected = isQuickToolsOpen,
                isDark = theme.isDark,
                primaryColor = Color(theme.accentColor),
                onClick = onToggleQuickTools,
                testTag = "dock_tools_btn"
            )

            // Astronomy Details (Sun & Moon) - 2.5D Badge
            TwoPointFiveIconBadge(
                imageVector = Icons.Default.NightsStay,
                contentDescription = "Güneş & Ay Döngüsü",
                isSelected = isAstronomyOpen,
                isDark = theme.isDark,
                primaryColor = Color(theme.accentColor),
                onClick = onToggleAstronomy,
                testTag = "dock_astronomy_btn"
            )

            // Telemetry & Hardware - 2.5D Badge
            TwoPointFiveIconBadge(
                imageVector = Icons.Default.Bolt,
                contentDescription = "Sistem & Donanım",
                isSelected = isTelemetryOpen,
                isDark = theme.isDark,
                primaryColor = Color(theme.accentColor),
                onClick = onToggleTelemetry,
                testTag = "dock_telemetry_btn"
            )

            // Theme Switcher - 2.5D Badge
            TwoPointFiveIconBadge(
                imageVector = Icons.Default.Palette,
                contentDescription = "Tema Değiştir",
                isSelected = false,
                isDark = theme.isDark,
                primaryColor = Color(theme.accentColor),
                onClick = onCycleTheme,
                testTag = "dock_theme_btn"
            )

            // Weather Sync Refresh - 2.5D Badge with rotation
            Box(modifier = Modifier.rotate(refreshRotation)) {
                TwoPointFiveIconBadge(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Hava Durumunu Yenile",
                    isSelected = isRefreshing,
                    isDark = theme.isDark,
                    primaryColor = Color(theme.accentColor),
                    onClick = onRefreshWeather,
                    testTag = "dock_refresh_btn"
                )
            }
        }
    }
}

