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
import com.studentanalytics.app.data.models.ScholarshipEligibilityRequest
import com.studentanalytics.app.ui.viewmodels.ScholarshipEligibilityViewModel
import com.studentanalytics.app.ui.components.*
import com.studentanalytics.app.ui.theme.*
import kotlinx.coroutines.delay

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
    var contentVisible by remember { mutableStateOf(false) }
    
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberLazyListState()
    
    // Helper functions to map UI values to SOAP service values
    fun mapYearLevelToServiceValue(displayValue: String): String {
        return when (displayValue) {
            "1st Year" -> "1"
            "2nd Year" -> "2"
            "3rd Year" -> "3"
            "4th Year" -> "4"
            "5th Year" -> "5"
            else -> displayValue
        }
    }
    
    fun mapDeansListToServiceValue(displayValue: String): String {
        return when (displayValue) {
            "Top Spot" -> "top_spot"
            "Regular" -> "regular"
            "None" -> "none"
            else -> displayValue
        }
    }
    
    // Standardized animation timing
    LaunchedEffect(Unit) {
        delay(MotionTokens.ScreenEntranceDelay.toLong())
        contentVisible = true
    }

    OneUILayout(
        title = "Academic Scholarship Eligibility",
        subtitle = "Check eligibility based on academic performance criteria",
        icon = Icons.Default.School,
        onBackClick = onBack,
        scrollState = scrollState,
        headerContent = {
            Spacer(modifier = Modifier.height(Spacing.small))
        }
    ) {
        item {
            Spacer(modifier = Modifier.height(Spacing.medium))
        }
        
        // Input form with cascading wave animation
        item {
            SlideInFromEdge(
                visible = contentVisible,
                edge = AnimationEdge.Top,
                delayMillis = 150
            ) {
                AdvancedCard(
                    onClick = { /* No action for form card */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.medium),
                    elevation = Elevation.medium
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

                    Spacer(modifier = Modifier.height(Spacing.medium))

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
                        label = "TWA (1.00 - 2.00)",
                        placeholder = "1.75",
                        leadingIcon = Icons.Default.Grade,
                        helperText = "Enter Term Weighted Average (1.00 is highest)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )

                    EnhancedTextField(
                        value = creditUnits,
                        onValueChange = { creditUnits = it },
                        label = "Current Credit Units",
                        placeholder = "18",
                        leadingIcon = Icons.Default.CreditScore,
                        helperText = "Enter currently enrolled credit units",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    EnhancedTextField(
                        value = completedUnits,
                        onValueChange = { completedUnits = it },
                        label = "Completed Credit Units",
                        placeholder = "45",
                        leadingIcon = Icons.Default.CheckCircle,
                        helperText = "Enter total completed credit units",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    // Year Level Dropdown
                    Column {
                        Text(
                            text = "Year Level",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(Spacing.small))
                        
                        var yearLevelExpanded by remember { mutableStateOf(false) }
                        val yearLevelOptions = listOf("1st Year", "2nd Year", "3rd Year", "4th Year", "5th Year")
                        
                        ExposedDropdownMenuBox(
                            expanded = yearLevelExpanded,
                            onExpandedChange = { yearLevelExpanded = !yearLevelExpanded }
                        ) {
                            OutlinedTextField(
                                value = yearLevel,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Select Year Level") },
                                trailingIcon = { 
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearLevelExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            
                            ExposedDropdownMenu(
                                expanded = yearLevelExpanded,
                                onDismissRequest = { yearLevelExpanded = false }
                            ) {
                                yearLevelOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            yearLevel = option
                                            yearLevelExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.medium))

                    // Dean's List Status Dropdown
                    Column {
                        Text(
                            text = "Dean's List Status",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(Spacing.small))
                        
                        var deansListExpanded by remember { mutableStateOf(false) }
                        val deansListOptions = listOf("Top Spot", "Regular", "None")
                        
                        ExposedDropdownMenuBox(
                            expanded = deansListExpanded,
                            onExpandedChange = { deansListExpanded = !deansListExpanded }
                        ) {
                            OutlinedTextField(
                                value = deansListStatus,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Dean's List Status") },
                                trailingIcon = { 
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = deansListExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            
                            ExposedDropdownMenu(
                                expanded = deansListExpanded,
                                onDismissRequest = { deansListExpanded = false }
                            ) {
                                deansListOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            deansListStatus = option
                                            deansListExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Action button with dramatic entrance
        item {
            ContainerTransform(
                visible = contentVisible,
                modifier = Modifier.padding(top = Spacing.medium)
            ) {
                ModernButton(
                    text = "Check Eligibility",
                    onClick = {
                        val request = ScholarshipEligibilityRequest(
                            studentId = studentId,
                            twa = twa.toDoubleOrNull() ?: 0.0,
                            creditUnits = creditUnits.toIntOrNull() ?: 0,
                            completedUnits = completedUnits.toIntOrNull() ?: 0,
                            yearLevel = mapYearLevelToServiceValue(yearLevel),
                            deansListStatus = mapDeansListToServiceValue(deansListStatus)
                        )
                        viewModel.checkEligibility(request)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.medium),
                    enabled = !uiState.isLoading,
                    isLoading = uiState.isLoading,
                    icon = Icons.Default.School
                )
            }
        }

        // Error display with elastic bounce
        if (uiState.error != null) {
            item {
                SpringScaleTransition(
                    visible = true,
                    initialScale = 0.5f,
                    targetScale = 1.0f
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

        // Results with comprehensive choreographed animations
        uiState.result?.let { result ->
            item {
                ContainerTransform(
                    visible = true
                ) {
                    ResultsCard(
                        title = "Eligibility Results",
                        icon = Icons.Default.School,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.medium)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                        ) {
                            // Eligibility status with prominent display
                            StaggeredListAnimation(
                                visible = true,
                                itemIndex = 0,
                                staggerDelayMs = 100
                            ) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(CornerRadius.medium),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(Spacing.large),
                                        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.School,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Eligibility Status",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            
                                            Text(
                                                text = result.eligibilityStatus,
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                            
                            // Performance metrics with sequential reveal
                            StaggeredListAnimation(
                                visible = true,
                                itemIndex = 1,
                                staggerDelayMs = 150
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(Spacing.small)
                                ) {
                                    MetricRow(
                                        label = "Overall Score",
                                        value = String.format(Locale.getDefault(), "%.2f", result.overallScore),
                                        icon = Icons.Default.Calculate,
                                        valueColor = MaterialTheme.colorScheme.secondary
                                    )
                                    
                                    MetricRow(
                                        label = "TWA",
                                        value = String.format(Locale.getDefault(), "%.2f", result.twa),
                                        icon = Icons.Default.Grade,
                                        valueColor = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                            
                            // Academic load information
                            StaggeredListAnimation(
                                visible = true,
                                itemIndex = 2,
                                staggerDelayMs = 200
                            ) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(CornerRadius.small),
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(Spacing.medium),
                                        verticalArrangement = Arrangement.spacedBy(Spacing.small)
                                    ) {
                                        Text(
                                            text = "Academic Load",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        
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
                                    }
                                }
                            }
                            
                            // Eligible scholarships with emphasis
                            if (result.eligibleScholarships.isNotEmpty()) {
                                StaggeredListAnimation(
                                    visible = true,
                                    itemIndex = 3,
                                    staggerDelayMs = 250
                                ) {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(CornerRadius.medium),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(Spacing.medium)
                                        ) {
                                            Text(
                                                text = "Eligible Scholarships",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            
                                            Spacer(modifier = Modifier.height(Spacing.small))
                                            
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
                            }
                            
                            // Recommendations with final emphasis
                            if (result.recommendations.isNotEmpty()) {
                                StaggeredListAnimation(
                                    visible = true,
                                    itemIndex = 4,
                                    staggerDelayMs = 300
                                ) {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(CornerRadius.small),
                                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(Spacing.medium)
                                        ) {
                                            Text(
                                                text = "Recommendations",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.tertiary
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
                            
                            // Additional notes if available
                            result.notes?.let { notes ->
                                StaggeredListAnimation(
                                    visible = true,
                                    itemIndex = 5,
                                    staggerDelayMs = 350
                                ) {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(CornerRadius.small),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(Spacing.medium)
                                        ) {
                                            Text(
                                                text = "Additional Notes",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            
                                            Spacer(modifier = Modifier.height(Spacing.small))
                                            
                                            Text(
                                                text = notes,
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
        
        item {
            Spacer(modifier = Modifier.height(Spacing.extraLarge))
        }
    }
}