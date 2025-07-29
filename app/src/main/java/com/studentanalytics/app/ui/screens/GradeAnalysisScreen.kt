package com.studentanalytics.app.ui.screens

import java.util.Locale
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studentanalytics.app.data.models.GradeAnalysisRequest
import com.studentanalytics.app.ui.viewmodels.GradeAnalysisViewModel
import com.studentanalytics.app.ui.components.*
import com.studentanalytics.app.ui.theme.*
import kotlinx.coroutines.delay

// TODO: Add grade prediction feature later
// TODO: Maybe add more visualization types

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeAnalysisScreen(
    onBack: () -> Unit,
    viewModel: GradeAnalysisViewModel = viewModel()
) {
    var studentID by remember { mutableStateOf("") } // Using ID instead of Id for variety
    var grades by remember { mutableStateOf("") }
    var units by remember { mutableStateOf("") }
    var pastGrades by remember { mutableStateOf("") }
    var gradeFormat by remember { mutableStateOf("raw") }

    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberLazyListState()
    
    // Simple animation
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(200) // Just a quick delay
        isVisible = true
    }

    OneUILayout(
        title = "Grade Analysis",
        subtitle = "Analyze academic performance with detailed insights",
        icon = Icons.Default.Assessment,
        onBackClick = onBack,
        scrollState = scrollState,
        headerContent = {
            Spacer(modifier = Modifier.height(Spacing.small))
        }
    ) {
        item {
            Spacer(modifier = Modifier.height(Spacing.medium))
        }
        
        // Input form with advanced choreographed animations
        item {
            StaggeredListAnimation(
                visible = isVisible,
                itemIndex = 0,
                staggerDelayMs = 100
            ) {
                AdvancedCard(
                    onClick = { /* No action for form card */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.medium),
                    elevation = Elevation.medium
                ) {
                    Text(
                        text = "Student Information",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )

                    EnhancedTextField(
                        value = studentID,
                        onValueChange = { studentID = it },
                        label = "Student ID",
                        placeholder = "Enter student ID",
                        leadingIcon = Icons.Default.Person,
                        helperText = "Enter the unique identifier for the student"
                    )

                    EnhancedTextField(
                        value = grades,
                        onValueChange = { grades = it },
                        label = "Current Grades",
                        placeholder = if (gradeFormat == "raw") "85,92,78,88" else "1.25,1.00,1.75,1.50",
                        leadingIcon = Icons.Default.Grade,
                        helperText = "Enter grades separated by commas",
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )

                    EnhancedRadioGroup(
                        title = "Grade Format",
                        options = listOf(
                            RadioOption(
                                value = "raw",
                                label = "Raw (0-100)",
                                description = "Traditional percentage-based grading"
                            ),
                            RadioOption(
                                value = "transmuted",
                                label = "Transmuted (1.00-5.00)",
                                description = "Mapua MCL's grading system where 1.00 is highest"
                            )
                        ),
                        selectedOption = gradeFormat,
                        onSelectionChange = { gradeFormat = it }
                    )

                    EnhancedTextField(
                        value = units,
                        onValueChange = { units = it },
                        label = "Course Units",
                        placeholder = "3,4,3,3",
                        leadingIcon = Icons.Default.School,
                        helperText = "Enter credit hours for each course",
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        )
                    )

                    EnhancedTextField(
                        value = pastGrades,
                        onValueChange = { pastGrades = it },
                        label = "Historical Grades",
                        placeholder = "80,85,75,82;83,88,77,85",
                        leadingIcon = Icons.Default.History,
                        helperText = "Enter previous term grades (semicolon-separated terms)",
                        minLines = 3,
                        maxLines = 5
                    )
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(Spacing.medium))
        }
        
        // Action button with enhanced spring animation
        item {
            StaggeredListAnimation(
                visible = isVisible,
                itemIndex = 1,
                staggerDelayMs = 150
            ) {
                ModernButton(
                    text = "Analyze Grades",
                    onClick = {
                        val request = GradeAnalysisRequest(
                            studentId = studentID,
                            currentGrades = grades.split(",").mapNotNull { it.trim().toDoubleOrNull() },
                            courseUnits = units.split(",").mapNotNull { it.trim().toDoubleOrNull() },
                            historicalGrades = pastGrades.split(";").map { term ->
                                term.split(",").mapNotNull { it.trim().toDoubleOrNull() }
                            },
                            gradeFormat = gradeFormat
                        )
                        viewModel.analyzeGrades(request)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.medium),
                    enabled = !uiState.isLoading,
                    isLoading = uiState.isLoading,
                    icon = Icons.Default.Analytics
                )
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(Spacing.medium))
        }

        // Error display with dramatic entrance animation
        if (uiState.error != null) {
            item {
                SpringScaleTransition(
                    visible = true,
                    initialScale = 0.5f,
                    targetScale = 1.0f
                ) {
                    ErrorCard(
                        message = uiState.error!!,
                        onRetry = {},
                        modifier = Modifier.padding(horizontal = Spacing.medium)
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(Spacing.medium))
            }
        }

        // Loading state with advanced container transform
        if (uiState.isLoading) {
            item {
                ContainerTransform(visible = true) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.medium),
                        shape = RoundedCornerShape(CornerRadius.medium),
                        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.medium)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.large),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                        ) {
                            AdvancedLoadingAnimation(
                                modifier = Modifier.size(32.dp)
                            )
                            
                            Column {
                                Text(
                                    text = "Analyzing Grades",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Processing your academic data...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(Spacing.medium))
            }
        }

        // Results display with sophisticated orchestrated animations
        if (uiState.result != null) {
            item {
                StaggeredListAnimation(
                    visible = true,
                    itemIndex = 0,
                    staggerDelayMs = 100
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = Spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                    ) {
                        // Results header card with morphing animation
                        ContainerTransform(visible = true) {
                            AdvancedCard(
                                onClick = { /* No action for results header */ },
                                elevation = Elevation.large
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                                ) {
                                    Surface(
                                        modifier = Modifier.size(48.dp),
                                        shape = RoundedCornerShape(CornerRadius.medium),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Assessment,
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp),
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                    
                                    Column {
                                        Text(
                                            text = "Analysis Results",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Your academic performance breakdown",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Metrics cards with choreographed staggered animation
                        val result = uiState.result!!
                        
                        // Weighted Average with pulse effect
                        StaggeredListAnimation(
                            visible = true,
                            itemIndex = 1,
                            staggerDelayMs = 150
                        ) {
                            AdvancedCard(
                                onClick = { /* No action */ },
                                elevation = Elevation.medium
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Weighted Average",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = String.format(Locale.US, "%.2f", result.weightedAverage),
                                            style = MaterialTheme.typography.headlineMedium,
                                            color = when {
                                                result.weightedAverage >= 90 -> Color(0xFF4CAF50)
                                                result.weightedAverage >= 75 -> MaterialTheme.colorScheme.primary
                                                else -> MaterialTheme.colorScheme.error
                                            },
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    
                                    AnimatedStatusChip(
                                        text = when {
                                            result.weightedAverage >= 90 -> "Excellent"
                                            result.weightedAverage >= 75 -> "Good"
                                            else -> "Needs Improvement"
                                        },
                                        isPositive = result.weightedAverage >= 75
                                    )
                                }
                                
                                AdvancedProgressIndicator(
                                    progress = (result.weightedAverage / 100.0).coerceIn(0.0, 1.0).toFloat(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        
                        // Current TWA with bounce animation
                        StaggeredListAnimation(
                            visible = true,
                            itemIndex = 2,
                            staggerDelayMs = 200
                        ) {
                            AdvancedCard(
                                onClick = { /* No action */ },
                                elevation = Elevation.medium
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Current TWA",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = String.format(Locale.US, "%.2f", result.currentTwa),
                                            style = MaterialTheme.typography.headlineMedium,
                                            color = when {
                                                result.currentTwa <= 2.0 -> Color(0xFF4CAF50)
                                                result.currentTwa <= 3.0 -> MaterialTheme.colorScheme.primary
                                                else -> MaterialTheme.colorScheme.error
                                            },
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                        
                        // Additional metrics with slide-in effect
                        StaggeredListAnimation(
                            visible = true,
                            itemIndex = 3,
                            staggerDelayMs = 250
                        ) {
                            AdvancedCard(
                                onClick = { /* No action */ },
                                elevation = Elevation.small
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                                ) {
                                    Text(
                                        text = "Additional Metrics",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    
                                    MetricRow(
                                        label = "Grade Distribution",
                                        value = result.gradeDistribution,
                                        icon = Icons.Default.PieChart
                                    )

                                    MetricRow(
                                        label = "Performance Trend",
                                        value = result.performanceTrend,
                                        icon = when (result.performanceTrend.lowercase()) {
                                            "improving" -> Icons.AutoMirrored.Filled.TrendingUp
                                            "declining" -> Icons.AutoMirrored.Filled.TrendingDown
                                            else -> Icons.AutoMirrored.Filled.TrendingFlat
                                        },
                                        valueColor = when (result.performanceTrend.lowercase()) {
                                            "improving" -> Color(0xFF4CAF50)
                                            "declining" -> MaterialTheme.colorScheme.error
                                            else -> MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                }
                            }
                        }
                        
                        // Improvement suggestions with fade-in scale
                        StaggeredListAnimation(
                            visible = true,
                            itemIndex = 4,
                            staggerDelayMs = 300
                        ) {
                            AdvancedCard(
                                onClick = { /* No action */ },
                                elevation = Elevation.medium
                            ) {
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                                ) {
                                    Surface(
                                        modifier = Modifier.size(40.dp),
                                        shape = RoundedCornerShape(CornerRadius.small),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lightbulb,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp),
                                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        }
                                    }
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Improvement Suggestions",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(Spacing.small))
                                        Text(
                                            text = result.suggestions,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(Spacing.medium))
            }

            // Chart display with animation
            item {
                ContainerTransform(
                    visible = !uiState.isLoadingChart
                ) {
                    if (uiState.isLoadingChart) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Spacing.medium),
                            shape = RoundedCornerShape(CornerRadius.medium)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Spacing.large),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                            ) {
                                AdvancedLoadingAnimation(
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Generating chart...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    } else if (uiState.chartError != null) {
                        ErrorCard(
                            message = uiState.chartError!!,
                            onRetry = {
                                if (studentID.isNotBlank()) {
                                    val request = GradeAnalysisRequest(
                                        studentId = studentID,
                                        currentGrades = grades.split(",").mapNotNull { it.trim().toDoubleOrNull() },
                                        courseUnits = units.split(",").mapNotNull { it.trim().toDoubleOrNull() },
                                        historicalGrades = pastGrades.split(";").map { term ->
                                            term.split(",").mapNotNull { it.trim().toDoubleOrNull() }
                                        },
                                        gradeFormat = gradeFormat
                                    )
                                    viewModel.analyzeGrades(request)
                                }
                            },
                            modifier = Modifier.padding(horizontal = Spacing.medium)
                        )
                    } else {
                        ChartDisplay(
                            chartResponse = uiState.chartResponse,
                            isLoading = false,
                            error = null,
                            onRetry = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Spacing.medium)
                        )
                    }
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(Spacing.extraLarge))
        }
    }
}