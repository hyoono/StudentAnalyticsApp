package com.studentanalytics.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartsScreen(
    onNavigateToGradesTrend: () -> Unit,
    onNavigateToSubjectComparison: () -> Unit,
    onNavigateToGPAProgress: () -> Unit,
    onNavigateToPerformanceDistribution: () -> Unit,
    onNavigateToClassAverage: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Chart Analytics") },
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
                text = "Visual Analytics Dashboard",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Generate interactive charts and visualizations:",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnalyticsCard(
                title = "Grades Trend Chart",
                description = "Line chart showing grade progression over time for individual students",
                icon = Icons.Default.TrendingUp,
                onClick = onNavigateToGradesTrend
            )

            AnalyticsCard(
                title = "Subject Comparison Chart",
                description = "Bar chart comparing student performance across different subjects",
                icon = Icons.Default.BarChart,
                onClick = onNavigateToSubjectComparison
            )

            AnalyticsCard(
                title = "GPA Progress Chart",
                description = "Line chart showing GPA changes and trends over academic terms",
                icon = Icons.Default.ShowChart,
                onClick = onNavigateToGPAProgress
            )

            AnalyticsCard(
                title = "Performance Distribution Chart",
                description = "Pie chart showing grade distribution across an entire class",
                icon = Icons.Default.PieChart,
                onClick = onNavigateToPerformanceDistribution
            )

            AnalyticsCard(
                title = "Class Average Chart",
                description = "Bar chart comparing class averages by subject and performance metrics",
                icon = Icons.Default.Assessment,
                onClick = onNavigateToClassAverage
            )
        }
    }
}