package com.example.aigiri.dao

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


}
