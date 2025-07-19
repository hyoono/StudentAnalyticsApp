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
    val error: String? = null
)

class GradeAnalysisViewModel : ViewModel() {
    private val soapService = SoapService()
    private val _uiState = MutableStateFlow(GradeAnalysisUiState())
    val uiState: StateFlow<GradeAnalysisUiState> = _uiState.asStateFlow()

    fun analyzeGrades(request: GradeAnalysisRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val result = soapService.analyzeGrades(request)
                _uiState.value = _uiState.value.copy(isLoading = false, result = result)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}

data class CourseComparisonUiState(
    val isLoading: Boolean = false,
    val result: CourseComparisonResponse? = null,
    val error: String? = null
)

class CourseComparisonViewModel : ViewModel() {
    private val soapService = SoapService()
    private val _uiState = MutableStateFlow(CourseComparisonUiState())
    val uiState: StateFlow<CourseComparisonUiState> = _uiState.asStateFlow()

    fun compareCourses(request: CourseComparisonRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val result = soapService.compareCourses(request)
                _uiState.value = _uiState.value.copy(isLoading = false, result = result)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}

data class PredictiveModelingUiState(
    val isLoading: Boolean = false,
    val result: PredictiveModelingResponse? = null,
    val error: String? = null
)

class PredictiveModelingViewModel : ViewModel() {
    private val soapService = SoapService()
    private val _uiState = MutableStateFlow(PredictiveModelingUiState())
    val uiState: StateFlow<PredictiveModelingUiState> = _uiState.asStateFlow()

    fun generatePrediction(request: PredictiveModelingRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val result = soapService.generatePrediction(request)
                _uiState.value = _uiState.value.copy(isLoading = false, result = result)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}

data class ScholarshipEligibilityUiState(
    val isLoading: Boolean = false,
    val result: ScholarshipEligibilityResponse? = null,
    val error: String? = null
)

class ScholarshipEligibilityViewModel : ViewModel() {
    private val soapService = SoapService()
    private val _uiState = MutableStateFlow(ScholarshipEligibilityUiState())
    val uiState: StateFlow<ScholarshipEligibilityUiState> = _uiState.asStateFlow()

    fun checkEligibility(request: ScholarshipEligibilityRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val result = soapService.checkEligibility(request)
                _uiState.value = _uiState.value.copy(isLoading = false, result = result)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
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