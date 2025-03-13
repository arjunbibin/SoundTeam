package com.arjun.soundteam.data.model

sealed class AuthResult {
    data class PhoneVerificationRequired(val phoneNumber: String) : AuthResult()

    data class Success(val user: UserData) : AuthResult()
    data class Failure(val errorMessage: String) : AuthResult()
    object Loading : AuthResult()
    object LoggedOut : AuthResult()
}
