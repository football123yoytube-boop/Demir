package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.DashboardThemeConfig
import com.example.data.UserDashboardPreferences
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun AmbientBackground(
    theme: DashboardThemeConfig,
    preferences: UserDashboardPreferences,
    isNightHour: Boolean,
    tiltPitch: Float,
    tiltRoll: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_anim")
    
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_shift"
    )

    val starAlphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "stars_twinkle"
    )

    val bgColors = theme.backgroundColors.map { Color(it) }

    // Parallax offset from gyro
    val parallaxX = (tiltRoll * 1.2f).coerceIn(-30f, 30f)
    val parallaxY = (tiltPitch * 1.2f).coerceIn(-30f, 30f)

    Box(modifier = modifier.fillMaxSize()) {
        // Base Background Layer
        if (preferences.backgroundType == "USER_IMAGE" && !preferences.userImageUri.isNullOrBlank()) {
            val context = LocalContext.current
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(preferences.userImageUri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Arka Plan",
                modifier = Modifier
                    .fillMaxSize()
                    .blur(preferences.backgroundBlur.dp),
                contentScale = ContentScale.Crop,
                alpha = preferences.backgroundOpacity
            )
            if (preferences.backgroundDarkeningScrim > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = preferences.backgroundDarkeningScrim))
                )
            }
        } else if (theme.id == "pure_white") {
            // Pure White - Clean, Crisp Apple-style background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFFFFF))
            )
        } else if (theme.id == "liquid_glass") {
            // Liquid Glass - Iridescent fluid refraction blobs & caustic glass lighting
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF030712))
            )
            // Liquid Fluid Blobs
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx1 = size.width * 0.35f + (sin(animProgress * 3.14f * 2f) * 150f) + parallaxX * 6f
                val cy1 = size.height * 0.40f + (sin(animProgress * 3.14f) * 80f) + parallaxY * 6f
                val cx2 = size.width * 0.75f - (sin(animProgress * 3.14f * 2f) * 120f) - parallaxX * 4f
                val cy2 = size.height * 0.65f - (sin(animProgress * 3.14f) * 100f) - parallaxY * 4f
                val cx3 = size.width * 0.50f + (sin(animProgress * 3.14f * 1.5f) * 100f)
                val cy3 = size.height * 0.20f + (sin(animProgress * 3.14f * 2f) * 60f)

                // Cyan / Turquoise Fluid Pool
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x9900E5FF), Color(0x4400B0FF), Color.Transparent),
                        center = Offset(cx1, cy1),
                        radius = size.width * 0.45f
                    ),
                    radius = size.width * 0.45f,
                    center = Offset(cx1, cy1)
                )

                // Iridescent Deep Azure / Violet Fluid Pool
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x887C4DFF), Color(0x332979FF), Color.Transparent),
                        center = Offset(cx2, cy2),
                        radius = size.width * 0.40f
                    ),
                    radius = size.width * 0.40f,
                    center = Offset(cx2, cy2)
                )

                // High-Light Caustic Specular Pool
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x6600E5FF), Color.Transparent),
                        center = Offset(cx3, cy3),
                        radius = size.width * 0.30f
                    ),
                    radius = size.width * 0.30f,
                    center = Offset(cx3, cy3)
                )
            }
        } else if (theme.id == "premium_design") {
            // Premium Design - Luxury Obsidian Black with Champagne Gold Light Caustics
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF090A0F))
            )
            Canvas(modifier = Modifier.fillMaxSize()) {
                val goldX = size.width * 0.60f + (sin(animProgress * 3.14f) * 180f) + parallaxX * 5f
                val goldY = size.height * 0.35f + (sin(animProgress * 3.14f * 1.5f) * 90f) + parallaxY * 5f

                // Warm Champagne Gold Ambient Radiance
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x55D4AF37),
                            Color(0x228B6F20),
                            Color(0x0D1E222D),
                            Color.Transparent
                        ),
                        center = Offset(goldX, goldY),
                        radius = size.width * 0.55f
                    ),
                    radius = size.width * 0.55f,
                    center = Offset(goldX, goldY)
                )

                // Obsidian Titanium Counter-Glow
                val titanX = size.width * 0.25f - parallaxX * 3f
                val titanY = size.height * 0.70f - parallaxY * 3f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x2E3B4252), Color.Transparent),
                        center = Offset(titanX, titanY),
                        radius = size.width * 0.40f
                    ),
                    radius = size.width * 0.40f,
                    center = Offset(titanX, titanY)
                )
            }
        } else if (theme.id == "texture_design") {
            // Texture Design - Deep Slate Carbon Canvas with Tactile Micro-Matrix Pattern
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0D1117))
            )
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Tactile Dot Matrix Grid
                val spacing = 28f
                val cols = (size.width / spacing).toInt() + 1
                val rows = (size.height / spacing).toInt() + 1

                for (r in 0..rows) {
                    for (c in 0..cols) {
                        val px = c * spacing + (parallaxX * 0.3f)
                        val py = r * spacing + (parallaxY * 0.3f)
                        
                        // Subtle harmonic wave through the matrix
                        val wave = sin((c * 0.2f) + (r * 0.2f) + (animProgress * 3.14f * 2f))
                        val dotAlpha = ((wave + 1f) / 2f * 0.12f + 0.05f).coerceIn(0.02f, 0.25f)
                        
                        drawCircle(
                            color = Color(0xFF00FFC2).copy(alpha = dotAlpha),
                            radius = 1.4f,
                            center = Offset(px, py)
                        )
                    }
                }

                // Ambient Electric Mint / Cyan Glow
                val glowX = size.width * 0.5f + (sin(animProgress * 3.14f) * 120f)
                val glowY = size.height * 0.5f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x2B00FFC2), Color.Transparent),
                        center = Offset(glowX, glowY),
                        radius = size.width * 0.45f
                    ),
                    radius = size.width * 0.45f,
                    center = Offset(glowX, glowY)
                )
            }
        } else if (!theme.isDark) {
            // Light Minimal Themes (Minimal Gray, Sand, Custom Light)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = bgColors
                        )
                    )
            )
        } else {
            // Dark Themes Ambient Mesh Gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = bgColors,
                            center = Offset(
                                x = 800f + (animProgress * 300f) + parallaxX * 4f,
                                y = 400f + (sin(animProgress * Math.PI.toFloat()) * 200f) + parallaxY * 4f
                            ),
                            radius = 1800f
                        )
                    )
            )
        }

        // Night stars subtle canvas (ONLY in dark mode)
        if (theme.isDark && preferences.animationIntensity != "OFF") {
            val stars = remember {
                List(35) {
                    Triple(
                        Random.nextFloat(), // x ratio
                        Random.nextFloat(), // y ratio
                        Random.nextFloat() * 2f + 0.8f // radius
                    )
                }
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                stars.forEachIndexed { idx, (rx, ry, radius) ->
                    val dynamicAlpha = ((sin(animProgress * 5f + idx) + 1f) / 2f * 0.5f + 0.2f) * starAlphaAnim
                    drawCircle(
                        color = Color.White.copy(alpha = dynamicAlpha),
                        radius = radius,
                        center = Offset(
                            x = size.width * rx + parallaxX * (radius / 2f),
                            y = size.height * ry + parallaxY * (radius / 2f)
                        )
                    )
                }
            }
        }

        // Warm night tone overlay (if enabled during night hours)
        if (preferences.autoWarmNightTint && isNightHour) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFF9800).copy(alpha = 0.05f))
            )
        }
    }
}
