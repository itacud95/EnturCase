package com.example.enturcase.data.repository

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.enturcase.GraphQLClient
import com.example.enturcase.StopPlaceQuery
import com.example.enturcase.type.TransportMode
import com.example.enturcase.ui.viewmodel.DeparturesViewModel
import com.example.enturcase.utils.Logger
import java.time.ZonedDateTime


data class Departure(
    val transportMode: TransportMode,
    val lineId: Int,
    val lineName: String,
    val destination: String,
    val departure: ZonedDateTime,
)

class DeparturesRepository(private val client: GraphQLClient) {


    // todo empty list
    fun listDeparturesForStop(stopPlaceId: String): List<Departure> {
        val result = client.fetchStopPlace(stopPlaceId)
        return formatStopPlace(result)
    }

    private fun extractLineId(lineIdString: String?): Int {
        val regex = Regex("\\d+$")
        return lineIdString?.let { regex.find(it)?.value?.toIntOrNull() } ?: 0

    }

    private fun formatStopPlace(stopPlace: StopPlaceQuery.StopPlace?): List<Departure> {
        if (stopPlace == null) {
            return listOf()
        }

        val builder = StringBuilder()
        builder.append("Stop Place: ${stopPlace.name} (ID: ${stopPlace.id})\n")
        builder.append("------------------------------------------------\n")

        val departures: MutableList<Departure> = mutableListOf()

        val estimatedCalls = stopPlace.estimatedCalls
        estimatedCalls.forEachIndexed { _, call ->

            Logger.debug("here comes one")
            Logger.debug(call.toString())

            // todo: make this more robust, can be other fields.
            val departure = ZonedDateTime.parse(call.expectedDepartureTime.toString())

            departures.add(
                Departure(
                    call.serviceJourney.journeyPattern?.line?.transportMode
                        ?: TransportMode.unknown,
                    extractLineId(call.serviceJourney.journeyPattern?.line?.id),
                    call.serviceJourney.journeyPattern?.line?.name ?: "unknown",
                    call.destinationDisplay?.frontText ?: "unknown",
                    departure,
                )
            )

        }

//        if (estimatedCalls.isEmpty()) {
//            builder.append("No upcoming departures found.\n")
//        } else {
//            builder.append("Upcoming Departures:\n\n")
//            estimatedCalls.forEachIndexed { index, call ->
//                builder.append("Departure ${index + 1}:\n")
//                builder.append("   - Destination: ${call.destinationDisplay?.frontText ?: "Unknown"}\n")
//                builder.append("   - Realtime: ${if (call.realtime) "Yes" else "No"}\n")
//                builder.append("   - Aimed Arrival: ${call.aimedArrivalTime ?: "N/A"}\n")
//                builder.append("   - Expected Arrival: ${call.expectedArrivalTime ?: "N/A"}\n")
//                builder.append("   - Aimed Departure: ${call.aimedDepartureTime ?: "N/A"}\n")
//                builder.append("   - Expected Departure: ${call.expectedDepartureTime ?: "N/A"}\n")
//                builder.append("   - Quay ID: ${call.quay?.id ?: "N/A"}\n")
//
//                val line = call.serviceJourney?.journeyPattern?.line
//                if (line != null) {
//                    builder.append("   - Line: ${line.name} (ID: ${line.id})\n")
//                    builder.append("   - Transport Mode: ${line.transportMode}\n")
//                }
//
//                builder.append("------------------------------------------------\n")
//            }
//        }

        return departures
    }

}