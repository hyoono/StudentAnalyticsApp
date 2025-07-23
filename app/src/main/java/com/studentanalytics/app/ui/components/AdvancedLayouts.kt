package com.studentanalytics.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.studentanalytics.app.ui.theme.*
import kotlin.math.max
import kotlin.math.min

// Samsung One UI inspired large header layout
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneUILayout(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    headerContent: @Composable ColumnScope.() -> Unit = {},
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit
) {
    val density = LocalDensity.current
    val headerHeight = 200.dp
    val minHeaderHeight = 64.dp
    val headerHeightPx = with(density) { headerHeight.toPx() }
    val minHeaderHeightPx = with(density) { minHeaderHeight.toPx() }
    
    val collapsingState = rememberCollapsingHeaderState(scrollState, headerHeight)
    
    // Calculate dynamic values based on scroll with proper bounds
    val progress = collapsingState.progress.coerceIn(0f, 1f)
    val headerCurrentHeight = lerp(minHeaderHeight, headerHeight, (1f - progress).coerceIn(0f, 1f))
    val titleScale = androidx.compose.ui.util.lerp(0.7f, 1f, (1f - progress).coerceIn(0f, 1f))
    val titleAlpha = androidx.compose.ui.util.lerp(0.3f, 1f, (1f - progress).coerceIn(0f, 1f))
    val backgroundAlpha = androidx.compose.ui.util.lerp(0f, 1f, progress)
    
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = headerHeight,
                bottom = Spacing.extraLarge
            )
        ) {
            content()
        }
        
        // Collapsing header
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerCurrentHeight),
            color = MaterialTheme.colorScheme.surface.copy(alpha = backgroundAlpha.coerceIn(0f, 1f)),
            tonalElevation = if (progress > 0.1f) 4.dp else 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = if (progress < 0.1f) {
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f)
                                )
                            )
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                    )
                    .padding(
                        horizontal = Spacing.large,
                        vertical = Spacing.medium
                    ),
                verticalArrangement = Arrangement.Bottom
            ) {
                // Icon and title row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                ) {
                    // Large icon that shrinks
                    icon?.let {
                        val iconSize = lerp(24.dp, 48.dp, (1f - progress).coerceIn(0f, 1f))
                        Surface(
                            modifier = Modifier.size((iconSize + Spacing.small).coerceAtLeast(24.dp)),
                            shape = RoundedCornerShape(CornerRadius.small),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(
                                alpha = androidx.compose.ui.util.lerp(0.1f, 0.2f, (1f - progress).coerceIn(0f, 1f))
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = it,
                                    contentDescription = null,
                                    modifier = Modifier.size(iconSize),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    
                    // Title that scales and moves
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = androidx.compose.ui.unit.lerp(24.sp, 32.sp, (1f - progress).coerceIn(0f, 1f)),
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = titleAlpha.coerceIn(0f, 1f)),
                            modifier = Modifier.graphicsLayer {
                                scaleX = titleScale.coerceIn(0.1f, 2f)
                                scaleY = titleScale.coerceIn(0.1f, 2f)
                            }
                        )
                        
                        subtitle?.let { sub ->
                            Text(
                                text = sub,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha = androidx.compose.ui.util.lerp(0f, 0.8f, (1f - progress).coerceIn(0f, 1f))
                                ),
                                modifier = Modifier.alpha((1f - progress).coerceIn(0f, 1f))
                            )
                        }
                    }
                }
                
                // Additional header content that fades out
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha((1f - progress).coerceIn(0f, 1f))
                ) {
                    Column {
                        headerContent()
                    }
                }
            }
        }
    }
}

// Modern card with advanced animations and gestures
@Composable
fun AdvancedCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    elevation: Dp = 4.dp,
    cornerRadius: Dp = CornerRadius.medium,
    content: @Composable ColumnScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    var isHovered by remember { mutableStateOf(false) }
    
    val animatedElevation by animateDpAsState(
        targetValue = when {
            !enabled -> 0.dp
            isPressed -> elevation - 2.dp
            isHovered -> elevation + 2.dp
            else -> elevation
        },
        animationSpec = MotionTokens.SpringSoftDp,
        label = "cardElevation"
    )
    
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = MotionTokens.SpringMediumDamping,
        label = "cardScale"
    )
    
    val animatedAlpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.6f,
        animationSpec = MotionTokens.SpringHighDamping,
        label = "cardAlpha"
    )
    
    Surface(
        onClick = if (enabled) onClick else { {} },
        modifier = modifier
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
                alpha = animatedAlpha
            }
            .pointerInput(enabled) {
                if (enabled) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        }
                    )
                }
            },
        enabled = enabled,
        shape = RoundedCornerShape(cornerRadius),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = animatedElevation,
        shadowElevation = animatedElevation
    ) {
        Column(
            modifier = Modifier.padding(Spacing.large),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            content = content
        )
    }
}

// Floating Action Button with sophisticated animations
@Composable
fun AdvancedFloatingActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    text: String? = null,
    expanded: Boolean = false
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = MotionTokens.SpringMediumDamping,
        label = "fabScale"
    )
    
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 12.dp,
        animationSpec = MotionTokens.SpringSoftDp,
        label = "fabElevation"
    )
    
    val fabWidth by animateDpAsState(
        targetValue = if (expanded && text != null) 200.dp else 56.dp,
        animationSpec = MotionTokens.SpringMediumDampingDp,
        label = "fabWidth"
    )
    
    Surface(
        onClick = onClick,
        modifier = modifier
            .width(fabWidth)
            .height(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        tonalElevation = elevation,
        shadowElevation = elevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            
            if (expanded && text != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1
                )
            }
        }
    }
}

// Pull-to-refresh style indicator
@Composable
fun PullRefreshIndicator(
    refreshing: Boolean,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (refreshing) 360f else progress * 180f,
        animationSpec = if (refreshing) {
            infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        } else {
            MotionTokens.SpringHighDamping
        },
        label = "refreshRotation"
    )
    
    val scale by animateFloatAsState(
        targetValue = min(1f, progress),
        animationSpec = MotionTokens.SpringMediumDamping,
        label = "refreshScale"
    )
    
    Box(
        modifier = modifier
            .size(40.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = rotation
            },
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = if (refreshing) 1f else max(0.1f, progress),
            modifier = Modifier.size(32.dp),
            strokeWidth = 3.dp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// Status chip with animation
@Composable
fun AnimatedStatusChip(
    text: String,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    ContainerTransform(
        visible = visible,
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isPositive) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            },
            modifier = Modifier.advancedPressAnimation()
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 6.dp
                ),
                style = MaterialTheme.typography.labelMedium,
                color = if (isPositive) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onErrorContainer
                }
            )
        }
    }
}

// Progress indicator with smooth animations
@Composable
fun AdvancedProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    label: String? = null,
    showPercentage: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = MotionTokens.DurationLong2,
            easing = MotionTokens.EmphasizedDecelerate
        ),
        label = "progressAnimation"
    )
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        if (label != null || showPercentage) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (label != null) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (showPercentage) {
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        
        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}