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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studentanalytics.app.data.models.PredictiveModelingRequest
import com.studentanalytics.app.ui.viewmodels.PredictiveModelingViewModel
import com.studentanalytics.app.ui.components.*
import com.studentanalytics.app.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictiveModelingScreen(
    onBack: () -> Unit,
    viewModel: PredictiveModelingViewModel = viewModel()
) {
    var studentId by remember { mutableStateOf("") }
    var historicalGrades by remember { mutableStateOf("") }
    var attendanceRate by remember { mutableStateOf("") }
    var courseHours by remember { mutableStateOf("") }
    var creditUnits by remember { mutableStateOf("") }
    var gradeFormat by remember { mutableStateOf("raw") }
    var contentVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberLazyListState()
    
    // Standardized animation timing
    LaunchedEffect(Unit) {
        delay(MotionTokens.ScreenEntranceDelay.toLong())
        contentVisible = true
    }

    OneUILayout(
        title = "Predictive Performance Modeling",
        subtitle = "Forecast academic performance using statistical analysis",
        icon = Icons.Default.TrendingUp,
        onBackClick = onBack,
        scrollState = scrollState,
        headerContent = {
            Spacer(modifier = Modifier.height(Spacing.small))
        }
    ) {
        item {
            Spacer(modifier = Modifier.height(Spacing.medium))
        }
        
        // Input form with wave animation entrance
        item {
            SlideInFromEdge(
                visible = contentVisible,
                edge = AnimationEdge.End,
                delayMillis = 100
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
                        value = studentId,
                        onValueChange = { studentId = it },
                        label = "Student ID",
                        placeholder = "Enter student ID",
                        leadingIcon = Icons.Default.Person,
                        helperText = "Enter the unique identifier for the student"
                    )

                    EnhancedTextField(
                        value = historicalGrades,
                        onValueChange = { historicalGrades = it },
                        label = "Historical Grades",
                        placeholder = if (gradeFormat == "raw") "85,87,89,92,78" else "1.50,1.25,1.00,1.00,1.75",
                        leadingIcon = Icons.Default.History,
                        helperText = "Enter past grades separated by commas",
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
                        value = attendanceRate,
                        onValueChange = { attendanceRate = it },
                        label = "Attendance Rate (%)",
                        placeholder = "85.5",
                        leadingIcon = Icons.Default.EventAvailable,
                        helperText = "Enter attendance percentage (0-100)",
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        )
                    )

                    EnhancedTextField(
                        value = courseHours,
                        onValueChange = { courseHours = it },
                        label = "Course Hours/Week",
                        placeholder = "3.5",
                        leadingIcon = Icons.Default.Schedule,
                        helperText = "Enter weekly course hours",
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        )
                    )

                    EnhancedTextField(
                        value = creditUnits,
                        onValueChange = { creditUnits = it },
                        label = "Credit Units",
                        placeholder = "15",
                        leadingIcon = Icons.Default.School,
                        helperText = "Enter total credit units enrolled",
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                }
            }
        }
        
        // Action button with elastic bounce
        item {
            SpringScaleTransition(
                visible = contentVisible,
                initialScale = 0.8f,
                modifier = Modifier.padding(top = Spacing.medium)
            ) {
                ModernButton(
                    text = "Generate Prediction",
                    onClick = {
                        val request = PredictiveModelingRequest(
                            studentId = studentId,
                            historicalGrades = historicalGrades.split(",").mapNotNull { it.trim().toDoubleOrNull() },
                            attendanceRate = attendanceRate.toDoubleOrNull() ?: 0.0,
                            courseHours = courseHours.toDoubleOrNull() ?: 0.0,
                            creditUnits = creditUnits.toIntOrNull() ?: 0,
                            gradeFormat = gradeFormat
                        )
                        viewModel.generatePrediction(request)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.medium),
                    enabled = !uiState.isLoading,
                    isLoading = uiState.isLoading,
                    icon = Icons.Default.TrendingUp
                )
            }
        }

        // Error display with slide from top
        if (uiState.error != null) {
            item {
                SlideInFromEdge(
                    visible = true,
                    edge = AnimationEdge.Top
                ) {
                    ErrorCard(
                        message = uiState.error!!,
                        onRetry = { /* Implement retry logic */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.medium)
                    )
                }
            }
        }

        // Results with morphing reveal animation
        uiState.result?.let { result ->
            item {
                ContainerTransform(
                    visible = true
                ) {
                    ResultsCard(
                        title = "Prediction Results",
                        icon = Icons.Default.TrendingUp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.medium)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                        ) {
                            // Key prediction metrics with sequential reveal
                            StaggeredListAnimation(
                                visible = true,
                                itemIndex = 0,
                                staggerDelayMs = 100
                            ) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(CornerRadius.medium),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(Spacing.medium)
                                    ) {
                                        MetricRow(
                                            label = "Predicted Next Grade",
                                            value = String.format(Locale.getDefault(), "%.2f", result.predictedGrade),
                                            icon = Icons.Default.Star,
                                            valueColor = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                            
                            // Risk assessment with attention-grabbing animation
                            StaggeredListAnimation(
                                visible = true,
                                itemIndex = 1,
                                staggerDelayMs = 150
                            ) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(CornerRadius.medium),
                                    color = when (result.riskLevel.lowercase()) {
                                        "high" -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                                        "medium" -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                                        else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                                    }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(Spacing.medium)
                                    ) {
                                        MetricRow(
                                            label = "Risk Level",
                                            value = result.riskLevel,
                                            icon = when (result.riskLevel.lowercase()) {
                                                "low" -> Icons.Default.CheckCircle
                                                "medium" -> Icons.Default.Warning
                                                "high" -> Icons.Default.Error
                                                else -> Icons.Default.Info
                                            },
                                            valueColor = when (result.riskLevel.lowercase()) {
                                                "low" -> MaterialTheme.colorScheme.primary
                                                "medium" -> MaterialTheme.colorScheme.tertiary
                                                "high" -> MaterialTheme.colorScheme.error
                                                else -> MaterialTheme.colorScheme.secondary
                                            }
                                        )
                                    }
                                }
                            }
                            
                            // Confidence score with pulse effect
                            StaggeredListAnimation(
                                visible = true,
                                itemIndex = 2,
                                staggerDelayMs = 200
                            ) {
                                MetricRow(
                                    label = "Confidence Score",
                                    value = "${String.format(Locale.getDefault(), "%.1f", result.confidenceScore)}%",
                                    icon = Icons.Default.Psychology,
                                    valueColor = MaterialTheme.colorScheme.secondary
                                )
                            }
                            
                            // Trend analysis with sophisticated reveal
                            if (result.trendAnalysis.isNotEmpty()) {
                                StaggeredListAnimation(
                                    visible = true,
                                    itemIndex = 3,
                                    staggerDelayMs = 250
                                ) {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(CornerRadius.small),
                                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(Spacing.medium)
                                        ) {
                                            Text(
                                                text = "Trend Analysis",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                            
                                            Spacer(modifier = Modifier.height(Spacing.small))
                                            
                                            Text(
                                                text = result.trendAnalysis,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                            
                            // Recommendations with final emphasis
                            if (result.recommendations.isNotEmpty()) {
                                StaggeredListAnimation(
                                    visible = true,
                                    itemIndex = 4,
                                    staggerDelayMs = 300
                                ) {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(CornerRadius.small),
                                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(Spacing.medium)
                                        ) {
                                            Text(
                                                text = "Recommendations",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                            
                                            Spacer(modifier = Modifier.height(Spacing.small))
                                            
                                            Text(
                                                text = result.recommendations,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Chart display with morphing reveal animation
        if (uiState.chartResponse != null || uiState.isLoadingChart || uiState.chartError != null) {
            item {
                StaggeredListAnimation(
                    visible = true,
                    itemIndex = 5,
                    staggerDelayMs = 400
                ) {
                    ChartDisplay(
                        chartResponse = uiState.chartResponse,
                        isLoading = uiState.isLoadingChart,
                        error = uiState.chartError,
                        onRetry = {
                            // Retry chart generation with the last used request
                            uiState.result?.let {
                                val request = PredictiveModelingRequest(
                                    studentId = studentId,
                                    historicalGrades = historicalGrades.split(",").mapNotNull { it.trim().toDoubleOrNull() },
                                    attendanceRate = attendanceRate.toDoubleOrNull() ?: 0.0,
                                    courseHours = courseHours.toDoubleOrNull() ?: 0.0,
                                    creditUnits = creditUnits.toIntOrNull() ?: 0,
                                    gradeFormat = gradeFormat
                                )
                                viewModel.generatePrediction(request)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.medium)
                    )
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(Spacing.extraLarge))
        }
    }
}