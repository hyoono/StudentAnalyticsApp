package com.studentanalytics.app.ui.screens

import java.util.Locale
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studentanalytics.app.data.models.CourseComparisonRequest
import com.studentanalytics.app.ui.viewmodels.CourseComparisonViewModel
import com.studentanalytics.app.ui.components.*
import com.studentanalytics.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseComparisonScreen(
    onBack: () -> Unit,
    viewModel: CourseComparisonViewModel = viewModel()
) {
    var studentId by remember { mutableStateOf("2022161330") }
    var courseNames by remember { mutableStateOf("") }
    var studentGrades by remember { mutableStateOf("") }
    var classAverages by remember { mutableStateOf("") }
    var creditHours by remember { mutableStateOf("") }
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
                    imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                    contentDescription = null,
                    modifier = Modifier.size(Dimensions.iconMedium),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                
                Text(
                    text = "Course Comparison",
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
                        text = "Comparison Settings",
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
                        value = courseNames,
                        onValueChange = { courseNames = it },
                        label = "Course Names",
                        placeholder = "Mathematics,Physics,Chemistry,Biology",
                        leadingIcon = Icons.Default.MenuBook,
                        helperText = "Enter course names separated by commas"
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
                        value = studentGrades,
                        onValueChange = { studentGrades = it },
                        label = "Student Grades",
                        placeholder = if (gradeFormat == "raw") "85,92,78,88" else "1.25,1.00,1.75,1.50",
                        leadingIcon = Icons.Default.Grade,
                        helperText = "Enter grades for each course",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    EnhancedTextField(
                        value = classAverages,
                        onValueChange = { classAverages = it },
                        label = "Class Averages",
                        placeholder = if (gradeFormat == "raw") "82,89,75,85" else "1.50,1.25,1.75,1.50",
                        leadingIcon = Icons.Default.BarChart,
                        helperText = "Enter class average for each course",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    EnhancedTextField(
                        value = creditHours,
                        onValueChange = { creditHours = it },
                        label = "Credit Hours",
                        placeholder = "3,4,3,3",
                        leadingIcon = Icons.Default.AccessTime,
                        helperText = "Enter credit hours for each course",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
            }

            // Action button
            ModernButton(
                text = "Compare Performance",
                onClick = {
                    val request = CourseComparisonRequest(
                        studentId = studentId,
                        courseNames = courseNames.split(",").map { it.trim() },
                        studentGrades = studentGrades.split(",").mapNotNull { it.trim().toDoubleOrNull() },
                        classAverages = classAverages.split(",").mapNotNull { it.trim().toDoubleOrNull() },
                        creditHours = creditHours.split(",").mapNotNull { it.trim().toIntOrNull() },
                        gradeFormat = gradeFormat
                    )
                    viewModel.compareCourses(request)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                isLoading = uiState.isLoading,
                icon = Icons.AutoMirrored.Filled.CompareArrows
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
                LoadingCard(message = "Comparing performance...")
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
                            title = "Comparison Results",
                            icon = Icons.Default.Assessment
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                                ) {
                                    ProgressIndicatorCard(
                                        title = "Best Performance",
                                        progress = 0.85f,
                                        progressText = result.bestCourse.take(8),
                                        modifier = Modifier.weight(1f),
                                        color = androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                    )
                                    
                                    ProgressIndicatorCard(
                                        title = "Weakest Course",
                                        progress = 0.65f,
                                        progressText = result.weakestCourse.take(8),
                                        modifier = Modifier.weight(1f),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }

                                MetricRow(
                                    label = "Best Course",
                                    value = "${result.bestCourse} (${String.format(Locale.US, "%.2f", result.bestGrade)})",
                                    icon = Icons.Default.TrendingUp,
                                    valueColor = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                                    badge = "Top"
                                )

                                MetricRow(
                                    label = "Weakest Course", 
                                    value = "${result.weakestCourse} (${String.format(Locale.US, "%.2f", result.weakestGrade)})",
                                    icon = Icons.Default.TrendingDown,
                                    valueColor = MaterialTheme.colorScheme.error,
                                    badge = "Focus Area"
                                )

                                MetricRow(
                                    label = "Overall TWA",
                                    value = String.format(Locale.US, "%.2f", result.overallTwa),
                                    icon = Icons.Default.Calculate,
                                    valueColor = when {
                                        result.overallTwa <= 2.0 -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                        result.overallTwa <= 3.0 -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.error
                                    }
                                )

                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                    thickness = 1.dp
                                )

                                MetricRow(
                                    label = "Above Class Average",
                                    value = result.coursesAboveAverage.joinToString(", "),
                                    icon = Icons.Default.TrendingUp
                                )

                                MetricRow(
                                    label = "Below Class Average", 
                                    value = result.coursesBelowAverage.joinToString(", "),
                                    icon = Icons.Default.TrendingDown
                                )

                                MetricRow(
                                    label = "Performance Variance",
                                    value = String.format(Locale.US, "%.2f", result.performanceVariance),
                                    icon = Icons.Default.Analytics
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
                                        val request = CourseComparisonRequest(
                                            studentId = studentId,
                                            courseNames = courseNames.split(",").map { it.trim() },
                                            studentGrades = studentGrades.split(",").mapNotNull { it.trim().toDoubleOrNull() },
                                            classAverages = classAverages.split(",").mapNotNull { it.trim().toDoubleOrNull() },
                                            creditHours = creditHours.split(",").mapNotNull { it.trim().toIntOrNull() },
                                            gradeFormat = gradeFormat
                                        )
                                        viewModel.compareCourses(request)
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