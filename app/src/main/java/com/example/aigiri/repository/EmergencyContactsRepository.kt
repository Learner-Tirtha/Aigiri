package com.example.aigiri.repository

import com.example.aigiri.dao.EmergencyContactDao
import com.example.aigiri.model.EmergencyContact

class EmergencyContactsRepository(
    private val contactDao: EmergencyContactDao = EmergencyContactDao()
) {
    suspend fun addEmergencyContact(userUid: String, contact: EmergencyContact): Result<Unit> {
        return contactDao.addEmergencyContact(userUid, contact)
    }

    suspend fun getEmergencyContacts(userUid: String): Result<List<EmergencyContact>> {
        return contactDao.getEmergencyContacts(userUid)
    }

    suspend fun deleteEmergencyContact(userUid: String, contactDocId: String): Result<Unit> {
        return contactDao.deleteEmergencyContact(userUid, contactDocId)
    }

    suspend fun updateEmergencyContact(
        userUid: String,
        contactDocId: String,
        updatedFields: Map<String, Any>
    ): Result<Unit> {
        return contactDao.updateEmergencyContact(userUid, contactDocId, updatedFields)
    }


}
