package com.arjun.soundteam.data.model

import com.google.firebase.firestore.PropertyName

data class WishlistItem(
    @get:PropertyName("id") @set:PropertyName("id") var id: String = "",
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("price") @set:PropertyName("price") var price: Double = 0.0,
    @get:PropertyName("category") @set:PropertyName("category") var category: String = "",
    @get:PropertyName("specifications") @set:PropertyName("specifications") var specifications: String = "",
    @get:PropertyName("purchaseLink") @set:PropertyName("purchaseLink") var purchaseLink: String = "",
    @get:PropertyName("status") @set:PropertyName("status") var status: String = "pending",
    @get:PropertyName("timestamp") @set:PropertyName("timestamp") var timestamp: Long = 0L
) {
    constructor() : this("", "", 0.0, "", "", "", "pending", 0L)
}
