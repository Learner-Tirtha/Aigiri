package com.example.aigiri.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.aigiri.R
import com.example.aigiri.dao.LiveLocationDao
import com.example.aigiri.model.UserLocation
import com.google.android.gms.location.*

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null

    private lateinit var liveLocationDao: LiveLocationDao

    private var userId: String? = null
    private var sessionId: String? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("LocationService", "✅ onCreate: Service created")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        liveLocationDao = LiveLocationDao() // or inject if using DI
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("LocationService", "🚀 onStartCommand: Service started with intent=$intent")
        userId = intent?.getStringExtra("USER_ID")
        sessionId = intent?.getStringExtra("SESSION_ID")

        if (userId == null || sessionId == null) {
            Log.w("LocationService", "⚠️ Missing USER_ID or SESSION_ID → stopping service")
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(1, createNotification())
        startLocationUpdates()

        // Mark active in DB
        Log.d("LocationService", "🟢 Setting isActive=true for user=$userId")
        liveLocationDao.dbRef.child("users").child(userId!!).child("isActive").setValue(true)

        return START_STICKY
    }

    override fun onDestroy() {
        Log.d("LocationService", "🛑 onDestroy: Service destroyed → set isActive=false")
        super.onDestroy()
        stopLocationUpdates()

        // Mark user inactive in DB
        userId?.let {
            liveLocationDao.dbRef
                .child("users")
                .child(it)
                .child("isActive")
                .setValue(false)
        }
    }

    private fun stopLocationUpdates() {
        Log.d("LocationService", "🛑 stopLocationUpdates: Removing location callback")
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            locationCallback = null
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        Log.d("LocationService", "🔔 Creating notification for foreground service")
        val channelId = "location_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Location Tracking", NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Aigiri Location Sharing")
            .setContentText("Your live location is being shared")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }

    private fun startLocationUpdates() {
        Log.d("LocationService", "📍 startLocationUpdates called for user=$userId, session=$sessionId")

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 5000L
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                val lat = loc.latitude
                val lng = loc.longitude
                val timestamp = System.currentTimeMillis()

                Log.d(
                    "LocationService",
                    "📌 New location → lat=$lat, lng=$lng, time=$timestamp"
                )

                val userLoc = UserLocation(lat, lng, timestamp)
                userId?.let { uid ->
                    sessionId?.let { sid ->
                        Log.d(
                            "LocationService",
                            "🔥 Writing to DB → user=$uid, session=$sid, location=$userLoc"
                        )
                        liveLocationDao.updateUserLocationInSession(sid, uid, userLoc)
                        liveLocationDao.updateUserOwnLocation(uid, userLoc)
                    }
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("LocationService", "❌ Missing location permissions → stopping service")
            stopSelf()
            return
        }

        Log.d("LocationService", "✅ Requesting location updates (5s interval)")
        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback!!,
            mainLooper
        )
    }
}
