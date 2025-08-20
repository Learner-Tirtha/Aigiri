package com.example.aigiri.viewmodel
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PermissionViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val _locationPermissionGranted = MutableStateFlow(checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION))
    val locationPermissionGranted: StateFlow<Boolean> = _locationPermissionGranted

    private val _cameraPermissionGranted = MutableStateFlow(checkPermission(android.Manifest.permission.CAMERA))
    val cameraPermissionGranted: StateFlow<Boolean> = _cameraPermissionGranted

    private val _microphonePermissionGranted = MutableStateFlow(checkPermission(android.Manifest.permission.RECORD_AUDIO))
    val microphonePermissionGranted: StateFlow<Boolean> = _microphonePermissionGranted

    private val _contactsPermissionGranted = MutableStateFlow(checkPermission(android.Manifest.permission.READ_CONTACTS))
    val contactsPermissionGranted: StateFlow<Boolean> = _contactsPermissionGranted

    private val _notificationsPermissionGranted = MutableStateFlow(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            checkPermission(android.Manifest.permission.POST_NOTIFICATIONS)
        else true
    )
    val notificationsPermissionGranted: StateFlow<Boolean> = _notificationsPermissionGranted

    fun onPermissionResult(permission: String, isGranted: Boolean) {
        when (permission) {
            android.Manifest.permission.ACCESS_FINE_LOCATION -> _locationPermissionGranted.value = isGranted
            android.Manifest.permission.CAMERA -> _cameraPermissionGranted.value = isGranted
            android.Manifest.permission.RECORD_AUDIO -> _microphonePermissionGranted.value = isGranted
            android.Manifest.permission.READ_CONTACTS -> _contactsPermissionGranted.value = isGranted
            android.Manifest.permission.POST_NOTIFICATIONS -> _notificationsPermissionGranted.value = isGranted
        }
    }

    fun allPermissionsGranted(): Boolean {
        return locationPermissionGranted.value &&
                cameraPermissionGranted.value &&
                microphonePermissionGranted.value &&
                contactsPermissionGranted.value &&
                notificationsPermissionGranted.value
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}
