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
import com.studentanalytics.app.data.models.PredictiveModelingRequest
import com.studentanalytics.app.ui.viewmodels.PredictiveModelingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictiveModelingScreen(
    onBack: () -> Unit,
    viewModel: PredictiveModelingViewModel = viewModel()
) {
    var studentId by remember { mutableStateOf("") }
    var historicalGrades by remember { mutableStateOf("") }
    var attendanceRate by remember { mutableStateOf("") }
    var participationScore by remember { mutableStateOf("") }
    var studyHoursPerWeek by remember { mutableStateOf("") }
    var extracurricularHours by remember { mutableStateOf("") }

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
                text = "Predictive Performance Modeling",
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
            value = historicalGrades,
            onValueChange = { historicalGrades = it },
            label = { Text("Historical Grades (comma-separated)") },
            placeholder = { Text("85,87,82,89,91") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = attendanceRate,
            onValueChange = { attendanceRate = it },
            label = { Text("Attendance Rate (%)") },
            placeholder = { Text("95.5") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = participationScore,
            onValueChange = { participationScore = it },
            label = { Text("Participation Score (1-10)") },
            placeholder = { Text("8.5") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = studyHoursPerWeek,
            onValueChange = { studyHoursPerWeek = it },
            label = { Text("Study Hours Per Week") },
            placeholder = { Text("25") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = extracurricularHours,
            onValueChange = { extracurricularHours = it },
            label = { Text("Extracurricular Hours Per Week") },
            placeholder = { Text("5") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val request = PredictiveModelingRequest(
                    studentId = studentId,
                    historicalGrades = historicalGrades.split(",").mapNotNull { it.trim().toDoubleOrNull() },
                    attendanceRate = attendanceRate.toDoubleOrNull() ?: 0.0,
                    participationScore = participationScore.toDoubleOrNull() ?: 0.0,
                    studyHoursPerWeek = studyHoursPerWeek.toIntOrNull() ?: 0,
                    extracurricularHours = extracurricularHours.toIntOrNull() ?: 0
                )
                viewModel.generatePrediction(request)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Generate Prediction")
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
                        text = "Prediction Results",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Predicted Next Grade: ${String.format(Locale.US,"%.2f", result.predictedGrade)}")
                    Text("Risk Level: ${result.riskLevel}")
                    Text("Confidence Score: ${String.format(Locale.US,"%.2f", result.confidenceScore)}%")
                    Text("Trend Analysis: ${result.trendAnalysis}")
                    Text("Performance Factors: ${result.keyFactors.joinToString(", ")}")
                    Text("Recommendations: ${result.recommendations}")

                    if (result.atRisk) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Text(
                                text = "⚠️ Student identified as at-risk",
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}