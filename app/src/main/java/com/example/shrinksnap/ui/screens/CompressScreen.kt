package com.example.shrinksnap.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.shrinksnap.R
import com.example.shrinksnap.ui.theme.AccentBlue
import com.example.shrinksnap.ui.theme.DarkSurfaceVariant
import com.example.shrinksnap.ui.components.StatsRow
import com.example.shrinksnap.ui.components.AnimatedCounter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun CompressScreen(
    onNavigateToResults: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var timeValue by remember { mutableStateOf(30) }
    var timeUnit by remember { mutableStateOf("Days") }
    var timeUnitExpanded by remember { mutableStateOf(false) }
    var photosFound by remember { mutableStateOf(0) }
    var potentialSavings by remember { mutableStateOf("0 MB") }
    var isAnalyzing by remember { mutableStateOf(false) }
    var isAnalyzed by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }
    
    // Scroll effect for parallax
    val scrollState = rememberScrollState()
    val parallaxOffset by remember {
        derivedStateOf { scrollState.value * 0.15f }
    }
    
    // Button hover effect with haptic feedback
    var buttonHovered by remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current
    val buttonScale = animateFloatAsState(
        targetValue = when {
            isAnalyzing -> 1f
            buttonHovered -> 1.05f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "buttonHover"
    )
    
    // Analyze icon rotation with smoother animation
    val searchRotation = animateFloatAsState(
        targetValue = if (isAnalyzing) 1440f else 0f, // Multiple full rotations for longer analysis
        animationSpec = if (isAnalyzing) {
            tween(2000, easing = EaseInOutQuart)
        } else {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        },
        label = "searchRotation"
    )
    
    // Shimmer animation for analysis with improved gradient
    val shimmerColorStops = listOf(
        0.0f to DarkSurfaceVariant,
        0.4f to DarkSurfaceVariant.lighten(0.1f),
        0.6f to DarkSurfaceVariant.lighten(0.2f),
        1.0f to DarkSurfaceVariant
    )
    
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = -1000f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "translate"
    )
    
    // Background gradient animation
    val gradientAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "backgroundGradient"
    )
    
    // Staggered reveal animation
    LaunchedEffect(key1 = true) {
        delay(100)
        showContent = true
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.time_filter_label),
                        modifier = Modifier.graphicsLayer {
                            translationY = -parallaxOffset * 0.2f
                        }
                    ) 
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onNavigateBack()
                        }
                    ) {
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
            // Animated background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = if (showContent) 0.5f else 0f
                    }
                    .background(
                        brush = Brush.sweepGradient(
                            colorStops = arrayOf(
                                0.0f to Color.Transparent,
                                0.3f to AccentBlue.copy(alpha = 0.03f),
                                0.6f to AccentBlue.copy(alpha = 0.02f),
                                1.0f to Color.Transparent
                            ),
                            center = Offset(0.5f + (sin(gradientAngle * PI/180) * 0.2f).toFloat(), 
                                           0.5f + (cos(gradientAngle * PI/180) * 0.2f).toFloat())
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Animated content reveal with staggered effect
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(
                        animationSpec = tween(800, easing = EaseOutQuart)
                    ) + slideInVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        initialOffsetY = { 100 }
                    )
                ) {
                    // Time period selection card with elevation and parallax
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                shadowElevation = 8f
                                translationY = -parallaxOffset * 0.5f
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
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.select_time_period),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.graphicsLayer {
                                    translationX = -parallaxOffset * 0.3f
                                }
                            )
                            
                            // Add explanation text about compression behavior with animated reveal
                            Text(
                                text = stringResource(R.string.compression_behavior),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.graphicsLayer {
                                    translationX = -parallaxOffset * 0.2f
                                }
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Time value input with interactive animation
                                val timeValueInteractionSource = remember { MutableInteractionSource() }
                                val isTimeValueFocused by timeValueInteractionSource.collectIsFocusedAsState()
                                
                                OutlinedTextField(
                                    value = timeValue.toString(),
                                    onValueChange = { 
                                        val newValue = it.toIntOrNull() ?: 0
                                        if (newValue != timeValue) {
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        }
                                        timeValue = newValue.coerceIn(1, 3650) // Max 10 years
                                    },
                                    modifier = Modifier
                                        .weight(0.5f)
                                        .graphicsLayer {
                                            translationX = -parallaxOffset * 0.6f
                                            scaleX = if (isTimeValueFocused) 1.02f else 1f
                                            scaleY = if (isTimeValueFocused) 1.02f else 1f
                                        },
                                    label = { Text("Value") },
                                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                    ),
                                    interactionSource = timeValueInteractionSource,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AccentBlue,
                                        focusedLabelColor = AccentBlue
                                    )
                                )
                                
                                // Time unit dropdown with enhanced animation
                                Box(modifier = Modifier
                                    .weight(0.5f)
                                    .graphicsLayer {
                                        translationX = -parallaxOffset * 0.3f
                                    }
                                ) {
                                    OutlinedTextField(
                                        value = timeUnit,
                                        onValueChange = {},
                                        modifier = Modifier.fillMaxWidth(),
                                        label = { Text("Unit") },
                                        readOnly = true,
                                        interactionSource = remember { MutableInteractionSource() }
                                            .also { interactionSource ->
                                                LaunchedEffect(interactionSource) {
                                                    interactionSource.interactions.collect {
                                                        if (it is PressInteraction.Release) {
                                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            timeUnitExpanded = !timeUnitExpanded
                                                        }
                                                    }
                                                }
                                            },
                                        trailingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = "Select Time Unit",
                                                modifier = Modifier
                                                    .graphicsLayer {
                                                        rotationZ = if (timeUnitExpanded) 180f else 0f
                                                        scaleX = if (timeUnitExpanded) 1.2f else 1f
                                                        scaleY = if (timeUnitExpanded) 1.2f else 1f
                                                    }
                                                    .padding(8.dp)
                                            )
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AccentBlue,
                                            focusedLabelColor = AccentBlue
                                        )
                                    )
                                    
                                    DropdownMenu(
                                        expanded = timeUnitExpanded,
                                        onDismissRequest = { timeUnitExpanded = false }
                                    ) {
                                        listOf(
                                            stringResource(R.string.days),
                                            stringResource(R.string.weeks),
                                            stringResource(R.string.months),
                                            stringResource(R.string.years)
                                        ).forEach { unit ->
                                            DropdownMenuItem(
                                                text = { Text(unit) },
                                                onClick = {
                                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    timeUnit = unit
                                                    timeUnitExpanded = false
                                                },
                                                colors = MenuDefaults.itemColors(
                                                    textColor = if (unit == timeUnit) AccentBlue else MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Analysis results with enhanced animation effects
                AnimatedVisibility(
                    visible = isAnalyzed,
                    enter = fadeIn(
                        animationSpec = tween(800, delayMillis = 300, easing = EaseOutQuint)
                    ) + expandVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        expandFrom = Alignment.Top
                    )
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
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Results header with effect
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Analysis Results",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.graphicsLayer {
                                        shadowElevation = 2f
                                    }
                                )
                            }
                            
                            Divider(
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .fillMaxWidth(0.7f)
                                    .align(Alignment.CenterHorizontally),
                                color = AccentBlue.copy(alpha = 0.5f),
                                thickness = 1.dp
                            )
                            
                            // Stats with animated counters
                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Photos found with animated number
                                StatsRow(
                                    icon = Icons.Default.PhotoCamera,
                                    label = "Photos found:",
                                    value = photosFound.toString(),
                                    valueColor = AccentBlue,
                                    animateValue = true
                                )
                                
                                // Potential savings with animated text
                                StatsRow(
                                    icon = Icons.Default.Storage,
                                    label = "Potential space savings:",
                                    value = potentialSavings,
                                    valueColor = AccentBlue,
                                    animateValue = true
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f).height(40.dp))
                
                // Show shimmer effect during analysis - enhanced with interactive elements
                AnimatedVisibility(
                    visible = isAnalyzing,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Enhanced shimmer loading indicator with depth
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .padding(16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(DarkSurfaceVariant)
                                .graphicsLayer {
                                    shadowElevation = 4f
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // Background static layer
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp)
                            )
                            
                            // Shimmer effect layer
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.linearGradient(
                                            colorStops = shimmerColorStops.toTypedArray(),
                                            start = Offset(translateAnim.value - 500, 0f),
                                            end = Offset(translateAnim.value + 500, 0f)
                                        )
                                    )
                            )
                            
                            // Scanning animation
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.03f)
                                    .fillMaxHeight()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                AccentBlue.copy(alpha = 0.6f),
                                                AccentBlue.copy(alpha = 0.8f),
                                                AccentBlue.copy(alpha = 0.6f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                    .align(Alignment.CenterStart)
                                    .graphicsLayer {
                                        translationX = (translateAnim.value + 500).coerceIn(0f, size.width)
                                    }
                            )
                            
                            // Pulsing analyze text
                            val pulseFactor = remember { Animatable(1f) }
                            LaunchedEffect(Unit) {
                                while (true) {
                                    pulseFactor.animateTo(
                                        targetValue = 1.2f,
                                        animationSpec = tween(700, easing = EaseInOutQuad)
                                    )
                                    pulseFactor.animateTo(
                                        targetValue = 1f,
                                        animationSpec = tween(700, easing = EaseInOutQuad)
                                    )
                                }
                            }
                            
                            Text(
                                text = "Scanning photo library...",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .graphicsLayer {
                                        scaleX = pulseFactor.value
                                        scaleY = pulseFactor.value
                                        alpha = (2 - pulseFactor.value)
                                    }
                            )
                        }
                        
                        Text(
                            text = "Analyzing photos...",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                
                // Analyze or compress button with enhanced animations
                Button(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (!isAnalyzed) {
                            // Simulate analysis
                            isAnalyzing = true
                            buttonHovered = false  // Reset hover
                            
                            // Simulate a delay for analysis with a more realistic pattern
                            kotlinx.coroutines.MainScope().launch {
                                delay(600)
                                photosFound = 42 // Initial discovery
                                delay(600)
                                photosFound = 85 // More photos found
                                delay(400)
                                photosFound = 127 // Final count
                                
                                // Calculate space with progressive updates
                                potentialSavings = "450 MB"
                                delay(200)
                                potentialSavings = "920 MB"
                                delay(200)
                                potentialSavings = "1.25 GB"
                                
                                delay(300)
                                isAnalyzed = true
                                isAnalyzing = false
                            }
                        } else {
                            // Start compression and navigate to results with a brief delay for natural feel
                            kotlinx.coroutines.MainScope().launch {
                                // Button press animation
                                delay(150)
                                onNavigateToResults()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .scale(buttonScale.value)
                        .graphicsLayer {
                            shadowElevation = if (buttonHovered) 10f else 4f
                            translationY = -parallaxOffset * 0.1f
                        }
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    // Update hover state based on pointer position
                                    buttonHovered = event.type == PointerEventType.Enter || 
                                                   event.type == PointerEventType.Move
                                    if (event.type == PointerEventType.Exit) {
                                        buttonHovered = false
                                    }
                                }
                            }
                        },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(28.dp),
                    enabled = !isAnalyzing,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    // Gradient background for button
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = if (!isAnalyzed) {
                                        listOf(
                                            Color(0xFF1976D2), // Darker blue
                                            AccentBlue,
                                            Color(0xFF42A5F5) // Lighter blue
                                        )
                                    } else {
                                        listOf(
                                            Color(0xFF388E3C), // Darker green
                                            Color(0xFF4CAF50), // Green
                                            Color(0xFF66BB6A) // Lighter green
                                        )
                                    }
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (isAnalyzing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = if (!isAnalyzed) Icons.Default.Search else Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .graphicsLayer {
                                            rotationZ = if (!isAnalyzed) searchRotation.value else 0f
                                        }
                                )
                            }
                            
                            Text(
                                text = if (!isAnalyzed) 
                                    stringResource(R.string.analyze_action) 
                                else 
                                    stringResource(R.string.compress_action),
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

@Composable
fun ShimmerLoadingIndicator(translateAnim: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            DarkSurfaceVariant,
                            Color.Gray.copy(alpha = 0.3f),
                            DarkSurfaceVariant
                        ),
                        startX = -translateAnim,
                        endX = translateAnim
                    )
                )
        )
    }
}

// Extension function to lighten a color
fun Color.lighten(factor: Float): Color {
    return copy(
        red = (red + factor).coerceAtMost(1f),
        green = (green + factor).coerceAtMost(1f),
        blue = (blue + factor).coerceAtMost(1f)
    )
}

// Custom easing curves for more fluid motion
private val EaseOutQuart = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)
private val EaseOutQuint = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)
private val EaseInOutQuad = CubicBezierEasing(0.45f, 0f, 0.55f, 1f)
private val EaseInOutQuart = CubicBezierEasing(0.76f, 0f, 0.24f, 1f) 