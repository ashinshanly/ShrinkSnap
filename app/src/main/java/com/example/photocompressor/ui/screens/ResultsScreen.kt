package com.example.photocompressor.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.example.photocompressor.R
import com.example.photocompressor.ui.theme.AccentBlue
import com.example.photocompressor.ui.theme.DarkSurfaceVariant
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun ResultsScreen(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit
) {
    var isCompressing by remember { mutableStateOf(true) }
    var progress by remember { mutableStateOf(0f) }
    var compressedPhotos by remember { mutableStateOf(0) }
    var spaceSaved by remember { mutableStateOf("0 MB") }
    var showSuccessAnimation by remember { mutableStateOf(false) }
    
    // Success check mark animation with spring physics for a more organic feel
    val checkScale = animateFloatAsState(
        targetValue = if (showSuccessAnimation) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "checkScale"
    )
    
    // Pulsing animation for the progress circle with a smoother curve
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulseSize by pulseAnim.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseSize"
    )
    
    // Rotating halo effect
    val haloRotation by pulseAnim.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "haloRotation"
    )
    
    // Completion burst animation
    val burstScale = animateFloatAsState(
        targetValue = if (showSuccessAnimation) 1f else 0f,
        animationSpec = tween(800, easing = EaseOutBounce),
        label = "burstScale"
    )
    
    // Button interaction
    val buttonInteractionSource = remember { MutableInteractionSource() }
    val isButtonHovered by buttonInteractionSource.collectIsHoveredAsState()
    val isButtonPressed by buttonInteractionSource.collectIsPressedAsState()
    
    val buttonScale = animateFloatAsState(
        targetValue = when {
            isButtonPressed -> 0.95f
            isButtonHovered -> 1.05f
            showSuccessAnimation -> 1f
            else -> 0f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "buttonScale"
    )
    
    // Simulate compression progress with a smoother curve
    LaunchedEffect(Unit) {
        // In a real app, this would be replaced with actual compression logic
        // Using a more natural progression curve for progress
        val totalDuration = 5000 // 5 seconds for full simulation
        val startTime = System.currentTimeMillis()
        
        while (isCompressing) {
            val elapsedTime = System.currentTimeMillis() - startTime
            val rawProgress = (elapsedTime.toFloat() / totalDuration).coerceIn(0f, 1f)
            
            // Apply easing for more natural progression (slower start, faster middle, slower end)
            progress = EaseInOutCubic.transform(rawProgress)
            compressedPhotos = (127 * progress).toInt()
            
            // Calculate simulated space saved
            val savedMB = 1280 * progress // 1.25 GB in MB
            spaceSaved = if (savedMB >= 1024) {
                String.format("%.2f GB", savedMB / 1024)
            } else {
                String.format("%.0f MB", savedMB)
            }
            
            if (progress >= 1f) {
                isCompressing = false
            }
            
            delay(16) // Approximately 60fps update rate
        }
        
        // Add a small delay before showing success animation for a cleaner transition
        delay(300)
        showSuccessAnimation = true
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    AnimatedContent(
                        targetState = isCompressing,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) with 
                            fadeOut(animationSpec = tween(300))
                        },
                        label = "titleAnimation"
                    ) { compressing ->
                        Text(
                            text = if (compressing) 
                                stringResource(R.string.compression_in_progress) 
                            else 
                                stringResource(R.string.compression_complete)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.cancel)
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
            // Background radial gradient
            if (!isCompressing && showSuccessAnimation) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    AccentBlue.copy(alpha = 0.03f),
                                    Color.Transparent
                                ),
                                center = Offset(0.5f, 0.3f),
                                radius = 500f
                            )
                        )
                        .animateContentSize(
                            animationSpec = tween(1000)
                        )
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                AnimatedVisibility(
                    visible = isCompressing,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    // Compression progress
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .scale(pulseSize),
                            contentAlignment = Alignment.Center
                        ) {
                            // Outer glow
                            Box(
                                modifier = Modifier
                                    .size(180.dp)
                                    .clip(CircleShape)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                AccentBlue.copy(alpha = 0.1f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                            
                            // Rotating halo
                            Box(
                                modifier = Modifier
                                    .size(180.dp)
                                    .graphicsLayer {
                                        rotationZ = haloRotation
                                    }
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val center = Offset(size.width / 2, size.height / 2)
                                    val radius = size.minDimension / 2
                                    
                                    // Draw a dotted circle
                                    val pathEffect = PathEffect.dashPathEffect(
                                        floatArrayOf(10f, 15f), 0f
                                    )
                                    
                                    drawCircle(
                                        color = AccentBlue.copy(alpha = 0.3f),
                                        radius = radius,
                                        center = center,
                                        style = Stroke(
                                            width = 2.dp.toPx(),
                                            pathEffect = pathEffect
                                        )
                                    )
                                }
                            }
                            
                            // Custom animated circular progress with higher quality rendering
                            CustomCircularProgressIndicator(
                                progress = progress,
                                modifier = Modifier.size(160.dp),
                                strokeWidth = 12.dp,
                                primaryColor = AccentBlue,
                                secondaryColor = Color.Gray.copy(alpha = 0.15f),
                                animationDuration = 800
                            )
                            
                            // Percentage text in the center with animated value
                            AnimatedContent(
                                targetState = (progress * 100).toInt(),
                                transitionSpec = {
                                    slideInVertically { height -> height } + fadeIn(
                                        animationSpec = tween(150, easing = LinearOutSlowInEasing)
                                    ) with slideOutVertically { height -> -height } + fadeOut(
                                        animationSpec = tween(150, easing = LinearOutSlowInEasing)
                                    )
                                },
                                label = "percentAnimation"
                            ) { targetPercent ->
                                Text(
                                    text = "$targetPercent%",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                        
                        // Photos count with animated value
                        AnimatedContent(
                            targetState = compressedPhotos,
                            transitionSpec = {
                                slideInVertically { height -> height } + fadeIn() with
                                slideOutVertically { height -> -height } + fadeOut()
                            },
                            label = "photosCountAnimation"
                        ) { targetCount ->
                            Text(
                                text = "Compressing $targetCount/127 photos...",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        // Space saved with animated value
                        AnimatedContent(
                            targetState = spaceSaved,
                            transitionSpec = {
                                slideInVertically { height -> height } + fadeIn() with
                                slideOutVertically { height -> -height } + fadeOut()
                            },
                            label = "spaceSavedAnimation"
                        ) { targetSaved ->
                            Text(
                                text = stringResource(R.string.space_saved, targetSaved),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                
                AnimatedVisibility(
                    visible = !isCompressing,
                    enter = fadeIn(animationSpec = tween(500)) + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    // Compression complete content
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Success animation with burst effect
                        Box(
                            modifier = Modifier
                                .size(180.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Background glow
                            Box(
                                modifier = Modifier
                                    .size(180.dp)
                                    .clip(CircleShape)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                AccentBlue.copy(alpha = 0.2f),
                                                AccentBlue.copy(alpha = 0.05f),
                                                Color.Transparent
                                            ),
                                            radius = 180f
                                        )
                                    )
                                    .scale(burstScale.value)
                            )
                            
                            // Circle with gradient
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .clip(CircleShape)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                AccentBlue.copy(alpha = 0.3f),
                                                AccentBlue.copy(alpha = 0.1f)
                                            )
                                        )
                                    )
                                    .scale(burstScale.value)
                            )
                            
                            if (showSuccessAnimation) {
                                // Enhanced particle animation
                                EnhancedSuccessParticleAnimation()
                            }
                            
                            // Check icon with scaling animation
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(80.dp)
                                    .scale(checkScale.value)
                                    .graphicsLayer {
                                        shadowElevation = 10f
                                    }
                            )
                        }
                        
                        // Results info with animations
                        AnimatedVisibility(
                            visible = showSuccessAnimation,
                            enter = fadeIn(
                                animationSpec = tween(500, easing = EaseOutCubic, delayMillis = 300)
                            ) + expandVertically(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.compression_complete),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                
                                Text(
                                    text = "Successfully compressed 127 photos",
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center
                                )
                                
                                Text(
                                    text = stringResource(R.string.space_saved, spaceSaved),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    textAlign = TextAlign.Center,
                                    color = AccentBlue
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Results card with enhanced animations
                        AnimatedVisibility(
                            visible = showSuccessAnimation,
                            enter = fadeIn(
                                animationSpec = tween(800, delayMillis = 500)
                            ) + slideInVertically(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow,
                                ),
                                initialOffsetY = { it / 2 }
                            ),
                            exit = slideOutVertically() + fadeOut()
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .graphicsLayer {
                                        shadowElevation = 8f
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = DarkSurfaceVariant
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 4.dp
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Compression Statistics",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    
                                    // Animated rows that slide in one by one
                                    StatRow(
                                        label = "Original Size:",
                                        value = "2.54 GB",
                                        delayMillis = 0,
                                        showAnimation = showSuccessAnimation
                                    )
                                    
                                    StatRow(
                                        label = "Compressed Size:",
                                        value = "1.29 GB",
                                        delayMillis = 100,
                                        showAnimation = showSuccessAnimation
                                    )
                                    
                                    StatRow(
                                        label = "Space Saved:",
                                        value = spaceSaved,
                                        valueColor = AccentBlue,
                                        delayMillis = 200,
                                        showAnimation = showSuccessAnimation
                                    )
                                    
                                    StatRow(
                                        label = "Reduction Percentage:",
                                        value = "49%",
                                        valueColor = AccentBlue,
                                        delayMillis = 300,
                                        showAnimation = showSuccessAnimation
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Done button with modern gradient and improved interaction
                AnimatedVisibility(
                    visible = !isCompressing && showSuccessAnimation,
                    enter = fadeIn(animationSpec = tween(300, delayMillis = 800)) + 
                            expandVertically(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                ),
                                expandFrom = Alignment.Bottom
                            )
                ) {
                    Button(
                        onClick = onNavigateHome,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .scale(buttonScale.value)
                            .graphicsLayer {
                                shadowElevation = if (isButtonHovered) 8f else 4f
                            },
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        interactionSource = buttonInteractionSource,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        // Gradient background for button
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            AccentBlue,
                                            AccentBlue.copy(red = 0.4f, green = 0.6f),
                                            AccentBlue
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Done",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatRow(
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified,
    delayMillis: Int = 0,
    showAnimation: Boolean
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(showAnimation) {
        if (showAnimation) {
            delay(delayMillis.toLong())
            isVisible = true
        }
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInHorizontally(
            initialOffsetX = { it / 4 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label)
            Text(
                text = value,
                color = valueColor,
                fontWeight = if (valueColor != Color.Unspecified) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun CustomCircularProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 8.dp,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    secondaryColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
    animationDuration: Int = 1000
) {
    val currentPercentage = remember { Animatable(0f) }
    
    LaunchedEffect(progress) {
        currentPercentage.animateTo(
            targetValue = progress,
            animationSpec = tween(
                durationMillis = animationDuration,
                easing = EaseOutCubic
            )
        )
    }
    
    Canvas(modifier = modifier) {
        val strokeWidthPx = strokeWidth.toPx()
        val diameter = size.minDimension - strokeWidthPx
        val radius = diameter / 2
        val topLeft = Offset(
            (size.width - diameter) / 2,
            (size.height - diameter) / 2
        )
        val arcSize = Size(diameter, diameter)
        
        // Background circle with more quality
        drawArc(
            color = secondaryColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(
                width = strokeWidthPx,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            )
        )
        
        // Progress arc with enhanced quality and glow effect
        drawArc(
            brush = Brush.sweepGradient(
                0f to primaryColor.copy(alpha = 0.6f),
                0.5f to primaryColor,
                1f to primaryColor
            ),
            startAngle = -90f,
            sweepAngle = 360f * currentPercentage.value,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(
                width = strokeWidthPx,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
        
        // Apply a subtle glow to the progress arc (extra visual effect)
        if (currentPercentage.value > 0.05f) {
            drawArc(
                color = primaryColor.copy(alpha = 0.2f),
                startAngle = -90f,
                sweepAngle = 360f * currentPercentage.value,
                useCenter = false,
                topLeft = Offset(
                    topLeft.x - strokeWidthPx / 4,
                    topLeft.y - strokeWidthPx / 4
                ),
                size = Size(
                    arcSize.width + strokeWidthPx / 2,
                    arcSize.height + strokeWidthPx / 2
                ),
                style = Stroke(
                    width = strokeWidthPx * 1.5f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                )
            )
        }
    }
}

@Composable
fun EnhancedSuccessParticleAnimation() {
    val particleCount = 60
    val duration = 4000
    
    // Create more varied particles with different properties
    val particles = remember {
        List(particleCount) {
            val isSlowParticle = it % 5 == 0
            val particleSize = Random.nextFloat() * if (isSlowParticle) 6f else 3f + 1f
            
            Particle(
                id = it,
                x = 0f,
                y = 0f,
                radius = particleSize,
                speed = Random.nextFloat() * (if (isSlowParticle) 1f else 2.5f) + 0.5f,
                angle = (Random.nextFloat() * 2f * PI).toFloat(),
                alpha = 1f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 10f,
                color = when {
                    it % 7 == 0 -> Color(0xFF64B5F6) // Light blue
                    it % 7 == 1 -> Color(0xFF81C784) // Light green
                    it % 7 == 2 -> Color(0xFFFFD54F) // Amber
                    it % 7 == 3 -> Color(0xFFE57373) // Light red
                    it % 7 == 4 -> Color(0xFF9575CD) // Light purple
                    it % 7 == 5 -> Color(0xFF4DB6AC) // Light teal
                    else -> Color.White
                }
            )
        }
    }
    
    val animatedParticles = particles.map { particle ->
        val animatable = remember { Animatable(0f) }
        
        LaunchedEffect(particle.id) {
            delay(Random.nextLong(0, 300))
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = duration,
                    easing = EaseOutCubic
                )
            )
        }
        
        val progress = animatable.value
        
        // Apply a more interesting motion pattern
        val distance = particle.speed * 120f * progress
        val wobble = sin(progress * 10 * particle.id % 5) * 5 * (1 - progress)
        val currentX = distance * cos(particle.angle) + wobble
        val currentY = distance * sin(particle.angle) + wobble
        
        // Alpha fades slower at start, then faster at end for a nicer trail effect
        val alpha = if (progress < 0.3f) {
            1f - progress / 0.3f * 0.2f
        } else {
            0.8f - (progress - 0.3f) / 0.7f * 0.8f
        }
        
        particle.copy(
            x = currentX,
            y = currentY,
            rotation = particle.rotationSpeed * progress * 360,
            alpha = alpha.coerceIn(0f, 1f)
        )
    }
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        
        // Draw particles with more sophisticated rendering
        animatedParticles.forEach { particle ->
            withTransform({
                translate(
                    left = center.x + particle.x,
                    top = center.y + particle.y
                )
                rotate(particle.rotation)
            }) {
                // Draw particle with a subtle glow effect
                drawCircle(
                    color = particle.color.copy(alpha = particle.alpha * 0.3f),
                    radius = (particle.radius * 1.8f),
                    style = Fill,
                    blendMode = BlendMode.SrcOver
                )
                
                drawCircle(
                    color = particle.color.copy(alpha = particle.alpha),
                    radius = particle.radius,
                    style = Fill,
                    blendMode = BlendMode.SrcOver
                )
            }
        }
    }
}

data class Particle(
    val id: Int,
    val x: Float,
    val y: Float,
    val radius: Float,
    val speed: Float,
    val angle: Float,
    val alpha: Float,
    val color: Color,
    val rotationSpeed: Float = 0f,
    val rotation: Float = 0f
)

// Custom easing curves for more fluid motion
private val EaseOutCubic = CubicBezierEasing(0.33f, 1f, 0.68f, 1f)
private val EaseInOutCubic = CubicBezierEasing(0.65f, 0f, 0.35f, 1f)
private val EaseOutBounce = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f) 