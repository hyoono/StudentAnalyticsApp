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

    suspend fun compareSubjects(request: SubjectComparisonRequest): SubjectComparisonResponse {
        return withContext(Dispatchers.IO) {
            val soapEnvelope = createSubjectComparisonSoapEnvelope(request)
            val response = sendSoapRequest(soapEnvelope, "compareSubjects")
            parseSubjectComparisonResponse(response)
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
                        <subjectWeights>${request.subjectWeights.joinToString(",")}</subjectWeights>
                        <historicalGrades>${request.historicalGrades.joinToString(";") { it.joinToString(",") }}</historicalGrades>
                    </analyzeGrades>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()
    }

    private fun createSubjectComparisonSoapEnvelope(request: SubjectComparisonRequest): String {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                <soap:Body>
                    <compareSubjects>
                        <studentId>${request.studentId}</studentId>
                        <subjectNames>${request.subjectNames.joinToString(",")}</subjectNames>
                        <subjectGrades>${request.subjectGrades.joinToString(",")}</subjectGrades>
                        <classAverages>${request.classAverages.joinToString(",")}</classAverages>
                        <creditHours>${request.creditHours.joinToString(",")}</creditHours>
                    </compareSubjects>
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
                        <participationScore>${request.participationScore}</participationScore>
                        <studyHoursPerWeek>${request.studyHoursPerWeek}</studyHoursPerWeek>
                        <extracurricularHours>${request.extracurricularHours}</extracurricularHours>
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
                        <gpa>${request.gpa}</gpa>
                        <extracurriculars>${request.extracurriculars.joinToString(",")}</extracurriculars>
                        <incomeLevel>${request.incomeLevel}</incomeLevel>
                        <honors>${request.honors.joinToString(",")}</honors>
                        <communityServiceHours>${request.communityServiceHours}</communityServiceHours>
                        <leadershipPositions>${request.leadershipPositions.joinToString(",")}</leadershipPositions>
                    </checkEligibility>
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
            currentGpa = json.getDouble("currentGpa"),
            gradeDistribution = json.getString("gradeDistribution"),
            performanceTrend = json.getString("performanceTrend"),
            suggestions = json.getString("suggestions")
        )
    }

    private fun parseSubjectComparisonResponse(response: String): SubjectComparisonResponse {
        val jsonStart = response.indexOf("{")
        val jsonEnd = response.lastIndexOf("}") + 1
        val jsonString = response.substring(jsonStart, jsonEnd)
        val json = JSONObject(jsonString)

        return SubjectComparisonResponse(
            bestSubject = json.getString("bestSubject"),
            bestGrade = json.getDouble("bestGrade"),
            weakestSubject = json.getString("weakestSubject"),
            weakestGrade = json.getDouble("weakestGrade"),
            overallGpa = json.getDouble("overallGpa"),
            subjectsAboveAverage = json.getString("subjectsAboveAverage").split(",").filter { it.isNotEmpty() },
            subjectsBelowAverage = json.getString("subjectsBelowAverage").split(",").filter { it.isNotEmpty() },
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
            gpaScore = json.getDouble("gpaScore"),
            extracurricularScore = json.getDouble("extracurricularScore"),
            serviceScore = json.getDouble("serviceScore"),
            leadershipScore = json.getDouble("leadershipScore"),
            needBasedBonus = json.getDouble("needBasedBonus"),
            eligibleScholarships = json.getString("eligibleScholarships").split(",").filter { it.isNotEmpty() },
            recommendations = json.getString("recommendations")
        )
    }
}