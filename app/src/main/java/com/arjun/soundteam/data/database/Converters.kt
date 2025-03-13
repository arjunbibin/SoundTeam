import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromSkillsList(skills: String): List<String> {
        return skills.split(",") // Convert comma-separated string to List
    }

    @TypeConverter
    fun toSkillsString(skills: List<String>): String {
        return skills.joinToString(",") // Convert List to comma-separated string
    }

    @TypeConverter
    fun fromUserRole(role: String): UserRole {
        return UserRole.valueOf(role)
    }

    @TypeConverter
    fun toUserRole(role: UserRole): String {
        return role.name
    }
}
