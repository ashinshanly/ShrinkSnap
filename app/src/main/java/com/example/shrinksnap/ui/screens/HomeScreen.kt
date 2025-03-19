package com.example.shrinksnap.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shrinksnap.R
import com.example.shrinksnap.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCompress: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    // Enhanced animation states
    var showContent by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }
    var showFloatingElements by remember { mutableStateOf(false) }
    
    // Particle system for visual flair
    val particleCount = 25
    val particles = remember {
        List(particleCount) {
            HomeParticle(
                id = it,
                initialX = Random.nextFloat() * 100f,
                initialY = Random.nextFloat() * 100f,
                size = Random.nextFloat() * 12f + 4f,
                speed = Random.nextFloat() * 1f + 0.5f,
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
    
    // Track particle positions for animation
    val particlePositions = remember { mutableStateListOf<Float>().apply {
            repeat(particleCount * 2) { add(0f) }
        }
    }
    
    // Background parallax effect
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val parallaxFactor = 0.2f
    
    // Button animation
    val buttonInteractionSource = remember { MutableInteractionSource() }
    val isButtonHovered by buttonInteractionSource.collectIsHoveredAsState()
    val isButtonPressed by buttonInteractionSource.collectIsPressedAsState()
    
    val buttonScale = animateFloatAsState(
        targetValue = when {
            isButtonPressed -> 0.95f
            isButtonHovered -> 1.05f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "buttonAnimation"
    )
    
    // Hero animation for title
    val titleEnterAnimation = rememberInfiniteTransition(label = "titlePulse")
    val titleScale by titleEnterAnimation.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseOutQuart),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titlePulse"
    )
    
    // Wave ripple effect
    val waveAnimation = rememberInfiniteTransition(label = "waveRipple")
    val wavePhase by waveAnimation.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(5000),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveRipple"
    )
    
    // Gear rotation animation
    val gearAnimation = rememberInfiniteTransition(label = "gearRotation")
    val gearRotation by gearAnimation.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gearRotation"
    )
    
    // Coroutine scope for animations
    val coroutineScope = rememberCoroutineScope()
    
    // Animation setup
    LaunchedEffect(key1 = Unit) {
        // Animate particles
        launch {
            while(true) {
                particles.forEachIndexed { index, particle ->
                    // Apply simple physics to particle positions
                    val time = System.currentTimeMillis() / 1000f
                    val offset = sin(time * particle.speed + index) * 15f
                    
                    particlePositions[index * 2] = particle.initialX + offset
                    particlePositions[index * 2 + 1] = particle.initialY + 
                        cos(time * particle.speed + index) * 15f
                    
                    delay(16) // ~60fps
                }
            }
        }
    
    // Staggered animation sequence
        delay(100)
        showContent = true
        delay(400)
        showFloatingElements = true
        delay(600)
        showButton = true
    }
    
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
    ) {
        // Parallax background image
        Image(
            painter = painterResource(id = R.drawable.bg_photo1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = 0.2f
                    translationY = -scrollState.value * parallaxFactor
                }
        )
        
        // Ambient particles floating in the background
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
        
        // Ripple wave effect overlay
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.2f)
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 3
            
            // Draw multiple ripple circles
            for (i in 0 until 5) {
                val radius = 100f + i * 70f
                drawCircle(
                    color = Color.White.copy(alpha = 0.03f * (5 - i)),
                    radius = radius + sin(wavePhase + i * 0.5f) * 20f,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 2f)
                    )
            }
        }
        
        // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            
            // Settings button with float animation
            AnimatedVisibility(
                visible = showFloatingElements,
                enter = fadeIn() + slideInVertically { -it },
                modifier = Modifier.align(Alignment.End)
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp, end = 8.dp)
                        .graphicsLayer {
                            translationY = sin(System.currentTimeMillis() / 1000f) * 5f
                        }
                ) {
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF2C2C2C).copy(alpha = 0.8f),
                                        Color(0xFF1A1A1A).copy(alpha = 0.6f)
                                    )
                                )
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings),
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier
                                .rotate(gearRotation)
                                .size(28.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Main app logo
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(
                    animationSpec = tween(1000, easing = EaseOutCubic)
                ) + expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    expandFrom = Alignment.CenterVertically
                )
            ) {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .scale(titleScale)
                        .graphicsLayer {
                            shadowElevation = 20f
                            shape = CircleShape
                            clip = true
                        }
                ) {
                    // Logo background with glowing effect
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        AccentBlue.copy(alpha = 0.7f),
                                        AccentBlue.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    
                    // Logo icon center
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(30.dp)
                            .graphicsLayer {
                                shadowElevation = 8f
                                shape = CircleShape
                                clip = true
                            }
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF2E2E2E),
                                        Color(0xFF232323)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Floating camera icon
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(70.dp)
                            .graphicsLayer {
                                    rotationZ = sin(System.currentTimeMillis() / 2000f) * 5f
                                    translationY = sin(System.currentTimeMillis() / 1000f) * 5f
                                }
                        )
                    }
                    
                    // Orbiting elements
                    val orbitElements = listOf(
                        Icons.Default.Storage to Color(0xFFFFB74D),
                        Icons.Default.Memory to Color(0xFF4DB6AC),
                        Icons.Default.Save to Color(0xFF64B5F6)
                    )
                    
                    orbitElements.forEachIndexed { index, (icon, color) ->
                        val orbitAngle = (System.currentTimeMillis() / 3000f + index * (2 * PI / 3f)).toFloat()
                        val radius = 75f
                        val x = cos(orbitAngle) * radius
                        val y = sin(orbitAngle) * radius
                        
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .offset(x = (x + 72).dp, y = (y + 72).dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            color.copy(alpha = 0.9f),
                                            color.copy(alpha = 0.7f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                    Icon(
                                imageVector = icon,
                        contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // App title with glow effect
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(
                    animationSpec = tween(800, delayMillis = 300)
                ) + slideInVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    initialOffsetY = { it / 2 }
                )
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 38.sp
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                        modifier = Modifier
                            .graphicsLayer {
                            shadowElevation = 10f
                        }
                        .drawWithContent {
                            // Draw glow effect
                            drawContent()
                            drawContent()
                        }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Subtitle with parallax effect
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(
                    animationSpec = tween(800, delayMillis = 500)
                    ) + slideInVertically(
                        animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                    initialOffsetY = { it / 2 }
                    )
                        ) {
                            Text(
                                text = stringResource(R.string.welcome_message),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .graphicsLayer {
                            translationY = -scrollState.value * 0.1f
                                }
                            )
            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
            // Descriptive text with fade in
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(
                    animationSpec = tween(800, delayMillis = 700)
                ) + slideInVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    initialOffsetY = { it / 2 }
                )
            ) {
                            Text(
                                text = stringResource(R.string.compression_subtitle),
                                style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                    modifier = Modifier
                        .graphicsLayer {
                            translationY = -scrollState.value * 0.05f
                        }
                        .padding(horizontal = 24.dp)
                            )
                        }
                        
            Spacer(modifier = Modifier.height(60.dp))
                        
            // Feature highlights with images and animations
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(
                    animationSpec = tween(800, delayMillis = 900)
                ) + expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                            ) {
                    // Feature 1: Smart Analysis
                    FeatureCard(
                        title = "Smart Analysis",
                        description = "Analyze photos based on age to quickly identify compression candidates",
                        icon = Icons.Default.Search,
                        scrollOffset = scrollState.value * 0.3f
                    )
                    
                    // Feature 2: Space Saving
                    FeatureCard(
                        title = "Massive Space Savings",
                        description = "Reduce photo size while maintaining quality for optimal storage",
                        icon = Icons.Default.Storage,
                        scrollOffset = scrollState.value * 0.2f
                    )
                    
                    // Feature 3: Easy to Use
                    FeatureCard(
                        title = "Beautiful Experience",
                        description = "Enjoy an intuitive, animated interface that makes compression fun",
                        icon = Icons.Default.TouchApp,
                        scrollOffset = scrollState.value * 0.1f
                    )
                    }
                }
                
            Spacer(modifier = Modifier.height(60.dp))
                
            // Start button with pulsing animation
                AnimatedVisibility(
                    visible = showButton,
                    enter = fadeIn() + expandVertically(
                        expandFrom = Alignment.Bottom,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                // Button click animation
                                onNavigateToCompress()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .scale(buttonScale.value)
                            .graphicsLayer {
                                shadowElevation = if (isButtonHovered) 10f else 5f
                            },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(28.dp),
                        interactionSource = buttonInteractionSource,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                    // Gradient background for button with animated shimmer
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                        AccentBlue.copy(alpha = 0.8f),
                                        Color(0xFF5C6BC0),
                                        AccentBlue.copy(alpha = 0.8f)
                                        ),
                                    start = Offset(-(System.currentTimeMillis() / 20f) % 1000f, 0f),
                                    end = Offset((System.currentTimeMillis() / 20f) % 1000f + 1000f, 0f)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null
                            )
                            Text(
                                text = "Get Started",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    scrollOffset: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationY = -scrollOffset
                shadowElevation = 12f
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF232323)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            // Background image with parallax effect
            Image(
                painter = painterResource(
                    id = when (title) {
                        "Smart Analysis" -> R.drawable.feature_photo1
                        "Massive Space Savings" -> R.drawable.feature_photo2
                        else -> R.drawable.feature_photo3
                    }
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = -scrollOffset * 0.5f
                        alpha = 0.3f
                    }
            )
            
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF232323).copy(alpha = 0.4f),
                                Color(0xFF232323).copy(alpha = 0.9f)
                            )
                        )
                    )
            )
            
            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                // Icon with background glow
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    AccentBlue.copy(alpha = 0.8f),
                                    AccentBlue.copy(alpha = 0.5f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Description
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// Data class for particle animation
data class HomeParticle(
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