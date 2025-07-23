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
        delay(100)
        contentVisible = true
    }

    OneUILayout(
        title = "Course Comparison",
        subtitle = "Compare performance across different courses",
        icon = Icons.AutoMirrored.Filled.CompareArrows,
        scrollState = scrollState,
        headerContent = {
            Spacer(modifier = Modifier.height(Spacing.small))
            
            // Back navigation in header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    ) {
        item {
            Spacer(modifier = Modifier.height(Spacing.medium))
        }
        
        // Input form with staggered animations
        item {
            SlideInFromEdge(
                visible = contentVisible,
                edge = AnimationEdge.Bottom,
                delayMillis = 0
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
        
        // Action button with animation
        item {
            SlideInFromEdge(
                visible = contentVisible,
                edge = AnimationEdge.Bottom,
                delayMillis = 200
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

        // Error display with animation
        if (uiState.error != null) {
            item {
                SlideInFromEdge(
                    visible = true,
                    edge = AnimationEdge.Bottom,
                    delayMillis = 100
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

        // Results with enhanced animations
        uiState.result?.let { result ->
            item {
                SlideInFromEdge(
                    visible = true,
                    edge = AnimationEdge.Bottom,
                    delayMillis = 300
                ) {
                    ResultsCard(
                        title = "Comparison Results",
                        icon = Icons.AutoMirrored.Filled.CompareArrows,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.medium)
                    ) {
                        // Best performing course
                        Text(
                            text = "Best Performing Course",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(Spacing.small))
                        
                        Text(
                            text = "${result.bestCourse}: ${String.format(Locale.getDefault(), "%.2f", result.bestGrade)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(Spacing.medium))
                        
                        // Weakest performing course
                        Text(
                            text = "Needs Improvement",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(Spacing.small))
                        
                        Text(
                            text = "${result.weakestCourse}: ${String.format(Locale.getDefault(), "%.2f", result.weakestGrade)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(Spacing.medium))
                        
                        // Overall TWA
                        Text(
                            text = "Overall TWA",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(Spacing.small))
                        
                        Text(
                            text = String.format(Locale.getDefault(), "%.2f", result.overallTwa),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(Spacing.medium))
                        
                        // Performance variance
                        Text(
                            text = "Performance Variance",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(Spacing.small))
                        
                        Text(
                            text = String.format(Locale.getDefault(), "%.2f", result.performanceVariance),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(Spacing.medium))
                        
                        // Above average courses
                        if (result.coursesAboveAverage.isNotEmpty()) {
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
                            
                            Spacer(modifier = Modifier.height(Spacing.medium))
                        }
                        
                        // Below average courses
                        if (result.coursesBelowAverage.isNotEmpty()) {
                            Text(
                                text = "Below Average Courses",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
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
                            
                            Spacer(modifier = Modifier.height(Spacing.medium))
                        }
                        
                        // Recommendations
                        if (result.recommendations.isNotEmpty()) {
                            Text(
                                text = "Recommendations",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
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
        
        item {
            Spacer(modifier = Modifier.height(Spacing.extraLarge))
        }
    }
}