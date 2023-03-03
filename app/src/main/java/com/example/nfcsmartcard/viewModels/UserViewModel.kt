package com.example.nfcsmartcard.viewModels

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nfcsmartcard.data.network.model.user.UserRequest
import com.example.nfcsmartcard.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val userResponseLiveData get() = userRepository.userResponseLiveData

    fun loginUser(userRequest: UserRequest) {
        viewModelScope.launch {
            userRepository.loginUser(userRequest)
        }
    }

    fun validateCredentials(userRequest: UserRequest): Pair<Boolean, String> {
        var result = Pair(true, "")

        if((TextUtils.isEmpty(userRequest.email) && TextUtils.isEmpty(userRequest.contact)) || TextUtils.isEmpty(userRequest.password)) {
            result = Pair(false, "Please enter all credentials")

        } else if(!TextUtils.isEmpty(userRequest.email) && !Patterns.EMAIL_ADDRESS.matcher(userRequest.email).matches()) {
            result = Pair(false, "Please provide valid email")

        } else if(userRequest.password.length <= 5) {
            result = Pair(false, "Password length must be greater than 5")

        }
        return result
    }
}