package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: String, // email or UUID
    val username: String,       // unique handle starting with @
    val displayName: String,
    val bio: String,
    val avatarUrl: String,
    val email: String,
    val createdAt: Long,
    val isVerified: Boolean = false, // Email verified indicator
    val isBlueVerified: Boolean = false, // VIP verified badge (Facebook/WhatsApp style)
    val passwordHash: String = ""
)

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val isGroup: Boolean,
    val name: String,         // Group name or individual display name
    val avatarUrl: String,     // Group avatar or individual avatar
    val createdBy: String,
    val createdAt: Long
)

@Entity(
    tableName = "conversation_members",
    primaryKeys = ["conversationId", "userId"]
)
data class ConversationMemberEntity(
    val conversationId: String,
    val userId: String,
    val role: String, // "admin" or "member"
    val joinedAt: Long
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val content: String,
    val mediaUrl: String?,
    val mediaType: String?, // "image" or null
    val createdAt: Long,
    val deletedAt: Long? = null
)

@Entity(tableName = "statuses")
data class StatusEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val content: String,
    val mediaUrl: String?,
    val backgroundColor: String?, // Hex string, e.g. "#1A237E"
    val createdAt: Long,
    val expiresAt: Long
)

@Entity(
    tableName = "status_views",
    primaryKeys = ["statusId", "viewerId"]
)
data class StatusViewEntity(
    val statusId: String,
    val viewerId: String,
    val viewedAt: Long
)
