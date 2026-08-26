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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DashboardThemeConfig
import com.example.data.UserDashboardPreferences
import com.example.manager.TimerManager
import java.util.Locale

@Composable
fun QuickToolsWidget(
    timerManager: TimerManager,
    preferences: UserDashboardPreferences,
    theme: DashboardThemeConfig,
    modifier: Modifier = Modifier
) {
    val stopwatchState by timerManager.stopwatch.collectAsState()
    val countdownState by timerManager.countdown.collectAsState()
    val alarmState by timerManager.alarm.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Kronometre, 1: Geri Sayım, 2: Alarm

    GlassCard(
        modifier = modifier,
        cornerRadius = preferences.cardCornerRadius.dp,
        backgroundColor = Color(theme.cardBackground).copy(alpha = preferences.cardOpacity),
        borderColor = Color(theme.cardBorder),
        shadowElevation = preferences.cardShadowElevation.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(theme.textPrimary),
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(theme.accentColor),
                        height = 2.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TwoPointFiveIcon(Icons.Default.Timer, contentDescription = null, size = 15.dp, tint = if (selectedTab == 0) Color(theme.accentColor) else Color(theme.textSecondary), depthOffset = 1.dp)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Kronometre", fontSize = 12.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TwoPointFiveIcon(Icons.Default.HourglassBottom, contentDescription = null, size = 15.dp, tint = if (selectedTab == 1) Color(theme.accentColor) else Color(theme.textSecondary), depthOffset = 1.dp)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Geri Sayım", fontSize = 12.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TwoPointFiveIcon(Icons.Default.Alarm, contentDescription = null, size = 15.dp, tint = if (selectedTab == 2) Color(theme.accentColor) else Color(theme.textSecondary), depthOffset = 1.dp)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Alarm", fontSize = 12.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (selectedTab) {
                0 -> {
                    // Stopwatch
                    val totalMs = stopwatchState.elapsedMillis
                    val mins = totalMs / 60000
                    val secs = (totalMs % 60000) / 1000
                    val ms = (totalMs % 1000) / 10
                    val timeStr = String.format(Locale.US, "%02d:%02d.%02d", mins, secs, ms)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = timeStr,
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                color = Color(theme.textPrimary)
                            )
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (stopwatchState.isRunning) {
                                IconButton(
                                    onClick = { timerManager.lapStopwatch() },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    TwoPointFiveIcon(
                                        imageVector = Icons.Default.Flag,
                                        contentDescription = "Tur",
                                        tint = Color(theme.accentColor),
                                        size = 18.dp,
                                        depthOffset = 1.dp
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    if (stopwatchState.isRunning) timerManager.pauseStopwatch()
                                    else timerManager.startStopwatch()
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(theme.accentColor), CircleShape)
                            ) {
                                TwoPointFiveIcon(
                                    imageVector = if (stopwatchState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Başlat/Durdur",
                                    tint = Color.Black,
                                    size = 20.dp,
                                    depthOffset = 1.dp
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { timerManager.resetStopwatch() },
                                modifier = Modifier.size(36.dp)
                            ) {
                                TwoPointFiveIcon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Sıfırla",
                                    tint = Color(theme.textSecondary),
                                    size = 18.dp,
                                    depthOffset = 1.dp
                                )
                            }
                        }
                    }

                    if (stopwatchState.laps.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            itemsIndexed(stopwatchState.laps.reversed()) { idx, lapMs ->
                                val lMin = lapMs / 60000
                                val lSec = (lapMs % 60000) / 1000
                                val lMs = (lapMs % 1000) / 10
                                val lapStr = String.format(Locale.US, "Tur %d: %02d:%02d.%02d", stopwatchState.laps.size - idx, lMin, lSec, lMs)
                                Text(
                                    text = lapStr,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(theme.accentColor),
                                    modifier = Modifier
                                        .background(Color(theme.cardBorder).copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // Countdown
                    val remaining = countdownState.remainingSeconds
                    val mins = remaining / 60
                    val secs = remaining % 60
                    val countStr = String.format(Locale.US, "%02d:%02d", mins, secs)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = countStr,
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp,
                                    color = if (countdownState.isFinished) Color(0xFFFF5252) else Color(theme.textPrimary)
                                )
                            )
                            if (countdownState.isFinished) {
                                Text(text = "Süre Doldu! 🔔", color = Color(0xFFFF5252), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    if (countdownState.isRunning) timerManager.pauseCountdown()
                                    else timerManager.startCountdown()
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(theme.accentColor), CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (countdownState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Başlat",
                                    tint = Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { timerManager.resetCountdown() },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Sıfırla", tint = Color(theme.textSecondary))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Preset buttons (1m, 3m, 5m, 10m, 25m Pomodoro)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(1 to "1 dk", 3 to "3 dk", 5 to "5 dk", 10 to "10 dk", 25 to "Pomodoro").forEach { (m, label) ->
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(theme.textPrimary),
                                modifier = Modifier
                                    .background(
                                        if (countdownState.totalSeconds == m * 60) Color(theme.accentColor).copy(alpha = 0.35f)
                                        else Color(theme.cardBorder).copy(alpha = 0.25f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                2 -> {
                    // Alarm
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            val alarmStr = String.format(Locale.US, "%02d:%02d", alarmState.hour, alarmState.minute)
                            Text(
                                text = alarmStr,
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp,
                                    color = if (alarmState.isEnabled) Color(theme.accentColor) else Color(theme.textSecondary)
                                )
                            )
                            Text(
                                text = "${alarmState.label} • ${if (alarmState.isEnabled) "Aktif" else "Kapalı"}",
                                fontSize = 11.sp,
                                color = Color(theme.textSecondary)
                            )
                        }

                        Button(
                            onClick = { timerManager.toggleAlarm() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (alarmState.isEnabled) Color(theme.accentColor) else Color(theme.cardBorder)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (alarmState.isEnabled) "Kapat" else "Aç",
                                color = if (alarmState.isEnabled) Color.Black else Color(theme.textPrimary),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
