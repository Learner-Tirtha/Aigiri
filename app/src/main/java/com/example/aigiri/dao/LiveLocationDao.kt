package com.example.aigiri.dao
import android.util.Log
import com.example.aigiri.model.UserLocation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LiveLocationDao {

     val dbRef: DatabaseReference = FirebaseDatabase
        .getInstance("https://uninotify-3e948-default-rtdb.asia-southeast1.firebasedatabase.app")
        .reference

    suspend fun getOrCreateSession(userId: String): String? = suspendCancellableCoroutine { cont ->
        val userRef = dbRef.child("users").child(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val isActive = snapshot.child("isActive").getValue(Boolean::class.java) ?: false
                    val sessionId = snapshot.child("sessionId").getValue(String::class.java)
                    Log.d("LiveLocationDao", "User $userId is active: $isActive")

                    if (isActive && !sessionId.isNullOrEmpty()) {
                        Log.d("LiveLocationDao", "Session ID already exists for user $userId: $sessionId")
                        cont.resume(sessionId) {}
                    } else {
                        // If user was inactive and sessionId exists, remove old session doc
                        if (!sessionId.isNullOrEmpty()) {
                            Log.d("LiveLocationDao", "Removing old session $sessionId for inactive user $userId")
                            dbRef.child("sos_session").child(sessionId).removeValue()
                        }

                        val newSessionId = generateSessionId()
                        val userUpdate = mapOf(
                            "isActive" to true,
                            "sessionId" to newSessionId
                        )

                        userRef.updateChildren(userUpdate).addOnSuccessListener {
                            dbRef.child("sos_session").child(newSessionId)
                                .child("users").child(userId).setValue("")
                                .addOnSuccessListener {
                                    cont.resume(newSessionId) {}
                                }
                                .addOnFailureListener { cont.resume(null) {} }
                        }.addOnFailureListener {
                            cont.resume(null) {}
                        }
                    }
                } else {
                    val newSessionId = generateSessionId()
                    val userData = mapOf("isActive" to true, "sessionId" to newSessionId)

                    userRef.setValue(userData).addOnSuccessListener {
                        dbRef.child("sos_session").child(newSessionId)
                            .child("users").child(userId).setValue("")
                            .addOnSuccessListener {
                                cont.resume(newSessionId) {}
                            }
                            .addOnFailureListener { cont.resume(null) {} }
                    }.addOnFailureListener {
                        cont.resume(null) {}
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                cont.resume(null) {}
            }
        })
    }




    private fun generateSessionId(): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "session_$timestamp"
    }





    // 3. Update user's location under /sos_session/sessionId/users/userId
    fun updateUserLocationInSession(sessionId: String, userId: String, location: UserLocation) {
        val path = dbRef.child("sos_session").child(sessionId).child("users").child(userId)
        path.setValue(location)
            .addOnSuccessListener {
                Log.d("LiveLocationDao", "Updated location for $userId in session $sessionId")
            }.addOnFailureListener {
                Log.e("LiveLocationDao", "Failed to update location: ${it.message}")
            }
    }

    // 4. Update user's location under /users/userId/location
    fun updateUserOwnLocation(userId: String, location: UserLocation) {
        dbRef.child("users").child(userId).child("location").setValue(location)
            .addOnSuccessListener {
                Log.d("LiveLocationDao", "Updated user personal location")
            }.addOnFailureListener {
                Log.e("LiveLocationDao", "Failed to update personal location: ${it.message}")
            }
    }

    }








