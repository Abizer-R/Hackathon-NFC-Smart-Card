package com.example.nfcsmartcard.data.network.api

import com.example.nfcsmartcard.data.network.model.studentDetailModel.StudentDetailsRequest
import com.example.nfcsmartcard.data.network.model.studentDetailModel.StudentDetailsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface StudentApi {

//    @GET("/students")
//    suspend fun getStudentList() : Response<StudentListResponse>
//
//    @POST("/students/details")
//    suspend fun getStudentDetails(@Body studentDetailsRequest: StudentDetailsRequest) : Response<StudentDetailsResponse>

    @POST("/students/")
    suspend fun getStudentDetails(@Body studentDetailsRequest: StudentDetailsRequest) : Response<StudentDetailsResponse>
}