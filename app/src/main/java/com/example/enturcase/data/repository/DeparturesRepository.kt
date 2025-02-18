package com.example.enturcase.data.repository

import com.example.enturcase.GraphQLClient
import com.example.enturcase.StopPlaceFormatter
import com.example.enturcase.StopPlaceQuery
import com.example.enturcase.utils.Logger
import okhttp3.OkHttpClient
import javax.inject.Inject

/**
 *     private fun listDeparturesForStop() {
 *         val stopPlaceId = "NSR:StopPlace:6547"
 *         val stopPlace = GraphQLClient.fetchStopPlace(stopPlaceId)
 *         val formattedData = StopPlaceFormatter.formatStopPlace(stopPlace)
 *         Logger.debug(formattedData)
 *     }
 *
 */

class DeparturesRepository @Inject constructor(private val client: GraphQLClient) {

    // todo empty list
    fun listDeparturesForStop(stopPlaceId: String): String? {
        val result = client.fetchStopPlace(stopPlaceId)
        return formatStopPlace(result)
//        val stopPlaceId = "NSR:StopPlace:6547"
//        val stopPlace = client.fetchStopPlace(stopPlaceId)
//        val formattedData = StopPlaceFormatter.formatStopPlace(stopPlace)
//        Logger.debug(formattedData)
    }

    fun formatStopPlace(stopPlace: StopPlaceQuery.StopPlace?): String {
        if (stopPlace == null) {
            return "No stop place data available."
        }

        val builder = StringBuilder()
        builder.append("Stop Place: ${stopPlace.name} (ID: ${stopPlace.id})\n")
        builder.append("------------------------------------------------\n")

        val estimatedCalls = stopPlace.estimatedCalls
        if (estimatedCalls.isEmpty()) {
            builder.append("No upcoming departures found.\n")
        } else {
            builder.append("Upcoming Departures:\n\n")
            estimatedCalls.forEachIndexed { index, call ->
                builder.append("Departure ${index + 1}:\n")
                builder.append("   - Destination: ${call.destinationDisplay?.frontText ?: "Unknown"}\n")
                builder.append("   - Realtime: ${if (call.realtime) "Yes" else "No"}\n")
                builder.append("   - Aimed Arrival: ${call.aimedArrivalTime ?: "N/A"}\n")
                builder.append("   - Expected Arrival: ${call.expectedArrivalTime ?: "N/A"}\n")
                builder.append("   - Aimed Departure: ${call.aimedDepartureTime ?: "N/A"}\n")
                builder.append("   - Expected Departure: ${call.expectedDepartureTime ?: "N/A"}\n")
                builder.append("   - Quay ID: ${call.quay?.id ?: "N/A"}\n")

                val line = call.serviceJourney?.journeyPattern?.line
                if (line != null) {
                    builder.append("   - Line: ${line.name} (ID: ${line.id})\n")
                    builder.append("   - Transport Mode: ${line.transportMode}\n")
                }

                builder.append("------------------------------------------------\n")
            }
        }

        return builder.toString()
    }

}