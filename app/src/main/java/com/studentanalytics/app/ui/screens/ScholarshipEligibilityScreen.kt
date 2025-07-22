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
import com.studentanalytics.app.data.models.ScholarshipEligibilityRequest
import com.studentanalytics.app.ui.viewmodels.ScholarshipEligibilityViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScholarshipEligibilityScreen(
    onBack: () -> Unit,
    viewModel: ScholarshipEligibilityViewModel = viewModel()
) {
    var studentId by remember { mutableStateOf("") }
    var twa by remember { mutableStateOf("") }
    var creditUnits by remember { mutableStateOf("") }
    var completedUnits by remember { mutableStateOf("") }
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
                text = "Scholarship Eligibility",
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
            value = twa,
            onValueChange = { twa = it },
            label = { Text("TWA (1.00 - 5.00, where 1.00 is highest)") },
            placeholder = { Text("1.25") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = creditUnits,
            onValueChange = { creditUnits = it },
            label = { Text("Current Credit Units") },
            placeholder = { Text("18") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = completedUnits,
            onValueChange = { completedUnits = it },
            label = { Text("Completed Units") },
            placeholder = { Text("75") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val request = ScholarshipEligibilityRequest(
                    studentId = studentId,
                    twa = twa.toDoubleOrNull() ?: 0.0,
                    creditUnits = creditUnits.toIntOrNull() ?: 0,
                    completedUnits = completedUnits.toIntOrNull() ?: 0
                )
                viewModel.checkEligibility(request)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Check Eligibility")
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
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (result.eligibilityStatus) {
                        "Eligible" -> MaterialTheme.colorScheme.primaryContainer
                        "Conditional" -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.errorContainer
                    }
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Eligibility Results",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Status: ${result.eligibilityStatus}")
                    Text("Overall Score: ${String.format(Locale.US,"%.2f", result.overallScore)}/100")
                    Text("TWA Score: ${String.format(Locale.US,"%.2f", result.twaScore)}/70")
                    Text("Academic Load Score: ${String.format(Locale.US,"%.2f", result.extracurricularScore)}/20")

                    if (result.eligibleScholarships.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Eligible Scholarships: ${result.eligibleScholarships.joinToString(", ")}")
                    }

                    if (result.recommendations.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Recommendations: ${result.recommendations}")
                    }
                }
            }
        }
    }
}