class RepositoryImpl(private val firestore: FirebaseFirestore) : Repository {

    override suspend fun addWishlistItem(
        name: String,
        price: Double,
        category: String,
        specifications: String,
        purchaseLink: String
    ) {
        val wishlistItem = hashMapOf(
            "name" to name,
            "price" to price,
            "category" to category,
            "specifications" to specifications,
            "purchaseLink" to purchaseLink,
            "timestamp" to FieldValue.serverTimestamp()
        )
        firestore.collection("wishlist").add(wishlistItem)
    }

    override fun getWishlistItems(): Flow<List<WishlistItem>> = callbackFlow {
        val listener = firestore.collection("wishlist").addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }
            val items = snapshot?.documents?.mapNotNull { 
                it.toObject(WishlistItem::class.java)?.copy(id = it.id)}
            items?.let { trySend(it).isSuccess }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun updateWishlistItemStatus(itemId: String, newStatus: WishlistStatus) {
        firestore.collection("wishlist").document(itemId)
            .update("status", newStatus.name)
    }

    override suspend fun deleteWishlistItem(itemId: String) {
        firestore.collection("wishlist").document(itemId).delete()
    }

    override suspend fun addTeamMember(name: String, phoneNumber: String, role: String, skills: String) {
        val teamMember = hashMapOf(
            "name" to name,
            "phoneNumber" to phoneNumber,
            "role" to role,
            "skills" to skills,
            "timestamp" to FieldValue.serverTimestamp()
        )
        firestore.collection("team").add(teamMember)
    }

    override suspend fun deleteTeamMemberById(memberId: String) {
        firestore.collection("team").document(memberId).delete()
    }

    override suspend fun trackAnalytics(type: String, data: Map<String, Any>) {
        val analyticsEntry = hashMapOf(
            "type" to type,
            "data" to data,
            "timestamp" to FieldValue.serverTimestamp()
        )
        firestore.collection("analytics").add(analyticsEntry)
    }

    override fun getAnalytics(type: String, startTime: Long, endTime: Long): Flow<List<AnalyticsData>> =
        callbackFlow {
            val query = firestore.collection("analytics")
                .whereEqualTo("type", type)
                .whereGreaterThanOrEqualTo("timestamp", startTime)
                .whereLessThanOrEqualTo("timestamp", endTime)

            val listener = query.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val data = snapshot?.documents?.mapNotNull { it.toObject(AnalyticsData::class.java) }
                data?.let { trySend(it).isSuccess }
            }
            awaitClose { listener.remove() }
        }

    override suspend fun sendMessage(content: String, type: String, attachmentUrl: String?) {
        val message = hashMapOf(
            "content" to content,
            "type" to type,
            "attachmentUrl" to attachmentUrl,
            "timestamp" to FieldValue.serverTimestamp()
        )
        firestore.collection("messages").add(message)
    }

    override fun getRecentMessages(limit: Int): Flow<List<ChatMessage>> =
        callbackFlow {
            val query = firestore.collection("messages")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit.toLong())

            val listener = query.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull { it.toObject(ChatMessage::class.java) }
                messages?.let { trySend(it).isSuccess }
            }
            awaitClose { listener.remove() }
        }
}
