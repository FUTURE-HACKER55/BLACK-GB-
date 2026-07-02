package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.api.GeminiClient
import com.example.data.db.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ChatRepository(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val profileDao = db.profileDao()
    private val conversationDao = db.conversationDao()
    private val messageDao = db.messageDao()
    private val statusDao = db.statusDao()

    private val prefs: SharedPreferences = context.getSharedPreferences("black_gb_prefs", Context.MODE_PRIVATE)

    companion object {
        const val CARL_ID = "black_carl_ai"
        private const val KEY_CURRENT_USER_ID = "current_user_id"
    }

    init {
        // Seed default system/simulation profiles on startup
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialData()
        }
    }

    // --- Authentication ---

    fun getCurrentUserId(): String? {
        return prefs.getString(KEY_CURRENT_USER_ID, null)
    }

    private fun setCurrentUserId(userId: String?) {
        if (userId == null) {
            prefs.edit().remove(KEY_CURRENT_USER_ID).apply()
        } else {
            prefs.edit().putString(KEY_CURRENT_USER_ID, userId).apply()
        }
    }

    suspend fun getProfile(id: String): ProfileEntity? = withContext(Dispatchers.IO) {
        profileDao.getProfileById(id)
    }

    suspend fun register(
        email: String,
        username: String,
        displayName: String,
        passwordHash: String
    ): Result<ProfileEntity> = withContext(Dispatchers.IO) {
        val cleanUsername = if (username.startsWith("@")) username else "@$username"
        
        // Validation: Unique username
        if (profileDao.getProfileByUsername(cleanUsername) != null) {
            return@withContext Result.failure(Exception("Ce nom d'utilisateur est déjà pris !"))
        }

        // Validation: Unique email
        if (profileDao.getProfileByEmail(email) != null) {
            return@withContext Result.failure(Exception("Cet email est déjà associé à un compte !"))
        }

        val userId = UUID.randomUUID().toString()
        val profile = ProfileEntity(
            id = userId,
            username = cleanUsername,
            displayName = displayName,
            bio = "Salut, j'utilise Black GB ! 🖤",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80", // Default nice avatar
            email = email,
            createdAt = System.currentTimeMillis(),
            isVerified = false, // Must verify email
            isBlueVerified = false,
            passwordHash = passwordHash
        )

        profileDao.insertProfile(profile)
        // Auto sign-in
        setCurrentUserId(userId)
        Result.success(profile)
    }

    suspend fun login(email: String, passwordHash: String): Result<ProfileEntity> = withContext(Dispatchers.IO) {
        val profile = profileDao.getProfileByEmail(email)
            ?: return@withContext Result.failure(Exception("Adresse email inconnue."))

        if (profile.passwordHash != passwordHash) {
            return@withContext Result.failure(Exception("Mot de passe incorrect."))
        }

        setCurrentUserId(profile.id)
        Result.success(profile)
    }

    fun logout() {
        setCurrentUserId(null)
    }

    suspend fun updateProfile(profile: ProfileEntity) = withContext(Dispatchers.IO) {
        profileDao.insertProfile(profile)
    }

    suspend fun verifyEmail(userId: String) = withContext(Dispatchers.IO) {
        val profile = profileDao.getProfileById(userId)
        if (profile != null) {
            profileDao.insertProfile(profile.copy(isVerified = true))
        }
    }

    suspend fun simulatePasswordReset(email: String): String = withContext(Dispatchers.IO) {
        val profile = profileDao.getProfileByEmail(email)
            ?: return@withContext "Aucun compte n'est enregistré avec cet email."
        
        val tempToken = UUID.randomUUID().toString().take(8).uppercase()
        // Save temporary password as token for direct recovery simulation
        profileDao.insertProfile(profile.copy(passwordHash = "RESET_$tempToken"))
        
        return@withContext "Token de réinitialisation envoyé à $email ! Utilisez le token : $tempToken"
    }

    suspend fun confirmPasswordReset(email: String, token: String, newPasswordHash: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val profile = profileDao.getProfileByEmail(email)
            ?: return@withContext Result.failure(Exception("Email inconnu."))

        if (profile.passwordHash != "RESET_$token") {
            return@withContext Result.failure(Exception("Token de réinitialisation invalide ou expiré."))
        }

        profileDao.insertProfile(profile.copy(passwordHash = newPasswordHash))
        Result.success(true)
    }

    // --- Seeding ---

    private suspend fun seedInitialData() {
        val existingCarl = profileDao.getProfileById(CARL_ID)
        if (existingCarl == null) {
            // Seed AI assistant Black-Carl GB
            profileDao.insertProfile(
                ProfileEntity(
                    id = CARL_ID,
                    username = "@carl",
                    displayName = "Black-Carl GB",
                    bio = "Assistant Officiel Black GB 🖤 (Gemini AI)",
                    avatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=150&q=80",
                    email = "carl@blackgb.ai",
                    createdAt = System.currentTimeMillis(),
                    isVerified = true,
                    isBlueVerified = true, // VIP badge by default
                    passwordHash = "ai_system_protected"
                )
            )

            // Seed Alice
            profileDao.insertProfile(
                ProfileEntity(
                    id = "alice_id",
                    username = "@alice",
                    displayName = "Alice Fontaine",
                    bio = "Black GB est tellement rapide ! 🟢",
                    avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&q=80",
                    email = "alice@gmail.com",
                    createdAt = System.currentTimeMillis(),
                    isVerified = true,
                    isBlueVerified = false,
                    passwordHash = "demo1234"
                )
            )

            // Seed Bob
            profileDao.insertProfile(
                ProfileEntity(
                    id = "bob_id",
                    username = "@bob",
                    displayName = "Bob Martin",
                    bio = "Ne pas déranger. 📱",
                    avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80",
                    email = "bob@gmail.com",
                    createdAt = System.currentTimeMillis(),
                    isVerified = true,
                    isBlueVerified = true, // Bob also paid for a blue check
                    passwordHash = "demo1234"
                )
            )

            // Seed standard active statuses for Alice and Bob
            val oneHourAgo = System.currentTimeMillis() - 3600000
            val expireTomorrow = System.currentTimeMillis() + 86400000

            statusDao.insertStatus(
                StatusEntity(
                    id = "status_alice_1",
                    userId = "alice_id",
                    content = "Une superbe journée pour tester Black GB ! 🟢🖤",
                    mediaUrl = null,
                    backgroundColor = "#005C4B",
                    createdAt = oneHourAgo,
                    expiresAt = expireTomorrow
                )
            )

            statusDao.insertStatus(
                StatusEntity(
                    id = "status_bob_1",
                    userId = "bob_id",
                    content = "Café du matin ☕️",
                    mediaUrl = "https://images.unsplash.com/photo-1509042239860-f550ce710b93?auto=format&fit=crop&w=400&q=80",
                    backgroundColor = "#111B21",
                    createdAt = oneHourAgo - 1800000,
                    expiresAt = expireTomorrow
                )
            )
        }
    }

    // --- Chats & Contacts ---

    fun searchContacts(query: String): Flow<List<ProfileEntity>> {
        val cleanQuery = "%${query.trim()}%"
        return profileDao.searchProfiles(cleanQuery)
    }

    suspend fun getAllProfiles(): List<ProfileEntity> = withContext(Dispatchers.IO) {
        profileDao.getAllProfiles()
    }

    fun getConversations(userId: String): Flow<List<ConversationEntity>> {
        return conversationDao.getConversationsForUser(userId)
    }

    suspend fun getOrCreateDirectConversation(senderId: String, receiverId: String): String = withContext(Dispatchers.IO) {
        val existingId = conversationDao.getDirectConversationBetweenUsers(senderId, receiverId)
        if (existingId != null) {
            return@withContext existingId
        }

        // Create new direct convo
        val convoId = UUID.randomUUID().toString()
        val receiverProfile = profileDao.getProfileById(receiverId)
        val receiverName = receiverProfile?.displayName ?: "Utilisateur"
        val receiverAvatar = receiverProfile?.avatarUrl ?: ""

        conversationDao.insertConversation(
            ConversationEntity(
                id = convoId,
                isGroup = false,
                name = receiverName,
                avatarUrl = receiverAvatar,
                createdBy = senderId,
                createdAt = System.currentTimeMillis()
            )
        )

        conversationDao.insertMember(
            ConversationMemberEntity(convoId, senderId, "admin", System.currentTimeMillis())
        )
        conversationDao.insertMember(
            ConversationMemberEntity(convoId, receiverId, "member", System.currentTimeMillis())
        )

        return@withContext convoId
    }

    suspend fun createGroupConversation(name: String, memberIds: List<String>, createdBy: String): String = withContext(Dispatchers.IO) {
        val convoId = UUID.randomUUID().toString()
        conversationDao.insertConversation(
            ConversationEntity(
                id = convoId,
                isGroup = true,
                name = name,
                avatarUrl = "https://images.unsplash.com/photo-1582213782179-e0d53f98f2ca?auto=format&fit=crop&w=150&q=80", // Group meeting placeholder
                createdBy = createdBy,
                createdAt = System.currentTimeMillis()
            )
        )

        conversationDao.insertMember(
            ConversationMemberEntity(convoId, createdBy, "admin", System.currentTimeMillis())
        )
        for (memberId in memberIds) {
            if (memberId != createdBy) {
                conversationDao.insertMember(
                    ConversationMemberEntity(convoId, memberId, "member", System.currentTimeMillis())
                )
            }
        }

        // Insert first system message
        messageDao.insertMessage(
            MessageEntity(
                id = UUID.randomUUID().toString(),
                conversationId = convoId,
                senderId = "system",
                content = "Groupe créé par le créateur. Bienvenue ! 💬🟢",
                mediaUrl = null,
                mediaType = null,
                createdAt = System.currentTimeMillis()
            )
        )

        return@withContext convoId
    }

    suspend fun getMembersForConversation(convoId: String): List<ProfileEntity> = withContext(Dispatchers.IO) {
        val members = conversationDao.getMembersForConversation(convoId)
        members.mapNotNull { profileDao.getProfileById(it.userId) }
    }

    // --- Messages ---

    fun getMessages(conversationId: String): Flow<List<MessageEntity>> {
        return messageDao.getMessagesForConversation(conversationId)
    }

    fun getLatestMessage(conversationId: String): Flow<MessageEntity?> {
        return messageDao.getLatestMessageForConversation(conversationId)
    }

    suspend fun sendMessage(
        conversationId: String,
        senderId: String,
        content: String,
        mediaUrl: String? = null,
        mediaType: String? = null
    ) = withContext(Dispatchers.IO) {
        val msgId = UUID.randomUUID().toString()
        val message = MessageEntity(
            id = msgId,
            conversationId = conversationId,
            senderId = senderId,
            content = content,
            mediaUrl = mediaUrl,
            mediaType = mediaType,
            createdAt = System.currentTimeMillis()
        )
        messageDao.insertMessage(message)

        // Check if conversation involves Black-Carl GB
        val members = conversationDao.getMembersForConversation(conversationId)
        val isWithCarl = members.any { it.userId == CARL_ID } && senderId != CARL_ID

        if (isWithCarl) {
            // Fetch recent conversation history for context (last 5 messages)
            val recentMessages = messageDao.getMessagesForConversation(conversationId).firstOrNull() ?: emptyList()
            val history = recentMessages.takeLast(5).map {
                Pair(it.content, it.senderId == senderId)
            }

            // Launch AI assistant response in the background
            CoroutineScope(Dispatchers.IO).launch {
                val carlResponse = GeminiClient.generateResponse(content, history)
                val replyMsgId = UUID.randomUUID().toString()
                messageDao.insertMessage(
                    MessageEntity(
                        id = replyMsgId,
                        conversationId = conversationId,
                        senderId = CARL_ID,
                        content = carlResponse,
                        mediaUrl = null,
                        mediaType = null,
                        createdAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    // --- Statuses ---

    fun getActiveStatuses(): Flow<List<StatusEntity>> {
        return statusDao.getActiveStatuses(System.currentTimeMillis())
    }

    fun getMyStatuses(userId: String): Flow<List<StatusEntity>> {
        return statusDao.getStatusesForUser(userId)
    }

    suspend fun postStatus(
        userId: String,
        content: String,
        mediaUrl: String? = null,
        backgroundColor: String? = null
    ) = withContext(Dispatchers.IO) {
        val id = UUID.randomUUID().toString()
        val createdAt = System.currentTimeMillis()
        val expiresAt = createdAt + 24 * 60 * 60 * 1000 // 24 hours

        statusDao.insertStatus(
            StatusEntity(
                id = id,
                userId = userId,
                content = content,
                mediaUrl = mediaUrl,
                backgroundColor = backgroundColor ?: "#111B21",
                createdAt = createdAt,
                expiresAt = expiresAt
            )
        )
    }

    suspend fun deleteStatus(statusId: String) = withContext(Dispatchers.IO) {
        statusDao.deleteStatus(statusId)
    }

    suspend fun viewStatus(statusId: String, viewerId: String) = withContext(Dispatchers.IO) {
        statusDao.insertStatusView(
            StatusViewEntity(statusId, viewerId, System.currentTimeMillis())
        )
    }

    fun getViewsForStatus(statusId: String): Flow<List<StatusViewEntity>> {
        return statusDao.getViewsForStatus(statusId)
    }

    suspend fun getAllRegisteredProfiles(): List<ProfileEntity> = withContext(Dispatchers.IO) {
        profileDao.getAllProfiles()
    }
}
