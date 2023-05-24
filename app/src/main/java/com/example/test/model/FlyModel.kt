package com.example.test.model

/**
 * data class FlyData is for the Opensky API
 */
data class FlyData(
    val time: Int,
    val states: List<List<Any?>>
)
