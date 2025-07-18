package com.studentanalytics.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.studentanalytics.app.ui.screens.*
import com.studentanalytics.app.ui.screens.HomeScreen
import com.studentanalytics.app.ui.theme.StudentAnalyticsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudentAnalyticsTheme {
                StudentAnalyticsApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun StudentAnalyticsApp() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavigationGraph(
            navController = navController,
            modifier = Modifier
                .padding(innerPadding)
                .padding(top = 64.dp) // Maintains exact same positioning
        )
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToGradeAnalysis = { navController.navigate("grade_analysis") },
                onNavigateToCourseComparison = { navController.navigate("subject_comparison") },
                onNavigateToPredictiveModeling = { navController.navigate("predictive_modeling") },
                onNavigateToScholarshipEligibility = { navController.navigate("scholarship_eligibility") },
                onNavigateToCharts = { navController.navigate("charts") }
            )
        }
        composable("grade_analysis") {
            GradeAnalysisScreen(onBack = { navController.popBackStack() })
        }
        composable("subject_comparison") {
            CourseComparisonScreen(onBack = { navController.popBackStack() })
        }
        composable("predictive_modeling") {
            PredictiveModelingScreen(onBack = { navController.popBackStack() })
        }
        composable("scholarship_eligibility") {
            ScholarshipEligibilityScreen(onBack = { navController.popBackStack() })
        }
        composable("charts") {
            ChartsScreen(
                onNavigateToGradesTrend = { navController.navigate("grades_trend_chart") },
                onNavigateToCourseComparison = { navController.navigate("subject_comparison_chart") },
                onNavigateToTWAProgress = { navController.navigate("gpa_progress_chart") },
                onNavigateToPerformanceDistribution = { navController.navigate("performance_distribution_chart") },
                onNavigateToClassAverage = { navController.navigate("class_average_chart") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("grades_trend_chart") {
            GradesTrendChartScreen(onBack = { navController.popBackStack() })
        }
        composable("subject_comparison_chart") {
            CourseComparisonChartScreen(onBack = { navController.popBackStack() })
        }
        composable("gpa_progress_chart") {
            TWAProgressChartScreen(onBack = { navController.popBackStack() })
        }
        composable("performance_distribution_chart") {
            PerformanceDistributionChartScreen(onBack = { navController.popBackStack() })
        }
        composable("class_average_chart") {
            ClassAverageChartScreen(onBack = { navController.popBackStack() })
        }
    }
}