package com.studentanalytics.app.data.network

import com.studentanalytics.app.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class SoapService {
    private val baseUrl = "http://10.0.2.2/student_analytics/soap_server.php"
    
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

    suspend fun analyzeGrades(request: GradeAnalysisRequest): GradeAnalysisResponse {
        return withContext(Dispatchers.IO) {
            val soapEnvelope = createGradeAnalysisSoapEnvelope(request)
            val response = sendSoapRequest(soapEnvelope, "analyzeGrades")
            parseGradeAnalysisResponse(response)
        }
    }

    suspend fun analyzeGradesWithChart(request: GradeAnalysisRequest): ChartResponse {
        return withContext(Dispatchers.IO) {
            val soapEnvelope = createGradeAnalysisWithChartSoapEnvelope(request)
            val response = sendSoapRequest(soapEnvelope, "generateGradeAnalysisWithChart")
            parseIntegratedAnalysisChartResponse(response)
        }
    }

    suspend fun compareCourses(request: CourseComparisonRequest): CourseComparisonResponse {
        return withContext(Dispatchers.IO) {
            val soapEnvelope = createCourseComparisonSoapEnvelope(request)
            val response = sendSoapRequest(soapEnvelope, "compareCourses")
            parseCourseComparisonResponse(response)
        }
    }

    suspend fun generatePrediction(request: PredictiveModelingRequest): PredictiveModelingResponse {
        return withContext(Dispatchers.IO) {
            val soapEnvelope = createPredictiveModelingSoapEnvelope(request)
            val response = sendSoapRequest(soapEnvelope, "generatePrediction")
            parsePredictiveModelingResponse(response)
        }
    }

    suspend fun checkEligibility(request: ScholarshipEligibilityRequest): ScholarshipEligibilityResponse {
        return withContext(Dispatchers.IO) {
            val soapEnvelope = createScholarshipEligibilitySoapEnvelope(request)
            val response = sendSoapRequest(soapEnvelope, "checkEligibility")
            parseScholarshipEligibilityResponse(response)
        }
    }

    suspend fun generateGradesTrendChart(request: ChartRequest): ChartResponse {
        return withContext(Dispatchers.IO) {
            // For now, use a simulated grade trend data format that the backend expects
            // This creates realistic grade progression data from student ID
            val gradeData = generateGradeTrendDataForStudent(request.studentId)
            val soapEnvelope = createGradesTrendChartWithDataSoapEnvelope(gradeData, request.width, request.height)
            val response = sendSoapRequest(soapEnvelope, "generateGradesTrendChart")
            parseChartResponse(response)
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
            // For now, use a simulated progress data format that the backend expects
            // This creates realistic TWA progression data from student ID
            val progressData = generateProgressDataForStudent(request.studentId)
            val soapEnvelope = createTWAProgressChartWithDataSoapEnvelope(progressData, request.width, request.height)
            val response = sendSoapRequest(soapEnvelope, "generateGPAProgressChart")
            parseChartResponse(response)
        }
    }

    suspend fun generatePerformanceDistributionChart(request: ChartRequest): ChartResponse {
        return withContext(Dispatchers.IO) {
            val soapEnvelope = createPerformanceDistributionChartSoapEnvelope(request)
            val response = sendSoapRequest(soapEnvelope, "generatePerformanceDistributionChart")
            parseChartResponse(response)
        }
    }

    suspend fun generateClassAverageChart(request: ChartRequest): ChartResponse {
        return withContext(Dispatchers.IO) {
            val soapEnvelope = createClassAverageChartSoapEnvelope(request)
            val response = sendSoapRequest(soapEnvelope, "generateClassAverageChart")
            parseChartResponse(response)
        }
    }

    private fun sendSoapRequest(soapEnvelope: String, action: String): String {
        val url = URL(baseUrl)
        val connection = url.openConnection() as HttpURLConnection

        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8")
        connection.setRequestProperty("SOAPAction", action)
        connection.doOutput = true

        val outputStream = connection.outputStream
        outputStream.write(soapEnvelope.toByteArray())
        outputStream.flush()
        outputStream.close()

        val responseCode = connection.responseCode
        val inputStream = if (responseCode == HttpURLConnection.HTTP_OK) {
            connection.inputStream
        } else {
            connection.errorStream
        }

        val response = inputStream.bufferedReader().use { it.readText() }
        inputStream.close()
        connection.disconnect()

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw Exception("HTTP Error: $responseCode - $response")
        }

        return response
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
                        <creditHours>${request.creditHours.joinToString(",")}</creditHours>
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
                    <checkEligibility>
                        <studentId>${request.studentId}</studentId>
                        <twa>${request.twa}</twa>
                        <extracurriculars>${request.extracurriculars.joinToString(",")}</extracurriculars>
                        <honors>${request.honors.joinToString(",")}</honors>
                    </checkEligibility>
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
                        <creditHours>${request.creditHours.joinToString(",")}</creditHours>
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
            twaScore = json.getDouble("twaScore"),
            extracurricularScore = json.getDouble("extracurricularScore"),
            eligibleScholarships = json.getString("eligibleScholarships").split(",").filter { it.isNotEmpty() },
            recommendations = json.getString("recommendations")
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