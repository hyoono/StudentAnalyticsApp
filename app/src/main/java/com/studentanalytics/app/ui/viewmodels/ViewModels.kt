package com.studentanalytics.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studentanalytics.app.data.models.*
import com.studentanalytics.app.data.network.SoapService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GradeAnalysisUiState(
    val isLoading: Boolean = false,
    val result: GradeAnalysisResponse? = null,
    val error: String? = null,
    val chartResponse: ChartResponse? = null,
    val chartError: String? = null,
    val isLoadingChart: Boolean = false
)

class GradeAnalysisViewModel : ViewModel() {
    private val soapService = SoapService()
    private val _uiState = MutableStateFlow(GradeAnalysisUiState())
    val uiState: StateFlow<GradeAnalysisUiState> = _uiState.asStateFlow()

    fun analyzeGrades(request: GradeAnalysisRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, chartError = null, isLoadingChart = true)
            try {
                // Perform grade analysis
                val result = soapService.analyzeGrades(request)
                _uiState.value = _uiState.value.copy(isLoading = false, result = result)
                
                // Generate integrated analysis with chart
                val chartResult = soapService.analyzeGradesWithChart(request)
                _uiState.value = _uiState.value.copy(isLoadingChart = false, chartResponse = chartResult)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, 
                    isLoadingChart = false,
                    error = e.message,
                    chartError = e.message
                )
            }
        }
    }
}

data class CourseComparisonUiState(
    val isLoading: Boolean = false,
    val result: CourseComparisonResponse? = null,
    val error: String? = null,
    val chartResponse: ChartResponse? = null,
    val chartError: String? = null,
    val isLoadingChart: Boolean = false
)

class CourseComparisonViewModel : ViewModel() {
    private val soapService = SoapService()
    private val _uiState = MutableStateFlow(CourseComparisonUiState())
    val uiState: StateFlow<CourseComparisonUiState> = _uiState.asStateFlow()

    fun compareCourses(request: CourseComparisonRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, chartError = null)
            try {
                // Perform course comparison analysis
                val result = soapService.compareCourses(request)
                _uiState.value = _uiState.value.copy(isLoading = false, result = result)
                
                // Automatically generate course comparison chart with the same data used for analysis
                generateCourseComparisonChart(request)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
    
    private fun generateCourseComparisonChart(comparisonRequest: CourseComparisonRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingChart = true, chartError = null)
            try {
                val chartRequest = ChartRequest(
                    studentId = comparisonRequest.studentId,
                    courseNames = comparisonRequest.courseNames,
                    studentGrades = comparisonRequest.studentGrades,
                    classAverages = comparisonRequest.classAverages,
                    creditHours = comparisonRequest.creditHours,
                    gradeFormat = comparisonRequest.gradeFormat,
                    // Ensure dimensions are within valid range (400-1200 width, 300-800 height)
                    width = 800.coerceIn(400, 1200),
                    height = 400.coerceIn(300, 800)
                )
                val chartResult = soapService.generateCourseComparisonChart(chartRequest)
                _uiState.value = _uiState.value.copy(isLoadingChart = false, chartResponse = chartResult)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoadingChart = false, chartError = e.message)
            }
        }
    }
}

data class PredictiveModelingUiState(
    val isLoading: Boolean = false,
    val result: PredictiveModelingResponse? = null,
    val error: String? = null,
    val chartResponse: ChartResponse? = null,
    val chartError: String? = null,
    val isLoadingChart: Boolean = false
)

class PredictiveModelingViewModel : ViewModel() {
    private val soapService = SoapService()
    private val _uiState = MutableStateFlow(PredictiveModelingUiState())
    val uiState: StateFlow<PredictiveModelingUiState> = _uiState.asStateFlow()

    fun generatePrediction(request: PredictiveModelingRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, chartError = null)
            try {
                // Perform predictive modeling analysis
                val result = soapService.generatePrediction(request)
                _uiState.value = _uiState.value.copy(isLoading = false, result = result)
                
                // Automatically generate TWA progress chart using the same user input data
                generateTWAProgressChart(request)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
    
    private fun generateTWAProgressChart(request: PredictiveModelingRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingChart = true, chartError = null)
            try {
                // Use the backend's integrated prediction with chart generation
                // This ensures the chart uses the same user input data as the prediction analysis
                val chartResult = soapService.generatePredictionWithChart(
                    request = request,
                    width = 800.coerceIn(400, 1200),
                    height = 400.coerceIn(300, 800)
                )
                _uiState.value = _uiState.value.copy(isLoadingChart = false, chartResponse = chartResult)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoadingChart = false, chartError = e.message)
            }
        }
    }
}

data class ScholarshipEligibilityUiState(
    val isLoading: Boolean = false,
    val result: ScholarshipEligibilityResponse? = null,
    val error: String? = null,
    val chartResponse: ChartResponse? = null,
    val chartError: String? = null,
    val isLoadingChart: Boolean = false
)

class ScholarshipEligibilityViewModel : ViewModel() {
    private val soapService = SoapService()
    private val _uiState = MutableStateFlow(ScholarshipEligibilityUiState())
    val uiState: StateFlow<ScholarshipEligibilityUiState> = _uiState.asStateFlow()

    fun checkEligibility(request: ScholarshipEligibilityRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, chartError = null)
            try {
                // Perform scholarship eligibility analysis
                val result = soapService.checkEligibility(request)
                _uiState.value = _uiState.value.copy(isLoading = false, result = result)
                
                // Automatically generate class average chart to show student position relative to peers
                generateClassAverageChart(request.studentId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
    
    private fun generateClassAverageChart(studentId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingChart = true, chartError = null)
            try {
                val chartRequest = ChartRequest(
                    studentId = studentId,
                    // Ensure dimensions are within valid range (400-1200 width, 300-800 height)
                    width = 800.coerceIn(400, 1200),
                    height = 400.coerceIn(300, 800)
                )
                val chartResult = soapService.generateClassAverageChart(chartRequest)
                _uiState.value = _uiState.value.copy(isLoadingChart = false, chartResponse = chartResult)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoadingChart = false, chartError = e.message)
            }
        }
    }
}

data class ChartUiState(
    val isLoading: Boolean = false,
    val result: ChartResponse? = null,
    val error: String? = null
)

class ChartViewModel : ViewModel() {
    private val soapService = SoapService()
    private val _uiState = MutableStateFlow(ChartUiState())
    val uiState: StateFlow<ChartUiState> = _uiState.asStateFlow()

    fun generateGradesTrendChart(request: ChartRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val result = soapService.generateGradesTrendChart(request)
                _uiState.value = _uiState.value.copy(isLoading = false, result = result)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun generateCourseComparisonChart(request: ChartRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val result = soapService.generateCourseComparisonChart(request)
                _uiState.value = _uiState.value.copy(isLoading = false, result = result)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun generateTWAProgressChart(request: ChartRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val result = soapService.generateTWAProgressChart(request)
                _uiState.value = _uiState.value.copy(isLoading = false, result = result)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun generatePerformanceDistributionChart(request: ChartRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val result = soapService.generatePerformanceDistributionChart(request)
                _uiState.value = _uiState.value.copy(isLoading = false, result = result)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun generateClassAverageChart(request: ChartRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val result = soapService.generateClassAverageChart(request)
                _uiState.value = _uiState.value.copy(isLoading = false, result = result)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}