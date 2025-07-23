package com.studentanalytics.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.studentanalytics.app.ui.components.EnhancedAnalyticsCard
import com.studentanalytics.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToGradeAnalysis: () -> Unit,
    onNavigateToCourseComparison: () -> Unit,
    onNavigateToPredictiveModeling: () -> Unit,
    onNavigateToScholarshipEligibility: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(800),
        label = "homeScreenFadeIn"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha)
                .padding(Spacing.medium)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacing.large)
        ) {
            // Hero section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.medium)
            ) {
                // App icon/logo area
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(CornerRadius.large),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Text(
                    text = "Student Performance Analytics",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Comprehensive analytics platform for academic performance tracking, insights, and predictions",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.medium)
                )
            }

            // Spacer for visual breathing room
            Spacer(modifier = Modifier.height(Spacing.medium))

            // Analytics tools section
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.medium)
            ) {
                Text(
                    text = "Analytics Tools",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = Spacing.small)
                )

                EnhancedAnalyticsCard(
                    title = "Grade Analysis",
                    description = "Comprehensive grade analysis with TWA calculation, performance trends, and improvement suggestions",
                    icon = Icons.Default.BarChart,
                    onClick = onNavigateToGradeAnalysis,
                    badge = "Popular"
                )

                EnhancedAnalyticsCard(
                    title = "Course Performance Comparison",
                    description = "Compare student performance across multiple courses with detailed statistical analysis",
                    icon = Icons.AutoMirrored.Filled.CompareArrows,
                    onClick = onNavigateToCourseComparison
                )

                EnhancedAnalyticsCard(
                    title = "Predictive Performance Modeling",
                    description = "Advanced predictive analytics to forecast academic performance and identify at-risk students",
                    icon = Icons.Default.Psychology,
                    onClick = onNavigateToPredictiveModeling,
                    badge = "AI Powered"
                )

                EnhancedAnalyticsCard(
                    title = "Academic Scholarship Eligibility",
                    description = "Evaluate scholarship eligibility based on comprehensive academic performance criteria",
                    icon = Icons.Default.School,
                    onClick = onNavigateToScholarshipEligibility
                )
            }

            // Bottom spacing
            Spacer(modifier = Modifier.height(Spacing.large))
        }
    }
}