package com.studentanalytics.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.studentanalytics.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedAnalyticsCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    badge: String? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    var isHovered by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    
    val elevation by animateDpAsState(
        targetValue = when {
            !isEnabled -> Elevation.none
            isPressed -> Elevation.small
            isHovered -> Elevation.large
            else -> Elevation.medium
        },
        animationSpec = tween(durationMillis = MotionTokens.DurationShort3),
        label = "cardElevation"
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = when {
            !isEnabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            isPressed -> MaterialTheme.colorScheme.surfaceContainer
            isHovered -> MaterialTheme.colorScheme.surfaceContainerHigh
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(
            durationMillis = MotionTokens.DurationShort3,
            easing = MotionTokens.Emphasized
        ),
        label = "cardColor"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = MotionTokens.SpringMediumDamping,
        label = "cardScale"
    )
    
    val contentAlpha by animateFloatAsState(
        targetValue = if (isEnabled) 1f else 0.6f,
        animationSpec = MotionTokens.SpringHighDamping,
        label = "contentAlpha"
    )

    Card(
        onClick = if (isEnabled) {
            {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
        } else { {} },
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = contentAlpha
            }
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(CornerRadius.medium),
                clip = false
            )
            .pointerInput(isEnabled) {
                if (isEnabled) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            tryAwaitRelease()
                            isPressed = false
                        }
                    )
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(
                alpha = if (isEnabled) 0.5f else 0.2f
            )
        ),
        shape = RoundedCornerShape(CornerRadius.medium),
        enabled = isEnabled,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.large),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
            ) {
                // Icon container with background and animation
                Surface(
                    modifier = Modifier.size(Dimensions.iconHuge + Spacing.medium),
                    shape = RoundedCornerShape(CornerRadius.small),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(
                        alpha = if (isEnabled) 0.15f else 0.05f
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(
                            alpha = if (isEnabled) 0.3f else 0.1f
                        )
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier
                                .size(Dimensions.iconLarge)
                                .graphicsLayer {
                                    val iconScale = if (isPressed) 0.9f else 1f
                                    scaleX = iconScale
                                    scaleY = iconScale
                                },
                            tint = MaterialTheme.colorScheme.primary.copy(
                                alpha = if (isEnabled) 1f else 0.5f
                            )
                        )
                    }
                }

                // Text content
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = if (isEnabled) 1f else 0.6f
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = if (isEnabled) 1f else 0.5f
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Badge overlay with entrance animation
            badge?.let {
                var badgeVisible by remember { mutableStateOf(false) }
                
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(300)
                    badgeVisible = true
                }
                
                AnimatedVisibility(
                    visible = badgeVisible,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-Spacing.small), y = Spacing.small),
                    enter = scaleIn(
                        initialScale = 0.3f,
                        animationSpec = tween(
                            durationMillis = MotionTokens.DurationMedium2,
                            easing = MotionTokens.EmphasizedDecelerate
                        )
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = MotionTokens.DurationMedium1,
                            easing = MotionTokens.EmphasizedDecelerate
                        )
                    )
                ) {
                    Surface(
                        shape = RoundedCornerShape(CornerRadius.circular),
                        color = MaterialTheme.colorScheme.secondary,
                        shadowElevation = Elevation.small
                    ) {
                        Text(
                            text = it,
                            modifier = Modifier.padding(
                                horizontal = Spacing.small,
                                vertical = Spacing.small
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    variant: ButtonVariant = ButtonVariant.Primary
) {
    var isPressed by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = MotionTokens.SpringMediumDamping,
        label = "buttonScale"
    )
    
    val elevation by animateDpAsState(
        targetValue = when {
            !enabled -> Elevation.none
            isPressed -> Elevation.small
            else -> Elevation.medium
        },
        animationSpec = tween(durationMillis = MotionTokens.DurationShort3),
        label = "buttonElevation"
    )
    
    val colors = when (variant) {
        ButtonVariant.Primary -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
        ButtonVariant.Secondary -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
        ButtonVariant.Outline -> ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    when (variant) {
        ButtonVariant.Outline -> {
            OutlinedButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                },
                modifier = modifier
                    .height(Dimensions.buttonHeight)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .pointerInput(enabled) {
                        if (enabled && !isLoading) {
                            detectTapGestures(
                                onPress = {
                                    isPressed = true
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    tryAwaitRelease()
                                    isPressed = false
                                }
                            )
                        }
                    },
                enabled = enabled && !isLoading,
                colors = colors,
                shape = RoundedCornerShape(CornerRadius.small),
                border = BorderStroke(
                    width = if (isPressed) 2.dp else 1.dp,
                    color = if (enabled) MaterialTheme.colorScheme.primary 
                           else MaterialTheme.colorScheme.outline
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = elevation,
                    pressedElevation = Elevation.none
                )
            ) {
                EnhancedButtonContent(text, isLoading, icon)
            }
        }
        else -> {
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                },
                modifier = modifier
                    .height(Dimensions.buttonHeight)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .pointerInput(enabled) {
                        if (enabled && !isLoading) {
                            detectTapGestures(
                                onPress = {
                                    isPressed = true
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    tryAwaitRelease()
                                    isPressed = false
                                }
                            )
                        }
                    },
                enabled = enabled && !isLoading,
                colors = colors,
                shape = RoundedCornerShape(CornerRadius.small),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = elevation,
                    pressedElevation = Elevation.none
                )
            ) {
                EnhancedButtonContent(text, isLoading, icon)
            }
        }
    }
}

@Composable
private fun EnhancedButtonContent(
    text: String,
    isLoading: Boolean,
    icon: ImageVector?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.small)
    ) {
        AnimatedVisibility(
            visible = isLoading,
            enter = scaleIn(
                initialScale = 0.3f,
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationMedium1,
                    easing = MotionTokens.EmphasizedDecelerate
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationMedium1,
                    easing = MotionTokens.EmphasizedDecelerate
                )
            ),
            exit = scaleOut(
                targetScale = 0.3f,
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationShort2,
                    easing = MotionTokens.EmphasizedAccelerate
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationShort2,
                    easing = MotionTokens.EmphasizedAccelerate
                )
            )
        ) {
            AdvancedLoadingAnimation(
                modifier = Modifier.size(Dimensions.iconSmall)
            )
        }
        
        AnimatedVisibility(
            visible = !isLoading && icon != null,
            enter = scaleIn(
                initialScale = 0.3f,
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationMedium1,
                    easing = MotionTokens.EmphasizedDecelerate
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationMedium1,
                    easing = MotionTokens.EmphasizedDecelerate
                )
            ),
            exit = scaleOut(
                targetScale = 0.3f,
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationShort2,
                    easing = MotionTokens.EmphasizedAccelerate
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationShort2,
                    easing = MotionTokens.EmphasizedAccelerate
                )
            )
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(Dimensions.iconSmall)
                )
            }
        }
        
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

enum class ButtonVariant {
    Primary,
    Secondary, 
    Outline
}