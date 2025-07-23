package com.studentanalytics.app.ui.screens

import java.util.Locale
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studentanalytics.app.data.models.PredictiveModelingRequest
import com.studentanalytics.app.ui.viewmodels.PredictiveModelingViewModel
import com.studentanalytics.app.ui.components.*
import com.studentanalytics.app.ui.theme.*

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

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Modern header with back navigation
        Surface(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.medium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                IconButton(
                    onClick = onBack,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack, 
                        contentDescription = "Back",
                        modifier = Modifier.size(Dimensions.iconMedium)
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    modifier = Modifier.size(Dimensions.iconMedium),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                
                Text(
                    text = "Predictive Performance Modeling",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Column(
            modifier = Modifier.padding(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.large)
        ) {
            // Input form card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = Elevation.medium),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(CornerRadius.medium)
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.large),
                    verticalArrangement = Arrangement.spacedBy(Spacing.medium)
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
                        placeholder = if (gradeFormat == "raw") "85,87,82,89,91" else "1.25,1.00,1.75,1.50,1.00",
                        leadingIcon = Icons.Default.History,
                        helperText = "Enter previous grades separated by commas",
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
                        placeholder = "95.50",
                        leadingIcon = Icons.Default.CheckCircle,
                        helperText = "Enter attendance percentage (0-100)",
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        )
                    )

                    EnhancedTextField(
                        value = courseHours,
                        onValueChange = { courseHours = it },
                        label = "Course Hours/Week",
                        placeholder = "40.00",
                        leadingIcon = Icons.Default.Schedule,
                        helperText = "Total weekly course hours (lecture + laboratory)",
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        )
                    )

                    EnhancedTextField(
                        value = creditUnits,
                        onValueChange = { creditUnits = it },
                        label = "Credit Units",
                        placeholder = "3",
                        leadingIcon = Icons.Default.School,
                        helperText = "Credit units for the course",
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                }
            }

            // Action button
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
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                isLoading = uiState.isLoading,
                icon = Icons.Default.Psychology
            )

            // Error display
            if (uiState.error != null) {
                ErrorCard(
                    message = uiState.error!!,
                    onRetry = {}
                )
            }

            // Loading state
            if (uiState.isLoading) {
                LoadingCard(message = "Generating prediction...")
            }

            // Results display
            AnimatedVisibility(
                visible = uiState.result != null,
                enter = fadeIn() + slideInVertically()
            ) {
                uiState.result?.let { result ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                    ) {
                        ResultsCard(
                            title = "Prediction Results",
                            icon = Icons.Default.Psychology
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                            ) {
                                MetricRow(
                                    label = "Predicted Next Grade",
                                    value = String.format(Locale.US, "%.2f", result.predictedGrade),
                                    icon = Icons.Default.TrendingUp,
                                    valueColor = when {
                                        result.predictedGrade >= 90 || result.predictedGrade <= 1.5 -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                        result.predictedGrade >= 75 || result.predictedGrade <= 2.5 -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.error
                                    },
                                    progress = if (gradeFormat == "raw") {
                                        (result.predictedGrade / 100.0).coerceIn(0.0, 1.0)
                                    } else {
                                        ((5.0 - result.predictedGrade) / 4.0).coerceIn(0.0, 1.0)
                                    }
                                )

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
                                        "low" -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                        "medium" -> androidx.compose.ui.graphics.Color(0xFFFF9800)
                                        "high" -> MaterialTheme.colorScheme.error
                                        else -> MaterialTheme.colorScheme.onSurface
                                    },
                                    badge = if (result.atRisk) "At Risk" else null
                                )

                                MetricRow(
                                    label = "Confidence Score",
                                    value = "${String.format(Locale.US, "%.1f", result.confidenceScore)}%",
                                    icon = Icons.Default.Analytics,
                                    progress = (result.confidenceScore / 100.0).coerceIn(0.0, 1.0)
                                )

                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                    thickness = 1.dp
                                )

                                MetricRow(
                                    label = "Trend Analysis",
                                    value = result.trendAnalysis,
                                    icon = Icons.Default.TrendingUp
                                )

                                MetricRow(
                                    label = "Performance Factors",
                                    value = result.keyFactors.joinToString(", "),
                                    icon = Icons.Default.Category
                                )

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                    ),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(CornerRadius.small)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(Spacing.medium),
                                        verticalArrangement = Arrangement.spacedBy(Spacing.small)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lightbulb,
                                                contentDescription = null,
                                                modifier = Modifier.size(Dimensions.iconSmall),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "Recommendations",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        Text(
                                            text = result.recommendations,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                // At-risk warning card
                                if (result.atRisk) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                                        ),
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(CornerRadius.small)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(Spacing.medium),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = null,
                                                modifier = Modifier.size(Dimensions.iconMedium),
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                            Text(
                                                text = "Student identified as at-risk",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.error,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Chart display
                        if (uiState.isLoadingChart) {
                            LoadingCard(message = "Generating chart...")
                        } else if (uiState.chartError != null) {
                            ErrorCard(
                                message = uiState.chartError!!,
                                onRetry = {
                                    if (studentId.isNotBlank()) {
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
                                }
                            )
                        } else {
                            ChartDisplay(
                                chartResponse = uiState.chartResponse,
                                isLoading = false,
                                error = null,
                                onRetry = {},
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}