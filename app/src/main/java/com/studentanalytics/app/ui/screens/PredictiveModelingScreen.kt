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
import com.studentanalytics.app.data.models.PredictiveModelingRequest
import com.studentanalytics.app.ui.viewmodels.PredictiveModelingViewModel
import com.studentanalytics.app.ui.components.ChartDisplay

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
            placeholder = { 
                if (gradeFormat == "raw") Text("85,87,82,89,91") 
                else Text("1.25,1.00,1.75,1.50,1.00") 
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
            value = attendanceRate,
            onValueChange = { attendanceRate = it },
            label = { Text("Attendance Rate (%)") },
            placeholder = { Text("95.50") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = courseHours,
            onValueChange = { courseHours = it },
            label = { Text("Course Hours/Week") },
            placeholder = { Text("40.00") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = creditUnits,
            onValueChange = { creditUnits = it },
            label = { Text("Credit Units") },
            placeholder = { Text("3") },
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
                    courseHours = courseHours.toDoubleOrNull() ?: 0.0,
                    creditUnits = creditUnits.toIntOrNull() ?: 0,
                    gradeFormat = gradeFormat
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
            
            // Add TWA progress chart automatically
            Spacer(modifier = Modifier.height(16.dp))
            
            ChartDisplay(
                chartResponse = uiState.chartResponse,
                isLoading = uiState.isLoadingChart,
                error = uiState.chartError,
                onRetry = { 
                    // Retry chart generation with the last successful parameters
                    if (uiState.result != null && studentId.isNotBlank()) {
                        viewModel.generatePrediction(
                            PredictiveModelingRequest(
                                studentId = studentId,
                                historicalGrades = historicalGrades.split(",").mapNotNull { it.trim().toDoubleOrNull() },
                                attendanceRate = attendanceRate.toDoubleOrNull() ?: 0.0,
                                courseHours = courseHours.toDoubleOrNull() ?: 0.0,
                                creditUnits = creditUnits.toIntOrNull() ?: 0,
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