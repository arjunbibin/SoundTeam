package com.arjun.soundteam.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arjun.soundteam.ui.state.LoginState
import com.arjun.soundteam.data.repository.AuthRepository
import com.arjun.soundteam.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState

    fun sendOtp(phoneNumber: String) {
        viewModelScope.launch {
            _loginState.value = _loginState.value.copy(isVerifying = true)
            val result = authRepository.sendOtp(phoneNumber)
            when (result) {
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
            val result = authRepository.verifyOtp(otp)
            when (result) {
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

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _loginState.value = LoginState()
        }
    }
}
