package com.example.test.model

/**
 * data class PlaneData is for the Opensky API
 */
data class PlaneData(
    val time: Int,
    val states: List<List<Any?>>
)
