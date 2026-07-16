package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OrangePrimary

@Composable
fun Valid8LogoIcon(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 64.dp,
    animate: Boolean = true
) {
    // Elegant infinite animations for the logo icon ONLY
    val infiniteTransition = rememberInfiniteTransition(label = "LogoIconAnim")
    
    val pulseScale by if (animate) {
        infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse"
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    val rotation by if (animate) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(10000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    // Glowing container for the logo icon
    Box(
        modifier = modifier
            .size(sizeDp)
            .graphicsLayer {
                scaleX = pulseScale
                scaleY = pulseScale
            },
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing ring aura
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF6366F1).copy(alpha = 0.25f),
                            Color(0xFF10B981).copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // The central gorgeous modern hexagonal shield badge
        Canvas(
            modifier = Modifier
                .size(sizeDp * 0.75f)
                .graphicsLayer {
                    rotationZ = rotation
                }
        ) {
            val width = size.width
            val height = size.height

            // 1. Draw a futuristic Hexagonal outer ring
            val hexPath = Path().apply {
                moveTo(width * 0.5f, 0f)
                lineTo(width * 0.93f, height * 0.25f)
                lineTo(width * 0.93f, height * 0.75f)
                lineTo(width * 0.5f, height)
                lineTo(width * 0.07f, height * 0.75f)
                lineTo(width * 0.07f, height * 0.25f)
                close()
            }

            // Beautiful gradient brush for the hexagonal outer shell
            val hexGradient = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF6366F1), // Premium Indigo
                    Color(0xFF10B981)  // Vibrant Neon Mint
                ),
                start = Offset(0f, 0f),
                end = Offset(width, height)
            )

            // Draw thick gradient outer shell hexagon
            drawPath(
                path = hexPath,
                brush = hexGradient,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        // Inner solid shield with neon checkmark (not rotating, only pulsing, to remain upright!)
        Canvas(
            modifier = Modifier.size(sizeDp * 0.5f)
        ) {
            val w = size.width
            val h = size.height

            // Inner checkmark mark path
            val checkPath = Path().apply {
                moveTo(w * 0.28f, h * 0.52f)
                lineTo(w * 0.45f, h * 0.68f)
                lineTo(w * 0.75f, h * 0.35f)
            }

            // Neon check glow
            drawPath(
                path = checkPath,
                color = Color(0xFF10B981).copy(alpha = 0.3f),
                style = Stroke(
                    width = 8.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Sharp main neon check
            drawPath(
                path = checkPath,
                color = Color(0xFF10B981),
                style = Stroke(
                    width = 4.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

@Composable
fun Valid8Logo(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Beautiful new different logo icon that animates only the icon
        Valid8LogoIcon(
            sizeDp = 60.dp,
            animate = true
        )
        
        Spacer(modifier = Modifier.width(14.dp))
        
        // Brand name text (remains static and crisp, fulfilling the requirement)
        Column {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "Valid",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = "8",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF10B981), // Vibrant Neon Mint
                    letterSpacing = (-1).sp
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "focus on quality",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981), // Vibrant Neon Mint
                letterSpacing = 1.sp
            )
        }
    }
}
