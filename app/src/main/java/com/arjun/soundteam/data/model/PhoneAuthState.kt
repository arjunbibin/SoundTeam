package com.arjun.soundteam.data.model

sealed class PhoneAuthState {
    object Idle : PhoneAuthState()
    object CodeSent : PhoneAuthState()
    data class VerificationSuccess(val user: UserData) : PhoneAuthState()
    data class VerificationFailed(val error: String) : PhoneAuthState()
}
