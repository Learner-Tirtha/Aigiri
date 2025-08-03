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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.aigiri.viewmodel.LiveStreamViewModel
import com.zegocloud.uikit.prebuilt.livestreaming.ZegoUIKitPrebuiltLiveStreamingConfig
import com.zegocloud.uikit.prebuilt.livestreaming.ZegoUIKitPrebuiltLiveStreamingFragment
import com.zegocloud.uikit.prebuilt.livestreaming.internal.components.ZegoLeaveLiveStreamingListener

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
    val context = LocalContext.current
    val session by viewModel.liveSession.collectAsState()

    val activity = remember(context) { context.findActivity() }
    val fragmentManager = activity?.supportFragmentManager

    val fragmentContainerId = remember { View.generateViewId() }

    if (activity == null || fragmentManager == null) {
        Text("Unable to load live stream UI")
        return
    }

    Box(modifier = modifier.fillMaxSize()) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx -> FrameLayout(ctx).apply { id = fragmentContainerId } },
            update = { container ->
                if (fragmentManager.findFragmentById(fragmentContainerId) == null) {
                    val config = ZegoUIKitPrebuiltLiveStreamingConfig.host()

                    config.leaveLiveStreamingListener = object : ZegoLeaveLiveStreamingListener {
                        override fun onLeaveLiveStreaming() {
                            navController.navigate("dashboard") {
                                popUpTo("livecall") { inclusive = true }
                            }
                        }
                    }

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
                }
            }
        )


//
//          val liveLink = "https://aigiri.app/join/${session.liveID}"
//
//            Card(
//                modifier = Modifier
//                    .align(Alignment.TopEnd)
//                    .padding(16.dp)
//                    .widthIn(min = 200.dp),
//                shape = RoundedCornerShape(12.dp),
//                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
//            ) {
//                Column(modifier = Modifier.padding(12.dp)) {
//                    Text(text = "Share this stream:", style = MaterialTheme.typography.bodyMedium)
//
//                    Text(
//                        text = liveLink,
//                        style = MaterialTheme.typography.bodySmall,
//                        modifier = Modifier.padding(top = 8.dp)
//                    )
//
//                    Icon(
//                        imageVector = Icons.Default.ContentCopy,
//                        contentDescription = "Copy",
//                        modifier = Modifier
//                            .padding(top = 8.dp)
//                            .size(24.dp)
//                            .clickable {
//                                val clipboard = ContextCompat.getSystemService(
//                                    context,
//                                    ClipboardManager::class.java
//                                )
//                                clipboard?.setPrimaryClip(
//                                    ClipData.newPlainText("LiveStream Link", liveLink)
//                                )
//                                Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
//                            }
//                    )
//                }
            }
        }

