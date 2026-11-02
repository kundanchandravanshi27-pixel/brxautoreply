package com.example.data.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val username: String,
    val mobile: String,
    val passwordHash: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String,
    val contactName: String,
    val packageName: String,
    val isGroup: Boolean = false,
    val paused: Boolean = false,
    val blocked: Boolean = false,
    val lastMessageAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages", foreignKeys = [
    ForeignKey(entity = ChatEntity::class, parentColumns = ["id"], childColumns = ["chatId"], onDelete = ForeignKey.CASCADE)
], indices = [Index("chatId")])
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chatId: String,
    val direction: String, // "IN" or "OUT"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "SENT", // "PENDING", "SENT", "FAILED", "SKIPPED"
    val reason: String? = null
)

@Entity(tableName = "stats")
data class StatEntity(
    @PrimaryKey val date: String, // yyyy-MM-dd
    val receivedCount: Int = 0,
    val sentCount: Int = 0,
    val leadsCount: Int = 0
)

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats ORDER BY lastMessageAt DESC")
    fun getAllChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE id = :chatId")
    suspend fun getChatById(chatId: String): ChatEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertChat(chat: ChatEntity)

    @Query("UPDATE chats SET paused = :paused WHERE id = :chatId")
    suspend fun setChatPaused(chatId: String, paused: Boolean)

    @Query("UPDATE chats SET blocked = :blocked WHERE id = :chatId")
    suspend fun setChatBlocked(chatId: String, blocked: Boolean)

    @Query("DELETE FROM chats WHERE id = :chatId")
    suspend fun deleteChat(chatId: String)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMessagesForChatSync(chatId: String, limit: Int = 10): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE timestamp < :threshold")
    suspend fun deleteOldMessages(threshold: Long)
}

@Dao
interface StatDao {
    @Query("SELECT * FROM stats WHERE date = :date")
    suspend fun getStatForDate(date: String): StatEntity?

    @Query("SELECT * FROM stats ORDER BY date DESC LIMIT 30")
    fun getRecentStats(): Flow<List<StatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStat(stat: StatEntity)
}

@Database(entities = [UserEntity::class, ChatEntity::class, MessageEntity::class, StatEntity::class], version = 2, exportSchema = false)
abstract class BizRynoDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun statDao(): StatDao

    companion object {
        @Volatile
        private var INSTANCE: BizRynoDatabase? = null

        fun getDatabase(context: android.content.Context): BizRynoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    BizRynoDatabase::class.java,
                    "bizryno_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
