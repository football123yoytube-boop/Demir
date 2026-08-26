package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ThemePresets
import com.example.ui.components.AmbientBackground
import com.example.ui.components.AstronomyWidget
import com.example.ui.components.ClockWidget
import com.example.ui.components.DashboardDetailModal
import com.example.ui.components.DeviceWidget
import com.example.ui.components.DynamicIslandWidget
import com.example.ui.components.LeftDockControlPanel
import com.example.ui.components.MinimalWeatherCard
import com.example.ui.components.QuickToolsWidget
import com.example.ui.components.RenderInteractiveWidget
import com.example.ui.components.TwoPointFiveIcon
import com.example.ui.components.WeatherWidget
import com.example.ui.components.WidgetCatalogModal
import com.example.viewmodel.DashboardViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val theme = uiState.effectiveTheme
    val prefs = uiState.preferences

    // Modal Sheet states
    var isQuickToolsModalOpen by remember { mutableStateOf(false) }
    var isAstronomyModalOpen by remember { mutableStateOf(false) }
    var isTelemetryModalOpen by remember { mutableStateOf(false) }
    var isWeatherDetailModalOpen by remember { mutableStateOf(false) }
    var isWidgetCatalogModalOpen by remember { mutableStateOf(false) }
    var isZenMode by remember { mutableStateOf(false) }

    val springSpec = spring<Float>(dampingRatio = 0.75f, stiffness = 380f)

    // Runtime location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.fetchLocationAndWeather()
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .combinedClickable(
                onClick = {
                    viewModel.onUserInteraction()
                    if (isZenMode) {
                        isZenMode = false
                    }
                },
                onDoubleClick = {
                    isZenMode = !isZenMode
                },
                onLongClick = {
                    viewModel.openSettings()
                }
            )
            .testTag("dashboard_root_canvas")
    ) {
        // Dynamic Ambient Mesh Background with Realtime Accelerometer Parallax
        AmbientBackground(
            theme = theme,
            preferences = prefs,
            isNightHour = uiState.time.isNightHour,
            tiltPitch = uiState.telemetry.tiltPitch,
            tiltRoll = uiState.telemetry.tiltRoll
        )

        // ==========================================
        // 1. SOL: DİKEY KONTROL PANELİ (LEFT DOCK)
        // ==========================================
        AnimatedVisibility(
            visible = !isZenMode,
            enter = fadeIn(spring(dampingRatio = 0.75f, stiffness = 380f)) +
                    slideInHorizontally(spring(dampingRatio = 0.75f, stiffness = 380f)) { -it },
            exit = fadeOut(spring(dampingRatio = 0.75f, stiffness = 380f)) +
                    slideOutHorizontally(spring(dampingRatio = 0.75f, stiffness = 380f)) { -it },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 20.dp)
        ) {
            LeftDockControlPanel(
                theme = theme,
                preferences = prefs,
                isQuickToolsOpen = isQuickToolsModalOpen,
                isAstronomyOpen = isAstronomyModalOpen,
                isTelemetryOpen = isTelemetryModalOpen,
                isRefreshing = uiState.isWeatherLoading,
                onToggleQuickTools = { isQuickToolsModalOpen = !isQuickToolsModalOpen },
                onToggleAstronomy = { isAstronomyModalOpen = !isAstronomyModalOpen },
                onToggleTelemetry = { isTelemetryModalOpen = !isTelemetryModalOpen },
                onCycleTheme = {
                    val all = ThemePresets.allThemes
                    val currentIndex = all.indexOfFirst { it.id == prefs.themeId }
                    val nextIndex = (currentIndex + 1) % all.size
                    val nextTheme = all[nextIndex]
                    viewModel.updatePreferences {
                        it.copy(
                            themeId = nextTheme.id,
                            isCustomThemeActive = false
                        )
                    }
                },
                onRefreshWeather = { viewModel.refreshWeatherManually() },
                onOpenSettings = { viewModel.openSettings() }
            )
        }

        // ==========================================
        // 2. ÜST ORTA: DYNAMIC ISLAND (STATUS CAPSULE)
        // ==========================================
        AnimatedVisibility(
            visible = !isZenMode,
            enter = fadeIn(spring(dampingRatio = 0.75f, stiffness = 380f)) +
                    slideInVertically(spring(dampingRatio = 0.75f, stiffness = 380f)) { -it },
            exit = fadeOut(spring(dampingRatio = 0.75f, stiffness = 380f)) +
                    slideOutVertically(spring(dampingRatio = 0.75f, stiffness = 380f)) { -it },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        ) {
            DynamicIslandWidget(
                location = uiState.location,
                currentWeather = uiState.currentWeather,
                dailyWeather = uiState.dailyWeather,
                telemetry = uiState.telemetry,
                isOffline = uiState.isOffline,
                weatherStatus = uiState.weatherStatusMessage,
                theme = theme,
                preferences = prefs,
                onOpenSettings = { viewModel.openSettings() }
            )
        }

        // ==========================================
        // 3. ORTA: DEV SAAT + ANA EKRAN WIDGETLARI (MAX 3 YAN YANA & HAVA KARTI KALDIRILAMAZ)
        // ==========================================
        val motionTiltX = (uiState.telemetry.tiltRoll * 0.4f).coerceIn(-12f, 12f)
        val motionTiltY = (uiState.telemetry.tiltPitch * 0.4f).coerceIn(-12f, 12f)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .offset {
                    IntOffset(
                        x = (uiState.screensaverOffsetX + motionTiltX).roundToInt(),
                        y = (uiState.screensaverOffsetY + motionTiltY).roundToInt()
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.widthIn(max = 840.dp)
            ) {
                // Giant Digital Clock (Includes Day + Date at top)
                ClockWidget(
                    timeState = uiState.time,
                    preferences = prefs,
                    theme = theme,
                    modifier = Modifier.testTag("clock_widget")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // =========================================================================
                // HOME SCREEN INTERACTIVE WIDGETS ROW (MAX 3 SIDE-BY-SIDE)
                // Weather Widget is permanently anchored and cannot be removed
                // =========================================================================
                val activeWidgets = prefs.activeHomeScreenWidgetIds

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Permanent Non-Removable Weather Widget Pill
                    MinimalWeatherCard(
                        currentWeather = uiState.currentWeather,
                        preferences = prefs,
                        theme = theme,
                        onClick = { isWeatherDetailModalOpen = true },
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .widthIn(max = 240.dp)
                            .testTag("weather_pill_card")
                    )

                    // 2. Extra Active Widgets (Up to 2 side-by-side with Weather = Max 3 total side-by-side)
                    activeWidgets.take(2).forEach { widgetId ->
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .widthIn(max = 240.dp)
                        ) {
                            RenderInteractiveWidget(
                                widgetId = widgetId,
                                theme = theme,
                                telemetry = uiState.telemetry
                            )
                        }
                    }

                    // 3. Quick Widget Library / Add Button
                    Spacer(modifier = Modifier.width(8.dp))
                    val addInteractionSource = remember { MutableInteractionSource() }
                    val isAddPressed by addInteractionSource.collectIsPressedAsState()
                    val addScale by androidx.compose.animation.core.animateFloatAsState(
                        targetValue = if (isAddPressed) 0.90f else 1.0f,
                        animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f),
                        label = "add_btn_scale"
                    )

                    Box(
                        modifier = Modifier
                            .scale(addScale)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(theme.cardBackground).copy(alpha = 0.6f))
                            .border(1.dp, Color(theme.cardBorder).copy(alpha = 0.45f), CircleShape)
                            .clickable(
                                interactionSource = addInteractionSource,
                                indication = null,
                                onClick = { isWidgetCatalogModalOpen = true }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        TwoPointFiveIcon(
                            imageVector = Icons.Default.Widgets,
                            contentDescription = "Widget Galerisi",
                            tint = Color(theme.accentColor),
                            size = 18.dp,
                            depthOffset = 1.dp
                        )
                    }
                }
            }
        }

        // ==========================================
        // DETAIL MODAL SHEETS (Spring-animated)
        // ==========================================

        // Widget Catalog (25 Interactive Widgets)
        if (isWidgetCatalogModalOpen) {
            WidgetCatalogModal(
                theme = theme,
                preferences = prefs,
                onUpdateActiveWidgets = { newWidgets ->
                    viewModel.updatePreferences { it.copy(activeHomeScreenWidgetIds = newWidgets) }
                },
                onDismiss = { isWidgetCatalogModalOpen = false }
            )
        }

        // Quick Tools Detail Modal (Timer & Stopwatch)
        if (isQuickToolsModalOpen) {
            DashboardDetailModal(
                title = "Zamanlayıcı & Kronometre",
                icon = Icons.Default.Timer,
                theme = theme,
                onDismiss = { isQuickToolsModalOpen = false }
            ) {
                QuickToolsWidget(
                    timerManager = viewModel.timerManager,
                    preferences = prefs,
                    theme = theme
                )
            }
        }

        // Astronomy Detail Modal (Sun & Moon details)
        if (isAstronomyModalOpen) {
            DashboardDetailModal(
                title = "Güneş & Ay Astronomi Döngüsü",
                icon = Icons.Default.NightsStay,
                theme = theme,
                onDismiss = { isAstronomyModalOpen = false }
            ) {
                AstronomyWidget(
                    dailyWeather = uiState.dailyWeather,
                    preferences = prefs,
                    theme = theme
                )
            }
        }

        // Telemetry Detail Modal (System & Battery details)
        if (isTelemetryModalOpen) {
            DashboardDetailModal(
                title = "Cihaz Telemetri & Donanım",
                icon = Icons.Default.Bolt,
                theme = theme,
                onDismiss = { isTelemetryModalOpen = false }
            ) {
                DeviceWidget(
                    telemetry = uiState.telemetry,
                    preferences = prefs,
                    theme = theme
                )
            }
        }

        // Weather Forecast & Sensor Details Modal
        if (isWeatherDetailModalOpen) {
            DashboardDetailModal(
                title = "Hava Durumu Detayları",
                icon = Icons.Default.Cloud,
                theme = theme,
                onDismiss = { isWeatherDetailModalOpen = false }
            ) {
                WeatherWidget(
                    currentWeather = uiState.currentWeather,
                    location = uiState.location,
                    preferences = prefs,
                    theme = theme,
                    weatherStatus = uiState.weatherStatusMessage,
                    isOffline = uiState.isOffline
                )
            }
        }

        // Settings Dialog Modal
        if (uiState.isSettingsOpen) {
            SettingsDialog(
                viewModel = viewModel,
                uiState = uiState,
                onDismiss = { viewModel.closeSettings() }
            )
        }
    }
}

