package com.arjun.soundteam.data.repository

import android.app.Activity
import com.arjun.soundteam.data.model.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun verifyPhoneNumber(phoneNumber: String, activity: Activity): Flow<AuthResult>
    suspend fun signInWithPhoneAuthCredential(verificationId: String, code: String): AuthResult
    suspend fun signOut()
    fun getCurrentUser(): AuthResult
}