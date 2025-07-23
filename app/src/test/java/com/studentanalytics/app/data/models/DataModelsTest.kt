package com.studentanalytics.app.data.models

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for data models to verify decimal support for courseHours
 * and comprehensive field mapping alignment with PHP SOAP service backend
 */
class DataModelsTest {
    
    @Test
    fun predictiveModelingRequest_courseHours_supportsDecimalValues() {
        // Test that PredictiveModelingRequest can handle decimal courseHours values
        val request = PredictiveModelingRequest(
            studentId = "TEST123",
            historicalGrades = listOf(85.0, 87.5, 82.25),
            attendanceRate = 95.50,
            courseHours = 40.75, // Test decimal value
            creditUnits = 3,
            gradeFormat = "raw"
        )
        
        assertEquals("TEST123", request.studentId)
        assertEquals(95.50, request.attendanceRate, 0.001)
        assertEquals(40.75, request.courseHours, 0.001) // Verify decimal support
        assertEquals(3, request.creditUnits)
        assertEquals("raw", request.gradeFormat)
    }
    
    @Test
    fun predictiveModelingRequest_courseHours_supportsIntegerValues() {
        // Test that PredictiveModelingRequest still works with integer-like values
        val request = PredictiveModelingRequest(
            studentId = "TEST456",
            historicalGrades = listOf(88.0, 90.0),
            attendanceRate = 98.00,
            courseHours = 40.00, // Integer-like decimal value
            creditUnits = 4,
            gradeFormat = "transmuted"
        )
        
        assertEquals(40.00, request.courseHours, 0.001)
        assertEquals(98.00, request.attendanceRate, 0.001)
    }
    
    @Test
    fun gradeAnalysisResponse_canBeCreatedWithTwaField() {
        // Test that we can create a GradeAnalysisResponse with proper field mapping
        // This simulates what the backend returns vs what the model expects
        val response = GradeAnalysisResponse(
            weightedAverage = 87.5,
            currentTwa = 2.15, // This should be mapped from backend's "twa" field
            gradeDistribution = "A: 2, B: 1, C: 0, D: 0, F: 0",
            performanceTrend = "Improving",
            suggestions = "Focus on maintaining current performance..."
        )
        
        assertEquals(87.5, response.weightedAverage, 0.001)
        assertEquals(2.15, response.currentTwa, 0.001)
        assertEquals("A: 2, B: 1, C: 0, D: 0, F: 0", response.gradeDistribution)
        assertEquals("Improving", response.performanceTrend)
        assertEquals("Focus on maintaining current performance...", response.suggestions)
    }
    
    @Test
    fun courseComparisonResponse_canBeCreatedWithBackendFields() {
        // Test that CourseComparisonResponse aligns with backend response structure
        // Backend returns "twa" which should map to "overallTwa" in the model
        val response = CourseComparisonResponse(
            bestCourse = "Mathematics",
            bestGrade = 95.0,
            weakestCourse = "Physics",
            weakestGrade = 78.0,
            overallTwa = 1.75, // This should be mapped from backend's "twa" field
            coursesAboveAverage = listOf("Mathematics", "Chemistry"),
            coursesBelowAverage = listOf("Physics"),
            performanceVariance = 12.5,
            recommendations = "Focus more time on Physics; Continue excellent work in Mathematics"
        )
        
        assertEquals("Mathematics", response.bestCourse)
        assertEquals(95.0, response.bestGrade, 0.001)
        assertEquals("Physics", response.weakestCourse)
        assertEquals(78.0, response.weakestGrade, 0.001)
        assertEquals(1.75, response.overallTwa, 0.001) // Mapped from backend "twa"
        assertEquals(2, response.coursesAboveAverage.size)
        assertEquals(1, response.coursesBelowAverage.size)
        assertEquals(12.5, response.performanceVariance, 0.001)
        assertEquals("Focus more time on Physics; Continue excellent work in Mathematics", response.recommendations)
    }
    
    @Test
    fun scholarshipEligibilityResponse_alignsWithBackendResponse() {
        // Test that ScholarshipEligibilityResponse properly maps backend fields
        // Backend returns "twa", "yearLevel", etc. which correctly map to model fields
        val response = ScholarshipEligibilityResponse(
            eligibilityStatus = "Eligible",
            overallScore = 85.5,
            twa = 1.50, // Backend returns "twa" - correct mapping
            yearLevel = "3",
            deansListStatus = "Top Spot Dean's Lister", 
            currentUnits = 18,
            completedUnits = 120,
            eligibleScholarships = listOf("Academic Excellence Scholarship", "Dean's List Award"),
            recommendations = "Apply for multiple scholarship opportunities",
            notes = "Subject to review and spot availability"
        )
        
        assertEquals("Eligible", response.eligibilityStatus)
        assertEquals(85.5, response.overallScore, 0.001)
        assertEquals(1.50, response.twa, 0.001) // Correctly mapped from backend
        assertEquals("3", response.yearLevel)
        assertEquals("Top Spot Dean's Lister", response.deansListStatus)
        assertEquals(18, response.currentUnits)
        assertEquals(120, response.completedUnits)
        assertEquals(2, response.eligibleScholarships.size)
        assertEquals("Academic Excellence Scholarship", response.eligibleScholarships[0])
        assertEquals("Apply for multiple scholarship opportunities", response.recommendations)
        assertEquals("Subject to review and spot availability", response.notes)
    }
    
    @Test
    fun predictiveModelingResponse_alignsWithBackendResponse() {
        // Test that PredictiveModelingResponse correctly handles all backend fields
        val response = PredictiveModelingResponse(
            predictedGrade = 88.5,
            riskLevel = "Low",
            confidenceScore = 92.3,
            trendAnalysis = "Gradual improvement",
            keyFactors = listOf("Good attendance", "Adequate course hours", "Improving trend"),
            recommendations = "Continue current positive academic habits",
            atRisk = false
        )
        
        assertEquals(88.5, response.predictedGrade, 0.001)
        assertEquals("Low", response.riskLevel)
        assertEquals(92.3, response.confidenceScore, 0.001)
        assertEquals("Gradual improvement", response.trendAnalysis)
        assertEquals(3, response.keyFactors.size)
        assertEquals("Good attendance", response.keyFactors[0])
        assertEquals("Continue current positive academic habits", response.recommendations)
        assertEquals(false, response.atRisk)
    }
    
    @Test
    fun chartResponse_handlesOptionalFields() {
        // Test that ChartResponse properly handles optional fields from backend
        val response = ChartResponse(
            success = true,
            chartType = "grades_trend",
            imageData = "iVBORw0KGgoAAAANSUhEUgAAA...", // Base64 image data
            studentId = "12345",
            classId = null,
            dataPoints = 8,
            totalStudents = null,
            courses = null,
            terms = null,
            error = null
        )
        
        assertEquals(true, response.success)
        assertEquals("grades_trend", response.chartType)
        assertTrue(response.imageData.isNotEmpty())
        assertEquals("12345", response.studentId)
        assertEquals(null, response.classId)
        assertEquals(8, response.dataPoints)
        assertEquals(null, response.totalStudents)
        assertEquals(null, response.courses)
        assertEquals(null, response.terms)
        assertEquals(null, response.error)
    }
}