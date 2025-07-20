package com.studentanalytics.app.ui.screens

import java.util.Locale
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studentanalytics.app.data.models.CourseComparisonRequest
import com.studentanalytics.app.ui.viewmodels.CourseComparisonViewModel
import com.studentanalytics.app.ui.components.ChartDisplay

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

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Course Performance Comparison",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = studentId,
            onValueChange = { studentId = it },
            label = { Text("Student ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = courseNames,
            onValueChange = { courseNames = it },
            label = { Text("Course Names (comma-separated)") },
            placeholder = { Text("Mathematics,Physics,Chemistry,Biology") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Grade Format Selection
        Text(
            text = "Grade Format",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .selectable(
                        selected = (gradeFormat == "raw"),
                        onClick = { gradeFormat = "raw" },
                        role = Role.RadioButton
                    )
                    .weight(1f)
            ) {
                RadioButton(
                    selected = (gradeFormat == "raw"),
                    onClick = { gradeFormat = "raw" }
                )
                Text(
                    text = "Raw (0-100)",
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .selectable(
                        selected = (gradeFormat == "transmuted"),
                        onClick = { gradeFormat = "transmuted" },
                        role = Role.RadioButton
                    )
                    .weight(1f)
            ) {
                RadioButton(
                    selected = (gradeFormat == "transmuted"),
                    onClick = { gradeFormat = "transmuted" }
                )
                Text(
                    text = "Transmuted (1.00-5.00)",
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = studentGrades,
            onValueChange = { studentGrades = it },
            label = { Text("Student Grades (comma-separated)") },
            placeholder = { 
                if (gradeFormat == "raw") Text("85,92,78,88") 
                else Text("1.25,1.00,1.75,1.50") 
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = classAverages,
            onValueChange = { classAverages = it },
            label = { Text("Class Averages (comma-separated)") },
            placeholder = { Text("82,89,75,85") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = creditHours,
            onValueChange = { creditHours = it },
            label = { Text("Credit Hours (comma-separated)") },
            placeholder = { Text("3,4,3,3") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
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
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Compare Performance")
        }

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = "Error: ${uiState.error}",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        uiState.result?.let { result ->
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Comparison Results",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Best Performance: ${result.bestCourse} (${String.format(Locale.US,"%.2f", result.bestGrade)})")
                    Text("Weakest Performance: ${result.weakestCourse} (${String.format(Locale.US,"%.2f", result.weakestGrade)})")
                    Text("Overall TWA: ${String.format(Locale.US,"%.2f", result.overallTwa)}")
                    Text("Above Class Average: ${result.coursesAboveAverage.joinToString(", ")}")
                    Text("Below Class Average: ${result.coursesBelowAverage.joinToString(", ")}")
                    Text("Performance Variance: ${String.format(Locale.US,"%.2f", result.performanceVariance)}")
                    Text("Recommendations: ${result.recommendations}")
                }
            }
            
            // Add course comparison chart automatically
            Spacer(modifier = Modifier.height(16.dp))
            
            ChartDisplay(
                chartResponse = uiState.chartResponse,
                isLoading = uiState.isLoadingChart,
                error = uiState.chartError,
                onRetry = { 
                    // Retry chart generation with the last successful parameters
                    if (uiState.result != null && studentId.isNotBlank()) {
                        viewModel.compareCourses(
                            CourseComparisonRequest(
                                studentId = studentId,
                                courseNames = courseNames.split(",").map { it.trim() },
                                studentGrades = studentGrades.split(",").mapNotNull { it.trim().toDoubleOrNull() },
                                classAverages = classAverages.split(",").mapNotNull { it.trim().toDoubleOrNull() },
                                creditHours = creditHours.split(",").mapNotNull { it.trim().toIntOrNull() },
                                gradeFormat = gradeFormat
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}