package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.DashboardThemeConfig
import com.example.data.ThemePresets
import com.example.data.UserDashboardPreferences
import com.example.ui.components.ColorPicker
import com.example.ui.components.TwoPointFiveIcon
import com.example.ui.components.TwoPointFiveIconBadge
import com.example.util.AppFontFamily
import com.example.util.ClockAlignment
import com.example.util.DayNumberFormat
import com.example.viewmodel.DashboardUiState
import com.example.viewmodel.DashboardViewModel

enum class SettingsCategory(val title: String, val icon: ImageVector) {
    GENERAL("Genel", Icons.Default.Tune),
    THEMES("Görünüm & Tema", Icons.Default.Palette),
    CLOCK("Saat & Tipografi", Icons.Default.AccessTime),
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    WEATHER("Hava Durumu", Icons.Default.Cloud),
    ANIMATIONS("Animasyonlar", Icons.Default.Animation),
    DISPLAY("Ekran & Güç", Icons.Default.BrightnessMedium),
    SYSTEM("Sistem", Icons.Default.Devices),
    BACKUP("Yedekleme", Icons.Default.Save),
    ABOUT("Hakkında", Icons.Default.Info)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsDialog(
    viewModel: DashboardViewModel,
    uiState: DashboardUiState,
    onDismiss: () -> Unit
) {
    val theme = uiState.effectiveTheme
    val prefs = uiState.preferences
    val isDark = theme.isDark

    var selectedCategory by remember { mutableStateOf(SettingsCategory.THEMES) }
    var searchQuery by remember { mutableStateOf("") }
    var showResetDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importJsonText by remember { mutableStateOf("") }
    var importError by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current

    // Gallery image picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.updatePreferences {
                it.copy(
                    backgroundType = "USER_IMAGE",
                    userImageUri = uri.toString()
                )
            }
        }
    }

    val dialogBg = if (!isDark) Color(0xFFFFFFFF) else Color(0xFF0C101A)
    val sidebarBg = if (!isDark) Color(0xFFF8F9FA) else Color(0xFF070A11)
    val textPrimary = if (!isDark) Color(0xFF111827) else Color(0xFFF9FAFB)
    val textSecondary = if (!isDark) Color(0xFF6B7280) else Color(0xFF9CA3AF)
    val cardSurface = if (!isDark) Color(0xFFF3F4F6) else Color(0xFF141926)
    val cardBorder = if (!isDark) Color(0x1F111827) else Color(0x2EFFFFFF)
    val accentColor = Color(theme.accentColor)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(dialogBg)
                .border(1.dp, cardBorder, RoundedCornerShape(28.dp))
                .testTag("settings_master_detail_dialog")
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // ==========================================
                // 1. TOP APP BAR & SEARCH BAR
                // ==========================================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(sidebarBg)
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Ayarlar",
                                tint = accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "AYARLAR & KONTROL PANELİ",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = textPrimary
                        )
                    }

                    // Search Filter Field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { query ->
                            searchQuery = query
                            val lower = query.lowercase()
                            when {
                                lower.contains("tema") || lower.contains("renk") || lower.contains("arka plan") -> selectedCategory = SettingsCategory.THEMES
                                lower.contains("saat") || lower.contains("font") || lower.contains("tarih") -> selectedCategory = SettingsCategory.CLOCK
                                lower.contains("hava") || lower.contains("sıcaklık") -> selectedCategory = SettingsCategory.WEATHER
                                lower.contains("dashboard") || lower.contains("island") -> selectedCategory = SettingsCategory.DASHBOARD
                                lower.contains("ekran") || lower.contains("parlaklık") -> selectedCategory = SettingsCategory.DISPLAY
                                lower.contains("animasyon") || lower.contains("titreşim") -> selectedCategory = SettingsCategory.ANIMATIONS
                                lower.contains("pil") || lower.contains("sistem") -> selectedCategory = SettingsCategory.SYSTEM
                                lower.contains("yedek") -> selectedCategory = SettingsCategory.BACKUP
                                lower.contains("hakkında") -> selectedCategory = SettingsCategory.ABOUT
                                lower.contains("genel") || lower.contains("sıfırla") -> selectedCategory = SettingsCategory.GENERAL
                            }
                        },
                        placeholder = { Text("Ayarlarda Ara (örn. font, tema, hava, pil)...", fontSize = 12.sp, color = textSecondary) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Ara", tint = textSecondary, modifier = Modifier.size(16.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "Temizle", tint = textSecondary, modifier = Modifier.size(14.dp))
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .width(360.dp)
                            .height(44.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = cardBorder,
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary
                        )
                    )

                    // Close Button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (!isDark) Color.Black.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = textPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                HorizontalDivider(color = cardBorder, thickness = 1.dp)

                // ==========================================
                // 2. MASTER-DETAIL SPLIT VIEW (LANDSCAPE)
                // ==========================================
                Row(modifier = Modifier.fillMaxSize()) {
                    // LEFT SIDEBAR: CATEGORIES
                    Column(
                        modifier = Modifier
                            .width(220.dp)
                            .fillMaxHeight()
                            .background(sidebarBg)
                            .padding(vertical = 8.dp, horizontal = 10.dp)
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(SettingsCategory.entries.size) { index ->
                                val category = SettingsCategory.entries[index]
                                val isSelected = selectedCategory == category

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isSelected) accentColor.copy(alpha = if (!isDark) 0.15f else 0.22f) else Color.Transparent
                                        )
                                        .clickable { selectedCategory = category }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TwoPointFiveIcon(
                                        imageVector = category.icon,
                                        contentDescription = category.title,
                                        tint = if (isSelected) accentColor else textSecondary,
                                        size = 18.dp,
                                        depthOffset = if (isSelected) 1.5.dp else 1.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = category.title,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) (if (!isDark) accentColor else Color.White) else textPrimary
                                    )
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(cardBorder)
                    )

                    // RIGHT CONTENT PANE: SELECTED CATEGORY DETAILS
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        when (selectedCategory) {
                            SettingsCategory.GENERAL -> item {
                                GeneralSettingsSection(
                                    onOpenReset = { showResetDialog = true },
                                    cardSurface = cardSurface,
                                    cardBorder = cardBorder,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary,
                                    accentColor = accentColor
                                )
                            }
                            SettingsCategory.THEMES -> item {
                                ThemesSettingsSection(
                                    viewModel = viewModel,
                                    prefs = prefs,
                                    theme = theme,
                                    cardSurface = cardSurface,
                                    cardBorder = cardBorder,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary,
                                    accentColor = accentColor,
                                    isDark = isDark
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                BackgroundSettingsSection(
                                    viewModel = viewModel,
                                    prefs = prefs,
                                    cardSurface = cardSurface,
                                    cardBorder = cardBorder,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary,
                                    accentColor = accentColor,
                                    isDark = isDark,
                                    onPickImage = { galleryLauncher.launch("image/*") }
                                )
                            }
                            SettingsCategory.CLOCK -> item {
                                ClockSettingsSection(
                                    viewModel = viewModel,
                                    prefs = prefs,
                                    cardSurface = cardSurface,
                                    cardBorder = cardBorder,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary,
                                    accentColor = accentColor,
                                    isDark = isDark
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                DateSettingsSection(
                                    viewModel = viewModel,
                                    prefs = prefs,
                                    cardSurface = cardSurface,
                                    cardBorder = cardBorder,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary,
                                    accentColor = accentColor,
                                    isDark = isDark
                                )
                            }
                            SettingsCategory.DASHBOARD -> item {
                                DashboardWidgetsSection(
                                    viewModel = viewModel,
                                    prefs = prefs,
                                    cardSurface = cardSurface,
                                    cardBorder = cardBorder,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary,
                                    accentColor = accentColor
                                )
                            }
                            SettingsCategory.WEATHER -> item {
                                WeatherSettingsSection(
                                    viewModel = viewModel,
                                    uiState = uiState,
                                    prefs = prefs,
                                    cardSurface = cardSurface,
                                    cardBorder = cardBorder,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary,
                                    accentColor = accentColor,
                                    isDark = isDark
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                AstronomySettingsSection(
                                    uiState = uiState,
                                    cardSurface = cardSurface,
                                    cardBorder = cardBorder,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary,
                                    accentColor = accentColor
                                )
                            }
                            SettingsCategory.ANIMATIONS -> item {
                                AnimationsSettingsSection(
                                    viewModel = viewModel,
                                    prefs = prefs,
                                    cardSurface = cardSurface,
                                    cardBorder = cardBorder,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary,
                                    accentColor = accentColor
                                )
                            }
                            SettingsCategory.DISPLAY -> item {
                                DisplaySettingsSection(
                                    viewModel = viewModel,
                                    prefs = prefs,
                                    cardSurface = cardSurface,
                                    cardBorder = cardBorder,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary,
                                    accentColor = accentColor
                                )
                            }
                            SettingsCategory.SYSTEM -> item {
                                SystemSettingsSection(
                                    uiState = uiState,
                                    cardSurface = cardSurface,
                                    cardBorder = cardBorder,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary,
                                    accentColor = accentColor
                                )
                            }
                            SettingsCategory.BACKUP -> item {
                                BackupSettingsSection(
                                    onOpenExport = { showExportDialog = true },
                                    onOpenImport = { showImportDialog = true },
                                    cardSurface = cardSurface,
                                    cardBorder = cardBorder,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary,
                                    accentColor = accentColor
                                )
                            }
                            SettingsCategory.ABOUT -> item {
                                AboutSettingsSection(
                                    cardSurface = cardSurface,
                                    cardBorder = cardBorder,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary,
                                    accentColor = accentColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Varsayılanlara Sıfırla") },
            text = { Text("Tüm özelleştirmeler ve ayarlar varsayılan (Pure White) değerlerine döndürülecektir. Onaylıyor musunuz?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetPreferences()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Sıfırla", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }

    // Export Theme JSON Dialog
    if (showExportDialog) {
        val exportedJson = viewModel.exportTheme()
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Tema JSON Dışa Aktar") },
            text = {
                Column {
                    Text("Mevcut temanızın yapılandırma JSON kodu:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = exportedJson,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().height(140.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(exportedJson))
                        showExportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text("Panoya Kopyala")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Kapat")
                }
            }
        )
    }

    // Import Theme JSON Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Tema JSON İçe Aktar") },
            text = {
                Column {
                    Text("Daha önce dışa aktarılan tema JSON metnini yapıştırın:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importJsonText,
                        onValueChange = { importJsonText = it; importError = false },
                        placeholder = { Text("{ \"id\": \"custom\", ... }") },
                        isError = importError,
                        modifier = Modifier.fillMaxWidth().height(140.dp)
                    )
                    if (importError) {
                        Text("Geçersiz JSON formatı! Lütfen kontrol edin.", color = Color(0xFFEF4444), fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val success = viewModel.importTheme(importJsonText)
                        if (success) {
                            showImportDialog = false
                            importJsonText = ""
                        } else {
                            importError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text("Uygula")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}

// ==========================================
// SUB-SECTIONS IMPLEMENTATIONS
// ==========================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ThemesSettingsSection(
    viewModel: DashboardViewModel,
    prefs: UserDashboardPreferences,
    theme: DashboardThemeConfig,
    cardSurface: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color,
    isDark: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("HAZIR TEMA SEÇİMİ (${ThemePresets.allThemes.size} TEMA)", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = textPrimary)

        // 15 Themes Visual FlowGrid
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ThemePresets.allThemes.forEach { itemTheme ->
                val isSelected = !prefs.isCustomThemeActive && prefs.themeId == itemTheme.id
                val itemBg = itemTheme.backgroundColors.firstOrNull() ?: 0xFF000000

                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(72.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(itemBg))
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) accentColor else Color.Black.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            viewModel.updatePreferences {
                                it.copy(
                                    themeId = itemTheme.id,
                                    isCustomThemeActive = false
                                )
                            }
                        }
                        .padding(10.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = itemTheme.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(itemTheme.textPrimary)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(itemTheme.accentColor)))
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(itemTheme.clockColor)))
                            if (isSelected) {
                                Spacer(modifier = Modifier.weight(1f))
                                Text("AKTİF", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = accentColor)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Custom Color Customization
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("ÖZEL TEMA & RENK PALETİ", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textPrimary)

                ColorPicker(
                    title = "Vurgu Rengi (Accent)",
                    selectedColorHex = theme.accentColor,
                    isDark = isDark,
                    onColorSelected = { color ->
                        val updated = theme.copy(accentColor = color)
                        viewModel.updatePreferences {
                            it.copy(customTheme = updated, isCustomThemeActive = true)
                        }
                    }
                )

                ColorPicker(
                    title = "Saat Rengi",
                    selectedColorHex = theme.clockColor,
                    isDark = isDark,
                    onColorSelected = { color ->
                        val updated = theme.copy(clockColor = color)
                        viewModel.updatePreferences {
                            it.copy(customTheme = updated, isCustomThemeActive = true, clockColorHex = color)
                        }
                    }
                )

                ColorPicker(
                    title = "Kart / Yüzey Arka Planı",
                    selectedColorHex = theme.cardBackground,
                    isDark = isDark,
                    onColorSelected = { color ->
                        val updated = theme.copy(cardBackground = color)
                        viewModel.updatePreferences {
                            it.copy(customTheme = updated, isCustomThemeActive = true)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun BackgroundSettingsSection(
    viewModel: DashboardViewModel,
    prefs: UserDashboardPreferences,
    cardSurface: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color,
    isDark: Boolean,
    onPickImage: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("ARKA PLAN ÖZELLEŞTİRME", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = textPrimary)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Background Mode Selection
                Text("Arka Plan Tipi", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.updatePreferences { it.copy(backgroundType = "THEME_GRADIENT") } },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (prefs.backgroundType == "THEME_GRADIENT") accentColor else Color.Black.copy(alpha = 0.08f)
                        )
                    ) {
                        Text("Tema Arka Planı", fontSize = 12.sp, color = if (prefs.backgroundType == "THEME_GRADIENT") Color.White else textPrimary)
                    }

                    Button(
                        onClick = onPickImage,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (prefs.backgroundType == "USER_IMAGE") accentColor else Color.Black.copy(alpha = 0.08f)
                        )
                    ) {
                        Text("Galeriden Fotoğraf Seç", fontSize = 12.sp, color = if (prefs.backgroundType == "USER_IMAGE") Color.White else textPrimary)
                    }
                }

                if (prefs.backgroundType == "USER_IMAGE") {
                    Text("Bulanıklık: ${prefs.backgroundBlur.toInt()} dp", fontSize = 12.sp, color = textSecondary)
                    Slider(
                        value = prefs.backgroundBlur,
                        onValueChange = { v -> viewModel.updatePreferences { it.copy(backgroundBlur = v) } },
                        valueRange = 0f..25f,
                        colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                    )

                    Text("Opaklık: ${(prefs.backgroundOpacity * 100).toInt()}%", fontSize = 12.sp, color = textSecondary)
                    Slider(
                        value = prefs.backgroundOpacity,
                        onValueChange = { v -> viewModel.updatePreferences { it.copy(backgroundOpacity = v) } },
                        valueRange = 0.2f..1.0f,
                        colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                    )

                    Text("Karartma Katmanı: ${(prefs.backgroundDarkeningScrim * 100).toInt()}%", fontSize = 12.sp, color = textSecondary)
                    Slider(
                        value = prefs.backgroundDarkeningScrim,
                        onValueChange = { v -> viewModel.updatePreferences { it.copy(backgroundDarkeningScrim = v) } },
                        valueRange = 0.0f..0.85f,
                        colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                    )
                }
            }
        }
    }
}

@Composable
private fun ClockSettingsSection(
    viewModel: DashboardViewModel,
    prefs: UserDashboardPreferences,
    cardSurface: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color,
    isDark: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("SAAT & TİPOGRAFİ AYARLARI", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = textPrimary)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // 24-Hour Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("24 Saat Formatı", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                        Text("Açık: 20:05 / Kapalı: 08:05 PM", fontSize = 11.sp, color = textSecondary)
                    }
                    Switch(
                        checked = prefs.is24Hour,
                        onCheckedChange = { v -> viewModel.updatePreferences { it.copy(is24Hour = v) } },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = accentColor)
                    )
                }

                HorizontalDivider(color = cardBorder)

                // Show Seconds Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Saniye Göstergesi", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                        Text("Gerçek zamanlı saniyeyi saat yanında göster", fontSize = 11.sp, color = textSecondary)
                    }
                    Switch(
                        checked = prefs.showSeconds,
                        onCheckedChange = { v -> viewModel.updatePreferences { it.copy(showSeconds = v) } },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = accentColor)
                    )
                }

                HorizontalDivider(color = cardBorder)

                // Clock Size Scale Slider
                Text("Saat Boyutu / Ölçek: ${(prefs.clockScale * 100).toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                Slider(
                    value = prefs.clockScale,
                    onValueChange = { v -> viewModel.updatePreferences { it.copy(clockScale = v) } },
                    valueRange = 0.7f..1.5f,
                    colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                )

                // Letter Spacing Slider
                Text("Harf & Rakam Aralığı: ${prefs.clockLetterSpacing.toInt()} sp", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                Slider(
                    value = prefs.clockLetterSpacing,
                    onValueChange = { v -> viewModel.updatePreferences { it.copy(clockLetterSpacing = v) } },
                    valueRange = -4f..10f,
                    colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                )

                // Clock Alignment
                Text("Saat Hizalaması", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ClockAlignment.entries.forEach { align ->
                        Button(
                            onClick = { viewModel.updatePreferences { it.copy(clockAlignment = align) } },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (prefs.clockAlignment == align) accentColor else Color.Black.copy(alpha = 0.08f)
                            )
                        ) {
                            Text(align.displayName, fontSize = 12.sp, color = if (prefs.clockAlignment == align) Color.White else textPrimary)
                        }
                    }
                }

                // Font Family Selection - Requested Fonts: Inter, DM Sans, Times New Roman, Arial, Atatürk
                Text("Saat Fontu / Tipografi Ailesi", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                Text("Saatin dijital gösterimi ve tarih yazıları için premium font seçin:", fontSize = 11.sp, color = textSecondary)

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppFontFamily.entries.forEach { fontItem ->
                        val isSelected = prefs.clockFont == fontItem
                        Card(
                            onClick = { viewModel.updatePreferences { it.copy(clockFont = fontItem) } },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) accentColor.copy(alpha = 0.14f) else Color.Black.copy(alpha = 0.04f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) accentColor else cardBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = fontItem.displayName,
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) accentColor else textPrimary
                                        )
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(accentColor)
                                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                                            ) {
                                                Text("AKTİF", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                        }
                                    }
                                    if (fontItem.description.isNotEmpty()) {
                                        Text(
                                            text = fontItem.description,
                                            fontSize = 11.sp,
                                            color = textSecondary
                                        )
                                    }
                                }

                                // Live Typographic Sample Preview
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (!isDark) Color(0xFFE5E7EB) else Color(0xFF1E2430))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "20:05",
                                        style = androidx.compose.ui.text.TextStyle(
                                            fontFamily = fontItem.family,
                                            fontWeight = fontItem.defaultWeight,
                                            fontStyle = fontItem.fontStyle,
                                            fontSize = 16.sp,
                                            color = if (isSelected) accentColor else textPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DateSettingsSection(
    viewModel: DashboardViewModel,
    prefs: UserDashboardPreferences,
    cardSurface: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color,
    isDark: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("TARİH & GÜN AYARLARI", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = textPrimary)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Show Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Gün & Tarih Göster", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                        Text("Saatin üstünde Türkçe gün ve tam tarih", fontSize = 11.sp, color = textSecondary)
                    }
                    Switch(
                        checked = prefs.showDateWidget,
                        onCheckedChange = { v -> viewModel.updatePreferences { it.copy(showDateWidget = v) } },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = accentColor)
                    )
                }

                HorizontalDivider(color = cardBorder)

                // Show Day Number Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Ayrı Gün Numarası Rozeti", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                        Text("Örn: '26 GÜN'", fontSize = 11.sp, color = textSecondary)
                    }
                    Switch(
                        checked = prefs.showDayNumberWidget,
                        onCheckedChange = { v -> viewModel.updatePreferences { it.copy(showDayNumberWidget = v) } },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = accentColor)
                    )
                }

                if (prefs.showDayNumberWidget) {
                    Text("Rozet Biçimi", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DayNumberFormat.entries.forEach { fmt ->
                            Button(
                                onClick = { viewModel.updatePreferences { it.copy(dayNumberFormat = fmt) } },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (prefs.dayNumberFormat == fmt) accentColor else Color.Black.copy(alpha = 0.08f)
                                )
                            ) {
                                Text(fmt.displayName, fontSize = 11.sp, color = if (prefs.dayNumberFormat == fmt) Color.White else textPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherSettingsSection(
    viewModel: DashboardViewModel,
    uiState: DashboardUiState,
    prefs: UserDashboardPreferences,
    cardSurface: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color,
    isDark: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("HAVA DURUMU (OPEN-METEO)", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = textPrimary)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Location & Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Konum: ${uiState.location.cityName}, ${uiState.location.countryName}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                        Text("Durum: ${uiState.weatherStatusMessage}", fontSize = 11.sp, color = textSecondary)
                    }

                    Button(
                        onClick = { viewModel.fetchLocationAndWeather() },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                    ) {
                        Text("Konumu Yenile", fontSize = 12.sp)
                    }
                }

                HorizontalDivider(color = cardBorder)

                // Temperature Unit
                Text("Sıcaklık Birimi", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.updatePreferences { it.copy(temperatureUnit = "C") } },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (prefs.temperatureUnit == "C") accentColor else Color.Black.copy(alpha = 0.08f)
                        )
                    ) {
                        Text("Santigrat (°C)", fontSize = 12.sp, color = if (prefs.temperatureUnit == "C") Color.White else textPrimary)
                    }

                    Button(
                        onClick = { viewModel.updatePreferences { it.copy(temperatureUnit = "F") } },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (prefs.temperatureUnit == "F") accentColor else Color.Black.copy(alpha = 0.08f)
                        )
                    ) {
                        Text("Fahrenhayt (°F)", fontSize = 12.sp, color = if (prefs.temperatureUnit == "F") Color.White else textPrimary)
                    }
                }

                // Refresh Interval
                Text("Yenileme Sıklığı: ${prefs.weatherRefreshIntervalMinutes} Dakika", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                Slider(
                    value = prefs.weatherRefreshIntervalMinutes.toFloat(),
                    onValueChange = { v -> viewModel.updatePreferences { it.copy(weatherRefreshIntervalMinutes = v.toInt()) } },
                    valueRange = 5f..60f,
                    steps = 10,
                    colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                )
            }
        }
    }
}

@Composable
private fun AstronomySettingsSection(
    uiState: DashboardUiState,
    cardSurface: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color
) {
    val daily = uiState.dailyWeather
    val sunrise = daily?.sunrise?.firstOrNull()?.takeLast(5) ?: "--:--"
    val sunset = daily?.sunset?.firstOrNull()?.takeLast(5) ?: "--:--"
    val daylightHours = (daily?.daylightDuration?.firstOrNull() ?: 43200.0) / 3600.0

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("ASTRONOMİ & DÖNGÜ BİLGİLERİ", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = textPrimary)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("• Gün Doğumu: $sunrise", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = textPrimary)
                Text("• Gün Batımı: $sunset", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = textPrimary)
                Text("• Toplam Gün Işığı Süresi: ${String.format("%.1f", daylightHours)} Saat", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = textPrimary)
                Text("• Ay Evresi: Büyüyen Hilal / Dolunay Döngüsü", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = textPrimary)
            }
        }
    }
}

@Composable
private fun DashboardWidgetsSection(
    viewModel: DashboardViewModel,
    prefs: UserDashboardPreferences,
    cardSurface: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("DASHBOARD DÜZENİ & WIDGET GÖRÜNÜRLÜKLERİ", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = textPrimary)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                WidgetToggleRow("Dev Saat Widget'ı", prefs.showClockWidget, textPrimary, textSecondary, accentColor) { v ->
                    viewModel.updatePreferences { it.copy(showClockWidget = v) }
                }
                HorizontalDivider(color = cardBorder)
                WidgetToggleRow("Hava Durumu Kartı", prefs.showWeatherWidget, textPrimary, textSecondary, accentColor) { v ->
                    viewModel.updatePreferences { it.copy(showWeatherWidget = v) }
                }
                HorizontalDivider(color = cardBorder)
                WidgetToggleRow("Konum Göstergesi", prefs.showLocationWidget, textPrimary, textSecondary, accentColor) { v ->
                    viewModel.updatePreferences { it.copy(showLocationWidget = v) }
                }
                HorizontalDivider(color = cardBorder)
                WidgetToggleRow("Hızlı Araçlar (Zamanlayıcı)", prefs.showQuickToolsWidget, textPrimary, textSecondary, accentColor) { v ->
                    viewModel.updatePreferences { it.copy(showQuickToolsWidget = v) }
                }
            }
        }
    }
}

@Composable
private fun WidgetToggleRow(
    title: String,
    checked: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = accentColor)
        )
    }
}

@Composable
private fun DisplaySettingsSection(
    viewModel: DashboardViewModel,
    prefs: UserDashboardPreferences,
    cardSurface: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("EKRAN, GÜÇ & KORUMA AYARLARI", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = textPrimary)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                WidgetToggleRow("Ekranı Her Zaman Açık Tut (WakeLock)", prefs.keepScreenOn, textPrimary, textSecondary, accentColor) { v ->
                    viewModel.updatePreferences { it.copy(keepScreenOn = v) }
                }
                HorizontalDivider(color = cardBorder)
                WidgetToggleRow("Ekran Koruyucu / OLED Burn-in Önleme", prefs.screenSaverMode, textPrimary, textSecondary, accentColor) { v ->
                    viewModel.updatePreferences { it.copy(screenSaverMode = v) }
                }
                HorizontalDivider(color = cardBorder)
                WidgetToggleRow("Gece Saatlerinde Otomatik Karartma", prefs.autoNightDim, textPrimary, textSecondary, accentColor) { v ->
                    viewModel.updatePreferences { it.copy(autoNightDim = v) }
                }
                HorizontalDivider(color = cardBorder)
                WidgetToggleRow("Gece Sıcak Renk Tonu (Eye Comfort)", prefs.autoWarmNightTint, textPrimary, textSecondary, accentColor) { v ->
                    viewModel.updatePreferences { it.copy(autoWarmNightTint = v) }
                }
            }
        }
    }
}

@Composable
private fun AnimationsSettingsSection(
    viewModel: DashboardViewModel,
    prefs: UserDashboardPreferences,
    cardSurface: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("ANİMASYON & GERİ BİLDİRİM", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = textPrimary)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Animasyon Yoğunluğu", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("OFF", "LOW", "MEDIUM", "HIGH").forEach { intensity ->
                        Button(
                            onClick = { viewModel.updatePreferences { it.copy(animationIntensity = intensity) } },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (prefs.animationIntensity == intensity) accentColor else Color.Black.copy(alpha = 0.08f)
                            )
                        ) {
                            Text(intensity, fontSize = 11.sp, color = if (prefs.animationIntensity == intensity) Color.White else textPrimary)
                        }
                    }
                }

                HorizontalDivider(color = cardBorder)

                WidgetToggleRow("Dokunsal / Titreşim Geri Bildirimi", prefs.hapticFeedbackEnabled, textPrimary, textSecondary, accentColor) { v ->
                    viewModel.updatePreferences { it.copy(hapticFeedbackEnabled = v) }
                }
            }
        }
    }
}

@Composable
private fun SystemSettingsSection(
    uiState: DashboardUiState,
    cardSurface: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color
) {
    val telem = uiState.telemetry

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("SİSTEM DONANIM & TELEMETRİ BİLGİLERİ", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = textPrimary)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("• Pil Seviyesi: %${telem.batteryPercent} (${if (telem.isCharging) "Şarj Oluyor" else "Deşarj"})", fontSize = 13.sp, color = textPrimary)
                Text("• Pil Sıcaklığı: ${telem.batteryTempCelsius}°C", fontSize = 13.sp, color = textPrimary)
                Text("• Pil Voltajı: ${telem.batteryVoltageMv} mV", fontSize = 13.sp, color = textPrimary)
                Text("• Ağ Bağlantısı: ${telem.networkType} (${if (telem.isOnline) "Çevrimiçi" else "Çevrimdışı"})", fontSize = 13.sp, color = textPrimary)
                Text("• Cihaz Modeli: ${telem.deviceModel} (Android ${telem.androidVersion})", fontSize = 13.sp, color = textPrimary)
                Text("• Jiroskop Eğimi: Pitch ${telem.tiltPitch.toInt()}° / Roll ${telem.tiltRoll.toInt()}°", fontSize = 13.sp, color = textPrimary)
            }
        }
    }
}

@Composable
private fun GeneralSettingsSection(
    onOpenReset: () -> Unit,
    cardSurface: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("GENEL AYARLAR", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = textPrimary)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onOpenReset,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = "Sıfırla", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tüm Ayarları Varsayılana Sıfırla (Pure White)", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun BackupSettingsSection(
    onOpenExport: () -> Unit,
    onOpenImport: () -> Unit,
    cardSurface: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("YEDEKLEME & GERİ YÜKLEME", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = textPrimary)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onOpenExport,
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = "Dışa Aktar", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Temayı Dışa Aktar", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onOpenImport,
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor.copy(alpha = 0.8f))
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = "İçe Aktar", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tema İçe Aktar", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutSettingsSection(
    cardSurface: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("HAKKINDA", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = textPrimary)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Smart Desk Clock & Dashboard v2.0",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Text(
                    text = "• Apple Style Minimal Design System\n• Open-Meteo Canlı Entegrasyonu\n• 15 Premium Tema Preset",
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = textSecondary
                )
            }
        }
    }
}
