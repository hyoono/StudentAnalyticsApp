package com.studentanalytics.app.data.models

// Request Models
data class GradeAnalysisRequest(
    val studentId: String,
    val currentGrades: List<Double>,
    val courseUnits: List<Double>,
    val historicalGrades: List<List<Double>>,
    val gradeFormat: String = "raw" // "raw" (0-100) or "transmuted" (1.00-5.00)
)

data class CourseComparisonRequest(
    val studentId: String,
    val courseNames: List<String>,
    val studentGrades: List<Double>,
    val classAverages: List<Double>,
    val creditHours: List<Int>,
    val gradeFormat: String = "raw" // "raw" (0-100) or "transmuted" (1.00-5.00)
)

data class PredictiveModelingRequest(
    val studentId: String,
    val historicalGrades: List<Double>,
    val attendanceRate: Double,
    val courseHours: Double, // lecture + laboratory hours
    val creditUnits: Int,
    val gradeFormat: String = "raw" // "raw" (0-100) or "transmuted" (1.00-5.00)
)

data class ScholarshipEligibilityRequest(
    val studentId: String,
    val twa: Double, // Term Weighted Average (1.00-5.00, where 1.00 is highest)
    val creditUnits: Int, // Current credit units enrolled
    val completedUnits: Int // Total completed units towards degree
)

// Response Models
data class GradeAnalysisResponse(
    val weightedAverage: Double,
    val currentTwa: Double,
    val gradeDistribution: String,
    val performanceTrend: String,
    val suggestions: String
)

data class CourseComparisonResponse(
    val bestCourse: String,
    val bestGrade: Double,
    val weakestCourse: String,
    val weakestGrade: Double,
    val overallTwa: Double,
    val coursesAboveAverage: List<String>,
    val coursesBelowAverage: List<String>,
    val performanceVariance: Double,
    val recommendations: String
)

data class PredictiveModelingResponse(
    val predictedGrade: Double,
    val riskLevel: String,
    val confidenceScore: Double,
    val trendAnalysis: String,
    val keyFactors: List<String>,
    val recommendations: String,
    val atRisk: Boolean
)

data class ScholarshipEligibilityResponse(
    val eligibilityStatus: String, // "eligible", "conditional", "not eligible"
    val overallScore: Double,
    val twaScore: Double,
    val extracurricularScore: Double,
    val eligibleScholarships: List<String>,
    val recommendations: String
)

// Chart Request Models
data class ChartRequest(
    val studentId: String? = null,
    val classId: String? = null,
    val width: Int = 800, // Safe default within backend constraints (400-1200)
    val height: Int = 600, // Safe default within backend constraints (300-800)
    // Course comparison specific fields
    val courseNames: List<String> = emptyList(),
    val studentGrades: List<Double> = emptyList(),
    val classAverages: List<Double> = emptyList(),
    val creditHours: List<Int> = emptyList(),
    val gradeFormat: String? = null
)

// Chart Response Model
data class ChartResponse(
    val success: Boolean,
    val chartType: String,
    val imageData: String, // Base64 encoded image
    val studentId: String? = null,
    val classId: String? = null,
    val dataPoints: Int? = null,
    val totalStudents: Int? = null,
    val courses: Int? = null,
    val terms: Int? = null,
    val error: String? = null
)