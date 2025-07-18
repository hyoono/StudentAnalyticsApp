package com.studentanalytics.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studentanalytics.app.data.models.ChartRequest
import com.studentanalytics.app.ui.components.ChartDisplay
import com.studentanalytics.app.ui.viewmodels.ChartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradesTrendChartScreen(
    onBack: () -> Unit,
    viewModel: ChartViewModel = viewModel()
) {
    var studentId by remember { mutableStateOf("") }
    var width by remember { mutableStateOf("800") }
    var height by remember { mutableStateOf("600") }
    
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Grades Trend Chart") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Generate Grades Trend Chart",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Text(
                text = "This chart shows grade progression over time for a specific student.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Input form
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Chart Parameters",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    OutlinedTextField(
                        value = studentId,
                        onValueChange = { studentId = it },
                        label = { Text("Student ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = width,
                            onValueChange = { width = it },
                            label = { Text("Width") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = height,
                            onValueChange = { height = it },
                            label = { Text("Height") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                    
                    Button(
                        onClick = {
                            val widthInt = width.toIntOrNull() ?: 800
                            val heightInt = height.toIntOrNull() ?: 600
                            
                            // Validate dimensions
                            val validatedWidth = widthInt.coerceIn(400, 1200)
                            val validatedHeight = heightInt.coerceIn(300, 800)
                            
                            viewModel.generateGradesTrendChart(
                                ChartRequest(
                                    studentId = studentId,
                                    width = validatedWidth,
                                    height = validatedHeight
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = studentId.isNotBlank() && !uiState.isLoading
                    ) {
                        Text("Generate Chart")
                    }
                    
                    if (width.toIntOrNull() != null && (width.toInt() < 400 || width.toInt() > 1200)) {
                        Text(
                            text = "Width must be between 400 and 1200 pixels",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    
                    if (height.toIntOrNull() != null && (height.toInt() < 300 || height.toInt() > 800)) {
                        Text(
                            text = "Height must be between 300 and 800 pixels",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Chart display
            ChartDisplay(
                chartResponse = uiState.result,
                isLoading = uiState.isLoading,
                error = uiState.error,
                onRetry = {
                    val widthInt = width.toIntOrNull() ?: 800
                    val heightInt = height.toIntOrNull() ?: 600
                    val validatedWidth = widthInt.coerceIn(400, 1200)
                    val validatedHeight = heightInt.coerceIn(300, 800)
                    
                    viewModel.generateGradesTrendChart(
                        ChartRequest(
                            studentId = studentId,
                            width = validatedWidth,
                            height = validatedHeight
                        )
                    )
                }
            )
        }
    }
}