package com.example.nfcsmartcard.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nfcsmartcard.data.network.api.StudentApi
import com.example.nfcsmartcard.data.network.model.studentDetails.StudentDetailsResponse
import com.example.nfcsmartcard.data.network.model.studentList.StudentDetailsRequest
import com.example.nfcsmartcard.data.network.model.studentList.StudentListResponse
import com.example.nfcsmartcard.utils.NetworkResult
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class StudentRepository
@Inject constructor(private val studentApi: StudentApi) {

    private val TAG = UserRepository::class.java.simpleName + "TESTING"

    private val _studentListLiveData = MutableLiveData<NetworkResult<StudentListResponse>>()
    val studentListLiveData: LiveData<NetworkResult<StudentListResponse>>
        get() = _studentListLiveData

    private val _studentDetailsLiveData = MutableLiveData<NetworkResult<StudentDetailsResponse>>()
    val studentDetailsLiveData: LiveData<NetworkResult<StudentDetailsResponse>>
        get() = _studentDetailsLiveData

    suspend fun getStudentsList() {
        _studentListLiveData.postValue(NetworkResult.Loading())
        try {
            val response = studentApi.getStudentList()
            handleStudentListResponse(response)

        } catch (e: Exception) {
            e.printStackTrace()
            _studentListLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    private fun handleStudentListResponse(response: Response<StudentListResponse>) {
        if(response.isSuccessful && response.body() != null) {
            _studentListLiveData.postValue(NetworkResult.Success(response.body()!!))

        } else if(response.errorBody() != null) {
            val errorObject = JSONObject(response.errorBody()!!.charStream().readText())
            val errorMessage = errorObject.getString("message")
            _studentListLiveData.postValue((NetworkResult.Error(errorMessage)))

        } else {
            _studentListLiveData.postValue(NetworkResult.Error("Something went wrong"))
        }
    }

    suspend fun getStudentDetails(studentDetailsRequest: StudentDetailsRequest) {
        _studentDetailsLiveData.postValue(NetworkResult.Loading())
        try {
            val response = studentApi.getStudentDetails(studentDetailsRequest)
            handleStudentDetailsResponse(response)

        } catch (e: Exception) {
            e.printStackTrace()
            _studentDetailsLiveData.postValue(NetworkResult.Error(e.message))
        }
    }

    private fun handleStudentDetailsResponse(response: Response<StudentDetailsResponse>) {
        if(response.isSuccessful && response.body() != null) {
            _studentDetailsLiveData.postValue(NetworkResult.Success(response.body()!!))

        } else if(response.errorBody() != null) {
            val errorObject = JSONObject(response.errorBody()!!.charStream().readText())
            val errorMessage = errorObject.getString("message")
            _studentDetailsLiveData.postValue((NetworkResult.Error(errorMessage)))

        } else {
            _studentDetailsLiveData.postValue(NetworkResult.Error("Something went wrong"))
        }
    }

}