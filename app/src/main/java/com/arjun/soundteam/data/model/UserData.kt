package com.arjun.soundteam.data.model

import com.arjun.soundteam.data.model.UserRole

data class UserData(
    val id: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val role: UserRole = UserRole.USER
)
