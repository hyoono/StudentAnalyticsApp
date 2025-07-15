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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studentanalytics.app.data.models.GradeAnalysisRequest
import com.studentanalytics.app.ui.viewmodels.GradeAnalysisViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeAnalysisScreen(
    onBack: () -> Unit,
    viewModel: GradeAnalysisViewModel = viewModel()
) {
    var studentId by remember { mutableStateOf("") }
    var currentGrades by remember { mutableStateOf("") }
    var subjectWeights by remember { mutableStateOf("") }
    var historicalGrades by remember { mutableStateOf("") }

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
                text = "Grade Analysis",
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
            value = currentGrades,
            onValueChange = { currentGrades = it },
            label = { Text("Current Grades (comma-separated)") },
            placeholder = { Text("85,92,78,88") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = subjectWeights,
            onValueChange = { subjectWeights = it },
            label = { Text("Subject Weights (comma-separated)") },
            placeholder = { Text("0.3,0.25,0.25,0.2") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = historicalGrades,
            onValueChange = { historicalGrades = it },
            label = { Text("Historical Grades (semicolon-separated terms)") },
            placeholder = { Text("80,85,75,82;83,88,77,85") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val request = GradeAnalysisRequest(
                    studentId = studentId,
                    currentGrades = currentGrades.split(",").mapNotNull { it.trim().toDoubleOrNull() },
                    subjectWeights = subjectWeights.split(",").mapNotNull { it.trim().toDoubleOrNull() },
                    historicalGrades = historicalGrades.split(";").map { term ->
                        term.split(",").mapNotNull { it.trim().toDoubleOrNull() }
                    }
                )
                viewModel.analyzeGrades(request)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Analyze Grades")
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
                        text = "Analysis Results",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Weighted Average: ${String.format(Locale.US, "%.2f", result.weightedAverage)}")
                    Text("Current GPA: ${String.format(Locale.US,"%.2f", result.currentGpa)}")
                    Text("Grade Distribution: ${result.gradeDistribution}")
                    Text("Performance Trend: ${result.performanceTrend}")
                    Text("Improvement Suggestions: ${result.suggestions}")
                }
            }
        }
    }
}