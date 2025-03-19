package com.example.shrinksnap.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shrinksnap.R
import com.example.shrinksnap.ui.theme.*
import com.example.shrinksnap.ui.components.StatsRow
import com.example.shrinksnap.ui.components.AnimatedCounter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun ResultsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }
    var showSuccessAnimation by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }
    var showShareButton by remember { mutableStateOf(false) }
    
    // Scroll effect for parallax
    val scrollState = rememberScrollState()
    val parallaxOffset by remember {
        derivedStateOf { scrollState.value * 0.15f }
    }
    
    // Success animation states
    val successScale = remember { Animatable(0f) }
    val successRotation = remember { Animatable(0f) }
    val successAlpha = remember { Animatable(0f) }
    
    // Particle system for celebration effect
    val particleCount = 30
    val particles = remember {
        List(particleCount) {
            ResultsParticle(
                id = it,
                initialX = Random.nextFloat() * 100f,
                initialY = Random.nextFloat() * 100f,
                size = Random.nextFloat() * 12f + 4f,
                speed = Random.nextFloat() * 2f + 1f,
                color = when (it % 5) {
                    0 -> Color(0x3364B5F6) // Light blue
                    1 -> Color(0x334DB6AC) // Light teal
                    2 -> Color(0x33FFB74D) // Light amber
                    3 -> Color(0x33E57373) // Light red
                    else -> Color(0x33FFFFFF) // White
                }
            )
        }
    }
    
    // Animated particle positions
    val particlePositions = remember { mutableStateListOf<Float>() }.apply {
        if (isEmpty()) {
            repeat(particleCount * 2) { add(0f) }
        }
    }
    
    // Success wave animation
    val wavePhase = remember { Animatable(0f) }
    
    // Launch animations sequentially
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(key1 = Unit) {
        // Start particle animations
        repeat(particleCount) { index ->
            launch {
                while (true) {
                    val xOffset = sin(System.currentTimeMillis() * particles[index].speed / 1000 + index) * 50f
                    val yOffset = cos(System.currentTimeMillis() * particles[index].speed / 800 + index) * 30f
                    
                    particlePositions[index * 2] = particles[index].initialX + xOffset
                    particlePositions[index * 2 + 1] = particles[index].initialY + yOffset
                    
                    delay(16) // ~60fps
                }
            }
        }
        
        // Staggered animation sequence
        delay(100)
        showContent = true
        delay(400)
        showSuccessAnimation = true
        
        // Success animation sequence
        successScale.animateTo(
            targetValue = 1.2f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        successScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
        
        successRotation.animateTo(
            targetValue = 360f,
            animationSpec = tween(1000, easing = EaseOutQuart)
        )
        
        successAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(800, easing = EaseOutCubic)
        )
        
        delay(800)
        showDetails = true
        delay(400)
        showShareButton = true
        
        // Start wave animation
        while (true) {
            wavePhase.animateTo(
                targetValue = 2 * PI.toFloat(),
                animationSpec = tween(3000, easing = LinearEasing)
            )
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.compression_results),
                        modifier = Modifier.graphicsLayer {
                            translationY = -parallaxOffset * 0.2f
                        }
                    ) 
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.graphicsLayer {
                            rotationZ = if (showContent) 0f else -180f
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background with animated gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF121212),
                                Color(0xFF1A1A1A),
                                Color(0xFF232323)
                            )
                        )
                    )
            )
            
            // Ambient particles
            particles.forEachIndexed { index, particle ->
                Box(
                    modifier = Modifier
                        .size(particle.size.dp)
                        .clip(CircleShape)
                        .background(particle.color)
                        .alpha(0.7f)
                        .offset(
                            x = particlePositions[index * 2].dp,
                            y = particlePositions[index * 2 + 1].dp
                        )
                )
            }
            
            // Success wave effect
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.2f)
            ) {
                val centerX = size.width / 2
                val centerY = size.height / 3
                
                for (i in 0 until 5) {
                    val radius = 100f + i * 70f
                    drawCircle(
                        color = Color.White.copy(alpha = 0.03f * (5 - i)),
                        radius = radius + sin(wavePhase.value + i * 0.5f) * 20f,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = 2f)
                    )
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Success animation
                AnimatedVisibility(
                    visible = showSuccessAnimation,
                    enter = fadeIn() + expandVertically()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Success checkmark with animations
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .scale(successScale.value)
                                .rotate(successRotation.value)
                                .alpha(successAlpha.value)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF4CAF50).copy(alpha = 0.8f),
                                            Color(0xFF388E3C).copy(alpha = 0.6f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                }
                
                // Results details
                AnimatedVisibility(
                    visible = showDetails,
                    enter = fadeIn() + slideInVertically()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                shadowElevation = 8f
                                translationY = -parallaxOffset * 0.2f
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = DarkSurfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Stats with animated counters
                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                StatsRow(
                                    icon = Icons.Default.PhotoCamera,
                                    label = "Photos compressed:",
                                    value = "127",
                                    valueColor = AccentBlue,
                                    animateValue = true
                                )
                                
                                StatsRow(
                                    icon = Icons.Default.Storage,
                                    label = "Space saved:",
                                    value = "1.25 GB",
                                    valueColor = AccentBlue,
                                    animateValue = true
                                )
                                
                                StatsRow(
                                    icon = Icons.Default.Timer,
                                    label = "Time taken:",
                                    value = "45s",
                                    valueColor = AccentBlue,
                                    animateValue = true
                                )
                            }
                            
                            Divider(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .fillMaxWidth(0.7f)
                                    .align(Alignment.CenterHorizontally),
                                color = AccentBlue.copy(alpha = 0.5f),
                                thickness = 1.dp
                            )
                            
                            // Quality preservation info
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(AccentBlue.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = AccentBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                
                                Text(
                                    text = "Quality preserved: 95%",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Share button with enhanced animation
                AnimatedVisibility(
                    visible = showShareButton,
                    enter = fadeIn() + slideInVertically { it }
                ) {
                    Button(
                        onClick = { /* Share results */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .graphicsLayer {
                                shadowElevation = 8f
                                translationY = -parallaxOffset * 0.1f
                            },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(28.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            AccentBlue.copy(alpha = 0.8f),
                                            Color(0xFF5C6BC0),
                                            AccentBlue.copy(alpha = 0.8f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null
                                )
                                Text(
                                    text = "Share Results",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
                
                // Done button with enhanced animation
                AnimatedVisibility(
                    visible = showShareButton,
                    enter = fadeIn() + slideInVertically { it }
                ) {
                    Button(
                        onClick = onNavigateToHome,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .graphicsLayer {
                                shadowElevation = 8f
                                translationY = -parallaxOffset * 0.1f
                            },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(28.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF4CAF50).copy(alpha = 0.8f),
                                            Color(0xFF388E3C),
                                            Color(0xFF4CAF50).copy(alpha = 0.8f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = null
                                )
                                Text(
                                    text = "Done",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// Data class for particle animation
data class ResultsParticle(
    val id: Int,
    val initialX: Float,
    val initialY: Float,
    val size: Float,
    val speed: Float,
    val color: Color
)

// Custom easing curves for more fluid motion
private val EaseOutQuart = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)
private val EaseOutCubic = CubicBezierEasing(0.33f, 1f, 0.68f, 1f)
private val EaseInOutQuart = CubicBezierEasing(0.76f, 0f, 0.24f, 1f)
private val EaseInOutQuad = CubicBezierEasing(0.45f, 0f, 0.55f, 1f) 