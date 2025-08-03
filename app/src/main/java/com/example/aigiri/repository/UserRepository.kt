package com.example.aigiri.repository

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
}
