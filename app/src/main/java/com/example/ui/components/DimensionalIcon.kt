package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-End 2.5D Dimensional Icon system providing layered isometric depth,
 * top specular lighting, bottom depth extrusion, ambient glow, and tactile spring physics.
 */
@Composable
fun TwoPointFiveIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = Color.White,
    depthColor: Color = tint.copy(alpha = 0.4f),
    glowColor: Color = tint.copy(alpha = 0.35f),
    hasGlow: Boolean = true,
    depthOffset: Dp = 2.dp
) {
    Box(
        modifier = modifier.size(size + depthOffset + 4.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1. Ambient Glow aura
        if (hasGlow) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = glowColor,
                modifier = Modifier
                    .size(size + 2.dp)
                    .offset(y = depthOffset / 2)
            )
        }

        // 2. Extruded Depth Shadow (Bottom Layer)
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = depthColor,
            modifier = Modifier
                .size(size)
                .offset(x = 1.dp, y = depthOffset)
        )

        // 3. Middle Core Shadow for isometric bevel
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = tint.copy(alpha = 0.75f),
            modifier = Modifier
                .size(size)
                .offset(x = 0.5.dp, y = depthOffset / 2)
        )

        // 4. Foreground Sharp Specular Icon (Top Layer)
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(size)
        )
    }
}

/**
 * 2.5D Embossed Capsule / Shield Icon Badge with tactile bevel,
 * ambient drop shadow, top specular rim, and interactive spring response.
 */
@Composable
fun TwoPointFiveIconBadge(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    badgeSize: Dp = 44.dp,
    iconSize: Dp = 22.dp,
    primaryColor: Color = Color(0xFF00E5FF),
    isDark: Boolean = true,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    testTag: String = ""
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else if (isSelected) 1.06f else 1.0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f),
        label = "badge_scale"
    )

    val shape = RoundedCornerShape(14.dp)

    // Specular and gradient definitions for 2.5D volumetric look
    val plateBrush = if (isSelected) {
        Brush.linearGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.95f),
                primaryColor.copy(alpha = 0.65f),
                primaryColor.copy(alpha = 0.40f)
            ),
            start = Offset(0f, 0f),
            end = Offset(100f, 100f)
        )
    } else {
        if (isDark) {
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFF222836),
                    Color(0xFF141923),
                    Color(0xFF0D1017)
                ),
                start = Offset(0f, 0f),
                end = Offset(100f, 100f)
            )
        } else {
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFFFFFF),
                    Color(0xFFF3F4F6),
                    Color(0xFFE5E7EB)
                ),
                start = Offset(0f, 0f),
                end = Offset(100f, 100f)
            )
        }
    }

    val topRimColor = if (isSelected) {
        Color.White.copy(alpha = 0.8f)
    } else {
        if (isDark) Color.White.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.9f)
    }

    val bottomShadowColor = if (isSelected) {
        primaryColor.copy(alpha = 0.8f)
    } else {
        if (isDark) Color.Black.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.15f)
    }

    Box(
        modifier = modifier
            .scale(scale)
            .size(badgeSize)
            .shadow(
                elevation = if (isSelected) 12.dp else if (isDark) 8.dp else 4.dp,
                shape = shape,
                ambientColor = if (isSelected) primaryColor.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.35f),
                spotColor = if (isSelected) primaryColor.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.5f)
            )
            .clip(shape)
            .background(plateBrush)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        topRimColor,
                        topRimColor.copy(alpha = 0.2f),
                        bottomShadowColor
                    )
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
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        TwoPointFiveIcon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            size = iconSize,
            tint = if (isSelected) Color.White else if (isDark) primaryColor else primaryColor.copy(alpha = 0.9f),
            depthColor = if (isSelected) Color.Black.copy(alpha = 0.4f) else (if (isDark) Color.Black.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.25f)),
            hasGlow = isSelected,
            depthOffset = 2.dp
        )
    }
}

/**
 * Specialized 2.5D Volumetric Weather Icon rendering sun rays, moon crescents,
 * clouds, and storm bolts with depth layers, ambient glow, and micro-animations.
 */
@Composable
fun TwoPointFiveWeatherIcon(
    weatherCode: Int?,
    isDay: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "weather_2_5d_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val sunRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(24000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sun_rotation"
    )

    val code = weatherCode ?: 0

    Box(
        modifier = modifier
            .size(size)
            .scale(pulseScale),
        contentAlignment = Alignment.Center
    ) {
        when {
            // Sunny / Clear Day (2.5D Sun with Radiant volumetric corona)
            code in listOf(0, 1) && isDay -> {
                Canvas(modifier = Modifier.size(size).rotate(sunRotation)) {
                    val center = Offset(this.size.width / 2, this.size.height / 2)
                    val r = this.size.width * 0.30f

                    // 1. Corona Rays (2.5D Sun Rays)
                    val rayCount = 8
                    for (i in 0 until rayCount) {
                        val angle = (i * (360f / rayCount)) * (Math.PI / 180f).toFloat()
                        val start = Offset(
                            center.x + (r + 4f) * cos(angle),
                            center.y + (r + 4f) * sin(angle)
                        )
                        val end = Offset(
                            center.x + (r + 14f) * cos(angle),
                            center.y + (r + 14f) * sin(angle)
                        )
                        drawLine(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFFFD54F), Color(0xFFFF8F00))
                            ),
                            start = start,
                            end = end,
                            strokeWidth = 4f,
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    }

                    // 2. 2.5D Depth Shadow for Sun sphere
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFE65100), Color(0x88BF360C)),
                            center = Offset(center.x + 2f, center.y + 4f),
                            radius = r
                        ),
                        radius = r,
                        center = Offset(center.x + 2f, center.y + 4f)
                    )

                    // 3. Volumetric Specular Sun Core
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFFFF9C4), Color(0xFFFFC107), Color(0xFFFF8F00)),
                            center = Offset(center.x - 4f, center.y - 4f),
                            radius = r
                        ),
                        radius = r,
                        center = center
                    )

                    // 4. Specular Glare Dot
                    drawCircle(
                        color = Color.White.copy(alpha = 0.85f),
                        radius = r * 0.22f,
                        center = Offset(center.x - r * 0.35f, center.y - r * 0.35f)
                    )
                }
            }

            // Clear Night (2.5D Glowing Crescent Moon)
            code in listOf(0, 1) && !isDay -> {
                Canvas(modifier = Modifier.size(size)) {
                    val center = Offset(this.size.width / 2, this.size.height / 2)
                    val r = this.size.width * 0.38f

                    // Ambient Moon Aura
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x6680DEEA), Color.Transparent),
                            center = center,
                            radius = r * 1.5f
                        ),
                        radius = r * 1.5f,
                        center = center
                    )

                    // 2.5D Crescent Moon Path
                    val moonPath = Path().apply {
                        moveTo(center.x + r * 0.2f, center.y - r)
                        cubicTo(
                            center.x - r * 1.1f, center.y - r * 0.8f,
                            center.x - r * 1.1f, center.y + r * 0.8f,
                            center.x + r * 0.2f, center.y + r
                        )
                        cubicTo(
                            center.x - r * 0.4f, center.y + r * 0.6f,
                            center.x - r * 0.4f, center.y - r * 0.6f,
                            center.x + r * 0.2f, center.y - r
                        )
                        close()
                    }

                    // Shadow layer
                    drawPath(
                        path = moonPath,
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF0D47A1), Color(0xFF1A237E)),
                            start = Offset(center.x, center.y),
                            end = Offset(center.x + 4f, center.y + 6f)
                        )
                    )

                    // Specular 2.5D Moon Body
                    drawPath(
                        path = moonPath,
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFE0F7FA), Color(0xFF80DEEA), Color(0xFF00ACC1)),
                            start = Offset(center.x - r, center.y - r),
                            end = Offset(center.x + r, center.y + r)
                        )
                    )
                }
            }

            // Rainy / Storm / Cloudy (2.5D Cloud with Volumetric Drops & Lightning)
            else -> {
                Canvas(modifier = Modifier.size(size)) {
                    val w = this.size.width
                    val h = this.size.height

                    // 1. Volumetric Cloud Shadow
                    val shadowOffset = 4f
                    drawCircle(
                        color = Color(0x66263238),
                        radius = w * 0.22f,
                        center = Offset(w * 0.35f, h * 0.45f + shadowOffset)
                    )
                    drawCircle(
                        color = Color(0x66263238),
                        radius = w * 0.28f,
                        center = Offset(w * 0.55f, h * 0.38f + shadowOffset)
                    )

                    // 2. 2.5D Cloud Body
                    val cloudBrush = if (code in listOf(95, 96, 99)) {
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF90A4AE), Color(0xFF37474F), Color(0xFF263238))
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFECEFF1), Color(0xFFCFD8DC), Color(0xFF90A4AE))
                        )
                    }

                    drawCircle(
                        brush = cloudBrush,
                        radius = w * 0.22f,
                        center = Offset(w * 0.35f, h * 0.45f)
                    )
                    drawCircle(
                        brush = cloudBrush,
                        radius = w * 0.28f,
                        center = Offset(w * 0.55f, h * 0.38f)
                    )
                    drawCircle(
                        brush = cloudBrush,
                        radius = w * 0.18f,
                        center = Offset(w * 0.72f, h * 0.50f)
                    )

                    // 3. Rain Drops or Lightning Bolt
                    if (code in listOf(95, 96, 99)) {
                        // 2.5D Golden Lightning Bolt
                        val boltPath = Path().apply {
                            moveTo(w * 0.52f, h * 0.50f)
                            lineTo(w * 0.44f, h * 0.70f)
                            lineTo(w * 0.54f, h * 0.70f)
                            lineTo(w * 0.42f, h * 0.95f)
                            lineTo(w * 0.58f, h * 0.64f)
                            lineTo(w * 0.48f, h * 0.64f)
                            close()
                        }
                        drawPath(
                            path = boltPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFFFFF59D), Color(0xFFFFD600), Color(0xFFFF6D00))
                            )
                        )
                    } else if (code in listOf(51, 53, 55, 61, 63, 65, 80, 81, 82)) {
                        // 2.5D Rain Drops
                        val dropColors = listOf(Color(0xFF4FC3F7), Color(0xFF0288D1))
                        drawLine(
                            brush = Brush.verticalGradient(dropColors),
                            start = Offset(w * 0.36f, h * 0.72f),
                            end = Offset(w * 0.30f, h * 0.90f),
                            strokeWidth = 3.5f,
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                        drawLine(
                            brush = Brush.verticalGradient(dropColors),
                            start = Offset(w * 0.55f, h * 0.72f),
                            end = Offset(w * 0.49f, h * 0.92f),
                            strokeWidth = 3.5f,
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                        drawLine(
                            brush = Brush.verticalGradient(dropColors),
                            start = Offset(w * 0.74f, h * 0.72f),
                            end = Offset(w * 0.68f, h * 0.90f),
                            strokeWidth = 3.5f,
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    }
                }
            }
        }
    }
}
