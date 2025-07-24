package com.studentanalytics.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

// Advanced Spring Specifications following Material 3 motion system
object MotionTokens {
    // Duration tokens
    val DurationShort1 = 50
    val DurationShort2 = 100  
    val DurationShort3 = 150
    val DurationShort4 = 200
    val DurationMedium1 = 250
    val DurationMedium2 = 300
    val DurationMedium3 = 350
    val DurationMedium4 = 400
    val DurationLong1 = 450
    val DurationLong2 = 500
    val DurationLong3 = 550
    val DurationLong4 = 600
    
    // Standardized screen entry timing
    val ScreenEntranceDelay = DurationShort3 // 150ms for all screens
    
    // Easing tokens
    val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    val EmphasizedAccelerate = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
    val Emphasized = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val Standard = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val StandardAccelerate = CubicBezierEasing(0.3f, 0.0f, 1.0f, 1.0f)
    val StandardDecelerate = CubicBezierEasing(0.0f, 0.0f, 0.0f, 1.0f)
    
    // Spring specifications for Float
    val SpringLowDamping = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    )
    
    val SpringMediumDamping = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
    
    val SpringHighDamping = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )
    
    val SpringSoft = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    // Spring specifications for Dp
    val SpringSoftDp = spring<Dp>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    val SpringMediumDampingDp = spring<Dp>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
}

// Advanced press animation that follows Material 3 guidelines
@Composable
fun Modifier.advancedPressAnimation(): Modifier {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = MotionTokens.SpringMediumDamping,
        label = "pressScale"
    )
    
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 6.dp,
        animationSpec = tween(
            durationMillis = MotionTokens.DurationShort3,
            easing = MotionTokens.Emphasized
        ),
        label = "pressElevation"
    )
    
    return this
        .scale(scale)
        .graphicsLayer {
            shadowElevation = elevation.toPx()
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                }
            )
        }
}

// Staggered list item animation
@Composable
fun rememberStaggeredAnimationSpec(
    index: Int,
    delayMillis: Int = 50
): AnimationSpec<Float> {
    return tween(
        durationMillis = MotionTokens.DurationMedium4,
        delayMillis = index * delayMillis,
        easing = MotionTokens.EmphasizedDecelerate
    )
}

// Parallax scroll effect
@Composable
fun rememberParallaxState(
    scrollState: LazyListState,
    rate: Float = 0.5f
): State<Float> {
    return remember(scrollState, rate) {
        derivedStateOf {
            scrollState.firstVisibleItemScrollOffset * rate
        }
    }
}

// Collapsing header animation
@Composable
fun rememberCollapsingHeaderState(
    scrollState: LazyListState,
    headerHeight: Dp
): CollapsingHeaderState {
    val density = LocalDensity.current
    val headerHeightPx = with(density) { headerHeight.toPx() }
    
    return remember(scrollState, headerHeightPx) {
        CollapsingHeaderState(scrollState, headerHeightPx)
    }
}

class CollapsingHeaderState(
    private val scrollState: LazyListState,
    private val headerHeight: Float
) {
    val scrollOffset: Float
        get() = if (scrollState.firstVisibleItemIndex == 0) {
            min(
                scrollState.firstVisibleItemScrollOffset.toFloat().coerceAtLeast(0f),
                headerHeight
            )
        } else {
            headerHeight
        }
    
    val progress: Float
        get() = if (headerHeight > 0f) {
            (scrollOffset / headerHeight).coerceIn(0f, 1f)
        } else {
            0f
        }
    
    val height: Float
        get() = (headerHeight - scrollOffset).coerceAtLeast(0f)
    
    val alpha: Float
        get() = (1f - progress).coerceIn(0f, 1f)
        
    val titleAlpha: Float
        get() = if (progress > 0.5f) ((progress - 0.5f) * 2f).coerceIn(0f, 1f) else 0f
}

// Container transform animation
@Composable
fun ContainerTransform(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn(
        animationSpec = tween(
            durationMillis = MotionTokens.DurationMedium2,
            easing = MotionTokens.EmphasizedDecelerate
        )
    ) + scaleIn(
        initialScale = 0.8f,
        animationSpec = tween(
            durationMillis = MotionTokens.DurationMedium2,
            easing = MotionTokens.EmphasizedDecelerate
        )
    ),
    exit: ExitTransition = fadeOut(
        animationSpec = tween(
            durationMillis = MotionTokens.DurationShort4,
            easing = MotionTokens.EmphasizedAccelerate
        )
    ) + scaleOut(
        targetScale = 0.8f,
        animationSpec = tween(
            durationMillis = MotionTokens.DurationShort4,
            easing = MotionTokens.EmphasizedAccelerate
        )
    ),
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = enter,
        exit = exit,
        content = content
    )
}

// Slide in from edge animations
@Composable
fun SlideInFromEdge(
    visible: Boolean,
    edge: AnimationEdge,
    modifier: Modifier = Modifier,
    delayMillis: Int = 0,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    val slideDistance = 300
    
    val (enter, exit) = when (edge) {
        AnimationEdge.Start -> Pair(
            slideInHorizontally(
                initialOffsetX = { -slideDistance },
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationMedium3,
                    delayMillis = delayMillis,
                    easing = MotionTokens.EmphasizedDecelerate
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationMedium2,
                    delayMillis = delayMillis,
                    easing = MotionTokens.EmphasizedDecelerate
                )
            ),
            slideOutHorizontally(
                targetOffsetX = { -slideDistance },
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationShort4,
                    easing = MotionTokens.EmphasizedAccelerate
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationShort3,
                    easing = MotionTokens.EmphasizedAccelerate
                )
            )
        )
        AnimationEdge.End -> Pair(
            slideInHorizontally(
                initialOffsetX = { slideDistance },
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationMedium3,
                    delayMillis = delayMillis,
                    easing = MotionTokens.EmphasizedDecelerate
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationMedium2,
                    delayMillis = delayMillis,
                    easing = MotionTokens.EmphasizedDecelerate
                )
            ),
            slideOutHorizontally(
                targetOffsetX = { slideDistance },
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationShort4,
                    easing = MotionTokens.EmphasizedAccelerate
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationShort3,
                    easing = MotionTokens.EmphasizedAccelerate
                )
            )
        )
        AnimationEdge.Top -> Pair(
            slideInVertically(
                initialOffsetY = { -slideDistance },
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationMedium3,
                    delayMillis = delayMillis,
                    easing = MotionTokens.EmphasizedDecelerate
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationMedium2,
                    delayMillis = delayMillis,
                    easing = MotionTokens.EmphasizedDecelerate
                )
            ),
            slideOutVertically(
                targetOffsetY = { -slideDistance },
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationShort4,
                    easing = MotionTokens.EmphasizedAccelerate
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationShort3,
                    easing = MotionTokens.EmphasizedAccelerate
                )
            )
        )
        AnimationEdge.Bottom -> Pair(
            slideInVertically(
                initialOffsetY = { slideDistance },
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationMedium3,
                    delayMillis = delayMillis,
                    easing = MotionTokens.EmphasizedDecelerate
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationMedium2,
                    delayMillis = delayMillis,
                    easing = MotionTokens.EmphasizedDecelerate
                )
            ),
            slideOutVertically(
                targetOffsetY = { slideDistance },
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationShort4,
                    easing = MotionTokens.EmphasizedAccelerate
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationShort3,
                    easing = MotionTokens.EmphasizedAccelerate
                )
            )
        )
    }
    
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = enter,
        exit = exit,
        content = content
    )
}

enum class AnimationEdge {
    Start, End, Top, Bottom
}

// Staggered entrance animation for lists
fun LazyListScope.animatedItems(
    count: Int,
    visible: Boolean,
    delayPerItem: Int = 50,
    content: @Composable (index: Int) -> Unit
) {
    items(count) { index ->
        var itemVisible by remember { mutableStateOf(false) }
        
        LaunchedEffect(visible) {
            if (visible) {
                delay((index * delayPerItem).toLong())
                itemVisible = true
            } else {
                itemVisible = false
            }
        }
        
        SlideInFromEdge(
            visible = itemVisible,
            edge = AnimationEdge.Bottom,
            delayMillis = 0 // Delay handled above
        ) {
            content(index)
        }
    }
}

// Loading animation with sophisticated springs
@Composable
fun AdvancedLoadingAnimation(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loadingAnimation")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = MotionTokens.DurationMedium4,
                easing = MotionTokens.Emphasized
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loadingScale"
    )
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = MotionTokens.DurationLong4 * 2,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "loadingRotation"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .graphicsLayer {
                rotationZ = rotation
            }
    ) {
        CircularProgressIndicator(
            strokeWidth = 3.dp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// Container transform animation for smooth content transitions
@Composable
fun ContainerTransform(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInVertically(
            initialOffsetY = { it / 3 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = MotionTokens.DurationMedium2,
                easing = MotionTokens.EmphasizedDecelerate
            )
        ) + scaleIn(
            initialScale = 0.9f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it / 3 },
            animationSpec = tween(
                durationMillis = MotionTokens.DurationShort4,
                easing = MotionTokens.EmphasizedAccelerate
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = MotionTokens.DurationShort3,
                easing = MotionTokens.EmphasizedAccelerate
            )
        ) + scaleOut(
            targetScale = 1.05f,
            animationSpec = tween(
                durationMillis = MotionTokens.DurationShort4,
                easing = MotionTokens.EmphasizedAccelerate
            )
        ),
        content = content
    )
}

// Staggered list animation for sequential item appearance
@Composable
fun StaggeredListAnimation(
    visible: Boolean,
    itemIndex: Int,
    modifier: Modifier = Modifier,
    staggerDelayMs: Int = 50,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    val delay = itemIndex * staggerDelayMs
    
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(
                durationMillis = MotionTokens.DurationMedium3 + delay,
                delayMillis = delay,
                easing = MotionTokens.EmphasizedDecelerate
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = MotionTokens.DurationMedium2,
                delayMillis = delay,
                easing = MotionTokens.Standard
            )
        ) + scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(
                durationMillis = MotionTokens.DurationMedium3,
                delayMillis = delay,
                easing = MotionTokens.EmphasizedDecelerate
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it / 4 },
            animationSpec = tween(
                durationMillis = MotionTokens.DurationShort4,
                easing = MotionTokens.EmphasizedAccelerate
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = MotionTokens.DurationShort3,
                easing = MotionTokens.EmphasizedAccelerate
            )
        ),
        content = content
    )
}

// Enhanced scale transition with spring physics
@Composable
fun SpringScaleTransition(
    visible: Boolean,
    modifier: Modifier = Modifier,
    initialScale: Float = 0.8f,
    targetScale: Float = 1.0f,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = scaleIn(
            initialScale = initialScale,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = MotionTokens.DurationMedium2,
                easing = MotionTokens.EmphasizedDecelerate
            )
        ),
        exit = scaleOut(
            targetScale = targetScale * 1.1f,
            animationSpec = tween(
                durationMillis = MotionTokens.DurationShort4,
                easing = MotionTokens.EmphasizedAccelerate
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = MotionTokens.DurationShort3,
                easing = MotionTokens.EmphasizedAccelerate
            )
        ),
        content = content
    )
}