package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    backgroundColor: Color = Color(0xF2F3F4F6),
    borderColor: Color = Color(0x24111827),
    shadowElevation: Dp = 6.dp,
    tiltPitch: Float = 0f,
    tiltRoll: Float = 0f,
    isLiquidGlass: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 380f),
        label = "glass_card_scale"
    )

    // Motion-responsive light angles
    val lightAngleX = (tiltRoll * 4f).coerceIn(-40f, 40f)
    val lightAngleY = (tiltPitch * 4f).coerceIn(-40f, 40f)

    Surface(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = shadowElevation,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.45f),
                spotColor = Color.Black.copy(alpha = 0.65f)
            )
            .clip(shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            ),
        shape = shape,
        color = backgroundColor,
        border = BorderStroke(
            width = 1.2.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    borderColor.copy(alpha = (borderColor.alpha * 1.6f).coerceAtMost(1f)),
                    Color.White.copy(alpha = 0.45f),
                    borderColor.copy(alpha = (borderColor.alpha * 0.35f).coerceAtLeast(0.08f))
                ),
                start = Offset(0f - lightAngleX, 0f - lightAngleY),
                end = Offset(400f + lightAngleX, 400f + lightAngleY)
            )
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Liquid Glass Specular Shimmer Layer
            if (isLiquidGlass) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val glintCenterX = size.width * 0.25f + lightAngleX * 2.5f
                    val glintCenterY = size.height * 0.20f + lightAngleY * 2.5f

                    // Subtle top-left caustic reflection pool
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.14f),
                                Color(0x3300E5FF),
                                Color.Transparent
                            ),
                            center = Offset(glintCenterX, glintCenterY),
                            radius = size.width * 0.65f
                        ),
                        center = Offset(glintCenterX, glintCenterY),
                        radius = size.width * 0.65f
                    )
                }
            }

            Box(
                modifier = Modifier.padding(14.dp),
                content = content
            )
        }
    }
}

