package com.example.data.auth

import android.content.Context
import com.example.data.room.BizRynoDatabase
import com.example.data.room.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val context: Context) {
    private val userDao = BizRynoDatabase.getDatabase(context).userDao()

    suspend fun checkUsernameAvailable(username: String): Boolean = withContext(Dispatchers.IO) {
        val clean = username.lowercase().trim()
        val user = userDao.getUserByUsername(clean)
        user == null
    }

    suspend fun signUp(username: String, mobile: String, pass: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val clean = username.lowercase().trim()
            if (clean.length < 3) return@withContext Result.failure(Exception("Username must be at least 3 chars"))
            if (mobile.length < 10) return@withContext Result.failure(Exception("Enter valid mobile number"))
            if (pass.length < 6) return@withContext Result.failure(Exception("Password must be at least 6 chars"))

            if (!checkUsernameAvailable(clean)) {
                return@withContext Result.failure(Exception("Username already taken"))
            }

            userDao.insertUser(UserEntity(username = clean, mobile = mobile, passwordHash = pass))
            Result.success(clean)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(username: String, pass: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val clean = username.lowercase().trim()
            val user = userDao.getUserByUsername(clean)
            if (user == null) {
                return@withContext Result.failure(Exception("User not found. Please sign up."))
            }
            if (user.passwordHash != pass) {
                return@withContext Result.failure(Exception("Incorrect password"))
            }
            Result.success(clean)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
