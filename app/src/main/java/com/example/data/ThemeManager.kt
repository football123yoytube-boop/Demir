package com.example.data

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DashboardThemeConfig(
    val id: String = "pure_white",
    val name: String = "Pure White",
    val isDark: Boolean = false,
    val backgroundColors: List<Long> = listOf(0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF),
    val cardBackground: Long = 0xF2F3F4F6,
    val cardBorder: Long = 0x24111827,
    val textPrimary: Long = 0xFF111827,
    val textSecondary: Long = 0xFF4B5563,
    val clockColor: Long = 0xFF111827,
    val accentColor: Long = 0xFF2563EB,
    val clockGlowColor: Long = 0x00000000,
    val glassOpacity: Float = 0.95f,
    val cornerRadiusDp: Int = 24,
    val shadowElevationDp: Int = 4
)

object ThemePresets {

    // 1. Pure White (Default - Clean solid white background)
    val PureWhite = DashboardThemeConfig(
        id = "pure_white",
        name = "Pure White",
        isDark = false,
        backgroundColors = listOf(0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF),
        cardBackground = 0xF2F3F4F6,
        cardBorder = 0x24111827,
        textPrimary = 0xFF111827,
        textSecondary = 0xFF4B5563,
        clockColor = 0xFF111827,
        accentColor = 0xFF000000,
        clockGlowColor = 0x00000000,
        glassOpacity = 0.95f,
        cornerRadiusDp = 24,
        shadowElevationDp = 4
    )

    // 2. Midnight
    val Midnight = DashboardThemeConfig(
        id = "midnight",
        name = "Midnight",
        isDark = true,
        backgroundColors = listOf(0xFF0F172A, 0xFF1E293B, 0xFF0F172A),
        cardBackground = 0x331E293B,
        cardBorder = 0x40334155,
        textPrimary = 0xFFF8FAFC,
        textSecondary = 0xFF94A3B8,
        clockColor = 0xFFF8FAFC,
        accentColor = 0xFF38BDF8,
        clockGlowColor = 0x00000000,
        glassOpacity = 0.60f,
        cornerRadiusDp = 24,
        shadowElevationDp = 8
    )

    // 3. OLED Black
    val OledBlack = DashboardThemeConfig(
        id = "oled_black",
        name = "OLED Black",
        isDark = true,
        backgroundColors = listOf(0xFF000000, 0xFF000000, 0xFF000000),
        cardBackground = 0x1AFFFFFF,
        cardBorder = 0x26FFFFFF,
        textPrimary = 0xFFFFFFFF,
        textSecondary = 0xFF888888,
        clockColor = 0xFFFFFFFF,
        accentColor = 0xFFFFFFFF,
        clockGlowColor = 0x00000000,
        glassOpacity = 0.40f,
        cornerRadiusDp = 24,
        shadowElevationDp = 0
    )

    // 4. Arctic
    val Arctic = DashboardThemeConfig(
        id = "arctic",
        name = "Arctic",
        isDark = false,
        backgroundColors = listOf(0xFFF8FAFC, 0xFFF1F5F9, 0xFFE2E8F0),
        cardBackground = 0x99FFFFFF,
        cardBorder = 0x240EA5E9,
        textPrimary = 0xFF0F172A,
        textSecondary = 0xFF475569,
        clockColor = 0xFF0F172A,
        accentColor = 0xFF0EA5E9,
        clockGlowColor = 0x00000000,
        glassOpacity = 0.85f,
        cornerRadiusDp = 24,
        shadowElevationDp = 4
    )

    // 5. Graphite
    val Graphite = DashboardThemeConfig(
        id = "graphite",
        name = "Graphite",
        isDark = true,
        backgroundColors = listOf(0xFF18181B, 0xFF27272A, 0xFF18181B),
        cardBackground = 0x3327272A,
        cardBorder = 0x4452525B,
        textPrimary = 0xFFFAFAFA,
        textSecondary = 0xFFA1A1AA,
        clockColor = 0xFFFAFAFA,
        accentColor = 0xFFA1A1AA,
        clockGlowColor = 0x00000000,
        glassOpacity = 0.55f,
        cornerRadiusDp = 24,
        shadowElevationDp = 8
    )

    // 6. Ocean
    val Ocean = DashboardThemeConfig(
        id = "ocean",
        name = "Ocean",
        isDark = true,
        backgroundColors = listOf(0xFF001B2E, 0xFF003049, 0xFF001B2E),
        cardBackground = 0x33003049,
        cardBorder = 0x4000B4D8,
        textPrimary = 0xFFE0FBFC,
        textSecondary = 0xFF90E0EF,
        clockColor = 0xFFFFFFFF,
        accentColor = 0xFF00B4D8,
        clockGlowColor = 0x00000000,
        glassOpacity = 0.60f,
        cornerRadiusDp = 24,
        shadowElevationDp = 8
    )

    // 7. Sunset
    val Sunset = DashboardThemeConfig(
        id = "sunset",
        name = "Sunset",
        isDark = true,
        backgroundColors = listOf(0xFF2B0F1A, 0xFF4A192C, 0xFF2B0F1A),
        cardBackground = 0x334A192C,
        cardBorder = 0x40F4A261,
        textPrimary = 0xFFFFF3E0,
        textSecondary = 0xFFFFCCBC,
        clockColor = 0xFFFFFFFF,
        accentColor = 0xFFE76F51,
        clockGlowColor = 0x00000000,
        glassOpacity = 0.60f,
        cornerRadiusDp = 24,
        shadowElevationDp = 8
    )

    // 8. Forest
    val Forest = DashboardThemeConfig(
        id = "forest",
        name = "Forest",
        isDark = true,
        backgroundColors = listOf(0xFF0F1F14, 0xFF1A3322, 0xFF0F1F14),
        cardBackground = 0x331A3322,
        cardBorder = 0x402A9D8F,
        textPrimary = 0xFFE8F5E9,
        textSecondary = 0xFFA5D6A7,
        clockColor = 0xFFFFFFFF,
        accentColor = 0xFF2A9D8F,
        clockGlowColor = 0x00000000,
        glassOpacity = 0.60f,
        cornerRadiusDp = 24,
        shadowElevationDp = 8
    )

    // 9. Lavender
    val Lavender = DashboardThemeConfig(
        id = "lavender",
        name = "Lavender",
        isDark = true,
        backgroundColors = listOf(0xFF1C1936, 0xFF2D2854, 0xFF1C1936),
        cardBackground = 0x332D2854,
        cardBorder = 0x409D4EDD,
        textPrimary = 0xFFF3E8FF,
        textSecondary = 0xFFD8B4FE,
        clockColor = 0xFFFFFFFF,
        accentColor = 0xFFC77DFF,
        clockGlowColor = 0x00000000,
        glassOpacity = 0.60f,
        cornerRadiusDp = 24,
        shadowElevationDp = 8
    )

    // 10. Glass
    val Glass = DashboardThemeConfig(
        id = "glass",
        name = "Glass",
        isDark = true,
        backgroundColors = listOf(0xFF111827, 0xFF1F2937, 0xFF374151),
        cardBackground = 0x1AFFFFFF,
        cardBorder = 0x33FFFFFF,
        textPrimary = 0xFFFFFFFF,
        textSecondary = 0xFFD1D5DB,
        clockColor = 0xFFFFFFFF,
        accentColor = 0xFFFFFFFF,
        clockGlowColor = 0x00000000,
        glassOpacity = 0.30f,
        cornerRadiusDp = 28,
        shadowElevationDp = 16
    )

    // 11. Minimal
    val Minimal = DashboardThemeConfig(
        id = "minimal",
        name = "Minimal",
        isDark = false,
        backgroundColors = listOf(0xFFF9FAFB, 0xFFF3F4F6, 0xFFE5E7EB),
        cardBackground = 0xFFFFFFFF,
        cardBorder = 0x1A000000,
        textPrimary = 0xFF111827,
        textSecondary = 0xFF6B7280,
        clockColor = 0xFF111827,
        accentColor = 0xFF000000,
        clockGlowColor = 0x00000000,
        glassOpacity = 1.0f,
        cornerRadiusDp = 24,
        shadowElevationDp = 2
    )

    // 12. Cyber
    val Cyber = DashboardThemeConfig(
        id = "cyber",
        name = "Cyber",
        isDark = true,
        backgroundColors = listOf(0xFF09090B, 0xFF18181B, 0xFF09090B),
        cardBackground = 0x3318181B,
        cardBorder = 0x6600FF9D,
        textPrimary = 0xFFFFFFFF,
        textSecondary = 0xFFA1A1AA,
        clockColor = 0xFF00FF9D,
        accentColor = 0xFF00FF9D,
        clockGlowColor = 0x3300FF9D,
        glassOpacity = 0.70f,
        cornerRadiusDp = 16,
        shadowElevationDp = 0
    )

    // 13. Aurora
    val Aurora = DashboardThemeConfig(
        id = "aurora",
        name = "Aurora",
        isDark = true,
        backgroundColors = listOf(0xFF0B1B22, 0xFF112D32, 0xFF0B1B22),
        cardBackground = 0x33112D32,
        cardBorder = 0x4038BDF8,
        textPrimary = 0xFFF0F9FF,
        textSecondary = 0xFF7DD3FC,
        clockColor = 0xFFFFFFFF,
        accentColor = 0xFF38BDF8,
        clockGlowColor = 0x00000000,
        glassOpacity = 0.60f,
        cornerRadiusDp = 24,
        shadowElevationDp = 8
    )

    // 14. Space
    val Space = DashboardThemeConfig(
        id = "space",
        name = "Space",
        isDark = true,
        backgroundColors = listOf(0xFF030014, 0xFF0A0028, 0xFF030014),
        cardBackground = 0x330A0028,
        cardBorder = 0x408A2BE2,
        textPrimary = 0xFFF5F3FF,
        textSecondary = 0xFFC4B5FD,
        clockColor = 0xFFFFFFFF,
        accentColor = 0xFF8A2BE2,
        clockGlowColor = 0x00000000,
        glassOpacity = 0.60f,
        cornerRadiusDp = 24,
        shadowElevationDp = 12
    )

    // 15. Custom
    val Custom = DashboardThemeConfig(
        id = "custom",
        name = "Özel Tema",
        isDark = false,
        backgroundColors = listOf(0xFFFFFFFF, 0xFFF3F4F6, 0xFFFFFFFF),
        cardBackground = 0xF2F3F4F6,
        cardBorder = 0x24111827,
        textPrimary = 0xFF111827,
        textSecondary = 0xFF6B7280,
        clockColor = 0xFF111827,
        accentColor = 0xFF2563EB,
        clockGlowColor = 0x00000000,
        glassOpacity = 0.95f,
        cornerRadiusDp = 24,
        shadowElevationDp = 4
    )

    val allThemes = listOf(
        PureWhite, Midnight, OledBlack, Arctic, Graphite,
        Ocean, Sunset, Forest, Lavender, Glass,
        Minimal, Cyber, Aurora, Space, Custom
    )

    fun getThemeById(id: String): DashboardThemeConfig {
        return allThemes.firstOrNull { it.id == id } ?: PureWhite
    }
}
