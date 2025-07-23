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
    
    LaunchedEffect(Unit) {
        delay(100)
        contentVisible = true
    }

    OneUILayout(
        title = "Academic Scholarship Eligibility",
        subtitle = "Check eligibility based on academic performance criteria",
        icon = Icons.Default.School,
        scrollState = scrollState,
        headerContent = {
            Spacer(modifier = Modifier.height(Spacing.small))
            
            // Back navigation in header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    ) {
        item {
            Spacer(modifier = Modifier.height(Spacing.medium))
        }
        
        // Input form with staggered animations
        item {
            SlideInFromEdge(
                visible = contentVisible,
                edge = AnimationEdge.Bottom,
                delayMillis = 0
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
                        val deansListOptions = listOf("Yes", "No")
                        
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
        
        // Action button with animation
        item {
            SlideInFromEdge(
                visible = contentVisible,
                edge = AnimationEdge.Bottom,
                delayMillis = 200
            ) {
                ModernButton(
                    text = "Check Eligibility",
                    onClick = {
                        val request = ScholarshipEligibilityRequest(
                            studentId = studentId,
                            twa = twa.toDoubleOrNull() ?: 0.0,
                            creditUnits = creditUnits.toIntOrNull() ?: 0,
                            completedUnits = completedUnits.toIntOrNull() ?: 0,
                            yearLevel = yearLevel,
                            deansListStatus = deansListStatus
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

        // Error display with animation
        if (uiState.error != null) {
            item {
                SlideInFromEdge(
                    visible = true,
                    edge = AnimationEdge.Bottom,
                    delayMillis = 100
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

        // Results with enhanced animations
        uiState.result?.let { result ->
            item {
                SlideInFromEdge(
                    visible = true,
                    edge = AnimationEdge.Bottom,
                    delayMillis = 300
                ) {
                    ResultsCard(
                        title = "Eligibility Results",
                        icon = Icons.Default.School,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.medium)
                    ) {
                        // Eligibility status with appropriate styling
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Eligibility Status",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                Text(
                                    text = result.eligibilityStatus,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(Spacing.medium))
                        
                        // Overall score metric
                        MetricRow(
                            label = "Overall Score",
                            value = String.format(Locale.getDefault(), "%.2f", result.overallScore),
                            icon = Icons.Default.Calculate,
                            valueColor = MaterialTheme.colorScheme.secondary
                        )
                        
                        // TWA metric
                        MetricRow(
                            label = "TWA",
                            value = String.format(Locale.getDefault(), "%.2f", result.twa),
                            icon = Icons.Default.Grade,
                            valueColor = MaterialTheme.colorScheme.secondary
                        )
                        
                        // Current units
                        MetricRow(
                            label = "Current Units",
                            value = "${result.currentUnits} units",
                            icon = Icons.Default.Assignment,
                            valueColor = MaterialTheme.colorScheme.secondary
                        )
                        
                        // Completed units
                        MetricRow(
                            label = "Completed Units",
                            value = "${result.completedUnits} units",
                            icon = Icons.Default.TaskAlt,
                            valueColor = MaterialTheme.colorScheme.secondary
                        )
                        
                        Spacer(modifier = Modifier.height(Spacing.medium))
                        
                        // Eligible scholarships
                        if (result.eligibleScholarships.isNotEmpty()) {
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
                            
                            Spacer(modifier = Modifier.height(Spacing.medium))
                        }
                        
                        // Recommendations
                        if (result.recommendations.isNotEmpty()) {
                            Text(
                                text = "Recommendations",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            
                            Spacer(modifier = Modifier.height(Spacing.small))
                            
                            Text(
                                text = result.recommendations,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Spacer(modifier = Modifier.height(Spacing.medium))
                        }
                        
                        // Notes if available
                        result.notes?.let { notes ->
                            Text(
                                text = "Additional Notes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
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
        
        item {
            Spacer(modifier = Modifier.height(Spacing.extraLarge))
        }
    }
}