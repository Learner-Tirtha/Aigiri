package com.example.aigiri.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import android.content.Intent
import android.net.Uri
import com.example.aigiri.viewmodel.LiveStreamViewModel
import com.zegocloud.uikit.prebuilt.livestreaming.ZegoUIKitPrebuiltLiveStreamingConfig
import com.zegocloud.uikit.prebuilt.livestreaming.ZegoUIKitPrebuiltLiveStreamingFragment
import com.zegocloud.uikit.prebuilt.livestreaming.internal.components.ZegoLeaveLiveStreamingListener
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import android.util.Log
fun Context.findActivity(): AppCompatActivity? {
    var ctx = this
    var depth = 0
    while (ctx is android.content.ContextWrapper) {
        if (ctx is AppCompatActivity) {
            return ctx
        }
        ctx = ctx.baseContext
        depth++
    }
    return null
}



@Composable
fun LiveStreamScreen(
    viewModel: LiveStreamViewModel,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val TAG = "LiveStreamScreen"
    val context = LocalContext.current
    val session = viewModel.liveSession.collectAsState().value

    val activity = remember(context) { context.findActivity() }
    val fragmentManager = activity?.supportFragmentManager
    val fragmentContainerId = remember { View.generateViewId() }

    // 🔹 Always prepare a fresh session when entering
    LaunchedEffect(Unit) {
        Log.d(TAG, "Preparing live session...")
        viewModel.prepareLiveSession()
    }

    if (session == null || activity == null || fragmentManager == null) {
        Log.w(TAG, "Session or Activity or FragmentManager is NULL. session=$session activity=$activity fm=$fragmentManager")
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(15.dp)
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(32.dp),
        )
        return
    }

    Log.d(TAG, "Session loaded: liveID=${session.liveID}, userID=${session.userID}, userName=${session.userName}")

    Box(modifier = modifier.fillMaxSize()) {

        // ===================== Zego Fragment =====================
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                Log.d(TAG, "Creating FrameLayout container with id=$fragmentContainerId")
                FrameLayout(ctx).apply { id = fragmentContainerId }
            },
            update = {
                val existingFragment = fragmentManager.findFragmentById(fragmentContainerId)
                if (existingFragment == null) {
                    Log.d(TAG, "No existing fragment. Creating new Zego fragment...")

                    // 🔹 For now assuming host mode; you can add isHost flag later
                    val config = ZegoUIKitPrebuiltLiveStreamingConfig.host()

                    config.leaveLiveStreamingListener = object : ZegoLeaveLiveStreamingListener {
                        override fun onLeaveLiveStreaming() {
                            Log.d(TAG, "LeaveLiveStreamingListener triggered")
                            fragmentManager.findFragmentById(fragmentContainerId)?.let {
                                fragmentManager.beginTransaction()
                                    .remove(it)
                                    .commitNowAllowingStateLoss()
                                Log.d(TAG, "Fragment removed")
                            }
                            viewModel.releaseSession()
                            Log.d(TAG, "Session released. Navigating back to dashboard...")
                            activity?.window?.decorView?.post {
                                navController.navigate("dashboard") {
                                    popUpTo("livecall") { inclusive = true }
                                }
                            }
                        }
                    }

                    Log.d(TAG, "Creating Zego fragment with appID=${session.appID}, liveID=${session.liveID}")
                    val fragment = ZegoUIKitPrebuiltLiveStreamingFragment.newInstance(
                        session.appID,
                        session.appSign,
                        session.userID,
                        session.userName,
                        session.liveID,
                        config
                    )

                    fragmentManager.beginTransaction()
                        .replace(fragmentContainerId, fragment)
                        .commitNowAllowingStateLoss()

                    Log.d(TAG, "Zego fragment committed")
                } else {
                    Log.d(TAG, "Fragment already exists, skipping creation")
                }
            }
        )

        // ===================== Share Link =====================
        val baseUrl = "https://uninotify-3e948.web.app"
        val liveLink = "$baseUrl/join/${session.liveID}?name=${Uri.encode(session.userName)}"
        Log.d(TAG, "Live link: $liveLink")

        var showShare by remember { mutableStateOf(true) }

        if (showShare) {
            Log.d(TAG, "Showing share dialog")
            AlertDialog(
                onDismissRequest = { showShare = false },
                title = { Text("Share this live stream") },
                text = { Text(liveLink) },
                confirmButton = {
                    TextButton(onClick = {
                        Log.d(TAG, "User clicked Share")
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, liveLink)
                        }
                        ContextCompat.startActivity(
                            context,
                            Intent.createChooser(shareIntent, "Share live link"),
                            null
                        )
                        showShare = false
                    }) { Text("Share") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        Log.d(TAG, "User clicked Copy")
                        val clipboard = ContextCompat.getSystemService(
                            context,
                            ClipboardManager::class.java
                        )
                        clipboard?.setPrimaryClip(
                            ClipData.newPlainText("LiveStream Link", liveLink)
                        )
                        Toast.makeText(context, "Link copied", Toast.LENGTH_SHORT).show()
                        showShare = false
                    }) { Text("Copy") }
                }
            )
        }

        // ===================== Back Press =====================
        var showExit by remember { mutableStateOf(false) }
        BackHandler(enabled = true) {
            Log.d(TAG, "BackHandler triggered, showing exit dialog")
            showExit = true
        }

        if (showExit) {
            AlertDialog(
                onDismissRequest = { showExit = false },
                title = { Text("Stop live streaming?") },
                text = { Text("Are you sure you want to stop the live and leave this screen?") },
                confirmButton = {
                    TextButton(onClick = {
                        Log.d(TAG, "User confirmed Stop")
                        fragmentManager.findFragmentById(fragmentContainerId)?.let {
                            fragmentManager.beginTransaction()
                                .remove(it)
                                .commitNowAllowingStateLoss()
                            Log.d(TAG, "Fragment removed on stop")
                        }
                        viewModel.releaseSession()
                        Log.d(TAG, "Session released on stop. Navigating back to dashboard...")
                        navController.navigate("dashboard") {
                            popUpTo("livecall") { inclusive = true }
                        }
                        showExit = false
                    }) { Text("Stop") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        Log.d(TAG, "User cancelled exit")
                        showExit = false
                    }) { Text("Cancel") }
                }
            )
        }
    }
}

