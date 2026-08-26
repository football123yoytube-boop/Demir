package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DashboardThemeConfig
import com.example.data.UserDashboardPreferences

@Composable
fun WidgetCatalogModal(
    theme: DashboardThemeConfig,
    preferences: UserDashboardPreferences,
    onUpdateActiveWidgets: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    val activeIds = preferences.activeHomeScreenWidgetIds
    var selectedCategory by remember { mutableStateOf("Tümü") }
    val categories = listOf("Tümü", "Verimlilik", "Sağlık", "Zaman", "Cihaz", "Araçlar", "Sensörler", "Astronomi", "Eğlence", "Finans")

    val filteredWidgets = remember(selectedCategory) {
        if (selectedCategory == "Tümü") {
            InteractiveWidgetRegistry.allWidgets
        } else {
            InteractiveWidgetRegistry.allWidgets.filter { it.category == selectedCategory }
        }
    }

    DashboardDetailModal(
        title = "İnteraktif Widget Galerisi (25 Widget)",
        icon = Icons.Default.Widgets,
        theme = theme,
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            // Info Header Banner: Explain constraints (Max 3 side-by-side & Weather widget locked)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(theme.accentColor).copy(alpha = 0.15f))
                    .border(1.dp, Color(theme.accentColor).copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TwoPointFiveIcon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Kilitli",
                        tint = Color(theme.accentColor),
                        size = 18.dp,
                        depthOffset = 1.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Hava Durumu Kartı Sabittir (Kaldırılamaz)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(theme.textPrimary)
                        )
                        Text(
                            text = "Ana ekranda yan yana en fazla 3 widget yer alır. Buradan dilediğiniz widget'ları aktif edebilirsiniz.",
                            fontSize = 11.sp,
                            color = Color(theme.textSecondary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.take(5).forEach { cat ->
                    val isCatSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isCatSelected) Color(theme.accentColor) else Color(theme.cardBackground))
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cat,
                            fontSize = 11.sp,
                            fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCatSelected) Color.Black else Color(theme.textSecondary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Widget List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Permanent Weather Widget Card Entry
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(theme.cardBackground).copy(alpha = 0.6f))
                            .border(1.dp, Color(theme.cardBorder).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            TwoPointFiveIconBadge(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = "Hava Durumu",
                                badgeSize = 38.dp,
                                iconSize = 20.dp,
                                primaryColor = Color(0xFFFFB300),
                                isDark = theme.isDark
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Canlı Hava Durumu (Kalıcı)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(theme.textPrimary)
                                )
                                Text(
                                    text = "Sıcaklık, konum & sensör özetleri (Sabit)",
                                    fontSize = 11.sp,
                                    color = Color(theme.textSecondary)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(theme.textSecondary).copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Sabit", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(theme.textSecondary))
                        }
                    }
                }

                // 25 Interactive Widgets
                items(filteredWidgets) { widget ->
                    val isActive = activeIds.contains(widget.id)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isActive) Color(theme.accentColor).copy(alpha = 0.12f)
                                else Color(theme.cardBackground).copy(alpha = 0.5f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isActive) Color(theme.accentColor).copy(alpha = 0.45f)
                                else Color(theme.cardBorder).copy(alpha = 0.3f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                val updated = if (isActive) {
                                    activeIds.filter { it != widget.id }
                                } else {
                                    activeIds + widget.id
                                }
                                onUpdateActiveWidgets(updated)
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            TwoPointFiveIconBadge(
                                imageVector = widget.icon,
                                contentDescription = widget.title,
                                badgeSize = 38.dp,
                                iconSize = 20.dp,
                                primaryColor = Color(theme.accentColor),
                                isDark = theme.isDark,
                                isSelected = isActive
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = widget.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(theme.textPrimary)
                                )
                                Text(
                                    text = "${widget.subtitle} • ${widget.category}",
                                    fontSize = 11.sp,
                                    color = Color(theme.textSecondary),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isActive) Color(theme.accentColor) else Color(theme.textSecondary).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            TwoPointFiveIcon(
                                imageVector = if (isActive) Icons.Default.Check else Icons.Default.Add,
                                contentDescription = if (isActive) "Aktif" else "Ekle",
                                tint = if (isActive) Color.Black else Color(theme.textSecondary),
                                size = 16.dp,
                                depthOffset = 1.dp
                            )
                        }
                    }
                }
            }
        }
    }
}
