package com.arjun.soundteam.ui.state  // ✅ Corrected

data class LoginState(
    val phoneNumber: String = "",
    val verificationId: String? = null,
    val otpCode: String = "",
    val isOtpSent: Boolean = false,
    val isVerifying: Boolean = false,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null
)
