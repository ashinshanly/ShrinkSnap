package com.example.shrinksnap.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shrinksnap.ui.theme.AccentBlue

@Composable
fun StatsRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified,
    animateValue: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon with slight rotation animation
            val infiniteTransition = rememberInfiniteTransition(label = "iconAnimation")
            val iconRotation by infiniteTransition.animateFloat(
                initialValue = -3f,
                targetValue = 3f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = EaseInOutQuad),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "iconRotate"
            )
            
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AccentBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer {
                            rotationZ = iconRotation
                        }
                )
            }
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        
        // Animated value if requested
        if (animateValue && value.all { it.isDigit() || it == '.' }) {
            AnimatedCounter(
                targetValue = value.filter { it.isDigit() }.toIntOrNull() ?: 0,
                suffix = value.dropWhile { it.isDigit() },
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = valueColor,
                    fontWeight = FontWeight.Bold
                )
            )
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = valueColor,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun AnimatedCounter(
    targetValue: Int,
    suffix: String = "",
    style: TextStyle
) {
    val animatedCount = remember { Animatable(0f) }
    
    LaunchedEffect(targetValue) {
        animatedCount.animateTo(
            targetValue = targetValue.toFloat(),
            animationSpec = tween(
                durationMillis = 1200,
                easing = EaseOutQuart
            )
        )
    }
    
    Text(
        text = "${animatedCount.value.toInt()}$suffix",
        style = style
    )
}

// Custom easing curves for more fluid motion
private val EaseOutQuart = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)
private val EaseInOutQuad = CubicBezierEasing(0.45f, 0f, 0.55f, 1f) 