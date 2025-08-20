package com.example.aigiri.network

import android.util.Log
import com.example.aigiri.model.RouteInfo
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object OsrmClient {

    private const val TAG = "OsrmClient"

    suspend fun fetchRoute(from: LatLng, to: LatLng): RouteInfo? = withContext(Dispatchers.IO) {
        try {
            val urlStr =
                "https://router.project-osrm.org/route/v1/driving/" +
                        "${from.longitude},${from.latitude};${to.longitude},${to.latitude}" +
                        "?overview=full&geometries=geojson"

            val conn = URL(urlStr).openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 7000
            conn.readTimeout = 7000

            if (conn.responseCode != HttpURLConnection.HTTP_OK) return@withContext null

            val responseText = conn.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(responseText)
            val routes = json.getJSONArray("routes")
            if (routes.length() == 0) return@withContext null

            val route = routes.getJSONObject(0)
            val geometry = route.getJSONObject("geometry")
            val coords = geometry.getJSONArray("coordinates")

            val points = ArrayList<LatLng>()
            for (i in 0 until coords.length()) {
                val pair = coords.getJSONArray(i)
                points.add(LatLng(pair.getDouble(1), pair.getDouble(0)))
            }

            val distance = route.optDouble("distance", 0.0)   // in meters
            val duration = route.optDouble("duration", 0.0)   // in seconds

            RouteInfo(points, distance, duration)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}