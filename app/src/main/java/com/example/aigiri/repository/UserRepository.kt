package com.example.aigiri.repository

import android.util.Log
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.aigiri.dao.UserDao
import com.example.aigiri.model.EmergencyContact
import com.example.aigiri.model.User

class UserRepository(private val userDao: UserDao = UserDao()) {

    suspend fun saveUser(user: User): Result<String> {
        return userDao.saveUser(user)
    }
    suspend fun fetchPhoneNoByuserID(UID:String):String{
      return userDao.getUserById(UID).getOrNull()?.phoneNo?:"+919978920881"

    }

    suspend fun isPhoneTaken(phoneNo: String): Result<Boolean> {
        return userDao.isPhoneTaken(phoneNo)
    }

    suspend fun isEmailTaken(email: String): Result<Boolean> {
        return userDao.isEmailTaken(email)
    }

    suspend fun isUsernameTaken(username: String): Result<Boolean> {
        return userDao.isUsernameTaken(username)
    }
    suspend fun fetchUserByUsername(username: String): Result<User?> {
        return userDao.getUserByUsername(username)
    }
    suspend fun fetchPasswordByUsername(username: String):Result<String?>{
        return userDao.getPasswordByUsername(username)
    }
    suspend fun updatePasswordByPhone(phoneNo: String, newPassword: String): Result<Unit> {
        return userDao.updatePasswordByPhoneNo(phoneNo, newPassword)
    }
    suspend fun isPasswordValid(userId: String, inputPassword: String): Result<Boolean> {
        return try {
            val hashResult = userDao.getHashedPasswordByUserId(userId)
            if (hashResult.isSuccess) {
                val storedHashedPassword = hashResult.getOrNull()
                if (storedHashedPassword != null) {
                    val verifyResult = BCrypt.verifyer()
                        .verify(inputPassword.toCharArray(), storedHashedPassword)
                  Log.e("oldpassword","corrected: ${verifyResult.verified}")
                    Result.success(verifyResult.verified)
                } else {
                    Result.failure(Exception("Password not found for user"))
                }
            } else {
                Result.failure(hashResult.exceptionOrNull() ?: Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 🔄 Update password (hashed first)
    suspend fun updatePassword(userId: String, newPassword: String): Result<Unit> {
        return try {
            val hashedPassword = BCrypt.withDefaults()
                .hashToString(10, newPassword.toCharArray())

            userDao.updatePasswordByUserId(userId, hashedPassword)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

