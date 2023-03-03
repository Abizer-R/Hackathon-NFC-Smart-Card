package com.example.nfcsmartcard.data.network.api

import com.example.nfcsmartcard.data.network.model.studentDetails.StudentDetailsResponse
import com.example.nfcsmartcard.data.network.model.studentList.StudentDetailsRequest
import com.example.nfcsmartcard.data.network.model.studentList.StudentListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface StudentApi {

    @GET("/students")
    suspend fun getStudentList() : Response<StudentListResponse>

    @POST("/students/details")
    suspend fun getStudentDetails(@Body studentDetailsRequest: StudentDetailsRequest) : Response<StudentDetailsResponse>
}