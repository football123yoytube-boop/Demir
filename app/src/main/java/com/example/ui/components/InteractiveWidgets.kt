package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DashboardThemeConfig
import com.example.manager.DeviceTelemetry
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Metadata descriptor for all 25 Interactive Widgets
data class InteractiveWidgetMeta(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val category: String,
    val isRemovable: Boolean = true
)

object InteractiveWidgetRegistry {
    val allWidgets: List<InteractiveWidgetMeta> = listOf(
        InteractiveWidgetMeta("focus_pomodoro", "Odaklanma & Pomodoro", "25dk Çalışma Sayacı", Icons.Default.HourglassBottom, "Verimlilik"),
        InteractiveWidgetMeta("hydration_tracker", "Su Takibi", "Günlük Hedef & Sayaç", Icons.Default.LocalDrink, "Sağlık"),
        InteractiveWidgetMeta("quick_notes", "Hızlı Not & Görevler", "Yapılacaklar Listesi", Icons.Default.EditNote, "Verimlilik"),
        InteractiveWidgetMeta("world_clock", "Dünya Saatleri", "Global Saat Dilimleri", Icons.Default.Public, "Zaman"),
        InteractiveWidgetMeta("air_quality_uv", "Hava Kalitesi & UV", "AQI & Güneş İndeksi", Icons.Default.Air, "Çevre"),
        InteractiveWidgetMeta("media_playback", "Müzik & Medya Kontrolü", "Ekolayzer & Oynatıcı", Icons.Default.MusicNote, "Eğlence"),
        InteractiveWidgetMeta("activity_rings", "Aktivite Halkaları", "Adım & Kalori Hedefi", Icons.Default.DirectionsRun, "Sağlık"),
        InteractiveWidgetMeta("quick_calculator", "Hızlı Hesap Makinesi", "Anlık Hesaplama", Icons.Default.Calculate, "Araçlar"),
        InteractiveWidgetMeta("battery_power", "Pil & Güç Modu", "Performans / Eko Mod", Icons.Default.BatteryChargingFull, "Cihaz"),
        InteractiveWidgetMeta("zen_breathing", "Zen Nefes Rehberi", "4-7-8 Sakinleşme", Icons.Default.SelfImprovement, "Sağlık"),
        InteractiveWidgetMeta("currency_crypto", "Döviz & Kripto Kuru", "USD / EUR / BTC / ETH", Icons.Default.CurrencyExchange, "Finans"),
        InteractiveWidgetMeta("daily_habits", "Alışkanlık Zinciri", "Günlük Rutinler", Icons.Default.CheckCircle, "Kişisel"),
        InteractiveWidgetMeta("unit_converter", "Birim Dönüştürücü", "C/F, Km/Mil, Kg/Lb", Icons.Default.Speed, "Araçlar"),
        InteractiveWidgetMeta("compass_level", "Pusula & Su Terazisi", "3D Jiroskop & Eğim", Icons.Default.Explore, "Sensörler"),
        InteractiveWidgetMeta("system_resource", "RAM & Sistem Hızlandırıcı", "Bellek Optimizasyonu", Icons.Default.Memory, "Cihaz"),
        InteractiveWidgetMeta("moon_phase", "Ay Evreleri & Takvim", "Aydınlanma & Döngü", Icons.Default.DarkMode, "Astronomi"),
        InteractiveWidgetMeta("sun_ephemeris", "Güneş Döngüsü & Altın Saat", "Gündoğumu & Batımı", Icons.Default.WbSunny, "Astronomi"),
        InteractiveWidgetMeta("network_speed", "Ağ Hızı & Ping Testi", "Gecikme & Wi-Fi", Icons.Default.Wifi, "Bağlantı"),
        InteractiveWidgetMeta("screen_flashlight", "Ekran Feneri & Flaş", "SOS & Parlaklık", Icons.Default.FlashlightOn, "Araçlar"),
        InteractiveWidgetMeta("dice_coin", "Zar & Yazı Tura", "Şans & Karar Aracı", Icons.Default.Refresh, "Eğlence"),
        InteractiveWidgetMeta("daily_motivation", "Günün İlhamı & Söz", "Motivasyon Alıntıları", Icons.Default.Favorite, "Kişisel"),
        InteractiveWidgetMeta("white_noise", "Doğa Sesleri & Ambiyans", "Yağmur, Dalga, Ateş", Icons.Default.VolumeUp, "Meditasyon"),
        InteractiveWidgetMeta("countdown_event", "Özel Gün Geri Sayımı", "Etkinlik Sayacı", Icons.Default.Alarm, "Zaman"),
        InteractiveWidgetMeta("caffeine_tracker", "Kafein Takipçisi", "Kahve & Uyku Sınırı", Icons.Default.Coffee, "Sağlık"),
        InteractiveWidgetMeta("decibel_meter", "Ses Seviyesi (dB)", "Ortam Gürültü Ölçer", Icons.Default.Mic, "Sensörler")
    )
}

// -------------------------------------------------------------
// Interactive Widget Card Frame (Liquid Glass 2.5D Styling)
// -------------------------------------------------------------
@Composable
fun InteractiveWidgetCard(
    title: String,
    icon: ImageVector,
    theme: DashboardThemeConfig,
    modifier: Modifier = Modifier,
    tiltPitch: Float = 0f,
    tiltRoll: Float = 0f,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val lightAngleX = (tiltRoll * 3.5f).coerceIn(-30f, 30f)
    val lightAngleY = (tiltPitch * 3.5f).coerceIn(-30f, 30f)

    val shape = RoundedCornerShape(22.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f),
        label = "widget_press_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (theme.isDark) 12.dp else 5.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = if (theme.isDark) 0.45f else 0.08f),
                spotColor = Color.Black.copy(alpha = if (theme.isDark) 0.60f else 0.12f)
            )
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(theme.cardBackground).copy(alpha = if (theme.isDark) 0.50f else 0.85f),
                        Color(theme.cardBackground).copy(alpha = if (theme.isDark) 0.28f else 0.65f)
                    ),
                    start = Offset(0f - lightAngleX, 0f - lightAngleY),
                    end = Offset(300f + lightAngleX, 300f + lightAngleY)
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(theme.cardBorder).copy(alpha = 0.85f),
                        Color.White.copy(alpha = if (theme.isDark) 0.35f else 0.60f),
                        Color(theme.cardBorder).copy(alpha = 0.20f)
                    ),
                    start = Offset(0f - lightAngleX, 0f - lightAngleY),
                    end = Offset(300f + lightAngleX, 300f + lightAngleY)
                ),
                shape = shape
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TwoPointFiveIcon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color(theme.accentColor),
                        size = 18.dp,
                        depthOffset = 1.5.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(theme.textPrimary),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Body
            content()
        }
    }
}

// -------------------------------------------------------------
// 1. FOCUS & POMODORO WIDGET
// -------------------------------------------------------------
@Composable
fun FocusPomodoroWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    var isRunning by remember { mutableStateOf(false) }
    var secondsLeft by remember { mutableIntStateOf(25 * 60) }
    var sessionsCompleted by remember { mutableIntStateOf(2) }

    LaunchedEffect(isRunning) {
        while (isRunning && secondsLeft > 0) {
            delay(1000)
            secondsLeft -= 1
        }
        if (secondsLeft == 0 && isRunning) {
            isRunning = false
            sessionsCompleted += 1
            secondsLeft = 25 * 60
        }
    }

    val mins = secondsLeft / 60
    val secs = secondsLeft % 60
    val timeFormatted = String.format(Locale.US, "%02d:%02d", mins, secs)
    val progress = (25 * 60 - secondsLeft).toFloat() / (25 * 60)

    InteractiveWidgetCard(
        title = "Odaklanma",
        icon = Icons.Default.HourglassBottom,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = timeFormatted,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp,
                    color = Color(theme.textPrimary)
                )
                Text(
                    text = "$sessionsCompleted seans tamamlandı",
                    fontSize = 11.sp,
                    color = Color(theme.textSecondary)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Reset Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(theme.textSecondary).copy(alpha = 0.15f))
                        .clickable {
                            isRunning = false
                            secondsLeft = 25 * 60
                        },
                    contentAlignment = Alignment.Center
                ) {
                    TwoPointFiveIcon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Sıfırla",
                        tint = Color(theme.textSecondary),
                        size = 16.dp,
                        depthOffset = 1.dp
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Play / Pause Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(theme.accentColor))
                        .clickable { isRunning = !isRunning },
                    contentAlignment = Alignment.Center
                ) {
                    TwoPointFiveIcon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Başlat",
                        tint = Color.Black,
                        size = 20.dp,
                        depthOffset = 1.dp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. HYDRATION & WATER INTAKE TRACKER WIDGET
// -------------------------------------------------------------
@Composable
fun HydrationTrackerWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    var mlIntake by remember { mutableIntStateOf(1500) }
    val targetMl = 2500
    val progress = (mlIntake.toFloat() / targetMl).coerceIn(0f, 1f)

    InteractiveWidgetCard(
        title = "Su Takibi",
        icon = Icons.Default.LocalDrink,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "$mlIntake ml",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(theme.textPrimary)
                    )
                    Text(
                        text = "Hedef: $targetMl ml (%${(progress * 100).toInt()})",
                        fontSize = 11.sp,
                        color = Color(theme.textSecondary)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // -250ml
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(theme.textSecondary).copy(alpha = 0.15f))
                            .clickable { if (mlIntake >= 250) mlIntake -= 250 },
                        contentAlignment = Alignment.Center
                    ) {
                        TwoPointFiveIcon(Icons.Default.Remove, "Azalt", size = 14.dp, tint = Color(theme.textSecondary), depthOffset = 1.dp)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // +250ml
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E5FF))
                            .clickable { mlIntake += 250 },
                        contentAlignment = Alignment.Center
                    ) {
                        TwoPointFiveIcon(Icons.Default.Add, "Ekle", size = 16.dp, tint = Color.Black, depthOffset = 1.dp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(theme.textSecondary).copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF00E5FF), Color(0xFF0091EA))
                            )
                        )
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 3. QUICK NOTES & TODOS WIDGET
// -------------------------------------------------------------
@Composable
fun QuickNotesWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    var notes = remember {
        mutableStateListOf(
            Pair("Toplantı notlarını gözden geçir", true),
            Pair("Günlük 2L su hedefini tamamla", false),
            Pair("Akşam yürüyüşü yap", false)
        )
    }

    InteractiveWidgetCard(
        title = "Hızlı Notlar",
        icon = Icons.Default.EditNote,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        modifier = modifier
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            notes.take(3).forEachIndexed { index, item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            notes[index] = notes[index].copy(second = !notes[index].second)
                        }
                        .padding(vertical = 2.dp)
                ) {
                    TwoPointFiveIcon(
                        imageVector = if (item.second) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Durum",
                        tint = if (item.second) Color(theme.accentColor) else Color(theme.textSecondary),
                        size = 14.dp,
                        depthOffset = 1.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.first,
                        fontSize = 11.sp,
                        color = if (item.second) Color(theme.textSecondary).copy(alpha = 0.7f) else Color(theme.textPrimary),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. WORLD CLOCK MATRIX WIDGET
// -------------------------------------------------------------
@Composable
fun WorldClockMatrixWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    var selectedZone by remember { mutableIntStateOf(0) }
    val zones = listOf(
        Triple("Tokyo", "Asia/Tokyo", "+9h"),
        Triple("New York", "America/New_York", "-5h"),
        Triple("Londra", "Europe/London", "-3h")
    )

    val currentZone = zones[selectedZone]
    val timeInZone = remember(selectedZone) {
        try {
            LocalDateTime.now(ZoneId.of(currentZone.second)).format(DateTimeFormatter.ofPattern("HH:mm"))
        } catch (e: Exception) {
            "--:--"
        }
    }

    InteractiveWidgetCard(
        title = "Dünya Saati",
        icon = Icons.Default.Public,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        onClick = { selectedZone = (selectedZone + 1) % zones.size },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = timeInZone,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(theme.textPrimary)
                )
                Text(
                    text = "${currentZone.first} (${currentZone.third})",
                    fontSize = 11.sp,
                    color = Color(theme.accentColor),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(theme.accentColor).copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("Değiştir", fontSize = 11.sp, color = Color(theme.accentColor), fontWeight = FontWeight.Bold)
            }
        }
    }
}

// -------------------------------------------------------------
// 5. AIR QUALITY & UV INDEX WIDGET
// -------------------------------------------------------------
@Composable
fun AirQualityUvWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    InteractiveWidgetCard(
        title = "Hava Kalitesi & UV",
        icon = Icons.Default.Air,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // AQI
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF00E676)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AQI 32", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(theme.textPrimary))
                }
                Text("Mükemmel", fontSize = 11.sp, color = Color(0xFF00E676))
            }

            // UV
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TwoPointFiveIcon(Icons.Default.WbSunny, "UV", size = 14.dp, tint = Color(0xFFFFD54F), depthOffset = 1.dp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("UV 4.2", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(theme.textPrimary))
                }
                Text("Orta Seviye", fontSize = 11.sp, color = Color(0xFFFFD54F))
            }
        }
    }
}

// -------------------------------------------------------------
// 6. MEDIA PLAYBACK CONTROLLER WIDGET
// -------------------------------------------------------------
@Composable
fun MediaPlaybackWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(true) }
    var trackIndex by remember { mutableIntStateOf(0) }
    val tracks = listOf(
        "Midnight City - M83",
        "Starboy - The Weeknd",
        "Resonance - HOME",
        "Get Lucky - Daft Punk"
    )

    InteractiveWidgetCard(
        title = "Medya Kontrolü",
        icon = Icons.Default.MusicNote,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        modifier = modifier
    ) {
        Column {
            Text(
                text = tracks[trackIndex],
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(theme.textPrimary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Equalizer wave bars
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    val transition = rememberInfiniteTransition(label = "eq")
                    for (i in 0..4) {
                        val barHeight by transition.animateFloat(
                            initialValue = 4f,
                            targetValue = if (isPlaying) (12f + i * 3f) else 4f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(300 + i * 80, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "eq_bar_$i"
                        )
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(barHeight.dp)
                                .clip(RoundedCornerShape(1.5.dp))
                                .background(Color(theme.accentColor))
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(theme.accentColor))
                            .clickable { isPlaying = !isPlaying },
                        contentAlignment = Alignment.Center
                    ) {
                        TwoPointFiveIcon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Oynat",
                            tint = Color.Black,
                            size = 18.dp,
                            depthOffset = 1.dp
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(theme.textSecondary).copy(alpha = 0.15f))
                            .clickable { trackIndex = (trackIndex + 1) % tracks.size },
                        contentAlignment = Alignment.Center
                    ) {
                        TwoPointFiveIcon(Icons.Default.FastForward, "İleri", size = 14.dp, tint = Color(theme.textSecondary), depthOffset = 1.dp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 7. ACTIVITY RINGS & STEP WIDGET
// -------------------------------------------------------------
@Composable
fun ActivityRingsWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    var steps by remember { mutableIntStateOf(6842) }
    val stepGoal = 10000

    InteractiveWidgetCard(
        title = "Aktivite Halkaları",
        icon = Icons.Default.DirectionsRun,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "$steps Adım",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(theme.textPrimary)
                )
                Text(
                    text = "${(steps * 0.04).toInt()} kcal • 4.8 km",
                    fontSize = 11.sp,
                    color = Color(theme.textSecondary)
                )
            }

            // Mini Rings Canvas
            Canvas(modifier = Modifier.size(38.dp)) {
                val strokeWidth = 4.dp.toPx()
                // Move Ring (Red/Coral)
                drawArc(
                    color = Color(0xFFFF5252).copy(alpha = 0.25f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(strokeWidth, cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color(0xFFFF5252),
                    startAngle = -90f,
                    sweepAngle = (steps.toFloat() / stepGoal * 360f).coerceIn(0f, 360f),
                    useCenter = false,
                    style = Stroke(strokeWidth, cap = StrokeCap.Round)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 8. QUICK CALCULATOR WIDGET
// -------------------------------------------------------------
@Composable
fun QuickCalculatorWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    var calcDisplay by remember { mutableStateOf("0") }
    var operand1 by remember { mutableDoubleStateOf(0.0) }
    var operator by remember { mutableStateOf("") }
    var resetNext by remember { mutableStateOf(false) }

    fun onNumber(n: String) {
        calcDisplay = if (calcDisplay == "0" || resetNext) n else calcDisplay + n
        resetNext = false
    }

    fun onOp(op: String) {
        operand1 = calcDisplay.toDoubleOrNull() ?: 0.0
        operator = op
        resetNext = true
    }

    fun onEqual() {
        val operand2 = calcDisplay.toDoubleOrNull() ?: 0.0
        val res = when (operator) {
            "+" -> operand1 + operand2
            "-" -> operand1 - operand2
            "×" -> operand1 * operand2
            "÷" -> if (operand2 != 0.0) operand1 / operand2 else 0.0
            else -> operand2
        }
        calcDisplay = if (res % 1.0 == 0.0) res.toInt().toString() else String.format(Locale.US, "%.2f", res)
        operator = ""
        resetNext = true
    }

    InteractiveWidgetCard(
        title = "Hesap Makinesi",
        icon = Icons.Default.Calculate,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        modifier = modifier
    ) {
        Column {
            Text(
                text = calcDisplay,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                color = Color(theme.textPrimary),
                modifier = Modifier.fillMaxWidth().padding(end = 4.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("7", "8", "9", "+").forEach { key ->
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (key == "+") Color(theme.accentColor) else Color(theme.textSecondary).copy(alpha = 0.15f))
                            .clickable { if (key == "+") onOp("+") else onNumber(key) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(key, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (key == "+") Color.Black else Color(theme.textPrimary))
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("4", "5", "6", "=").forEach { key ->
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (key == "=") Color(0xFF00E676) else Color(theme.textSecondary).copy(alpha = 0.15f))
                            .clickable { if (key == "=") onEqual() else onNumber(key) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(key, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (key == "=") Color.Black else Color(theme.textPrimary))
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 9. BATTERY POWER SAVER & MODE WIDGET
// -------------------------------------------------------------
@Composable
fun BatteryPowerModeWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    var powerModeIndex by remember { mutableIntStateOf(1) } // 0: Eko, 1: Dengeli, 2: Performans
    val modes = listOf("Eko", "Dengeli", "Maks")

    InteractiveWidgetCard(
        title = "Pil & Güç Modu",
        icon = Icons.Default.BatteryChargingFull,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        modifier = modifier
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "%${telemetry.batteryPercent}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(theme.textPrimary)
                )
                Text(
                    text = if (telemetry.isCharging) "Hızlı Şarj" else "Kalan ~14s",
                    fontSize = 11.sp,
                    color = Color(0xFF81C784)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Mode Selector Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(theme.textSecondary).copy(alpha = 0.15f))
                    .padding(2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                modes.forEachIndexed { index, mode ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (powerModeIndex == index) Color(theme.accentColor) else Color.Transparent)
                            .clickable { powerModeIndex = index }
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (powerModeIndex == index) Color.Black else Color(theme.textSecondary)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 10. ZEN BREATHING COACH WIDGET
// -------------------------------------------------------------
@Composable
fun ZenBreathingWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    var isBreathingActive by remember { mutableStateOf(false) }
    var breathPhase by remember { mutableStateOf("Nefes Al") }
    var phaseSeconds by remember { mutableIntStateOf(4) }

    LaunchedEffect(isBreathingActive) {
        while (isBreathingActive) {
            breathPhase = "Nefes Al"
            for (i in 4 downTo 1) { phaseSeconds = i; delay(1000) }
            breathPhase = "Tut"
            for (i in 7 downTo 1) { phaseSeconds = i; delay(1000) }
            breathPhase = "Nefes Ver"
            for (i in 8 downTo 1) { phaseSeconds = i; delay(1000) }
        }
    }

    val breathTransition = rememberInfiniteTransition(label = "breath_pulse")
    val breathScale by breathTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath_scale"
    )

    InteractiveWidgetCard(
        title = "Zen Nefes",
        icon = Icons.Default.SelfImprovement,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = if (isBreathingActive) breathPhase else "4-7-8 Tekniği",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(theme.accentColor)
                )
                Text(
                    text = if (isBreathingActive) "$phaseSeconds sn" else "Dokun ve Sakinleş",
                    fontSize = 11.sp,
                    color = Color(theme.textSecondary)
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(theme.accentColor).copy(alpha = 0.2f))
                    .clickable { isBreathingActive = !isBreathingActive },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .scale(if (isBreathingActive) breathScale else 1f)
                        .clip(CircleShape)
                        .background(Color(theme.accentColor))
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 11. CURRENCY & CRYPTO CONVERTER WIDGET
// -------------------------------------------------------------
@Composable
fun CurrencyCryptoWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val rates = listOf(
        Pair("USD/TRY", "₺38.45"),
        Pair("EUR/TRY", "₺41.20"),
        Pair("BTC/USD", "$94,250"),
        Pair("ETH/USD", "$3,140")
    )

    InteractiveWidgetCard(
        title = "Piyasalar",
        icon = Icons.Default.CurrencyExchange,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        onClick = { selectedIndex = (selectedIndex + 1) % rates.size },
        modifier = modifier
    ) {
        val current = rates[selectedIndex]
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = current.second,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(theme.textPrimary)
                )
                Text(
                    text = current.first,
                    fontSize = 11.sp,
                    color = Color(0xFF00E676),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF00E676).copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("+%2.4", fontSize = 11.sp, color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
            }
        }
    }
}

// -------------------------------------------------------------
// 12. DAILY HABIT STREAK WIDGET
// -------------------------------------------------------------
@Composable
fun DailyHabitsWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    var habits = remember {
        mutableStateListOf(
            Pair("Kitap Oku", true),
            Pair("Meditasyon", false),
            Pair("Egzersiz", true)
        )
    }

    val doneCount = habits.count { it.second }

    InteractiveWidgetCard(
        title = "Alışkanlıklar",
        icon = Icons.Default.CheckCircle,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        modifier = modifier
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$doneCount/${habits.size} Tamamlandı",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(theme.textPrimary)
                )
                Text("🔥 7 Gün", fontSize = 11.sp, color = Color(0xFFFF9100), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                habits.forEachIndexed { index, habit ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (habit.second) Color(theme.accentColor).copy(alpha = 0.25f) else Color(theme.textSecondary).copy(alpha = 0.12f))
                            .clickable { habits[index] = habits[index].copy(second = !habits[index].second) }
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = habit.first,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (habit.second) Color(theme.accentColor) else Color(theme.textSecondary),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 13. UNIT CONVERTER WIDGET
// -------------------------------------------------------------
@Composable
fun UnitConverterWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    var modeIndex by remember { mutableIntStateOf(0) }
    var inputValue by remember { mutableFloatStateOf(24f) }

    val conversions = listOf(
        Triple("24°C", "= 75.2°F", "Sıcaklık"),
        Triple("10 km", "= 6.2 mil", "Mesafe"),
        Triple("70 kg", "= 154.3 lb", "Ağırlık")
    )

    InteractiveWidgetCard(
        title = "Birim Çevirici",
        icon = Icons.Default.Speed,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        onClick = { modeIndex = (modeIndex + 1) % conversions.size },
        modifier = modifier
    ) {
        val curr = conversions[modeIndex]
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "${curr.first} ${curr.second}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(theme.textPrimary)
                )
                Text(curr.third, fontSize = 11.sp, color = Color(theme.accentColor))
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(theme.accentColor).copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("Değiştir", fontSize = 10.sp, color = Color(theme.accentColor), fontWeight = FontWeight.Bold)
            }
        }
    }
}

// -------------------------------------------------------------
// 14. COMPASS & SPIRIT LEVEL WIDGET
// -------------------------------------------------------------
@Composable
fun CompassLevelWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    val azimuth = (telemetry.compassDegrees).toInt()
    val pitch = (telemetry.tiltPitch).toInt()
    val roll = (telemetry.tiltRoll).toInt()

    InteractiveWidgetCard(
        title = "Pusula & Terazi",
        icon = Icons.Default.Explore,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "$azimuth° ${telemetry.compassDirection}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(theme.textPrimary)
                )
                Text(
                    text = "Eğim: ${pitch}° | Yatma: ${roll}°",
                    fontSize = 11.sp,
                    color = Color(theme.textSecondary)
                )
            }

            // Gyro Indicator Bubble
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(theme.textSecondary).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                TwoPointFiveIcon(
                    imageVector = Icons.Default.Explore,
                    contentDescription = "Pusula",
                    tint = Color(theme.accentColor),
                    size = 20.dp,
                    depthOffset = 1.dp,
                    modifier = Modifier.rotate(telemetry.compassDegrees)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 15. SYSTEM RAM & SPEED BOOSTER WIDGET
// -------------------------------------------------------------
@Composable
fun SystemResourceWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    var isBoosting by remember { mutableStateOf(false) }
    var ramUsedPercent by remember { mutableIntStateOf(64) }

    InteractiveWidgetCard(
        title = "RAM & Performans",
        icon = Icons.Default.Memory,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "%$ramUsedPercent Kullanım",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(theme.textPrimary)
                )
                Text(
                    text = "${(ramUsedPercent * 0.08).toInt()} GB / 8 GB RAM",
                    fontSize = 11.sp,
                    color = Color(theme.textSecondary)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF00E5FF))
                    .clickable {
                        isBoosting = true
                        ramUsedPercent = 42
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isBoosting) "Hızlandırıldı" else "Temizle",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 16. MOON PHASE & LUNAR CALENDAR WIDGET
// -------------------------------------------------------------
@Composable
fun MoonPhaseWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    InteractiveWidgetCard(
        title = "Ay Evresi",
        icon = Icons.Default.DarkMode,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Büyüyen Ay",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(theme.textPrimary)
                )
                Text(
                    text = "%78 Aydınlık • 11. Gün",
                    fontSize = 11.sp,
                    color = Color(theme.textSecondary)
                )
            }

            TwoPointFiveIcon(
                imageVector = Icons.Default.DarkMode,
                contentDescription = "Ay",
                tint = Color(0xFFE0E0E0),
                size = 28.dp,
                depthOffset = 2.dp
            )
        }
    }
}

// -------------------------------------------------------------
// 17. SUN EPHEMERIS & GOLDEN HOUR WIDGET
// -------------------------------------------------------------
@Composable
fun SunEphemerisWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    InteractiveWidgetCard(
        title = "Güneş Döngüsü",
        icon = Icons.Default.WbSunny,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Altın Saat: 19:15",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFB300)
                )
                Text(
                    text = "Gün Batımı: 19:54 (4s 30dk)",
                    fontSize = 11.sp,
                    color = Color(theme.textSecondary)
                )
            }

            TwoPointFiveIcon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = "Güneş",
                tint = Color(0xFFFFB300),
                size = 28.dp,
                depthOffset = 2.dp
            )
        }
    }
}

// -------------------------------------------------------------
// 18. NETWORK SPEED & PING TESTER WIDGET
// -------------------------------------------------------------
@Composable
fun NetworkSpeedWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    var pingMs by remember { mutableIntStateOf(18) }
    var isTesting by remember { mutableStateOf(false) }

    LaunchedEffect(isTesting) {
        if (isTesting) {
            delay(800)
            pingMs = Random.nextInt(12, 28)
            isTesting = false
        }
    }

    InteractiveWidgetCard(
        title = "Ağ & Ping",
        icon = Icons.Default.Wifi,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "$pingMs ms Ping",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E676)
                )
                Text(
                    text = "84 Mbps • Wi-Fi 6",
                    fontSize = 11.sp,
                    color = Color(theme.textSecondary)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(theme.accentColor))
                    .clickable { isTesting = true }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(if (isTesting) "..." else "Test Et", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

// -------------------------------------------------------------
// 19. SCREEN FLASHLIGHT & STROBE WIDGET
// -------------------------------------------------------------
@Composable
fun ScreenFlashlightWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    var isOn by remember { mutableStateOf(false) }
    var isSos by remember { mutableStateOf(false) }

    InteractiveWidgetCard(
        title = "Ekran Feneri",
        icon = Icons.Default.FlashlightOn,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = if (isOn) "Fener Açık" else "Fener Kapalı",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isOn) Color(0xFFFFEB3B) else Color(theme.textPrimary)
                )
                Text(
                    text = if (isSos) "SOS Modu Aktif" else "Normal Işık",
                    fontSize = 11.sp,
                    color = Color(theme.textSecondary)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isOn) Color(0xFFFFEB3B) else Color(theme.textSecondary).copy(alpha = 0.15f))
                        .clickable { isOn = !isOn },
                    contentAlignment = Alignment.Center
                ) {
                    TwoPointFiveIcon(
                        imageVector = Icons.Default.FlashlightOn,
                        contentDescription = "Aç/Kapa",
                        tint = if (isOn) Color.Black else Color(theme.textSecondary),
                        size = 18.dp,
                        depthOffset = 1.dp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 20. DICE ROLLER & DECISION COIN WIDGET
// -------------------------------------------------------------
@Composable
fun DiceCoinWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    var diceResult by remember { mutableIntStateOf(6) }
    var coinResult by remember { mutableStateOf("Yazı") }

    InteractiveWidgetCard(
        title = "Zar & Yazı Tura",
        icon = Icons.Default.Refresh,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Dice
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(theme.accentColor).copy(alpha = 0.2f))
                    .clickable { diceResult = Random.nextInt(1, 7) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("🎲 Zar: $diceResult", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(theme.accentColor))
            }

            // Coin
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFFD54F).copy(alpha = 0.2f))
                    .clickable { coinResult = if (Random.nextBoolean()) "Yazı" else "Tura" }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("🪙 $coinResult", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F))
            }
        }
    }
}

// -------------------------------------------------------------
// 21. DAILY MOTIVATION & QUOTE WIDGET
// -------------------------------------------------------------
@Composable
fun DailyMotivationWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    val quotes = listOf(
        "\"Zaman en değerli sermayedir.\" - Seneca",
        "\"Başarı her gün atılan küçük adımlarla gelir.\"",
        "\"Zihnini sakinleştir, dünya berraklaşsın.\"",
        "\"Bugün harika bir gün yaratmak senin elinde.\""
    )
    var quoteIndex by remember { mutableIntStateOf(0) }

    InteractiveWidgetCard(
        title = "Günün Sözü",
        icon = Icons.Default.Favorite,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        onClick = { quoteIndex = (quoteIndex + 1) % quotes.size },
        modifier = modifier
    ) {
        Column {
            Text(
                text = quotes[quoteIndex],
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(theme.textPrimary),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text("Yeni söz için dokun", fontSize = 9.sp, color = Color(theme.accentColor))
        }
    }
}

// -------------------------------------------------------------
// 22. WHITE NOISE & AMBIENT SOUND WIDGET
// -------------------------------------------------------------
@Composable
fun WhiteNoiseWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    var selectedSound by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    val sounds = listOf("🌧️ Yağmur", "🌊 Dalga", "🔥 Şömine", "🌲 Orman")

    InteractiveWidgetCard(
        title = "Doğa Ambiyansı",
        icon = Icons.Default.VolumeUp,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = sounds[selectedSound],
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(theme.textPrimary)
                )
                Text(
                    text = if (isPlaying) "Çalıyor..." else "Duraklatıldı",
                    fontSize = 11.sp,
                    color = if (isPlaying) Color(0xFF00E676) else Color(theme.textSecondary)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(theme.textSecondary).copy(alpha = 0.15f))
                        .clickable { selectedSound = (selectedSound + 1) % sounds.size },
                    contentAlignment = Alignment.Center
                ) {
                    TwoPointFiveIcon(Icons.Default.Refresh, "Değiştir", size = 14.dp, tint = Color(theme.textSecondary), depthOffset = 1.dp)
                }

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(theme.accentColor))
                        .clickable { isPlaying = !isPlaying },
                    contentAlignment = Alignment.Center
                ) {
                    TwoPointFiveIcon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Oynat",
                        tint = Color.Black,
                        size = 18.dp,
                        depthOffset = 1.dp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 23. COUNTDOWN EVENT TRACKER WIDGET
// -------------------------------------------------------------
@Composable
fun CountdownEventWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    var eventIndex by remember { mutableIntStateOf(0) }
    val events = listOf(
        Pair("Yılbaşı 2027", "127 Gün"),
        Pair("Hafta Sonu", "2 Gün"),
        Pair("Yaz Tatili", "45 Gün")
    )

    InteractiveWidgetCard(
        title = "Geri Sayım",
        icon = Icons.Default.Alarm,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        onClick = { eventIndex = (eventIndex + 1) % events.size },
        modifier = modifier
    ) {
        val curr = events[eventIndex]
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = curr.second,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(theme.accentColor)
                )
                Text(curr.first, fontSize = 11.sp, color = Color(theme.textSecondary))
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(theme.accentColor).copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("Sonraki", fontSize = 10.sp, color = Color(theme.accentColor), fontWeight = FontWeight.Bold)
            }
        }
    }
}

// -------------------------------------------------------------
// 24. CAFFEINE TRACKER WIDGET
// -------------------------------------------------------------
@Composable
fun CaffeineTrackerWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    var cups by remember { mutableIntStateOf(2) }
    val totalMg = cups * 80

    InteractiveWidgetCard(
        title = "Kafein Takibi",
        icon = Icons.Default.Coffee,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "$totalMg mg Kafein",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFB74D)
                )
                Text(
                    text = "$cups Fincan • Uyku sınırı 16:00",
                    fontSize = 11.sp,
                    color = Color(theme.textSecondary)
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFB74D))
                    .clickable { cups += 1 },
                contentAlignment = Alignment.Center
            ) {
                TwoPointFiveIcon(Icons.Default.Add, "+1 Kahve", size = 16.dp, tint = Color.Black, depthOffset = 1.dp)
            }
        }
    }
}

// -------------------------------------------------------------
// 25. DECIBEL SOUND LEVEL METER WIDGET
// -------------------------------------------------------------
@Composable
fun DecibelMeterWidget(
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    var dbLevel by remember { mutableIntStateOf(42) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1500)
            dbLevel = Random.nextInt(36, 58)
        }
    }

    val status = when {
        dbLevel < 45 -> "Sessiz Ortam"
        dbLevel < 65 -> "Normal Konuşma"
        else -> "Gürültülü"
    }

    InteractiveWidgetCard(
        title = "Ses Seviyesi (dB)",
        icon = Icons.Default.Mic,
        theme = theme,
        tiltPitch = telemetry.tiltPitch,
        tiltRoll = telemetry.tiltRoll,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "$dbLevel dB",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (dbLevel > 60) Color(0xFFFF5252) else Color(theme.textPrimary)
                )
                Text(status, fontSize = 11.sp, color = Color(0xFF00E676))
            }

            TwoPointFiveIcon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = "Ses",
                tint = Color(theme.accentColor),
                size = 24.dp,
                depthOffset = 1.5.dp
            )
        }
    }
}

// -------------------------------------------------------------
// Universal Interactive Widget Renderer
// -------------------------------------------------------------
@Composable
fun RenderInteractiveWidget(
    widgetId: String,
    theme: DashboardThemeConfig,
    telemetry: DeviceTelemetry,
    modifier: Modifier = Modifier
) {
    when (widgetId) {
        "focus_pomodoro" -> FocusPomodoroWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "hydration_tracker" -> HydrationTrackerWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "quick_notes" -> QuickNotesWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "world_clock" -> WorldClockMatrixWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "air_quality_uv" -> AirQualityUvWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "media_playback" -> MediaPlaybackWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "activity_rings" -> ActivityRingsWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "quick_calculator" -> QuickCalculatorWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "battery_power" -> BatteryPowerModeWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "zen_breathing" -> ZenBreathingWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "currency_crypto" -> CurrencyCryptoWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "daily_habits" -> DailyHabitsWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "unit_converter" -> UnitConverterWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "compass_level" -> CompassLevelWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "system_resource" -> SystemResourceWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "moon_phase" -> MoonPhaseWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "sun_ephemeris" -> SunEphemerisWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "network_speed" -> NetworkSpeedWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "screen_flashlight" -> ScreenFlashlightWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "dice_coin" -> DiceCoinWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "daily_motivation" -> DailyMotivationWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "white_noise" -> WhiteNoiseWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "countdown_event" -> CountdownEventWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "caffeine_tracker" -> CaffeineTrackerWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        "decibel_meter" -> DecibelMeterWidget(theme = theme, telemetry = telemetry, modifier = modifier)
        else -> FocusPomodoroWidget(theme = theme, telemetry = telemetry, modifier = modifier)
    }
}
