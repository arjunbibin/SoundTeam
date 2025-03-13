package com.arjun.soundteam.data.repository

import android.util.Log
import com.arjun.soundteam.data.model.FirebaseItem
import com.arjun.soundteam.data.model.Item
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ItemRepository"

@Singleton
class ItemRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val itemsRef = database.getReference("items")

    init {
        try {
            itemsRef.keepSynced(true)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to enable sync for items", e)
        }
    }

    val items: Flow<List<Item>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val items = snapshot.children.mapNotNull { child ->
                        try {
                            child.getValue(FirebaseItem::class.java)?.toItem()
                        } catch (e: Exception) {
                            Log.e(TAG, "Error converting item: ${child.key}", e)
                            null
                        }
                    }
                    trySend(items)
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing items snapshot", e)
                    trySend(emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Items listener cancelled: ${error.message}", error.toException())
                trySend(emptyList())
            }
        }

        itemsRef.addValueEventListener(listener)
        awaitClose { itemsRef.removeEventListener(listener) }
    }.catch { e ->
        Log.e(TAG, "Error in items flow", e)
        emit(emptyList())
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    suspend fun getItem(itemId: String): Item? {
        return try {
            val snapshot = itemsRef.child(itemId).get().await()
            snapshot.getValue(FirebaseItem::class.java)?.toItem()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting item: $itemId", e)
            null
        }
    }

    suspend fun addItem(item: Item): Boolean {
        return try {
            val firebaseItem = FirebaseItem.fromItem(item)
            val newRef = itemsRef.push()
            firebaseItem.id = newRef.key ?: return false
            newRef.setValue(firebaseItem).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding item", e)
            false
        }
    }

    suspend fun updateItem(item: Item): Boolean {
        return try {
            val firebaseItem = FirebaseItem.fromItem(item)
            itemsRef.child(item.id).setValue(firebaseItem).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating item: ${item.id}", e)
            false
        }
    }

    suspend fun deleteItem(itemId: String): Boolean {
        return try {
            itemsRef.child(itemId).removeValue().await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting item: $itemId", e)
            false
        }
    }
} 