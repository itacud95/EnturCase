package com.example.enturcase.data.repository

import com.example.enturcase.StopPlaceQuery
import com.example.enturcase.data.model.Departure
import com.example.enturcase.data.remote.GraphQLClient
import com.example.enturcase.type.TransportMode
import java.time.ZonedDateTime

class DeparturesRepository(private val client: GraphQLClient) {

    suspend fun listDeparturesForStop(stopPlaceId: String): List<Departure> {
        val result = client.fetchStopPlace(stopPlaceId)
        return formatStopPlace(result)
    }

    private fun extractLineId(lineIdString: String?): Int {
        val regex = Regex("\\d+$")
        return lineIdString?.let { regex.find(it)?.value?.toIntOrNull() } ?: 0

    }

    private fun formatStopPlace(stopPlaceQuery: StopPlaceQuery.StopPlace?): List<Departure> {
        if (stopPlaceQuery == null) {
            return listOf()
        }

        val departures: MutableList<Departure> = mutableListOf()
        val estimatedCalls = stopPlaceQuery.estimatedCalls
        estimatedCalls.forEachIndexed { _, call ->

            // todo: make this more robust, use the other fields.
            val departure = ZonedDateTime.parse(call.expectedDepartureTime.toString())

            departures.add(
                Departure(
                    call.serviceJourney.journeyPattern?.line?.transportMode
                        ?: TransportMode.unknown,
                    extractLineId(call.serviceJourney.journeyPattern?.line?.id),
                    call.destinationDisplay?.frontText ?: "unknown",
                    departure,
                )
            )
        }

        return departures
    }

}