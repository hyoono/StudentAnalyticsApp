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
    var gpa by remember { mutableStateOf("") }
    var extracurriculars by remember { mutableStateOf("") }
    var selectedIncomeLevel by remember { mutableStateOf("Middle") }
    var honors by remember { mutableStateOf("") }
    var communityServiceHours by remember { mutableStateOf("") }
    var leadershipPositions by remember { mutableStateOf("") }

    val incomeLevels = listOf("Low", "Middle", "High")
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
            value = gpa,
            onValueChange = { gpa = it },
            label = { Text("GPA (0.0 - 4.0)") },
            placeholder = { Text("3.75") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Income Level",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        incomeLevels.forEach { level ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (level == selectedIncomeLevel),
                        onClick = { selectedIncomeLevel = level },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (level == selectedIncomeLevel),
                    onClick = null
                )
                Text(
                    text = level,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = extracurriculars,
            onValueChange = { extracurriculars = it },
            label = { Text("Extracurricular Activities (comma-separated)") },
            placeholder = { Text("Basketball,Debate Club,Student Council") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = honors,
            onValueChange = { honors = it },
            label = { Text("Honors and Awards (comma-separated)") },
            placeholder = { Text("Dean's List,Academic Excellence Award") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = communityServiceHours,
            onValueChange = { communityServiceHours = it },
            label = { Text("Community Service Hours") },
            placeholder = { Text("120") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = leadershipPositions,
            onValueChange = { leadershipPositions = it },
            label = { Text("Leadership Positions (comma-separated)") },
            placeholder = { Text("Class President,Team Captain") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val request = ScholarshipEligibilityRequest(
                    studentId = studentId,
                    gpa = gpa.toDoubleOrNull() ?: 0.0,
                    extracurriculars = extracurriculars.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    incomeLevel = selectedIncomeLevel,
                    honors = honors.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    communityServiceHours = communityServiceHours.toIntOrNull() ?: 0,
                    leadershipPositions = leadershipPositions.split(",").map { it.trim() }.filter { it.isNotEmpty() }
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
                    Text("GPA Score: ${String.format(Locale.US,"%.2f", result.gpaScore)}/30")
                    Text("Extracurricular Score: ${String.format(Locale.US,"%.2f", result.extracurricularScore)}/25")
                    Text("Service Score: ${String.format(Locale.US,"%.2f", result.serviceScore)}/20")
                    Text("Leadership Score: ${String.format(Locale.US,"%.2f", result.leadershipScore)}/15")
                    Text("Need-based Bonus: ${String.format(Locale.US,"%.2f", result.needBasedBonus)}/10")

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