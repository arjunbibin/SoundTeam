package com.arjun.soundteam.data.model

import com.google.firebase.firestore.PropertyName

data class AnalyticsData(
    @get:PropertyName("type") @set:PropertyName("type") var type: String = "",
    @get:PropertyName("data") @set:PropertyName("data") var data: Map<String, Any> = emptyMap(),
    @get:PropertyName("timestamp") @set:PropertyName("timestamp") var timestamp: Long = 0L
) {
    constructor() : this("", emptyMap(), 0L)
}
