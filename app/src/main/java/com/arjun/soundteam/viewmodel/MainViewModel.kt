package com.arjun.soundteam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arjun.soundteam.auth.LoginState
import com.arjun.soundteam.data.repository.AuthRepository
import com.arjun.soundteam.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState

    fun loginWithPhone(phoneNumber: String) {
        viewModelScope.launch {
            _loginState.value = _loginState.value.copy(isVerifying = true)
            when (val result = authRepository.sendOtp(phoneNumber)) {
                is Resource.Success -> _loginState.value = _loginState.value.copy(
                    isVerifying = false,
                    isOtpSent = true,
                    verificationId = result.data
                )
                is Resource.Error -> _loginState.value = _loginState.value.copy(
                    isVerifying = false,
                    errorMessage = result.message
                )
            }
        }
    }

    fun verifyOtp(otp: String) {
        viewModelScope.launch {
            _loginState.value = _loginState.value.copy(isVerifying = true)
            when (val result = authRepository.verifyOtp(otp)) {
                is Resource.Success -> _loginState.value = _loginState.value.copy(
                    isVerifying = false,
                    isAuthenticated = true
                )
                is Resource.Error -> _loginState.value = _loginState.value.copy(
                    isVerifying = false,
                    errorMessage = result.message
                )
            }
        }
    }
}
