package com.example.nfcsmartcard.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nfcsmartcard.data.network.model.studentDetailModel.StudentDetailsRequest
import com.example.nfcsmartcard.data.repository.StudentRepository
import com.example.nfcsmartcard.utils.ScanStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentViewModel
@Inject constructor(
    private val studentRepository: StudentRepository
) : ViewModel(){

    val studentDetailsLiveData get() = studentRepository.studentDetailsLiveData


    // TODO: Remove all this
//    val studentListLiveData get() = studentRepository.studentListLiveData

//    private val _scanStatusLiveData = MutableLiveData<ScanStatus<String>>()
//    val scanStatusLiveData: LiveData<ScanStatus<String>>
//        get() = _scanStatusLiveData

//    private var _validStudentList: List<String>? = null
//    val validStudentList get() = _validStudentList
//    fun setValidStudentList(stdList: List<String>) {_validStudentList = stdList}
//
//    private var _invalidStudentList: List<String>? = null
//    val invalidStudentList get() = _invalidStudentList
//    fun setInvalidStudentList(stdList: List<String>) {_invalidStudentList = stdList}

//    fun getStudentListResponse() {
//        viewModelScope.launch {
//            studentRepository.getStudentsList()
//        }
//    }

//    fun getStudentDetailsResponse() {
//        viewModelScope.launch {
//            // TODO: model is changed
////            studentRepository.getStudentDetails(
////                StudentDetailsRequest(enroll_number = scanStatusLiveData.value!!.enrollment)
////            )
//        }
//    }

    fun verifyEnrollment(enrollment: String) {
        Log.i("TAG", "verifyEnrollment: Called")
        viewModelScope.launch {
            studentRepository.getStudentDetails(
                StudentDetailsRequest(enroll_number = enrollment)
            )
        }

//        if(validStudentList!!.contains(enrollment)) {
//            _scanStatusLiveData.postValue(ScanStatus.Subscribed(enrollment))
//
//        } else if(invalidStudentList!!.contains(enrollment)) {
//            _scanStatusLiveData.postValue(ScanStatus.NotSubscribed(enrollment))
//
//        } else {
//            _scanStatusLiveData.postValue(ScanStatus.OtherBus(enrollment))
//        }
    }


}