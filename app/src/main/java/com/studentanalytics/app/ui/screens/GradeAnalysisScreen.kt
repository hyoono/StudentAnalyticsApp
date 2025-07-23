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
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studentanalytics.app.data.models.GradeAnalysisRequest
import com.studentanalytics.app.ui.viewmodels.GradeAnalysisViewModel
import com.studentanalytics.app.ui.components.*
import com.studentanalytics.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeAnalysisScreen(
    onBack: () -> Unit,
    viewModel: GradeAnalysisViewModel = viewModel()
) {
    var studentId by remember { mutableStateOf("") }
    var currentGrades by remember { mutableStateOf("") }
    var courseUnits by remember { mutableStateOf("") }
    var historicalGrades by remember { mutableStateOf("") }
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
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    modifier = Modifier.size(Dimensions.iconMedium),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                
                Text(
                    text = "Grade Analysis",
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
                        value = currentGrades,
                        onValueChange = { currentGrades = it },
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
                        value = courseUnits,
                        onValueChange = { courseUnits = it },
                        label = "Course Units",
                        placeholder = "3,4,3,3",
                        leadingIcon = Icons.Default.School,
                        helperText = "Enter credit hours for each course",
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        )
                    )

                    EnhancedTextField(
                        value = historicalGrades,
                        onValueChange = { historicalGrades = it },
                        label = "Historical Grades",
                        placeholder = "80,85,75,82;83,88,77,85",
                        leadingIcon = Icons.Default.History,
                        helperText = "Enter previous term grades (semicolon-separated terms)",
                        minLines = 3,
                        maxLines = 5
                    )
                }
            }

            // Action button
            ModernButton(
                text = "Analyze Grades",
                onClick = {
                    val request = GradeAnalysisRequest(
                        studentId = studentId,
                        currentGrades = currentGrades.split(",").mapNotNull { it.trim().toDoubleOrNull() },
                        courseUnits = courseUnits.split(",").mapNotNull { it.trim().toDoubleOrNull() },
                        historicalGrades = historicalGrades.split(";").map { term ->
                            term.split(",").mapNotNull { it.trim().toDoubleOrNull() }
                        },
                        gradeFormat = gradeFormat
                    )
                    viewModel.analyzeGrades(request)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                isLoading = uiState.isLoading,
                icon = Icons.Default.Analytics
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
                LoadingCard(message = "Analyzing grades...")
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
                            title = "Analysis Results",
                            icon = Icons.Default.Assessment
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                            ) {
                                MetricRow(
                                    label = "Weighted Average",
                                    value = String.format(Locale.US, "%.2f", result.weightedAverage),
                                    icon = Icons.Default.BarChart,
                                    valueColor = when {
                                        result.weightedAverage >= 90 -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                        result.weightedAverage >= 75 -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.error
                                    },
                                    progress = (result.weightedAverage / 100.0).coerceIn(0.0, 1.0),
                                    badge = when {
                                        result.weightedAverage >= 90 -> "Excellent"
                                        result.weightedAverage >= 75 -> "Good" 
                                        else -> "Needs Improvement"
                                    }
                                )

                                MetricRow(
                                    label = "Current TWA",
                                    value = String.format(Locale.US, "%.2f", result.currentTwa),
                                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                                    valueColor = when {
                                        result.currentTwa <= 2.0 -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                        result.currentTwa <= 3.0 -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.error
                                    }
                                )

                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                    thickness = 1.dp
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
                                        "improving" -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                        "declining" -> MaterialTheme.colorScheme.error
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
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
                                                text = "Improvement Suggestions",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        Text(
                                            text = result.suggestions,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
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
                                        val request = GradeAnalysisRequest(
                                            studentId = studentId,
                                            currentGrades = currentGrades.split(",").mapNotNull { it.trim().toDoubleOrNull() },
                                            courseUnits = courseUnits.split(",").mapNotNull { it.trim().toDoubleOrNull() },
                                            historicalGrades = historicalGrades.split(";").map { term ->
                                                term.split(",").mapNotNull { it.trim().toDoubleOrNull() }
                                            },
                                            gradeFormat = gradeFormat
                                        )
                                        viewModel.analyzeGrades(request)
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