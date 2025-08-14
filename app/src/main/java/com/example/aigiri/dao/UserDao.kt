package com.example.aigiri.dao

import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.aigiri.model.EmergencyContact
import com.example.aigiri.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserDao(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    private val usersCollection = db.collection("User")
    suspend fun saveUser(user: User): Result<String> {
        return try {
            val docRef = usersCollection
                .add(user)  // Firestore generates a unique document ID automatically
                .await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    // Check if phone number exists inside users collection (query phoneNo field)
    suspend fun isPhoneTaken(phoneNo: String): Result<Boolean> {
        return try {
            val snapshot = usersCollection
                .whereEqualTo("phone_no", phoneNo)
                .get()
                .await()
            Result.success(!snapshot.isEmpty)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Check if email is already used
    suspend fun isEmailTaken(email: String): Result<Boolean> {
        return try {
            val snapshot = usersCollection
                .whereEqualTo("email", email)
                .get()
                .await()
            Result.success(!snapshot.isEmpty)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Check if username is already used
    suspend fun isUsernameTaken(username: String): Result<Boolean> {
        return try {
            val snapshot = usersCollection
                .whereEqualTo("username", username)
                .get()
                .await()
            Result.success(!snapshot.isEmpty)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getUserByUsername(username: String): Result<User?> {
        return try {
            val snapshot = usersCollection
                .whereEqualTo("username", username)
                .limit(1)
                .get()
                .await()

            val user = if (snapshot.documents.isNotEmpty())
                snapshot.documents[0].toObject(User::class.java)
            else
                null

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getUserById(userId: String): Result<User?> {
        return try {
            val document = usersCollection
                .document(userId)
                .get()
                .await()
            val user = document.toObject(User::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getPasswordByUsername(username: String): Result<String?> {
        return try {
            val snapshot = usersCollection
                .whereEqualTo("username", username)
                .limit(1)
                .get()
                .await()

            val password = if (snapshot.documents.isNotEmpty()) {
                snapshot.documents[0].getString("password")
            } else {
                null
            }

            Result.success(password)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun updatePasswordByPhoneNo(phoneNo: String, newPassword: String): Result<Unit> {
        return try {
            val snapshot = usersCollection
                .whereEqualTo("phone_no", phoneNo)
                .limit(1)
                .get()
                .await()

            if (snapshot.documents.isNotEmpty()) {
                val docId = snapshot.documents[0].id
                usersCollection.document(docId)
                    .update("password", newPassword)
                    .await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("User with phone number $phoneNo not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getHashedPasswordByUserId(userId: String): Result<String?> {
        return try {
            val document = usersCollection
                .document(userId)
                .get()
                .await()

            val hashedPassword = document.getString("password")
            Result.success(hashedPassword)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePasswordByUserId(userId: String, hashedPassword: String): Result<Unit> {
        return try {
            usersCollection
                .document(userId)
                .update("password", hashedPassword)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}
