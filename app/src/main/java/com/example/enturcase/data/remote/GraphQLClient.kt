package com.example.enturcase.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.enturcase.StopPlaceQuery
import com.example.enturcase.utils.Logger

object GraphQLClient {
    private val apolloClient: ApolloClient = ApolloClient.Builder()
        .serverUrl("https://api.entur.io/journey-planner/v3/graphql")
        .build()

    suspend fun fetchStopPlace(stopPlaceId: String): StopPlaceQuery.StopPlace? {
        return try {
            val response = apolloClient.query(StopPlaceQuery(stopPlaceId)).execute()

            if (response.hasErrors()) {
                Logger.debug("GraphQL Error: ${response.errors}")
                null
            } else {
                response.data?.stopPlace
            }
        } catch (e: ApolloException) {
            Logger.debug("Request failed: ${e.message}")
            null
        }
    }
}
