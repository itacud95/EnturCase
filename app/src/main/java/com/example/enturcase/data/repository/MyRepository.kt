package com.example.enturcase.data.repository

import com.example.enturcase.data.model.Location
import com.example.enturcase.data.model.StopPlace
import com.example.enturcase.utils.Logger
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject







class MyRepository @Inject constructor(private val client: OkHttpClient) {

    suspend fun loadStopPlacesForLocation(location: Location): List<StopPlace> {
        val response = fetchData(location) ?: return listOf()

        val json = parseJson(response) ?: return emptyList()
        return extractStopPlaces(json)
    }

    private fun parseJson(response: String): JsonObject? {
        return try {
            JsonParser.parseString(response).asJsonObject
        } catch (e: Exception) {
            Logger.debug("Failed to parse JSON: ${e.message}")
            null
        }
    }

    private fun extractStopPlaces(json: JsonObject): List<StopPlace> {
        val features = json.getAsJsonArray("features") ?: return emptyList()
        val gson = Gson()

        return features.mapNotNull { feature ->
            try {
                val properties = feature.asJsonObject.getAsJsonObject("properties")
                gson.fromJson(properties, StopPlace::class.java)
            } catch (e: Exception) {
                Logger.debug("Skipping invalid stop place: ${e.message}")
                null
            }
        }.also {
            Logger.debug("Loaded ${it.size} stop places")
        }
    }


    private suspend fun fetchData(location: Location): String? {
        val url = buildUrl(location)
        Logger.debug("requesting: $url")

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response: Response ->
                    if (response.isSuccessful) {
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

    private fun buildUrl(location: Location): String {
        return "https://api.entur.io/geocoder/v1/reverse" +
                "?point.lat=${location.latitude}" +
                "&point.lon=${location.longitude}" +
                "&boundary.circle.radius=1" +
                "&size=10" +
                "&layers=venue"
    }
}