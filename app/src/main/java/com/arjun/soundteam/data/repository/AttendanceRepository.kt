package com.arjun.soundteam.data.repository

import android.content.Context
import com.arjun.soundteam.data.model.TeamMember
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepository @Inject constructor(
    private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val firebaseDb: FirebaseDatabase = FirebaseDatabase.getInstance()

    // Helper function to safely access Firebase reference
    private fun getFirebaseReference(path: String): DatabaseReference = firebaseDb.getReference(path)

    fun getTeamMembersWithAttendance(): Flow<List<TeamMember>> = callbackFlow {
        val teamMembersRef = getFirebaseReference("team_members")
        val listener = teamMembersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val teamMembers = snapshot.children.mapNotNull { 
                    it.getValue(TeamMember::class.java)?.copy(id = it.key ?: "")
                }
                trySend(teamMembers)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })

        awaitClose { teamMembersRef.removeEventListener(listener) }
    }

    suspend fun updateAttendance(memberId: String, date: String, isPresent: Boolean) {
        val memberRef = getFirebaseReference("team_members").child(memberId)
        val snapshot = memberRef.get().await()
        val member = snapshot.getValue(TeamMember::class.java)
            ?: throw Exception("Team member not found")

        val updatedAttendanceData = member.attendanceData.toMutableMap()
        updatedAttendanceData[date] = isPresent  // 🔹 Store date as a string

        val updatedMember = member.copy(attendanceData = updatedAttendanceData)
        memberRef.setValue(updatedMember).await()
    }

    suspend fun updateBulkAttendance(data: Map<String, Map<String, Boolean>>) {
        val updates = mutableMapOf<String, Any>()
        
        data.forEach { (memberId, attendanceData) ->
            val memberRef = getFirebaseReference("team_members").child(memberId)
            val snapshot = memberRef.get().await()
            val member = snapshot.getValue(TeamMember::class.java)
                ?: throw Exception("Team member not found: $memberId")

            val updatedMember = member.copy(attendanceData = attendanceData)
            updates["/team_members/$memberId"] = updatedMember
        }

        getFirebaseReference("/").updateChildren(updates).await()
    }
}
