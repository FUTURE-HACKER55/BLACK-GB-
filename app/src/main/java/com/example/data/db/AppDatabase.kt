package com.example.data.db

import android.content.Context
import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: String): ProfileEntity?

    @Query("SELECT * FROM profiles WHERE email = :email LIMIT 1")
    suspend fun getProfileByEmail(email: String): ProfileEntity?

    @Query("SELECT * FROM profiles WHERE username = :username LIMIT 1")
    suspend fun getProfileByUsername(username: String): ProfileEntity?

    @Query("SELECT * FROM profiles WHERE username LIKE :query OR displayName LIKE :query OR email LIKE :query")
    fun searchProfiles(query: String): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles")
    suspend fun getAllProfiles(): List<ProfileEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)
}

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations WHERE id = :id")
    suspend fun getConversationById(id: String): ConversationEntity?

    @Query("""
        SELECT c.* FROM conversations c
        INNER JOIN conversation_members cm ON c.id = cm.conversationId
        WHERE cm.userId = :userId
        ORDER BY c.createdAt DESC
    """)
    fun getConversationsForUser(userId: String): Flow<List<ConversationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: ConversationMemberEntity)

    @Query("SELECT * FROM conversation_members WHERE conversationId = :conversationId")
    suspend fun getMembersForConversation(conversationId: String): List<ConversationMemberEntity>

    @Query("""
        SELECT cm.conversationId FROM conversation_members cm
        INNER JOIN conversation_members cm2 ON cm.conversationId = cm2.conversationId
        INNER JOIN conversations c ON cm.conversationId = c.id
        WHERE cm.userId = :user1 AND cm2.userId = :user2 AND c.isGroup = 0
        LIMIT 1
    """)
    suspend fun getDirectConversationBetweenUsers(user1: String, user2: String): String?
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY createdAt ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY createdAt DESC LIMIT 1")
    fun getLatestMessageForConversation(conversationId: String): Flow<MessageEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("UPDATE messages SET deletedAt = :deletedAt WHERE id = :messageId")
    suspend fun softDeleteMessage(messageId: String, deletedAt: Long)
}

@Dao
interface StatusDao {
    @Query("SELECT * FROM statuses WHERE expiresAt > :now ORDER BY createdAt DESC")
    fun getActiveStatuses(now: Long): Flow<List<StatusEntity>>

    @Query("SELECT * FROM statuses WHERE userId = :userId ORDER BY createdAt DESC")
    fun getStatusesForUser(userId: String): Flow<List<StatusEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatus(status: StatusEntity)

    @Query("DELETE FROM statuses WHERE id = :statusId")
    suspend fun deleteStatus(statusId: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStatusView(view: StatusViewEntity)

    @Query("SELECT * FROM status_views WHERE statusId = :statusId")
    fun getViewsForStatus(statusId: String): Flow<List<StatusViewEntity>>
}

@Database(
    entities = [
        ProfileEntity::class,
        ConversationEntity::class,
        ConversationMemberEntity::class,
        MessageEntity::class,
        StatusEntity::class,
        StatusViewEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun statusDao(): StatusDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "black_gb_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
