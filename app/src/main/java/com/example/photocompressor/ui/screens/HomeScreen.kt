package com.example.photocompressor.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.photocompressor.R
import com.example.photocompressor.ui.theme.AccentBlue
import com.example.photocompressor.ui.theme.DarkSurfaceVariant
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class
)
@Composable
fun HomeScreen(
    onNavigateToCompress: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    // Animation states
    var isLoaded by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    // Animated rotation for the logo with spring physics
    val rotation = animateFloatAsState(
        targetValue = if (isLoaded) 720f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "initialRotation"
    )
    
    // Continuous slow rotation after initial animation
    val infiniteRotation = rememberInfiniteTransition(label = "logoRotation")
    val continuousRotation by infiniteRotation.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    // Floating animation for logo
    val floatAnim by infiniteRotation.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )
    
    // Button animation and interaction
    val buttonInteractionSource = remember { MutableInteractionSource() }
    val isButtonHovered by buttonInteractionSource.collectIsHoveredAsState()
    val isButtonPressed by buttonInteractionSource.collectIsPressedAsState()
    
    val buttonScale = animateFloatAsState(
        targetValue = when {
            isButtonPressed -> 0.9f
            isButtonHovered -> 1.1f
            showButton -> 1f
            else -> 0.8f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "buttonScale"
    )
    
    // Gradient animation for button
    val gradientRotation by infiniteRotation.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradientRotation"
    )
    
    // Staggered animation sequence
    LaunchedEffect(key1 = true) {
        isLoaded = true
        delay(300)
        showContent = true
        delay(400)
        showButton = true
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.app_name),
                        modifier = Modifier.graphicsLayer {
                            alpha = if (isLoaded) 1f else 0f
                            translationY = if (isLoaded) 0f else -20f
                        }
                    ) 
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier
                            .graphicsLayer {
                                rotationZ = if (isLoaded) 0f else -90f
                                scaleX = if (isLoaded) 1f else 0f
                                scaleY = if (isLoaded) 1f else 0f
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings),
                            modifier = Modifier.graphicsLayer {
                                rotationZ = continuousRotation * 0.1f
                            }
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
            // Background gradient effect
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = if (isLoaded) 0.5f else 0f
                    }
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AccentBlue.copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            center = Offset(200f, 200f),
                            radius = 800f
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // App logo with animation
                Box(
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .size(136.dp)
                        .graphicsLayer {
                            scaleX = if (isLoaded) 1f else 0.2f
                            scaleY = if (isLoaded) 1f else 0.2f
                            rotationZ = rotation.value + (if (isLoaded) continuousRotation else 0f)
                            translationY = floatAnim * 8 // Floating effect
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Outer glow
                    Box(
                        modifier = Modifier
                            .size(136.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        AccentBlue.copy(alpha = 0.1f),
                                        AccentBlue.copy(alpha = 0.05f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    
                    // Outer circle with gradient
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.sweepGradient(
                                    colors = listOf(
                                        AccentBlue.copy(alpha = 0.3f),
                                        AccentBlue.copy(alpha = 0.1f),
                                        AccentBlue.copy(alpha = 0.3f)
                                    ),
                                    center = Offset(0.5f, 0.5f)
                                )
                            )
                    )
                    
                    // Middle circle
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        AccentBlue.copy(alpha = 0.4f),
                                        AccentBlue.copy(alpha = 0.2f)
                                    ),
                                    start = Offset(0f, 0f),
                                    end = Offset(90f, 90f)
                                )
                            )
                            .graphicsLayer {
                                rotationZ = -continuousRotation * 0.5f
                            }
                    )
                    
                    // Inner circle that rotates independently
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        AccentBlue.copy(alpha = 0.6f),
                                        AccentBlue.copy(alpha = 0.3f)
                                    )
                                )
                            )
                            .graphicsLayer {
                                rotationZ = -continuousRotation
                            }
                    )
                    
                    // Camera icon
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .graphicsLayer {
                                // Counter-rotate to keep the icon stable while background rotates
                                rotationZ = -continuousRotation * 0.2f
                            },
                        tint = Color.White.copy(alpha = 0.9f)
                    )
                }
                
                // Content with animations
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(
                        animationSpec = tween(800, easing = EaseOutQuart)
                    ) + slideInVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        initialOffsetY = { 50 }
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header text
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.welcome_message),
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.graphicsLayer {
                                    translationY = floatAnim * 4 // Subtle floating effect
                                }
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = stringResource(R.string.compression_subtitle),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.graphicsLayer {
                                    translationY = floatAnim * 2 // Even more subtle floating
                                }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // New information card about compression behavior
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    alpha = if (isLoaded) 1f else 0f
                                    translationY = if (isLoaded) floatAnim * 5 else 50f
                                    shadowElevation = 8f
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = DarkSurfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 8.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "How it works",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.graphicsLayer {
                                        translationX = floatAnim * -3 // Subtle side-to-side float
                                    }
                                )
                                
                                Text(
                                    text = stringResource(R.string.compression_behavior),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.graphicsLayer {
                                        translationX = floatAnim * 3 // Opposite direction float
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Start button with modern gradient and animation
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
                                        ),
                                        start = Offset(
                                            x = (gradientRotation * Math.PI / 180f).toFloat().coerceIn(0f, 1f) * 100f,
                                            y = 0f
                                        ),
                                        end = Offset(
                                            x = ((gradientRotation + 180) * Math.PI / 180f).toFloat().coerceIn(0f, 1f) * 100f,
                                            y = 100f
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.analyze_action),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Custom easing curve for more fluid motion
private val EaseOutQuart = CubicBezierEasing(0.25f, 1f, 0.5f, 1f) 