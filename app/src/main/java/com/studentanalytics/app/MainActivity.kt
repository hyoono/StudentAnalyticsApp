package com.studentanalytics.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.studentanalytics.app.ui.screens.*
import com.studentanalytics.app.ui.screens.HomeScreen
import com.studentanalytics.app.ui.theme.StudentAnalyticsTheme
import com.studentanalytics.app.ui.theme.Spacing

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
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "Student Analytics",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        NavigationGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

// Material 3 motion system transition specifications
object NavigationAnimations {
    private val motionDuration = 400
    private val motionEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    
    val enterTransition: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(
                durationMillis = motionDuration,
                easing = motionEasing
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = motionDuration / 2,
                delayMillis = motionDuration / 4,
                easing = LinearEasing
            )
        )
    }
    
    val exitTransition: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
        slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth / 3 },
            animationSpec = tween(
                durationMillis = motionDuration,
                easing = motionEasing
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = motionDuration / 2,
                easing = LinearEasing
            )
        )
    }
    
    val popEnterTransition: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth / 3 },
            animationSpec = tween(
                durationMillis = motionDuration,
                easing = motionEasing
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = motionDuration / 2,
                delayMillis = motionDuration / 4,
                easing = LinearEasing
            )
        )
    }
    
    val popExitTransition: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
        slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(
                durationMillis = motionDuration,
                easing = motionEasing
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = motionDuration / 2,
                easing = LinearEasing
            )
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
        modifier = modifier,
        enterTransition = NavigationAnimations.enterTransition,
        exitTransition = NavigationAnimations.exitTransition,
        popEnterTransition = NavigationAnimations.popEnterTransition,
        popExitTransition = NavigationAnimations.popExitTransition
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToGradeAnalysis = { navController.navigate("grade_analysis") },
                onNavigateToCourseComparison = { navController.navigate("subject_comparison") },
                onNavigateToPredictiveModeling = { navController.navigate("predictive_modeling") },
                onNavigateToScholarshipEligibility = { navController.navigate("scholarship_eligibility") }
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
    }
}