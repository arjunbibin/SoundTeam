package com.arjun.soundteam.data.model

data class FirebaseItem(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
