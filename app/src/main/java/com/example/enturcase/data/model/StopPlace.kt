package com.example.enturcase.data.model

// This class is used by GraphQL
data class StopPlace(
    val name: String,
    val source_id: String,
    val distance: Double,
)