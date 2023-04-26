package com.example.test.data

data class AirportData(
    val ID: Int, val name: String, val city: String, val country: String, val IATA: String,
    val ICAO: String, val Latitude: Double, val Longitude: Double, val Altitude: Int, val Timezone: Int,
    val DST: String, val timeZone: String, val Type: String, val Source: String)

data class PlaneSpottingLocation(
    val Name: String, val Latitude: Double, val Longitude: Double, val Description: String, val FlypalssICAO: String
)
