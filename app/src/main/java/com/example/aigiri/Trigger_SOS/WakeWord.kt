package com.example.aigiri.Trigger_SOS


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import ai.picovoice.porcupine.PorcupineException
import ai.picovoice.porcupine.PorcupineManager
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.aigiri.network.TokenManager
import com.example.aigiri.network.wordConfig
import com.example.aigiri.repository.EmergencyContactsRepository
import com.example.aigiri.repository.SOSRepository
import com.example.aigiri.repository.UserRepository
import com.example.aigiri.viewmodel.SOSManager
import com.example.aigiri.viewmodel.SOSViewModel
import com.google.android.gms.location.LocationServices
import java.io.File

class WakeWordService() : Service() {

    private var porcupineManager: PorcupineManager? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            startForegroundService()
            initializePorcupine()
        } catch (e: Exception) {
            Log.e("WakeWordService", "Service failed: ${e.message}", e)
            stopSelf()
        }
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Wake Word Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Detects 'Emergency' for trigger checking"
                setSound(null, null)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Wake Word Detection Active")
            .setContentText("Listening for the word 'Emergency'...")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSilent(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun initializePorcupine() {
        try {
            val assetManager = assets

            val keywordPath = File(cacheDir, "Emergency_en_android_v3_0_0.ppn").apply {
                if (!exists()) {
                    assetManager.open("Emergency_en_android_v3_0_0.ppn").use { input ->
                        outputStream().use { output -> input.copyTo(output) }
                    }
                }
            }.absolutePath

            val modelPath = File(cacheDir, "porcupine_params.pv").apply {
                if (!exists()) {
                    assetManager.open("porcupine_params.pv").use { input ->
                        outputStream().use { output -> input.copyTo(output) }
                    }
                }
            }.absolutePath

            porcupineManager = PorcupineManager.Builder()
                .setAccessKey(wordConfig.API_Key)
                .setKeywordPath(keywordPath)
                .setModelPath(modelPath)
                .setSensitivity(0.7f)
                .build(applicationContext) { keywordIndex ->
                    if (keywordIndex >= 0) {
                        Log.d("WakeWordService", "Wake word 'Emergency' detected")
                        triggerSOS()
                    }
                }

            porcupineManager?.start()
            Log.d("WakeWordService", "Porcupine started")
        } catch (e: PorcupineException) {
            Log.e("WakeWordService", "Porcupine error: ${e.message}", e)
            stopSelf()
        }
    }

    private fun triggerSOS() {
        val sosManager = SOSManager(
            context = applicationContext,
            sosRepository = SOSRepository(applicationContext),
            emergencyRepository = EmergencyContactsRepository(),
            userRepository = UserRepository(),
            tokenManager = TokenManager(applicationContext)
        )

        sosManager.triggerSOS()
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            porcupineManager?.stop()
            porcupineManager?.delete()
            Log.d("WakeWordService", "Porcupine cleaned up")
        } catch (e: PorcupineException) {
            Log.e("WakeWordService", "Cleanup error: ${e.message}", e)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "WakeWordServiceChannel"
        private const val NOTIFICATION_ID = 1
    }
}
