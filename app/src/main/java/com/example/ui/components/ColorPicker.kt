package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val PresetColors = listOf(
    0xFF111827, // Jet Charcoal
    0xFFFFFFFF, // Pure White
    0xFF2563EB, // Apple Blue
    0xFF3B82F6, // Electric Blue
    0xFF06B6D4, // Cyan
    0xFF10B981, // Emerald Green
    0xFF84CC16, // Lime
    0xFFF59E0B, // Amber Gold
    0xFFF97316, // Orange
    0xFFEF4444, // Crimson Red
    0xFFEC4899, // Neon Pink
    0xFF8B5CF6, // Deep Violet
    0xFF6B7280, // Slate Gray
    0xFFD4AF37, // Titanium Gold
    0xFF00E5FF, // Cyber Cyan
    0xFF76FF03  // Matrix Green
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPicker(
    selectedColorHex: Long,
    onColorSelected: (Long) -> Unit,
    title: String = "Renk Seçimi",
    isDark: Boolean = false,
    modifier: Modifier = Modifier
) {
    var hexInput by remember(selectedColorHex) {
        mutableStateOf(String.format("#%08X", selectedColorHex))
    }

    val titleColor = if (isDark) Color.White.copy(alpha = 0.9f) else Color(0xFF111827)
    val circleBorderUnselected = if (isDark) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.15f)
    val circleBorderSelected = if (isDark) Color.White else Color(0xFF111827)

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = titleColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Preset Colors FlowRow
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            PresetColors.forEach { colorVal ->
                val isSelected = selectedColorHex == colorVal
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(colorVal))
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) circleBorderSelected else circleBorderUnselected,
                            shape = CircleShape
                        )
                        .clickable {
                            onColorSelected(colorVal)
                            hexInput = String.format("#%08X", colorVal)
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Hex Code Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(selectedColorHex))
                    .border(
                        1.dp,
                        if (isDark) Color.White.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.2f),
                        RoundedCornerShape(8.dp)
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedTextField(
                value = hexInput,
                onValueChange = { input ->
                    hexInput = input
                    try {
                        val clean = input.removePrefix("#").trim()
                        val parsed = if (clean.length == 6) {
                            (0xFF000000 or clean.toLong(16))
                        } else if (clean.length == 8) {
                            clean.toLong(16)
                        } else null

                        if (parsed != null) {
                            onColorSelected(parsed)
                        }
                    } catch (e: Exception) {
                        // ignore invalid syntax while typing
                    }
                },
                label = { Text("HEX Renk Kodu", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedBorderColor = if (isDark) Color.White.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.2f),
                    focusedTextColor = if (isDark) Color.White else Color(0xFF111827),
                    unfocusedTextColor = if (isDark) Color.White.copy(alpha = 0.9f) else Color(0xFF1F2937),
                    focusedLabelColor = Color(0xFF2563EB),
                    unfocusedLabelColor = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF6B7280)
                )
            )
        }
    }
}
