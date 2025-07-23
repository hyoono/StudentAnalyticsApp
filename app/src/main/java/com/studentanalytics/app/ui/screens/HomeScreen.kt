package com.studentanalytics.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onNavigateToGradeAnalysis: () -> Unit,
    onNavigateToCourseComparison: () -> Unit,
    onNavigateToPredictiveModeling: () -> Unit,
    onNavigateToScholarshipEligibility: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Welcome to Student Performance Analytics",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Choose an analysis tool below:",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        AnalyticsCard(
            title = "Grade Analysis",
            description = "Analyze student grades, calculate averages, and view trends over time",
            icon = Icons.Default.BarChart,
            onClick = onNavigateToGradeAnalysis
        )

        AnalyticsCard(
            title = "Course Performance Comparison",
            description = "Compare student performance across different courses",
            icon = Icons.AutoMirrored.Filled.CompareArrows,
            onClick = onNavigateToCourseComparison
        )

        AnalyticsCard(
            title = "Predictive Performance Modeling",
            description = "Predict future performance and identify at-risk students",
            icon = Icons.Default.Psychology,
            onClick = onNavigateToPredictiveModeling
        )

        AnalyticsCard(
            title = "Academic Scholarship Eligibility",
            description = "Check student eligibility for academic scholarships based on performance criteria",
            icon = Icons.Default.School,
            onClick = onNavigateToScholarshipEligibility
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}