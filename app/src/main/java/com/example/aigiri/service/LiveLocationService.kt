package com.example.aigiri.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.aigiri.MainActivity
import com.example.aigiri.R
import com.example.aigiri.dao.LiveLocationDao
import com.example.aigiri.network.TokenManager
import com.example.aigiri.network.FirebaseConfig
import com.example.aigiri.repository.LiveLocationRepository
import com.google.android.gms.location.*
import com.google.firebase.database.*

class LiveLocationService : Service() {

    private lateinit var fusedClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var tokenManager: TokenManager
    private val liveRepo: LiveLocationRepository by lazy {
        val dao =LiveLocationDao()
        LiveLocationRepository(dao)
    }

    private var sharingListener: ValueEventListener? = null

    override fun onCreate() {
        super.onCreate()
        tokenManager = TokenManager(applicationContext)
        fusedClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val uid = tokenManager.getCachedUserId() ?: return
                val loc = result.lastLocation ?: return
                liveRepo.updateUserLocation(
                    userId = uid,
                    latitude = loc.latitude,
                    longitude = loc.longitude,
                    timestamp = System.currentTimeMillis()
                )
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!hasLocationPermission()) {
            Log.w("LiveLocationService", "Missing location permission. Stopping service.")
            stopSelf()
            return START_NOT_STICKY
        }

        startInForeground()
        startLocationUpdates()
        return START_STICKY
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startInForeground() {
        val channelId = "live_location_channel"
        val mgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Live Location",
                NotificationManager.IMPORTANCE_LOW
            )
            mgr.createNotificationChannel(channel)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or
                    (if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Aigiri is sharing your location")
            .setContentText("Live tracking is active for your SOS session")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        startForeground(1001, notification)

        val uid = tokenManager.getCachedUserId()
        if (uid != null) {
            val db = FirebaseDatabase.getInstance(FirebaseConfig.databaseURL)
            val ref = db.getReference("users").child(uid).child("sharing")
            ref.setValue(true)
            attachSharingListener(uid)
        } else {
            Log.w("LiveLocationService", "UID not found; cannot mark sharing=true")
        }
    }

    private fun startLocationUpdates() {
        if (!hasLocationPermission()) {
            Log.w("LiveLocationService", "Permission lost before starting location updates.")
            stopSelf()
            return
        }

        val request = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            15_000L
        )
            .setMinUpdateIntervalMillis(10_000L)
            .setMaxUpdateDelayMillis(30_000L)
            .build()

        try {
            fusedClient.requestLocationUpdates(request, locationCallback, mainLooper)
        } catch (se: SecurityException) {
            Log.e("LiveLocationService", "SecurityException while requesting updates: ${se.message}")
            stopSelf()
        }
    }


    private fun attachSharingListener(uid: String) {
        val db = FirebaseDatabase.getInstance(FirebaseConfig.databaseURL)
        val ref = db.getReference("users").child(uid).child("sharing")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val sharing = snapshot.getValue(Boolean::class.java) ?: false
                if (!sharing) {
                    Log.i("LiveLocationService", "Remote stop received (sharing=false). Stopping service.")
                    try { fusedClient.removeLocationUpdates(locationCallback) } catch (_: Exception) {}
                    stopForeground(true)
                    stopSelf()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("LiveLocationService", "Sharing listener cancelled: ${error.message}")
            }
        }
        sharingListener = listener
        ref.addValueEventListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            fusedClient.removeLocationUpdates(locationCallback)
        } catch (_: Exception) {}

        val uid = tokenManager.getCachedUserId()
        if (uid != null) {
            val db = FirebaseDatabase.getInstance(FirebaseConfig.databaseURL)
            val ref = db.getReference("users").child(uid).child("sharing")
            ref.setValue(false)
            sharingListener?.let { ref.removeEventListener(it) }
            sharingListener = null
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        fun start(context: Context) {
            val i = Intent(context, LiveLocationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, i)
            } else {
                context.startService(i)
            }
        }
        fun stop(context: Context) {
            context.stopService(Intent(context, LiveLocationService::class.java))
        }
    }
}
