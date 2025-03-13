package com.arjun.soundteam.data.repository

import android.content.Context
import com.arjun.soundteam.data.model.TeamMember
import com.arjun.soundteam.data.model.UserRole
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeamMemberRepository @Inject constructor(
    private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val firebaseDb: FirebaseDatabase = FirebaseDatabase.getInstance()

    // Helper function to safely access Firebase reference
    private fun getFirebaseReference(path: String): DatabaseReference = firebaseDb.getReference(path)

    fun getAllTeamMembers(): Flow<List<TeamMember>> = callbackFlow {
        val teamMembersRef = getFirebaseReference("team_members")
        val listener = teamMembersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val teamMembers = snapshot.children.mapNotNull { 
                    it.getValue(TeamMember::class.java)?.copy(id = it.key ?: "")
                }
                trySend(teamMembers).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })

        awaitClose { teamMembersRef.removeEventListener(listener) }
    }

    suspend fun addTeamMember(
        name: String,
        phoneNumber: String,
        role: UserRole,
        skills: List<String>
    ) {
        val teamMemberId = getFirebaseReference("team_members").push().key
            ?: throw Exception("Failed to generate team member ID")

        val teamMember = TeamMember(
            id = teamMemberId,
            name = name,
            phoneNumber = phoneNumber,
            role = role,
            skills = skills,
            joinDate = System.currentTimeMillis().toString() // 🔹 Store as a string timestamp
        )

        getFirebaseReference("team_members")
            .child(teamMember.id)
            .setValue(teamMember)
            .await()
    }

    suspend fun updateTeamMemberAttendance(memberId: String, date: String, isPresent: Boolean) {
        val memberRef = getFirebaseReference("team_members").child(memberId)
        val snapshot = memberRef.get().await()
        val member = snapshot.getValue(TeamMember::class.java)
            ?: throw Exception("Team member not found")

        val updatedAttendanceData = member.attendanceData.toMutableMap()
        updatedAttendanceData[date] = isPresent  // 🔹 Ensure date is stored as a string (yyyy-MM-dd)

        val updatedMember = member.copy(attendanceData = updatedAttendanceData)
        memberRef.setValue(updatedMember).await()
    }

    suspend fun updateTeamMemberAttendanceData(memberId: String, attendanceData: Map<String, Boolean>) {
        val memberRef = getFirebaseReference("team_members").child(memberId)
        val snapshot = memberRef.get().await()
        val member = snapshot.getValue(TeamMember::class.java)
            ?: throw Exception("Team member not found")

        val updatedMember = member.copy(attendanceData = attendanceData)
        memberRef.setValue(updatedMember).await()
    }
}
