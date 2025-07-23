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
    var yearLevel by remember { mutableStateOf("") }
    var deansListStatus by remember { mutableStateOf("") }
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

                    // Dropdown for Year Level
                    var yearLevelExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = yearLevelExpanded,
                        onExpandedChange = { yearLevelExpanded = !yearLevelExpanded }
                    ) {
                        OutlinedTextField(
                            value = when (yearLevel) {
                                "1" -> "1st Year"
                                "2" -> "2nd Year" 
                                "3" -> "3rd Year"
                                "4" -> "4th Year"
                                "5" -> "5th Year"
                                else -> ""
                            },
                            onValueChange = { },
                            label = { Text("Year Level") },
                            placeholder = { Text("Select year level") },
                            leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearLevelExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = yearLevelExpanded,
                            onDismissRequest = { yearLevelExpanded = false }
                        ) {
                            listOf("1st Year" to "1", "2nd Year" to "2", "3rd Year" to "3", "4th Year" to "4", "5th Year" to "5").forEach { (display, value) ->
                                DropdownMenuItem(
                                    text = { Text(display) },
                                    onClick = {
                                        yearLevel = value
                                        yearLevelExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Dropdown for Dean's List Status
                    var deansListExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = deansListExpanded,
                        onExpandedChange = { deansListExpanded = !deansListExpanded }
                    ) {
                        OutlinedTextField(
                            value = when (deansListStatus) {
                                "top_spot" -> "Top Spot Dean's Lister"
                                "regular" -> "Regular Dean's Lister"
                                "none" -> "Not on Dean's List"
                                else -> ""
                            },
                            onValueChange = { },
                            label = { Text("Dean's List Status") },
                            placeholder = { Text("Select Dean's List status") },
                            leadingIcon = { Icon(Icons.Default.EmojiEvents, contentDescription = null) },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deansListExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = deansListExpanded,
                            onDismissRequest = { deansListExpanded = false }
                        ) {
                            listOf(
                                "Top Spot Dean's Lister" to "top_spot",
                                "Regular Dean's Lister" to "regular", 
                                "Not on Dean's List" to "none"
                            ).forEach { (display, value) ->
                                DropdownMenuItem(
                                    text = { Text(display) },
                                    onClick = {
                                        deansListStatus = value
                                        deansListExpanded = false
                                    }
                                )
                            }
                        }
                    }
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
                        completedUnits = completedUnits.toIntOrNull() ?: 0,
                        yearLevel = yearLevel.ifBlank { null },
                        deansListStatus = deansListStatus.ifBlank { null }
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
                                    containerColor = when (result.eligibilityStatus.lowercase()) {
                                        "not eligible" -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                                        "eligible" -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                                        "conditional" -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.1f)
                                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                                    }
                                ),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(CornerRadius.small)
                            ) {
                                Row(
                                    modifier = Modifier.padding(Spacing.medium),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                                ) {
                                    Icon(
                                        imageVector = when (result.eligibilityStatus.lowercase()) {
                                            "not eligible" -> Icons.Default.Cancel
                                            "eligible" -> Icons.Default.School
                                            "conditional" -> Icons.Default.HourglassEmpty
                                            else -> Icons.Default.Help
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(Dimensions.iconLarge),
                                        tint = when (result.eligibilityStatus.lowercase()) {
                                            "not eligible" -> MaterialTheme.colorScheme.error
                                            "eligible" -> MaterialTheme.colorScheme.primary
                                            "conditional" -> MaterialTheme.colorScheme.tertiary
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = result.eligibilityStatus.replaceFirstChar { it.uppercase() },
                                            style = MaterialTheme.typography.titleLarge,
                                            color = when (result.eligibilityStatus.lowercase()) {
                                                "not eligible" -> MaterialTheme.colorScheme.error
                                                "eligible" -> MaterialTheme.colorScheme.primary
                                                "conditional" -> MaterialTheme.colorScheme.tertiary
                                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                                            },
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = when (result.eligibilityStatus.lowercase()) {
                                                "eligible" -> "Qualifies for academic scholarship"
                                                "conditional" -> "May qualify with additional requirements"
                                                "not eligible" -> "Does not meet scholarship criteria"
                                                else -> "Eligibility status unclear"
                                            },
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
                                label = "Term Weighted Average",
                                value = String.format(Locale.US, "%.2f", result.twa),
                                icon = Icons.Default.MenuBook,
                                valueColor = when {
                                    result.twa <= 1.25 -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                    result.twa <= 1.50 -> MaterialTheme.colorScheme.primary
                                    result.twa <= 2.00 -> androidx.compose.ui.graphics.Color(0xFFFF9800)
                                    else -> MaterialTheme.colorScheme.error
                                },
                                badge = when {
                                    result.twa <= 1.25 -> "Excellent"
                                    result.twa <= 1.50 -> "Very Good"
                                    result.twa <= 2.00 -> "Good"
                                    else -> "Needs Improvement"
                                }
                            )

                            // Additional metrics
                            result.yearLevel?.let { yearLevel ->
                                val suffix = when(yearLevel) { 
                                    "1" -> "st"
                                    "2" -> "nd" 
                                    "3" -> "rd"
                                    else -> "th"
                                }
                                MetricRow(
                                    label = "Year Level",
                                    value = "${yearLevel}${suffix} Year",
                                    icon = Icons.Default.School,
                                    valueColor = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            result.deansListStatus?.let { status ->
                                MetricRow(
                                    label = "Dean's List Status",
                                    value = status,
                                    icon = Icons.Default.EmojiEvents,
                                    valueColor = when (status.lowercase()) {
                                        "top spot dean's lister" -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                        "regular dean's lister" -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.onSurface
                                    },
                                    badge = when (status.lowercase()) {
                                        "top spot dean's lister" -> "Top Performer"
                                        "regular dean's lister" -> "High Achiever"
                                        else -> null
                                    }
                                )
                            }

                            MetricRow(
                                label = "Current Units",
                                value = "${result.currentUnits} units",
                                icon = Icons.Default.Assignment,
                                valueColor = MaterialTheme.colorScheme.onSurface
                            )

                            MetricRow(
                                label = "Completed Units",
                                value = "${result.completedUnits} units",
                                icon = Icons.Default.TaskAlt,
                                valueColor = MaterialTheme.colorScheme.onSurface
                            )

                            // Eligible scholarships
                            if (result.eligibleScholarships.isNotEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = androidx.compose.ui.graphics.Color(0xFF4CAF50).copy(alpha = 0.1f)
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
                                                imageVector = Icons.Default.EmojiEvents,
                                                contentDescription = null,
                                                modifier = Modifier.size(Dimensions.iconSmall),
                                                tint = androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                            )
                                            Text(
                                                text = "Eligible Scholarships",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        result.eligibleScholarships.forEach { scholarship ->
                                            Text(
                                                text = "• $scholarship",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(start = Spacing.medium)
                                            )
                                        }
                                    }
                                }
                            }

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

                            // Additional notes if available
                            result.notes?.let { notes ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
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
                                                imageVector = Icons.Default.Description,
                                                contentDescription = null,
                                                modifier = Modifier.size(Dimensions.iconSmall),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "Important Notes",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        Text(
                                            text = notes,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
}