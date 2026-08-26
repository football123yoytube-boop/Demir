package com.example.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object AstronomyFormatter {

    fun formatIsoTime(isoString: String?): String {
        if (isoString.isNullOrBlank()) return "--:--"
        return try {
            if (isoString.contains("T")) {
                val timePart = isoString.substringAfter("T")
                timePart.take(5) // e.g. "06:20"
            } else {
                isoString.takeLast(5)
            }
        } catch (e: Exception) {
            "--:--"
        }
    }

    fun formatDaylightDuration(seconds: Double?): String {
        if (seconds == null || seconds <= 0) return "--s --dk"
        val totalMinutes = (seconds / 60).toLong()
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return "${hours}s ${minutes}dk"
    }

    data class MoonPhaseInfo(
        val name: String,
        val emoji: String,
        val illuminationPercent: Int
    )

    fun getMoonPhaseInfo(phaseValue: Double?): MoonPhaseInfo {
        if (phaseValue == null) {
            return MoonPhaseInfo("Ay Bilgisi", "🌙", 50)
        }
        val normalized = ((phaseValue % 1.0) + 1.0) % 1.0 // Ensure 0.0..1.0
        val illumination = when {
            normalized <= 0.5 -> (normalized * 2 * 100).toInt()
            else -> ((1.0 - normalized) * 2 * 100).toInt()
        }

        val (name, emoji) = when {
            normalized < 0.03 || normalized > 0.97 -> "Yeni Ay" to "🌑"
            normalized < 0.22 -> "Hilal" to "🌒"
            normalized < 0.28 -> "İlkdördün" to "🌓"
            normalized < 0.47 -> "Şişkin Ay" to "🌔"
            normalized < 0.53 -> "Dolunay" to "🌕"
            normalized < 0.72 -> "Küçülen Şişkin Ay" to "🌖"
            normalized < 0.78 -> "Sondördün" to "🌗"
            else -> "Küçülen Hilal" to "🌘"
        }

        return MoonPhaseInfo(name, emoji, illumination)
    }
}
