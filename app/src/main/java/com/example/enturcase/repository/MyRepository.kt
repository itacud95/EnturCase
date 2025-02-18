package com.example.enturcase.repository

import com.example.enturcase.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class MyRepository @Inject constructor(private val client: OkHttpClient) {

    suspend fun fetchDataFromPosition(latitude: Double, longitude: Double): String? {
        val client = OkHttpClient()
        val url = "https://api.entur.io/geocoder/v1/reverse?point.lat=$latitude&point.lon=$longitude&boundary.circle.radius=1&size=10&layers=venue"
        Logger.debug("requesting: $url")

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response: Response ->
                    if (response.isSuccessful) {
//                        val source = response.body?.source()
//                        source?.buffer?.clone()?.readUtf8()
                        response.body?.string()
                    } else {
                        "Error: ${response.code}"
                    }
                }
            } catch (e: Exception) {
                "Request failed: ${e.message}"
            }
        }
    }
}