package com.example.test.data

/**
 * data class AirportData is for the Airports (Hardcoded in)
 */
data class AirportData(
    val id: Int,
    val name: String,
    val city: String,
    val country: String,
    val iata: String,
    val icao: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Int,
    val timezone: Int,
    val dst: String,
    val timeZone: String,
    val type: String,
    val source: String
)

/**
 * data class PlaneSpottingLocation is for the PlaneSpottingLocation (Hardcoded in)
 */
data class PlaneSpottingLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val flyplassIcao: String
)
