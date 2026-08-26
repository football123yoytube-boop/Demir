package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.DashboardThemeConfig

@Composable
fun DashboardDetailModal(
    title: String,
    icon: ImageVector,
    theme: DashboardThemeConfig,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val modalBg = if (!theme.isDark) Color(0xFFFFFFFF) else Color(0xF2070A12)
    val borderColor = if (!theme.isDark) Color(0x1F111827) else Color(0x2EFFFFFF)
    val closeBtnBg = if (!theme.isDark) Color.Black.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.08f)
    val closeBtnTint = if (!theme.isDark) Color(0xFF111827) else Color.White

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .widthIn(min = 360.dp, max = 560.dp)
                .shadow(
                    elevation = if (theme.isDark) 24.dp else 12.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = Color.Black.copy(alpha = if (theme.isDark) 0.6f else 0.12f),
                    spotColor = Color.Black.copy(alpha = if (theme.isDark) 0.8f else 0.18f)
                )
                .clip(RoundedCornerShape(28.dp))
                .background(modalBg)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(20.dp)
                .testTag("detail_modal_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TwoPointFiveIconBadge(
                            imageVector = icon,
                            contentDescription = title,
                            badgeSize = 40.dp,
                            iconSize = 20.dp,
                            primaryColor = Color(theme.accentColor),
                            isDark = theme.isDark,
                            isSelected = true
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(theme.textPrimary)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(closeBtnBg)
                    ) {
                        TwoPointFiveIcon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = closeBtnTint,
                            size = 18.dp,
                            depthOffset = 1.dp
                        )
                    }
                }

                // Modal Content
                content()
            }
        }
    }
}
