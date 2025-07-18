package com.studentanalytics.app.data.models

// Request Models
data class GradeAnalysisRequest(
    val studentId: String,
    val currentGrades: List<Double>,
    val subjectWeights: List<Double>,
    val historicalGrades: List<List<Double>>
)

data class SubjectComparisonRequest(
    val studentId: String,
    val subjectNames: List<String>,
    val subjectGrades: List<Double>,
    val classAverages: List<Double>,
    val creditHours: List<Int>
)

data class PredictiveModelingRequest(
    val studentId: String,
    val historicalGrades: List<Double>,
    val attendanceRate: Double,
    val participationScore: Double,
    val studyHoursPerWeek: Int,
    val extracurricularHours: Int
)

data class ScholarshipEligibilityRequest(
    val studentId: String,
    val gpa: Double,
    val extracurriculars: List<String>,
    val incomeLevel: String,
    val honors: List<String>,
    val communityServiceHours: Int,
    val leadershipPositions: List<String>
)

// Response Models
data class GradeAnalysisResponse(
    val weightedAverage: Double,
    val currentGpa: Double,
    val gradeDistribution: String,
    val performanceTrend: String,
    val suggestions: String
)

data class SubjectComparisonResponse(
    val bestSubject: String,
    val bestGrade: Double,
    val weakestSubject: String,
    val weakestGrade: Double,
    val overallGpa: Double,
    val subjectsAboveAverage: List<String>,
    val subjectsBelowAverage: List<String>,
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
    val eligibilityStatus: String,
    val overallScore: Double,
    val gpaScore: Double,
    val extracurricularScore: Double,
    val serviceScore: Double,
    val leadershipScore: Double,
    val needBasedBonus: Double,
    val eligibleScholarships: List<String>,
    val recommendations: String
)

// Chart Request Models
data class ChartRequest(
    val studentId: String? = null,
    val classId: String? = null,
    val width: Int = 800,
    val height: Int = 600
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
    val subjects: Int? = null,
    val terms: Int? = null,
    val error: String? = null
)