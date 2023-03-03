package com.example.nfcsmartcard.data.network.api

import com.example.nfcsmartcard.data.network.model.user.UserRequest
import com.example.nfcsmartcard.data.network.model.user.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {

    @POST("/users/signin")
    suspend fun signIn (@Body userRequest: UserRequest) : Response<UserResponse>
}