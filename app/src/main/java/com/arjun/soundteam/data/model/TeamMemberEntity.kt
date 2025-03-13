import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "team_members")
@TypeConverters(Converters::class) // 🔹 Apply Converters
data class TeamMemberEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phoneNumber: String,
    val role: UserRole, // Now directly stored as an Enum
    val skills: List<String>, // Now stored as a List
    val joinDate: String
)
