package com.arjun.soundteam.data.repository

import android.app.Activity
import android.util.Log
import com.arjun.soundteam.data.model.AuthResult
import com.arjun.soundteam.data.model.UserData
import com.arjun.soundteam.data.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override fun verifyPhoneNumber(phoneNumber: String, activity: Activity): Flow<AuthResult> = callbackFlow {
        trySend(AuthResult.Loading)

        val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                trySend(AuthResult.Success(UserData(phoneNumber = phoneNumber)))
            }

            override fun onVerificationFailed(e: Exception) {
                trySend(AuthResult.Failure(e.message ?: "Verification failed"))
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                trySend(AuthResult.PhoneVerificationRequired(phoneNumber))
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callback)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        awaitClose {
            try {
                auth.signOut()
            } catch (e: Exception) {
                Log.e("AuthRepository", "Error during cleanup", e)
            }
        }
    }

    override suspend fun signInWithPhoneAuthCredential(verificationId: String, code: String): AuthResult {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user
            if (user != null) {
                AuthResult.Success(UserData(
                    id = user.uid,
                    phoneNumber = user.phoneNumber ?: "",
                    role = UserRole.USER
                ))
            } else {
                AuthResult.Failure("Authentication failed")
            }
        } catch (e: Exception) {
            AuthResult.Failure(e.message ?: "Authentication failed")
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override fun getCurrentUser(): AuthResult {
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            AuthResult.Success(UserData(
                id = currentUser.uid,
                phoneNumber = currentUser.phoneNumber ?: "",
                role = UserRole.USER
            ))
        } else {
            AuthResult.LoggedOut
        }
    }
}