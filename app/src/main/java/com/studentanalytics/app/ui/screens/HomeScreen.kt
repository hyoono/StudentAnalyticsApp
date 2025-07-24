package com.studentanalytics.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.studentanalytics.app.ui.components.*
import com.studentanalytics.app.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToGradeAnalysis: () -> Unit,
    onNavigateToCourseComparison: () -> Unit,
    onNavigateToPredictiveModeling: () -> Unit,
    onNavigateToScholarshipEligibility: () -> Unit
) {
    val scrollState = rememberLazyListState()
    var contentVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        contentVisible = true
    }
    
    OneUILayout(
        title = "Student Performance Analytics",
        subtitle = "Comprehensive academic analysis platform",
        icon = Icons.Default.Analytics,
        scrollState = scrollState,
        headerContent = {
            Spacer(modifier = Modifier.height(Spacing.medium))
            Text(
                text = "Analyze grades, compare courses, predict performance, and check scholarship eligibility",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) {
        item {
            Spacer(modifier = Modifier.height(Spacing.large))
        }
        
        // Analytics cards with staggered animation
        repeat(4) { index ->
            item {
                val cardData = when (index) {
                    0 -> Triple(
                        "Grade Analysis",
                        "Analyze your academic performance with detailed grade breakdown and improvement suggestions",
                        Icons.Default.Assessment
                    ) to onNavigateToGradeAnalysis
                    1 -> Triple(
                        "Course Comparison", 
                        "Compare performance across different courses to identify strengths and areas for improvement",
                        Icons.AutoMirrored.Filled.CompareArrows
                    ) to onNavigateToCourseComparison
                    2 -> Triple(
                        "Predictive Performance Modeling",
                        "Get predictions for future academic performance based on current trends and historical data",
                        Icons.Default.TrendingUp
                    ) to onNavigateToPredictiveModeling
                    else -> Triple(
                        "Academic Scholarship Eligibility",
                        "Check your eligibility for academic scholarships based on performance criteria",
                        Icons.Default.School
                    ) to onNavigateToScholarshipEligibility
                }
                
                SlideInFromEdge(
                    visible = contentVisible,
                    edge = AnimationEdge.Bottom,
                    delayMillis = index * 100
                ) {
                    AdvancedCard(
                        onClick = cardData.second,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.medium),
                        elevation = Elevation.medium
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                        ) {
                            // Modern icon container
                            Surface(
                                modifier = Modifier.size(64.dp),
                                shape = RoundedCornerShape(CornerRadius.medium),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                                shadowElevation = Elevation.small
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = cardData.first.third,
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            
                            // Text content
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(Spacing.small)
                            ) {
                                Text(
                                    text = cardData.first.first,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = cardData.first.second,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                                )
                            }
                            
                            // Arrow indicator
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
            
            // Add spacer between items except after the last one
            if (index < 3) {
                item {
                    Spacer(modifier = Modifier.height(Spacing.medium))
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(Spacing.extraLarge))
            
            // Footer section with fade in animation
            SlideInFromEdge(
                visible = contentVisible,
                edge = AnimationEdge.Bottom,
                delayMillis = 500
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.medium),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(CornerRadius.large)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.large),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                        
                        Text(
                            text = "Mapua Malayan Colleges Laguna",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        
                        Text(
                            text = "Empowering students through data-driven insights",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}