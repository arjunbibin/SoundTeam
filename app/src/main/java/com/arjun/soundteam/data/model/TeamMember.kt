package com.arjun.soundteam.data.model

data class TeamMember(
    val id: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val role: UserRole = UserRole.USER,
    val skills: List<String> = emptyList(),
    val joinDate: String = "",
    val attendanceData: Map<String, Boolean> = emptyMap() // Map of "yyyy-MM-dd" -> true/false
)
