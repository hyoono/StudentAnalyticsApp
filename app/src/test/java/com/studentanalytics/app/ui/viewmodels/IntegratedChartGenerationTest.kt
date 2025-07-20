package com.studentanalytics.app.ui.viewmodels

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests to verify that UI states include chart-related fields for integrated chart generation
 */
class IntegratedChartGenerationTest {
    
    @Test
    fun gradeAnalysisUiState_includesChartFields() {
        // Test that GradeAnalysisUiState includes chart-related fields
        val uiState = GradeAnalysisUiState(
            isLoading = false,
            result = null,
            error = null,
            chartResponse = null,
            chartError = null,
            isLoadingChart = false
        )
        
        assertFalse(uiState.isLoading)
        assertNull(uiState.result)
        assertNull(uiState.error)
        assertNull(uiState.chartResponse)
        assertNull(uiState.chartError)
        assertFalse(uiState.isLoadingChart)
    }
    
    @Test
    fun courseComparisonUiState_includesChartFields() {
        // Test that CourseComparisonUiState includes chart-related fields
        val uiState = CourseComparisonUiState(
            isLoading = true,
            result = null,
            error = null,
            chartResponse = null,
            chartError = null,
            isLoadingChart = true
        )
        
        assertTrue(uiState.isLoading)
        assertNull(uiState.result)
        assertNull(uiState.error)
        assertNull(uiState.chartResponse)
        assertNull(uiState.chartError)
        assertTrue(uiState.isLoadingChart)
    }
    
    @Test
    fun predictiveModelingUiState_includesChartFields() {
        // Test that PredictiveModelingUiState includes chart-related fields
        val uiState = PredictiveModelingUiState(
            isLoading = false,
            result = null,
            error = "Test error",
            chartResponse = null,
            chartError = "Chart error",
            isLoadingChart = false
        )
        
        assertFalse(uiState.isLoading)
        assertNull(uiState.result)
        assertEquals("Test error", uiState.error)
        assertNull(uiState.chartResponse)
        assertEquals("Chart error", uiState.chartError)
        assertFalse(uiState.isLoadingChart)
    }
    
    @Test
    fun scholarshipEligibilityUiState_includesChartFields() {
        // Test that ScholarshipEligibilityUiState includes chart-related fields
        val uiState = ScholarshipEligibilityUiState(
            isLoading = false,
            result = null,
            error = null,
            chartResponse = null,
            chartError = null,
            isLoadingChart = true
        )
        
        assertFalse(uiState.isLoading)
        assertNull(uiState.result)
        assertNull(uiState.error)
        assertNull(uiState.chartResponse)
        assertNull(uiState.chartError)
        assertTrue(uiState.isLoadingChart)
    }
}