interface Repository {
    suspend fun addWishlistItem(
        name: String,
        price: Double,
        category: String,
        specifications: String,
        purchaseLink: String
    )

    fun getWishlistItems(): Flow<List<WishlistItem>>

    suspend fun updateWishlistItemStatus(itemId: String, newStatus: WishlistStatus)

    suspend fun deleteWishlistItem(itemId: String)

    suspend fun addTeamMember(name: String, phoneNumber: String, role: String, skills: String)

    suspend fun deleteTeamMemberById(memberId: String)

    suspend fun trackAnalytics(type: String, data: Map<String, Any>)

    fun getAnalytics(type: String, startTime: Long, endTime: Long): Flow<List<AnalyticsData>>

    suspend fun sendMessage(content: String, type: String = "text", attachmentUrl: String? = null)

    fun getRecentMessages(limit: Int): Flow<List<ChatMessage>>
}
