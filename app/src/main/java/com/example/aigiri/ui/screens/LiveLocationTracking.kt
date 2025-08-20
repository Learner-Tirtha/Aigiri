package com.example.aigiri.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.aigiri.R
import com.example.aigiri.dao.LiveLocationDao
import com.example.aigiri.model.RouteInfo
import com.example.aigiri.model.UserLocation
import com.example.aigiri.network.OsrmClient
import com.example.aigiri.service.LocationService
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LiveLocationTracking(
    userId: String,
    liveLocationDao: LiveLocationDao,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var isSharing by remember { mutableStateOf(false) }
    var sessionId by remember { mutableStateOf<String?>(null) }
    var lastUpdated by remember { mutableStateOf("—") }

    var userGeoPoint by remember { mutableStateOf<GeoPoint?>(null) }
    val viewerMarkers = remember { mutableStateMapOf<String, GeoPoint>() }
    val viewerRoutes = remember { mutableStateMapOf<String, RouteInfo>() }


    val coroutineScope = rememberCoroutineScope()
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }

    // 🔑 Fused location client + callback state
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    var locationCallback by remember { mutableStateOf<LocationCallback?>(null) }
    fun GeoPoint.toLatLng() = LatLng(latitude, longitude)
    fun LatLng.toGeoPoint() = GeoPoint(latitude, longitude)


    // init OSMDroid
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }



    LaunchedEffect(userId) {
        val userRef = liveLocationDao.dbRef.child("users").child(userId)
        userRef.get().addOnSuccessListener { snapshot ->
            val isActiveDb = snapshot.child("isActive").getValue(Boolean::class.java) ?: false
            val existingSessionId = snapshot.child("sessionId").getValue(String::class.java)

            if (isActiveDb && !existingSessionId.isNullOrEmpty()) {
                // Restore session
                sessionId = existingSessionId
                isSharing = true

                // Start foreground service
                val intent = Intent(context, LocationService::class.java).apply {
                    putExtra("USER_ID", userId)
                    putExtra("SESSION_ID", existingSessionId)
                }
                context.startForegroundService(intent)

                // Start UI location updates
                locationCallback = startOSMLocationUpdates(context, fusedLocationClient) { lat, lng ->
                    userGeoPoint = GeoPoint(lat, lng)
                    lastUpdated = SimpleDateFormat("HH:mm:ss").format(Date())
                }

            }
        }
    }




    DisposableEffect(userId) {
        val userRef = liveLocationDao.dbRef.child("users").child(userId).child("isActive")
        val listener = object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                // just update button state
                isSharing = snapshot.getValue(Boolean::class.java) ?: false
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                // optional: log
            }
        }

        userRef.addValueEventListener(listener)

        onDispose {
            userRef.removeEventListener(listener)
        }
    }


    fun startSharing() {
        Log.d("LiveLocation", "startSharing() called for userId=$userId, isSharing=$isSharing")
        if (isSharing) return

        CoroutineScope(Dispatchers.Main).launch {
            val newSessionId = liveLocationDao.getOrCreateSession(userId)
            if (newSessionId.isNullOrEmpty()) {
                Log.e("LiveLocation", "Failed to get sessionId")
                return@launch
            }

            sessionId = newSessionId
            isSharing = true

            // Start foreground service
            val intent = Intent(context, LocationService::class.java).apply {
                putExtra("USER_ID", userId)
                putExtra("SESSION_ID", newSessionId)
            }
            context.startForegroundService(intent)

            // Start local updates for UI marker
            locationCallback = startOSMLocationUpdates(
                context,
                fusedLocationClient
            ) { lat, lng ->
                userGeoPoint = GeoPoint(lat, lng)
                lastUpdated = SimpleDateFormat("HH:mm:ss").format(Date())
            }

            Log.d("LiveLocation", "startSharing() called with userId=$userId, session=$sessionId")
        }
    }



    fun stopSharing() {
        if (!isSharing) return
        isSharing = false

        // Stop background service
        val intent = Intent(context, LocationService::class.java)
        context.stopService(intent)

        // Stop location updates from UI
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
        locationCallback = null

        // Update DB → mark user safe
        liveLocationDao.dbRef.child("users").child(userId).child("isActive").setValue(false)
    }


    LaunchedEffect(userGeoPoint, viewerMarkers) {
        val userLoc = userGeoPoint ?: return@LaunchedEffect

        viewerMarkers.forEach { (vid, viewerLoc) ->
            coroutineScope.launch {
                val routeInfo = OsrmClient.fetchRoute(userLoc.toLatLng(), viewerLoc.toLatLng())
                routeInfo?.let {
                    viewerRoutes[vid] = it
                    mapViewRef?.invalidate() // update UI immediately
                }
            }
        }
    }


    fun formatDuration(seconds: Double): String {
        val h = (seconds / 3600).toInt()
        val m = ((seconds % 3600) / 60).toInt()
        return if (h > 0) "${h}h ${m}m" else "${m} min"
    }


    // ✅ Auto cleanup if screen is disposed
    DisposableEffect(sessionId) {
        if (sessionId == null) return@DisposableEffect onDispose { }
        val viewersRef = liveLocationDao.dbRef.child("sos_session").child(sessionId!!).child("viewer")

        val listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, prev: String?) {
                val vid = snapshot.key ?: return
                val loc = snapshot.getValue(UserLocation::class.java) ?: return
                viewerMarkers[vid] = GeoPoint(loc.latitude, loc.longitude)
            }
            override fun onChildChanged(snapshot: DataSnapshot, prev: String?) {
                val vid = snapshot.key ?: return
                val loc = snapshot.getValue(UserLocation::class.java) ?: return
                viewerMarkers[vid] = GeoPoint(loc.latitude, loc.longitude)
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                val vid = snapshot.key ?: return
                viewerMarkers.remove(vid)
                viewerRoutes.remove(vid)
            }
            override fun onChildMoved(snapshot: DataSnapshot, prev: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        }

        viewersRef.addChildEventListener(listener)
        onDispose { viewersRef.removeEventListener(listener) }
    }




    Column(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            if (locationPermissionState.status.isGranted) {
                AndroidView(
                    factory = { ctx ->
                        MapView(ctx).apply {
                            setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
                            controller.setZoom(18.0)
                            mapViewRef = this

                            // 🚀 Setup OSMDroid's built-in location overlay
                            val locationOverlay = MyLocationNewOverlay(
                                GpsMyLocationProvider(ctx),
                                this
                            )
                            locationOverlay.enableMyLocation()       // start GPS/fused updates
                            locationOverlay.enableFollowLocation()   // keep camera centered
                            overlays.add(locationOverlay)
                        }
                    },
                    update = { mapView ->
                        // Clear viewer overlays first (but keep location overlay intact)
                        mapView.overlays.removeAll { it is Marker || it is Polyline }

                        // Viewer markers
                        viewerMarkers.forEach { (id, pos) ->
                            val marker = Marker(mapView).apply {
                                position = pos
                                title = "Viewer ${id.take(5)}"
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                                // ✅ Use a green drawable for viewers
                                icon = ContextCompat.getDrawable(mapView.context, R.drawable.ic_person_green)
                            }
                            mapView.overlays.add(marker)
                        }

                        // Viewer routes
                        viewerRoutes.forEach { (vid, routeInfo) ->
                            val points = routeInfo.points.map { it.toGeoPoint() }
                            val polyline = Polyline().apply {
                                setPoints(points)
                                outlinePaint.color = android.graphics.Color.MAGENTA
                                outlinePaint.strokeWidth = 6f
                            }
                            mapView.overlays.add(polyline)



                        }


                        mapView.invalidate()
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LaunchedEffect(Unit) { locationPermissionState.launchPermissionRequest() }
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Location permission required to show the map.")
                }
            }

            // Banner Overlay
            // Banner Overlay
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .background(Color.White.copy(alpha = 0.9f), shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
                    .align(Alignment.TopStart)
            ) {
                Text("Aigiri", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Last update: $lastUpdated", color = Color(0xFF6A1B9A), fontSize = 12.sp)

                if (viewerRoutes.isEmpty()) {
                    Text("No active viewers yet.", fontSize = 12.sp)
                } else {
                    viewerRoutes.forEach { (vid, route) ->
                        val distanceText = if (route.distanceMeters > 0) "%.1f km".format(route.distanceMeters / 1000) else "Calculating..."
                        val etaText = if (route.durationSeconds > 0) formatDuration(route.durationSeconds) else "Calculating..."
                        Text(
                            text = "Viewer ${vid.take(5)} → $distanceText, ETA: $etaText",
                            fontSize = 12.sp
                        )
                    }

                }
            }

        }

        // Buttons row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { if (isSharing) stopSharing() else startSharing() },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(if (isSharing) "I'm Safe" else "I'm in Danger")
            }
            Button(
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Track my live location here: https://uninotify-3e948.web.app/live-location-osm/?sessionId=$sessionId&userId=$userId"
                        )
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share location"))
                },
                enabled = isSharing
            ) {
                Text("Share")
            }
        }
    }
}

@SuppressLint("MissingPermission")
fun startOSMLocationUpdates(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    onLocationUpdate: (Double, Double) -> Unit
): LocationCallback? {
    val fineGranted = ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val coarseGranted = ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    // 🚨 Bail out if no location permission at all
    if (!fineGranted && !coarseGranted) {
        Log.w("LiveLocation", "No location permission granted, cannot start updates")
        return null
    }

    // ✅ Modern request with proper intervals
    val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
        .setMinUpdateIntervalMillis(2000L) // fastest allowed update
        .setWaitForAccurateLocation(true)  // prefer high accuracy
        .build()

    val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val loc = result.lastLocation ?: return
            val lat = loc.latitude
            val lng = loc.longitude

            // ✅ Update UI marker (for your banner/time display)
            onLocationUpdate(lat, lng)


        }
    }

    // ✅ Register for updates
    fusedLocationClient.requestLocationUpdates(
        request,
        callback,
        context.mainLooper
    )

    return callback
}