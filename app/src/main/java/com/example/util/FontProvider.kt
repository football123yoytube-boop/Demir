package com.example.util

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

enum class AppFontFamily(
    val displayName: String,
    val family: FontFamily,
    val defaultWeight: FontWeight,
    val fontStyle: FontStyle = FontStyle.Normal,
    val description: String = ""
) {
    INTER(
        displayName = "Inter",
        family = FontFamily.SansSerif,
        defaultWeight = FontWeight.Bold,
        description = "Modern, ultra-okunaklı ve dengeli dijital tipografi"
    ),
    DM_SANS(
        displayName = "DM Sans",
        family = FontFamily.SansSerif,
        defaultWeight = FontWeight.SemiBold,
        description = "Geometrik, yuvarlatılmış ve çağdaş estetik"
    ),
    TIMES_NEW_ROMAN(
        displayName = "Times New Roman",
        family = FontFamily.Serif,
        defaultWeight = FontWeight.Normal,
        description = "Klasik, asil ve lüks editoryal serif stili"
    ),
    ARIAL(
        displayName = "Arial",
        family = FontFamily.SansSerif,
        defaultWeight = FontWeight.Normal,
        description = "Nötr, net ve zamansız akıcı sans-serif"
    ),
    ATATURK(
        displayName = "Atatürk",
        family = FontFamily.Cursive,
        defaultWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        description = "Zarif, hat sanatı ve imza estetiğinde akıcı kalligrafi"
    ),
    MONOSPACE(
        displayName = "Monospace (Kod)",
        family = FontFamily.Monospace,
        defaultWeight = FontWeight.Bold,
        description = "Tek aralıklı terminal ve hacker estetiği"
    ),
    DIGITAL_LED(
        displayName = "Dijital LED",
        family = FontFamily.Monospace,
        defaultWeight = FontWeight.ExtraBold,
        description = "Retro 7-segment LED saat görünümü"
    );

    companion object {
        fun fromName(name: String?): AppFontFamily {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: INTER
        }
    }
}

enum class ClockAlignment(val displayName: String) {
    LEFT("Sol"),
    CENTER("Orta"),
    RIGHT("Sağ")
}

enum class DayNumberFormat(val displayName: String, val pattern: String) {
    BIG_DAY_LABEL("26 GÜN (Büyük)", "BIG"),
    DAY_DOT("26. Gün", "DOT"),
    PLAIN_NUMBER("26", "PLAIN"),
    MONTH_DAY_FULL("AYIN 26. GÜNÜ", "FULL")
}

