package com.example.enturcase.data.model

data class StopPlace(
    val name: String,
    val label: String, // todo:  deprecate
    val source_id: String,
    val distance: Double,
)