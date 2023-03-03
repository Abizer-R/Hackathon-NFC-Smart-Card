package com.example.nfcsmartcard.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nfcsmartcard.data.network.api.UserApi
import com.example.nfcsmartcard.data.network.model.user.UserRequest
import com.example.nfcsmartcard.data.network.model.user.UserResponse
import com.example.nfcsmartcard.utils.NetworkResult
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class UserRepository
@Inject constructor(private val userApi: UserApi) {

    private val TAG = UserRepository::class.java.simpleName + "TESTING"

    private val _userResponseLiveData = MutableLiveData<NetworkResult<UserResponse>>()
    val userResponseLiveData: LiveData<NetworkResult<UserResponse>>
        get() = _userResponseLiveData

    suspend fun loginUser(userRequest: UserRequest) {
        _userResponseLiveData.postValue(NetworkResult.Loading())
        try {
            val response = userApi.signIn(userRequest)
            handleResponse(response)

        } catch (e: Exception) {
            e.printStackTrace()
            _userResponseLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    private fun handleResponse(response: Response<UserResponse>) {
        if(response.isSuccessful && response.body() != null) {
            _userResponseLiveData.postValue(NetworkResult.Success(response.body()!!))

        } else if(response.errorBody() != null) {
            val errorObject = JSONObject(response.errorBody()!!.charStream().readText())
            val errorMessage = errorObject.getString("message")
            _userResponseLiveData.postValue((NetworkResult.Error(errorMessage)))

        } else {
            _userResponseLiveData.postValue(NetworkResult.Error("Something went wrong"))
        }
    }
}