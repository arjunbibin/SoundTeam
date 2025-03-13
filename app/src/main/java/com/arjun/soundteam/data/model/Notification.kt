package com.arjun.soundteam.data.model

data class Notification(
    val id: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
