package com.arjun.soundteam.data.model

import com.google.firebase.firestore.PropertyName

data class ChatMessage(
    @get:PropertyName("id") @set:PropertyName("id") var id: String = "",
    @get:PropertyName("senderId") @set:PropertyName("senderId") var senderId: String = "",
    @get:PropertyName("content") @set:PropertyName("content") var content: String = "",
    @get:PropertyName("type") @set:PropertyName("type") var type: MessageType = MessageType.TEXT,
    @get:PropertyName("attachmentUrl") @set:PropertyName("attachmentUrl") var attachmentUrl: String? = null,
    @get:PropertyName("timestamp") @set:PropertyName("timestamp") var timestamp: Long = System.currentTimeMillis(),
    @get:PropertyName("isRead") @set:PropertyName("isRead") var isRead: Boolean = false
) {
    constructor() : this("", "", "", MessageType.TEXT, null, System.currentTimeMillis(), false)
}

enum class MessageType {
    TEXT, IMAGE, AUDIO, VIDEO, FILE
}
