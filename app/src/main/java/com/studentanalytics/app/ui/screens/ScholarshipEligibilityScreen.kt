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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studentanalytics.app.data.models.ScholarshipEligibilityRequest
import com.studentanalytics.app.ui.viewmodels.ScholarshipEligibilityViewModel
import com.studentanalytics.app.ui.components.*
import com.studentanalytics.app.ui.theme.*

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
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    modifier = Modifier.size(Dimensions.iconMedium),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                
                Text(
                    text = "Academic Scholarship",
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
                        text = "Eligibility Assessment",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Check eligibility for academic scholarships based on performance criteria",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        value = twa,
                        onValueChange = { twa = it },
                        label = "Term Weighted Average (TWA)",
                        placeholder = "1.25",
                        leadingIcon = Icons.Default.Grade,
                        helperText = "Enter TWA in 1.00-5.00 scale (1.00 is highest)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )

                    EnhancedTextField(
                        value = creditUnits,
                        onValueChange = { creditUnits = it },
                        label = "Credit Units (Current Term)",
                        placeholder = "21",
                        leadingIcon = Icons.Default.MenuBook,
                        helperText = "Enter total credit units for current term",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    EnhancedTextField(
                        value = completedUnits,
                        onValueChange = { completedUnits = it },
                        label = "Completed Units",
                        placeholder = "120",
                        leadingIcon = Icons.Default.TaskAlt,
                        helperText = "Enter total completed credit units",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            // Action button
            ModernButton(
                text = "Check Eligibility",
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
                enabled = !uiState.isLoading,
                isLoading = uiState.isLoading,
                icon = Icons.Default.Search
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
                LoadingCard(message = "Checking eligibility...")
            }

            // Results display
            AnimatedVisibility(
                visible = uiState.result != null,
                enter = fadeIn() + slideInVertically()
            ) {
                uiState.result?.let { result ->
                    ResultsCard(
                        title = "Eligibility Results",
                        icon = Icons.Default.AssignmentTurnedIn
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                        ) {
                            // Eligibility status card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (result.eligibilityStatus == "eligible") 
                                        androidx.compose.ui.graphics.Color(0xFF4CAF50).copy(alpha = 0.1f)
                                    else 
                                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                                ),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(CornerRadius.small)
                            ) {
                                Row(
                                    modifier = Modifier.padding(Spacing.medium),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                                ) {
                                    Icon(
                                        imageVector = if (result.eligibilityStatus == "eligible") Icons.Default.CheckCircle else Icons.Default.Cancel,
                                        contentDescription = null,
                                        modifier = Modifier.size(Dimensions.iconLarge),
                                        tint = if (result.eligibilityStatus == "eligible") 
                                            androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                        else 
                                            MaterialTheme.colorScheme.error
                                    )
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = result.eligibilityStatus.replaceFirstChar { it.uppercase() },
                                            style = MaterialTheme.typography.titleLarge,
                                            color = if (result.eligibilityStatus == "eligible") 
                                                androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                            else 
                                                MaterialTheme.colorScheme.error,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (result.eligibilityStatus == "eligible") 
                                                "Qualifies for academic scholarship"
                                            else 
                                                "Does not meet scholarship criteria",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }

                            MetricRow(
                                label = "Overall Score",
                                value = String.format(Locale.US, "%.1f", result.overallScore),
                                icon = Icons.Default.Star,
                                valueColor = when {
                                    result.overallScore >= 8.0 -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                    result.overallScore >= 6.0 -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.error
                                },
                                progress = (result.overallScore / 10.0),
                                badge = when {
                                    result.overallScore >= 8.0 -> "Excellent"
                                    result.overallScore >= 6.0 -> "Good"
                                    else -> "Below Threshold"
                                }
                            )

                            MetricRow(
                                label = "TWA Score",
                                value = String.format(Locale.US, "%.1f", result.twaScore),
                                icon = Icons.Default.MenuBook,
                                valueColor = MaterialTheme.colorScheme.primary
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
                                            imageVector = Icons.Default.Info,
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
                }
            }
        }
    }
}