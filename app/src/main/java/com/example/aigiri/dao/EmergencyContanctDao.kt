package com.example.aigiri.dao

import com.example.aigiri.model.EmergencyContact
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class EmergencyContactDao(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    private val usersCollection = db.collection("User")

    suspend fun addEmergencyContact(userDocId: String, contact: EmergencyContact): Result<Unit> {
        return try {
            val docRef = usersCollection
                .document(userDocId)
                .collection("emergency_contacts")
                .document()  // Generate auto-ID

            docRef.set(contact).await()  // Write contact without including the doc ID in the data

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getContactDocIdByPhone(userId: String, phoneNumber: String): String? {
        val snapshot = usersCollection
            .document(userId)
            .collection("emergency_contacts")
            .whereEqualTo("phoneNumber", phoneNumber)
            .get()
            .await()

        return snapshot.documents.firstOrNull()?.id  // Return doc ID if found
    }

    // Get user's emergency contacts by Firestore user document ID
    suspend fun getEmergencyContacts(userDocId: String): Result<List<EmergencyContact>> {
        return try {
            val snapshot = usersCollection
                .document(userDocId)
                .collection("emergency_contacts")
                .get()
                .await()

            val contacts = snapshot.documents.mapNotNull { doc ->
                doc.toObject(EmergencyContact::class.java)?.copy(id = doc.id)
            }

            Result.success(contacts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // Delete emergency contact by document ID
    suspend fun deleteEmergencyContact(userDocId: String, contactDocId: String): Result<Unit> {
        return try {
            usersCollection
                .document(userDocId)
                .collection("emergency_contacts")
                .document(contactDocId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update emergency contact by document ID and fields to change
    suspend fun updateEmergencyContact(
        userDocId: String,
        contactDocId: String,
        updatedFields: Map<String, Any>
    ): Result<Unit> {
        return try {
            usersCollection
                .document(userDocId)
                .collection("emergency_contacts")
                .document(contactDocId)
                .update(updatedFields)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
