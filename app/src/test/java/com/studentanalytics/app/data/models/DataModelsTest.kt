package com.studentanalytics.app.data.models

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for data models to verify decimal support for courseHours
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
}