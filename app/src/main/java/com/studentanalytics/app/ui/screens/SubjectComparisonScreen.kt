package com.studentanalytics.app.ui.screens

import java.util.Locale
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studentanalytics.app.data.models.SubjectComparisonRequest
import com.studentanalytics.app.ui.viewmodels.SubjectComparisonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectComparisonScreen(
    onBack: () -> Unit,
    viewModel: SubjectComparisonViewModel = viewModel()
) {
    var studentId by remember { mutableStateOf("") }
    var subjectNames by remember { mutableStateOf("") }
    var subjectGrades by remember { mutableStateOf("") }
    var classAverages by remember { mutableStateOf("") }
    var creditHours by remember { mutableStateOf("") }

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
                text = "Subject Performance Comparison",
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
            value = subjectNames,
            onValueChange = { subjectNames = it },
            label = { Text("Subject Names (comma-separated)") },
            placeholder = { Text("Mathematics,Physics,Chemistry,Biology") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = subjectGrades,
            onValueChange = { subjectGrades = it },
            label = { Text("Subject Grades (comma-separated)") },
            placeholder = { Text("85,92,78,88") },
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
                val request = SubjectComparisonRequest(
                    studentId = studentId,
                    subjectNames = subjectNames.split(",").map { it.trim() },
                    subjectGrades = subjectGrades.split(",").mapNotNull { it.trim().toDoubleOrNull() },
                    classAverages = classAverages.split(",").mapNotNull { it.trim().toDoubleOrNull() },
                    creditHours = creditHours.split(",").mapNotNull { it.trim().toIntOrNull() }
                )
                viewModel.compareSubjects(request)
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

                    Text("Best Performance: ${result.bestSubject} (${String.format(Locale.US,"%.2f", result.bestGrade)})")
                    Text("Weakest Performance: ${result.weakestSubject} (${String.format(Locale.US,"%.2f", result.weakestGrade)})")
                    Text("Overall GPA: ${String.format(Locale.US,"%.2f", result.overallGpa)}")
                    Text("Above Class Average: ${result.subjectsAboveAverage.joinToString(", ")}")
                    Text("Below Class Average: ${result.subjectsBelowAverage.joinToString(", ")}")
                    Text("Performance Variance: ${String.format(Locale.US,"%.2f", result.performanceVariance)}")
                    Text("Recommendations: ${result.recommendations}")
                }
            }
        }
    }
}