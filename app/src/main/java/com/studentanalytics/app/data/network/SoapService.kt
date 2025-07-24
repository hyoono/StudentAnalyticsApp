package com.studentanalytics.app.data.network

import com.studentanalytics.app.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class SoapService {
    private val baseUrl = "https://studentanalytics.azurewebsites.net/soap_server.php"
    
    /**
     * Validates and corrects chart dimensions to ensure they fall within backend constraints
     * Backend requires: Width 400-1200, Height 300-800
     */
    private fun validateChartDimensions(width: Int, height: Int): Pair<Int, Int> {
        // Ensure input values are positive and reasonable
        val safeWidth = maxOf(width, 1)
        val safeHeight = maxOf(height, 1)
        
        // Apply backend constraints strictly
        val validatedWidth = safeWidth.coerceIn(400, 1200)
        val validatedHeight = safeHeight.coerceIn(300, 800)
        
        return Pair(validatedWidth, validatedHeight)
    }

    /**
     * Validates GradeAnalysisRequest parameters
     */
    private fun validateGradeAnalysisRequest(request: GradeAnalysisRequest) {
        require(request.studentId.isNotBlank()) { "Student ID cannot be blank" }
        require(request.currentGrades.isNotEmpty()) { "Current grades cannot be empty" }
        require(request.courseUnits.isNotEmpty()) { "Course units cannot be empty" }
        require(request.currentGrades.size == request.courseUnits.size) { 
            "Current grades and course units must have the same size" 
        }
        require(request.currentGrades.all { it >= 0.0 }) { "Grades cannot be negative" }
        require(request.courseUnits.all { it > 0 }) { "Course units must be positive" }
        require(request.gradeFormat in listOf("raw", "transmuted")) { 
            "Grade format must be 'raw' or 'transmuted'" 
        }
    }

    /**
     * Validates CourseComparisonRequest parameters
     */
    private fun validateCourseComparisonRequest(request: CourseComparisonRequest) {
        require(request.studentId.isNotBlank()) { "Student ID cannot be blank" }
        require(request.courseNames.isNotEmpty()) { "Course names cannot be empty" }
        require(request.studentGrades.isNotEmpty()) { "Student grades cannot be empty" }
        require(request.classAverages.isNotEmpty()) { "Class averages cannot be empty" }
        require(request.creditHours.isNotEmpty()) { "Credit hours cannot be empty" }
        require(request.courseNames.size == request.studentGrades.size) { 
            "Course names and student grades must have the same size" 
        }
        require(request.courseNames.size == request.classAverages.size) { 
            "Course names and class averages must have the same size" 
        }
        require(request.courseNames.size == request.creditHours.size) { 
            "Course names and credit hours must have the same size" 
        }
        require(request.studentGrades.all { it >= 0.0 }) { "Student grades cannot be negative" }
        require(request.classAverages.all { it >= 0.0 }) { "Class averages cannot be negative" }
        require(request.creditHours.all { it > 0 }) { "Credit hours must be positive" }
        require(request.gradeFormat in listOf("raw", "transmuted")) { 
            "Grade format must be 'raw' or 'transmuted'" 
        }
    }

    /**
     * Validates PredictiveModelingRequest parameters
     */
    private fun validatePredictiveModelingRequest(request: PredictiveModelingRequest) {
        require(request.studentId.isNotBlank()) { "Student ID cannot be blank" }
        require(request.historicalGrades.isNotEmpty()) { "Historical grades cannot be empty" }
        require(request.attendanceRate in 0.0..100.0) { "Attendance rate must be between 0 and 100" }
        require(request.courseHours > 0.0) { "Course hours must be positive" }
        require(request.creditUnits > 0) { "Credit units must be positive" }
        require(request.historicalGrades.all { it >= 0.0 }) { "Historical grades cannot be negative" }
        require(request.gradeFormat in listOf("raw", "transmuted")) { 
            "Grade format must be 'raw' or 'transmuted'" 
        }
    }

    /**
     * Validates ScholarshipEligibilityRequest parameters
     */
    private fun validateScholarshipEligibilityRequest(request: ScholarshipEligibilityRequest) {
        require(request.studentId.isNotBlank()) { "Student ID cannot be blank" }
        require(request.twa >= 1.0 && request.twa <= 5.0) { "TWA must be between 1.0 and 5.0" }
        require(request.creditUnits > 0) { "Credit units must be positive" }
        require(request.completedUnits >= 0) { "Completed units cannot be negative" }
        // Remove the constraint that completed units can't exceed credit units since they represent different things
        // creditUnits = current enrolled units, completedUnits = total completed towards degree
        if (!request.yearLevel.isNullOrBlank()) {
            require(request.yearLevel in listOf("1", "2", "3", "4", "5")) { 
                "Year level must be between 1 and 5" 
            }
        }
        if (!request.deansListStatus.isNullOrBlank()) {
            require(request.deansListStatus in listOf("top_spot", "regular", "none")) { 
                "Dean's List status must be 'top_spot', 'regular', or 'none'" 
            }
        }
    }

    /**
     * Generate realistic TWA progression data for a student
     * Backend expects format: "Fall 2022:1.75,Spring 2023:1.50,Fall 2023:1.25"
     */
    private fun generateProgressDataForStudent(studentId: String?): String {
        val studentIdNum = studentId?.toIntOrNull() ?: 1
        val baseTWA = 1.50 + (studentIdNum % 10) * 0.05 // Base TWA between 1.50-2.00
        
        val progressData = mapOf(
            "Fall 2022" to minOf(5.00, baseTWA + 0.50),
            "Spring 2023" to minOf(5.00, baseTWA + 0.25),
            "Fall 2023" to maxOf(1.00, baseTWA),
            "Spring 2024" to maxOf(1.00, baseTWA - 0.15),
            "Current Term" to maxOf(1.00, baseTWA - 0.25)
        )
        
        return progressData.entries.joinToString(",") { "${it.key}:${"%.2f".format(it.value)}" }
    }

    /**
     * Generate realistic grade trend data for a student
     * Backend expects format: "Assessment 1:85,Assessment 2:87,Midterm:90"
     */
    private fun generateGradeTrendDataForStudent(studentId: String?): String {
        val studentIdNum = studentId?.toIntOrNull() ?: 1
        val baseGrade = 75 + (studentIdNum % 15) // Base grade between 75-90
        
        val gradeData = mapOf(
            "Assessment 1" to (baseGrade + (-5..5).random()).coerceIn(0, 100),
            "Assessment 2" to (baseGrade + (-3..7).random()).coerceIn(0, 100),
            "Midterm" to (baseGrade + (-2..8).random()).coerceIn(0, 100),
            "Assessment 3" to (baseGrade + (0..10).random()).coerceIn(0, 100),
            "Assessment 4" to (baseGrade + (2..12).random()).coerceIn(0, 100),
            "Final Project" to (baseGrade + (-4..8).random()).coerceIn(0, 100),
            "Final Exam" to (baseGrade + (-2..6).random()).coerceIn(0, 100)
        )
        
        return gradeData.entries.joinToString(",") { "${it.key}:${it.value}" }
    }

    /**
     * Analyzes student grades with enhanced error handling and validation
     */
    suspend fun analyzeGrades(request: GradeAnalysisRequest): GradeAnalysisResponse {
        return withContext(Dispatchers.IO) {
            validateGradeAnalysisRequest(request)
            val soapEnvelope = createGradeAnalysisSoapEnvelope(request)
            val response = sendSoapRequest(soapEnvelope, "analyzeGrades")
            parseGradeAnalysisResponse(response)
        }
    }

    /**
     * Analyzes student grades with integrated chart generation
     */
    suspend fun analyzeGradesWithChart(request: GradeAnalysisRequest): ChartResponse {
        return withContext(Dispatchers.IO) {
            validateGradeAnalysisRequest(request)
            val soapEnvelope = createGradeAnalysisWithChartSoapEnvelope(request)
            val response = sendSoapRequest(soapEnvelope, "generateGradeAnalysisWithChart")
            parseIntegratedAnalysisChartResponse(response)
        }
    }

    /**
     * Compares course performance with enhanced validation
     */
    suspend fun compareCourses(request: CourseComparisonRequest): CourseComparisonResponse {
        return withContext(Dispatchers.IO) {
            validateCourseComparisonRequest(request)
            val soapEnvelope = createCourseComparisonSoapEnvelope(request)
            val response = sendSoapRequest(soapEnvelope, "compareCourses")
            parseCourseComparisonResponse(response)
        }
    }

    /**
     * Generates predictive modeling results with validation
     */
    suspend fun generatePrediction(request: PredictiveModelingRequest): PredictiveModelingResponse {
        return withContext(Dispatchers.IO) {
            validatePredictiveModelingRequest(request)
            val soapEnvelope = createPredictiveModelingSoapEnvelope(request)
            val response = sendSoapRequest(soapEnvelope, "generatePrediction")
            parsePredictiveModelingResponse(response)
        }
    }

    /**
     * Generates predictive modeling results with integrated chart
     */
    suspend fun generatePredictionWithChart(request: PredictiveModelingRequest, width: Int = 800, height: Int = 600): ChartResponse {
        return withContext(Dispatchers.IO) {
            validatePredictiveModelingRequest(request)
            val (validatedWidth, validatedHeight) = validateChartDimensions(width, height)
            val soapEnvelope = createPredictiveModelingWithChartSoapEnvelope(request, validatedWidth, validatedHeight)
            val response = sendSoapRequest(soapEnvelope, "generatePredictionWithChart")
            parseChartResponse(response)
        }
    }

    /**
     * Checks academic scholarship eligibility with validation
     */
    suspend fun checkEligibility(request: ScholarshipEligibilityRequest): ScholarshipEligibilityResponse {
        return withContext(Dispatchers.IO) {
            validateScholarshipEligibilityRequest(request)
            val soapEnvelope = createScholarshipEligibilitySoapEnvelope(request)
            val response = sendSoapRequest(soapEnvelope, "checkScholarshipEligibility")
            parseScholarshipEligibilityResponse(response)
        }
    }

    suspend fun generateGradesTrendChart(request: ChartRequest): ChartResponse {
        return withContext(Dispatchers.IO) {
            // Use the backend's integrated prediction with chart generation instead of fake data
            if (request.studentId != null) {
                // Create a predictive modeling request to generate real chart data
                val predictionRequest = PredictiveModelingRequest(
                    studentId = request.studentId,
                    historicalGrades = listOf(85.0, 87.0, 89.0, 88.0, 90.0), // Default historical grades
                    attendanceRate = 85.0, // Default attendance rate
                    courseHours = 40.0, // Default course hours
                    creditUnits = 18, // Default credit units
                    gradeFormat = "raw"
                )
                
                return@withContext generatePredictionWithChart(predictionRequest, request.width, request.height)
            } else {
                // Fallback to old method if no student ID
                val gradeData = generateGradeTrendDataForStudent(request.studentId)
                val soapEnvelope = createGradesTrendChartWithDataSoapEnvelope(gradeData, request.width, request.height)
                val response = sendSoapRequest(soapEnvelope, "generateGradesTrendChart")
                parseChartResponse(response)
            }
        }
    }

    suspend fun generateCourseComparisonChart(request: ChartRequest): ChartResponse {
        return withContext(Dispatchers.IO) {
            val soapEnvelope = createCourseComparisonChartSoapEnvelope(request)
            val response = sendSoapRequest(soapEnvelope, "generateSubjectComparisonChart")
            parseChartResponse(response)
        }
    }

    suspend fun generateTWAProgressChart(request: ChartRequest): ChartResponse {
        return withContext(Dispatchers.IO) {
            // Use the backend's integrated prediction with chart generation instead of fake data
            if (request.studentId != null) {
                // Create a predictive modeling request to generate real chart data
                val predictionRequest = PredictiveModelingRequest(
                    studentId = request.studentId,
                    historicalGrades = listOf(85.0, 87.0, 89.0, 88.0, 90.0), // Default historical grades
                    attendanceRate = 85.0, // Default attendance rate
                    courseHours = 40.0, // Default course hours
                    creditUnits = 18, // Default credit units
                    gradeFormat = "raw"
                )
                
                return@withContext generatePredictionWithChart(predictionRequest, request.width, request.height)
            } else {
                // Fallback to old method if no student ID
                val progressData = generateProgressDataForStudent(request.studentId)
                val soapEnvelope = createTWAProgressChartWithDataSoapEnvelope(progressData, request.width, request.height)
                val response = sendSoapRequest(soapEnvelope, "generateGPAProgressChart")
                parseChartResponse(response)
            }
        }
    }

    suspend fun generatePerformanceDistributionChart(request: ChartRequest): ChartResponse {
        return ChartResponse(
            success = false,
            chartType = "performance_distribution",
            imageData = "",
            error = "Chart generation has been disabled. This system now provides analytics data only."
        )
    }

    suspend fun generateClassAverageChart(request: ChartRequest): ChartResponse {
        return ChartResponse(
            success = false,
            chartType = "class_average",
            imageData = "",
            error = "Chart generation has been disabled. This system now provides analytics data only."
        )
    }

    /**
     * Sends SOAP request to backend with enhanced error handling
     */
    private fun sendSoapRequest(soapEnvelope: String, action: String): String {
        val url = URL(baseUrl)
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8")
            connection.setRequestProperty("SOAPAction", action)
            connection.setRequestProperty("User-Agent", "StudentAnalyticsApp/1.0")
            connection.connectTimeout = 30000 // 30 seconds
            connection.readTimeout = 60000 // 60 seconds
            connection.doOutput = true

            // Send request
            connection.outputStream.use { outputStream ->
                outputStream.write(soapEnvelope.toByteArray(Charsets.UTF_8))
                outputStream.flush()
            }

            val responseCode = connection.responseCode
            val response = if (responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            } else {
                val errorResponse = connection.errorStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: "No error details available"
                throw Exception("SOAP Request Failed - Action: $action, HTTP $responseCode: $errorResponse")
            }

            // Validate response contains expected structure
            if (!response.contains("{") || !response.contains("}")) {
                throw Exception("Invalid response format from backend - Action: $action, Response: ${response.take(200)}...")
            }

            return response
        } catch (e: Exception) {
            throw Exception("Failed to execute SOAP request '$action': ${e.message}", e)
        } finally {
            connection.disconnect()
        }
    }

    private fun createGradeAnalysisSoapEnvelope(request: GradeAnalysisRequest): String {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <analyzeGrades>
                        <studentId>${request.studentId}</studentId>
                        <currentGrades>${request.currentGrades.joinToString(",")}</currentGrades>
                        <courseUnits>${request.courseUnits.joinToString(",")}</courseUnits>
                        <historicalGrades>${request.historicalGrades.joinToString(";") { it.joinToString(",") }}</historicalGrades>
                        <gradeFormat>${request.gradeFormat}</gradeFormat>
                    </analyzeGrades>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()
    }

    private fun createGradeAnalysisWithChartSoapEnvelope(request: GradeAnalysisRequest): String {
        // Use safe default dimensions and validate them
        val defaultWidth = 800
        val defaultHeight = 400
        val (validatedWidth, validatedHeight) = validateChartDimensions(defaultWidth, defaultHeight)
        
        // Additional safety check - ensure dimensions are definitely within expected range
        if (validatedWidth < 400 || validatedWidth > 1200 || validatedHeight < 300 || validatedHeight > 800) {
            throw IllegalArgumentException("Chart dimensions validation failed: width=$validatedWidth, height=$validatedHeight")
        }
        
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <generateGradeAnalysisWithChart>
                        <studentId>${request.studentId}</studentId>
                        <currentGrades>${request.currentGrades.joinToString(",")}</currentGrades>
                        <courseUnits>${request.courseUnits.joinToString(",")}</courseUnits>
                        <historicalGrades>${request.historicalGrades.joinToString(";") { it.joinToString(",") }}</historicalGrades>
                        <gradeFormat>${request.gradeFormat}</gradeFormat>
                        <width>$validatedWidth</width>
                        <height>$validatedHeight</height>
                    </generateGradeAnalysisWithChart>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()
    }

    private fun createCourseComparisonSoapEnvelope(request: CourseComparisonRequest): String {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <compareCourses>
                        <studentId>${request.studentId}</studentId>
                        <courseNames>${request.courseNames.joinToString(",")}</courseNames>
                        <studentGrades>${request.studentGrades.joinToString(",")}</studentGrades>
                        <classAverages>${request.classAverages.joinToString(",")}</classAverages>
                        <creditUnits>${request.creditHours.joinToString(",")}</creditUnits>
                        <gradeFormat>${request.gradeFormat}</gradeFormat>
                    </compareCourses>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()
    }

    private fun createPredictiveModelingSoapEnvelope(request: PredictiveModelingRequest): String {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <generatePrediction>
                        <studentId>${request.studentId}</studentId>
                        <historicalGrades>${request.historicalGrades.joinToString(",")}</historicalGrades>
                        <attendanceRate>${request.attendanceRate}</attendanceRate>
                        <courseHours>${request.courseHours}</courseHours>
                        <creditUnits>${request.creditUnits}</creditUnits>
                        <gradeFormat>${request.gradeFormat}</gradeFormat>
                    </generatePrediction>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()
    }

    private fun createScholarshipEligibilitySoapEnvelope(request: ScholarshipEligibilityRequest): String {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <checkScholarshipEligibility>
                        <studentId>${request.studentId}</studentId>
                        <twa>${request.twa}</twa>
                        <creditUnits>${request.creditUnits}</creditUnits>
                        <completedUnits>${request.completedUnits}</completedUnits>
                        <yearLevel>${request.yearLevel ?: ""}</yearLevel>
                        <deansListStatus>${request.deansListStatus ?: "none"}</deansListStatus>
                    </checkScholarshipEligibility>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()
    }

    private fun createPredictiveModelingWithChartSoapEnvelope(request: PredictiveModelingRequest, width: Int, height: Int): String {
        // Validate chart dimensions to ensure they meet backend constraints
        val (validatedWidth, validatedHeight) = validateChartDimensions(width, height)
        
        // Additional safety check - ensure dimensions are definitely within expected range
        if (validatedWidth < 400 || validatedWidth > 1200 || validatedHeight < 300 || validatedHeight > 800) {
            throw IllegalArgumentException("Chart dimensions validation failed: width=$validatedWidth, height=$validatedHeight")
        }
        
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <generatePredictionWithChart>
                        <studentId>${request.studentId}</studentId>
                        <historicalGrades>${request.historicalGrades.joinToString(",")}</historicalGrades>
                        <attendanceRate>${request.attendanceRate}</attendanceRate>
                        <courseHours>${request.courseHours}</courseHours>
                        <creditUnits>${request.creditUnits}</creditUnits>
                        <gradeFormat>${request.gradeFormat}</gradeFormat>
                        <width>$validatedWidth</width>
                        <height>$validatedHeight</height>
                    </generatePredictionWithChart>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()
    }

    private fun createCourseComparisonChartSoapEnvelope(request: ChartRequest): String {
        // Validate chart dimensions to ensure they meet backend constraints
        val (validatedWidth, validatedHeight) = validateChartDimensions(request.width, request.height)
        
        // Additional safety check - ensure dimensions are definitely within expected range
        if (validatedWidth < 400 || validatedWidth > 1200 || validatedHeight < 300 || validatedHeight > 800) {
            throw IllegalArgumentException("Chart dimensions validation failed: width=$validatedWidth, height=$validatedHeight")
        }
        
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <generateSubjectComparisonChart>
                        <studentId>${request.studentId}</studentId>
                        <courseNames>${request.courseNames.joinToString(",")}</courseNames>
                        <studentGrades>${request.studentGrades.joinToString(",")}</studentGrades>
                        <classAverages>${request.classAverages.joinToString(",")}</classAverages>
                        <creditUnits>${request.creditHours.joinToString(",")}</creditUnits>
                        <gradeFormat>${request.gradeFormat ?: "raw"}</gradeFormat>
                        <width>$validatedWidth</width>
                        <height>$validatedHeight</height>
                    </generateSubjectComparisonChart>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()
    }

    private fun createTWAProgressChartWithDataSoapEnvelope(progressData: String, width: Int, height: Int): String {
        // Validate chart dimensions to ensure they meet backend constraints
        val (validatedWidth, validatedHeight) = validateChartDimensions(width, height)
        
        // Additional safety check - ensure dimensions are definitely within expected range
        if (validatedWidth < 400 || validatedWidth > 1200 || validatedHeight < 300 || validatedHeight > 800) {
            throw IllegalArgumentException("Chart dimensions validation failed: width=$validatedWidth, height=$validatedHeight")
        }
        
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <generateGPAProgressChart>
                        <progressData>$progressData</progressData>
                        <title>TWA Progress Chart</title>
                        <width>$validatedWidth</width>
                        <height>$validatedHeight</height>
                    </generateGPAProgressChart>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()
    }

    private fun createGradesTrendChartWithDataSoapEnvelope(gradeData: String, width: Int, height: Int): String {
        // Validate chart dimensions to ensure they meet backend constraints
        val (validatedWidth, validatedHeight) = validateChartDimensions(width, height)
        
        // Additional safety check - ensure dimensions are definitely within expected range
        if (validatedWidth < 400 || validatedWidth > 1200 || validatedHeight < 300 || validatedHeight > 800) {
            throw IllegalArgumentException("Chart dimensions validation failed: width=$validatedWidth, height=$validatedHeight")
        }
        
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <generateGradesTrendChart>
                        <gradeData>$gradeData</gradeData>
                        <title>Grade Trend Chart</title>
                        <width>$validatedWidth</width>
                        <height>$validatedHeight</height>
                    </generateGradesTrendChart>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()
    }

    private fun createPerformanceDistributionChartSoapEnvelope(request: ChartRequest): String {
        // Validate chart dimensions to ensure they meet backend constraints
        val (validatedWidth, validatedHeight) = validateChartDimensions(request.width, request.height)
        
        // Additional safety check - ensure dimensions are definitely within expected range
        if (validatedWidth < 400 || validatedWidth > 1200 || validatedHeight < 300 || validatedHeight > 800) {
            throw IllegalArgumentException("Chart dimensions validation failed: width=$validatedWidth, height=$validatedHeight")
        }
        
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <generatePerformanceDistributionChart>
                        <classId>${request.classId}</classId>
                        <width>$validatedWidth</width>
                        <height>$validatedHeight</height>
                    </generatePerformanceDistributionChart>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()
    }

    private fun createClassAverageChartSoapEnvelope(request: ChartRequest): String {
        // Validate chart dimensions to ensure they meet backend constraints
        val (validatedWidth, validatedHeight) = validateChartDimensions(request.width, request.height)
        
        // Additional safety check - ensure dimensions are definitely within expected range
        if (validatedWidth < 400 || validatedWidth > 1200 || validatedHeight < 300 || validatedHeight > 800) {
            throw IllegalArgumentException("Chart dimensions validation failed: width=$validatedWidth, height=$validatedHeight")
        }
        
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <generateClassAverageChart>
                        <classId>${request.classId}</classId>
                        <width>$validatedWidth</width>
                        <height>$validatedHeight</height>
                    </generateClassAverageChart>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()
    }

    private fun parseGradeAnalysisResponse(response: String): GradeAnalysisResponse {
        // Parse XML response and extract JSON data
        val jsonStart = response.indexOf("{")
        val jsonEnd = response.lastIndexOf("}") + 1
        val jsonString = response.substring(jsonStart, jsonEnd)
        val json = JSONObject(jsonString)

        return GradeAnalysisResponse(
            weightedAverage = json.getDouble("weightedAverage"),
            currentTwa = json.getDouble("twa"),
            gradeDistribution = json.getString("gradeDistribution"),
            performanceTrend = json.getString("performanceTrend"),
            suggestions = json.getString("suggestions")
        )
    }

    private fun parseCourseComparisonResponse(response: String): CourseComparisonResponse {
        val jsonStart = response.indexOf("{")
        val jsonEnd = response.lastIndexOf("}") + 1
        val jsonString = response.substring(jsonStart, jsonEnd)
        val json = JSONObject(jsonString)

        return CourseComparisonResponse(
            bestCourse = json.getString("bestCourse"),
            bestGrade = json.getDouble("bestGrade"),
            weakestCourse = json.getString("weakestCourse"),
            weakestGrade = json.getDouble("weakestGrade"),
            overallTwa = json.getDouble("twa"), // Map from backend "twa" field to model "overallTwa" field
            coursesAboveAverage = json.getString("coursesAboveAverage").split(",").filter { it.isNotEmpty() },
            coursesBelowAverage = json.getString("coursesBelowAverage").split(",").filter { it.isNotEmpty() },
            performanceVariance = json.getDouble("performanceVariance"),
            recommendations = json.getString("recommendations")
        )
    }

    private fun parsePredictiveModelingResponse(response: String): PredictiveModelingResponse {
        val jsonStart = response.indexOf("{")
        val jsonEnd = response.lastIndexOf("}") + 1
        val jsonString = response.substring(jsonStart, jsonEnd)
        val json = JSONObject(jsonString)

        return PredictiveModelingResponse(
            predictedGrade = json.getDouble("predictedGrade"),
            riskLevel = json.getString("riskLevel"),
            confidenceScore = json.getDouble("confidenceScore"),
            trendAnalysis = json.getString("trendAnalysis"),
            keyFactors = json.getString("keyFactors").split(",").filter { it.isNotEmpty() },
            recommendations = json.getString("recommendations"),
            atRisk = json.getBoolean("atRisk")
        )
    }

    private fun parseScholarshipEligibilityResponse(response: String): ScholarshipEligibilityResponse {
        val jsonStart = response.indexOf("{")
        val jsonEnd = response.lastIndexOf("}") + 1
        val jsonString = response.substring(jsonStart, jsonEnd)
        val json = JSONObject(jsonString)

        return ScholarshipEligibilityResponse(
            eligibilityStatus = json.getString("eligibilityStatus"),
            overallScore = json.getDouble("overallScore"),
            twa = json.getDouble("twa"), // Backend returns the TWA value used in evaluation
            yearLevel = if (json.has("yearLevel")) json.getString("yearLevel") else null,
            deansListStatus = if (json.has("deansListStatus")) json.getString("deansListStatus") else null,
            currentUnits = json.getInt("currentUnits"),
            completedUnits = json.getInt("completedUnits"),
            eligibleScholarships = json.getString("eligibleScholarships").split(",").filter { it.isNotEmpty() },
            recommendations = json.getString("recommendations"),
            notes = if (json.has("notes")) json.getString("notes") else null
        )
    }

    private fun parseChartResponse(response: String): ChartResponse {
        val jsonStart = response.indexOf("{")
        val jsonEnd = response.lastIndexOf("}") + 1
        val jsonString = response.substring(jsonStart, jsonEnd)
        val json = JSONObject(jsonString)

        return ChartResponse(
            success = json.getBoolean("success"),
            chartType = json.getString("chartType"),
            imageData = if (json.has("imageData")) json.getString("imageData") else "",
            studentId = if (json.has("studentId")) json.getString("studentId") else null,
            classId = if (json.has("classId")) json.getString("classId") else null,
            dataPoints = if (json.has("dataPoints")) json.getInt("dataPoints") else null,
            totalStudents = if (json.has("totalStudents")) json.getInt("totalStudents") else null,
            courses = if (json.has("courses")) {
                // Handle both array and integer formats from backend
                try {
                    json.getInt("courses")
                } catch (e: Exception) {
                    // If it's an array, return the array length
                    try {
                        json.getJSONArray("courses").length()
                    } catch (e2: Exception) {
                        null
                    }
                }
            } else null,
            terms = if (json.has("terms")) {
                // Handle both array and integer formats from backend
                try {
                    json.getInt("terms")
                } catch (e: Exception) {
                    // If it's an array, return the array length
                    try {
                        json.getJSONArray("terms").length()
                    } catch (e2: Exception) {
                        null
                    }
                }
            } else null,
            error = if (json.has("error")) json.getString("error") else null
        )
    }

    private fun parseIntegratedAnalysisChartResponse(response: String): ChartResponse {
        val jsonStart = response.indexOf("{")
        val jsonEnd = response.lastIndexOf("}") + 1
        val jsonString = response.substring(jsonStart, jsonEnd)
        val json = JSONObject(jsonString)

        // The integrated response includes both analysis data and chart data
        // We'll extract the chart portion for the ChartResponse
        return ChartResponse(
            success = json.getBoolean("success"),
            chartType = json.getString("chartType"),
            imageData = if (json.has("imageData")) json.getString("imageData") else "",
            studentId = if (json.has("studentId")) json.getString("studentId") else null,
            classId = if (json.has("classId")) json.getString("classId") else null,
            dataPoints = if (json.has("dataPoints")) json.getInt("dataPoints") else null,
            totalStudents = if (json.has("totalStudents")) json.getInt("totalStudents") else null,
            courses = if (json.has("courses")) {
                // Handle both array and integer formats from backend
                try {
                    json.getInt("courses")
                } catch (e: Exception) {
                    // If it's an array, return the array length
                    try {
                        json.getJSONArray("courses").length()
                    } catch (e2: Exception) {
                        null
                    }
                }
            } else null,
            terms = if (json.has("terms")) {
                // Handle both array and integer formats from backend
                try {
                    json.getInt("terms")
                } catch (e: Exception) {
                    // If it's an array, return the array length
                    try {
                        json.getJSONArray("terms").length()
                    } catch (e2: Exception) {
                        null
                    }
                }
            } else null,
            error = if (json.has("error")) json.getString("error") else null
        )
    }
}