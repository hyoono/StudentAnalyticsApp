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

    suspend fun analyzeGrades(request: GradeAnalysisRequest): GradeAnalysisResponse {
        return withContext(Dispatchers.IO) {
            val soapEnvelope = createGradeAnalysisSoapEnvelope(request)
            val response = sendSoapRequest(soapEnvelope, "analyzeGrades")
            parseGradeAnalysisResponse(response)
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
            val soapEnvelope = createGradesTrendChartSoapEnvelope(request)
            val response = sendSoapRequest(soapEnvelope, "generateGradesTrendChart")
            parseChartResponse(response)
        }
    }

    suspend fun generateCourseComparisonChart(request: ChartRequest): ChartResponse {
        return withContext(Dispatchers.IO) {
            val soapEnvelope = createCourseComparisonChartSoapEnvelope(request)
            val response = sendSoapRequest(soapEnvelope, "generateCourseComparisonChart")
            parseChartResponse(response)
        }
    }

    suspend fun generateTWAProgressChart(request: ChartRequest): ChartResponse {
        return withContext(Dispatchers.IO) {
            val soapEnvelope = createTWAProgressChartSoapEnvelope(request)
            val response = sendSoapRequest(soapEnvelope, "generateTWAProgressChart")
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

    private fun createGradesTrendChartSoapEnvelope(request: ChartRequest): String {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <generateGradesTrendChart>
                        <studentId>${request.studentId}</studentId>
                        <width>${request.width}</width>
                        <height>${request.height}</height>
                    </generateGradesTrendChart>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()
    }

    private fun createCourseComparisonChartSoapEnvelope(request: ChartRequest): String {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <generateCourseComparisonChart>
                        <studentId>${request.studentId}</studentId>
                        <width>${request.width}</width>
                        <height>${request.height}</height>
                    </generateCourseComparisonChart>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()
    }

    private fun createTWAProgressChartSoapEnvelope(request: ChartRequest): String {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <generateTWAProgressChart>
                        <studentId>${request.studentId}</studentId>
                        <width>${request.width}</width>
                        <height>${request.height}</height>
                    </generateTWAProgressChart>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()
    }

    private fun createPerformanceDistributionChartSoapEnvelope(request: ChartRequest): String {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <generatePerformanceDistributionChart>
                        <classId>${request.classId}</classId>
                        <width>${request.width}</width>
                        <height>${request.height}</height>
                    </generatePerformanceDistributionChart>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()
    }

    private fun createClassAverageChartSoapEnvelope(request: ChartRequest): String {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <generateClassAverageChart>
                        <classId>${request.classId}</classId>
                        <width>${request.width}</width>
                        <height>${request.height}</height>
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
            currentTwa = json.getDouble("twa"), // Map from backend "twa" field to model "currentTwa" field
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
            overallTwa = json.getDouble("overallTwa"),
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
            imageData = json.getString("imageData"),
            studentId = if (json.has("studentId")) json.getString("studentId") else null,
            classId = if (json.has("classId")) json.getString("classId") else null,
            dataPoints = if (json.has("dataPoints")) json.getInt("dataPoints") else null,
            totalStudents = if (json.has("totalStudents")) json.getInt("totalStudents") else null,
            courses = if (json.has("courses")) json.getInt("courses") else null,
            terms = if (json.has("terms")) json.getInt("terms") else null,
            error = if (json.has("error")) json.getString("error") else null
        )
    }
}