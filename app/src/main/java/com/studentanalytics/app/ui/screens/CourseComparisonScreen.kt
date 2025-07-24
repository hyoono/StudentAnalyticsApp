package com.studentanalytics.app.ui.screens

import java.util.Locale
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
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
import com.studentanalytics.app.data.models.CourseComparisonRequest
import com.studentanalytics.app.ui.viewmodels.CourseComparisonViewModel
import com.studentanalytics.app.ui.components.*
import com.studentanalytics.app.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseComparisonScreen(
    onBack: () -> Unit,
    viewModel: CourseComparisonViewModel = viewModel()
) {
    var studentId by remember { mutableStateOf("") }
    var courseNames by remember { mutableStateOf("") }
    var studentGrades by remember { mutableStateOf("") }
    var classAverages by remember { mutableStateOf("") }
    var creditHours by remember { mutableStateOf("") }
    var gradeFormat by remember { mutableStateOf("raw") }
    var contentVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberLazyListState()
    
    LaunchedEffect(Unit) {
        delay(200) // Different timing for visual variety
        contentVisible = true
    }

    OneUILayout(
        title = "Course Comparison",
        subtitle = "Compare performance across different courses",
        icon = Icons.AutoMirrored.Filled.CompareArrows,
        scrollState = scrollState,
        headerContent = {
            Spacer(modifier = Modifier.height(Spacing.small))
            
            // Enhanced back navigation with slide animation
            SlideInFromEdge(
                visible = contentVisible,
                edge = AnimationEdge.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = onBack,
                        modifier = Modifier
                            .size(48.dp)
                            .advancedPressAnimation(),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                        tonalElevation = 2.dp
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    ) {
        item {
            Spacer(modifier = Modifier.height(Spacing.medium))
        }
        
        // Input form with morphing entrance animation
        item {
            ContainerTransform(
                visible = contentVisible
            ) {
                AdvancedCard(
                    onClick = { /* No action for form card */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.medium),
                    elevation = Elevation.medium
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
                        label = "Credit Units",
                        placeholder = "3,4,3,3",
                        leadingIcon = Icons.Default.AccessTime,
                        helperText = "Enter credit units for each course",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
            }
        }
        
        // Action button with enhanced spring animation
        item {
            SpringScaleTransition(
                visible = contentVisible,
                modifier = Modifier.padding(top = Spacing.medium)
            ) {
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.medium),
                    enabled = !uiState.isLoading,
                    isLoading = uiState.isLoading,
                    icon = Icons.AutoMirrored.Filled.CompareArrows
                )
            }
        }

        // Error display with dramatic scale entrance
        if (uiState.error != null) {
            item {
                SpringScaleTransition(
                    visible = true,
                    initialScale = 0.7f,
                    targetScale = 1.0f
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

        // Results with sophisticated choreographed animations
        uiState.result?.let { result ->
            item {
                ContainerTransform(
                    visible = true
                ) {
                    ResultsCard(
                        title = "Comparison Results",
                        icon = Icons.AutoMirrored.Filled.CompareArrows,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.medium)
                    ) {
                        // Performance metrics with staggered reveal
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                        ) {
                            // Best performing course with pulse animation
                            StaggeredListAnimation(
                                visible = true,
                                itemIndex = 0,
                                staggerDelayMs = 100
                            ) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(CornerRadius.small),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(Spacing.medium)
                                    ) {
                                        Text(
                                            text = "Best Performing Course",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        
                                        Spacer(modifier = Modifier.height(Spacing.small))
                                        
                                        Text(
                                            text = "${result.bestCourse}: ${String.format(Locale.getDefault(), "%.2f", result.bestGrade)}",
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            
                            // Improvement area with attention-grabbing animation
                            StaggeredListAnimation(
                                visible = true,
                                itemIndex = 1,
                                staggerDelayMs = 150
                            ) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(CornerRadius.small),
                                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(Spacing.medium)
                                    ) {
                                        Text(
                                            text = "Needs Improvement",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        
                                        Spacer(modifier = Modifier.height(Spacing.small))
                                        
                                        Text(
                                            text = "${result.weakestCourse}: ${String.format(Locale.getDefault(), "%.2f", result.weakestGrade)}",
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            
                            // Overall metrics with sequential reveal
                            StaggeredListAnimation(
                                visible = true,
                                itemIndex = 2,
                                staggerDelayMs = 200
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(Spacing.small)
                                ) {
                                    MetricRow(
                                        label = "Overall TWA",
                                        value = String.format(Locale.getDefault(), "%.2f", result.overallTwa),
                                        icon = Icons.Default.Grade,
                                        valueColor = MaterialTheme.colorScheme.primary
                                    )
                                    
                                    MetricRow(
                                        label = "Performance Variance",
                                        value = String.format(Locale.getDefault(), "%.2f", result.performanceVariance),
                                        icon = Icons.Default.Analytics,
                                        valueColor = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                            
                            // Course performance lists with cascading animation
                            if (result.coursesAboveAverage.isNotEmpty()) {
                                StaggeredListAnimation(
                                    visible = true,
                                    itemIndex = 3,
                                    staggerDelayMs = 250
                                ) {
                                    Column {
                                        Text(
                                            text = "Above Average Courses",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        
                                        Spacer(modifier = Modifier.height(Spacing.small))
                                        
                                        result.coursesAboveAverage.forEach { course ->
                                            Text(
                                                text = "• $course",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(start = Spacing.medium)
                                            )
                                        }
                                    }
                                }
                            }
                            
                            if (result.coursesBelowAverage.isNotEmpty()) {
                                StaggeredListAnimation(
                                    visible = true,
                                    itemIndex = 4,
                                    staggerDelayMs = 300
                                ) {
                                    Column {
                                        Text(
                                            text = "Below Average Courses",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        
                                        Spacer(modifier = Modifier.height(Spacing.small))
                                        
                                        result.coursesBelowAverage.forEach { course ->
                                            Text(
                                                text = "• $course",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(start = Spacing.medium)
                                            )
                                        }
                                    }
                                }
                            }
                            
                            // Recommendations with emphasis animation
                            if (result.recommendations.isNotEmpty()) {
                                StaggeredListAnimation(
                                    visible = true,
                                    itemIndex = 5,
                                    staggerDelayMs = 350
                                ) {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(CornerRadius.small),
                                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(Spacing.medium)
                                        ) {
                                            Text(
                                                text = "Recommendations",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.secondary
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
        
        item {
            Spacer(modifier = Modifier.height(Spacing.extraLarge))
        }
    }
}