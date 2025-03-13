package com.arjun.soundteam.ui.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arjun.soundteam.data.model.AuthResult
import com.arjun.soundteam.data.repository.AuthRepository
import com.arjun.soundteam.ui.state.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        when (val result = authRepository.getCurrentUser()) {
            is AuthResult.Success -> {
                _loginState.update { it.copy(
                    isAuthenticated = true,
                    errorMessage = null
                )}
            }
            else -> {
                _loginState.update { it.copy(
                    isAuthenticated = false,
                    errorMessage = null
                )}
            }
        }
    }

    fun verifyPhoneNumber(phoneNumber: String, activity: Activity) {
        viewModelScope.launch {
            authRepository.verifyPhoneNumber(phoneNumber, activity).collect { result ->
                when (result) {
                    is AuthResult.Loading -> {
                        _loginState.update { it.copy(
                            isVerifying = true,
                            errorMessage = null
                        )}
                    }
                    is AuthResult.PhoneVerificationRequired -> {
                        _loginState.update { it.copy(
                            phoneNumber = result.phoneNumber,
                            isOtpSent = true,
                            isVerifying = false,
                            errorMessage = null
                        )}
                    }
                    is AuthResult.Success -> {
                        _loginState.update { it.copy(
                            isAuthenticated = true,
                            isVerifying = false,
                            errorMessage = null
                        )}
                    }
                    is AuthResult.Failure -> {
                        _loginState.update { it.copy(
                            isVerifying = false,
                            errorMessage = result.errorMessage
                        )}
                    }
                    is AuthResult.LoggedOut -> {
                        _loginState.update { it.copy(
                            isAuthenticated = false,
                            isVerifying = false
                        )}
                    }
                }
            }
        }
    }

    fun verifyOtpCode(verificationId: String, code: String) {
        viewModelScope.launch {
            _loginState.update { it.copy(isVerifying = true) }
            when (val result = authRepository.signInWithPhoneAuthCredential(verificationId, code)) {
                is AuthResult.Success -> {
                    _loginState.update { it.copy(
                        isAuthenticated = true,
                        isVerifying = false,
                        errorMessage = null
                    )}
                }
                is AuthResult.Failure -> {
                    _loginState.update { it.copy(
                        isVerifying = false,
                        errorMessage = result.errorMessage
                    )}
                }
                else -> {}
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _loginState.update { LoginState() }
        }
    }
}