package com.example.test.data

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString

private const val TARGET =
    "https://gw-uio.intark.uh-it.no/in2000/weatherapi/tafmetar/1.0/tafmetar.xml?icao="

private const val TARGETREPORT =
    "https://gw-uio.intark.uh-it.no/in2000/weatherapi/tafmetar/1.0/metar.xml?icao="

suspend fun fetchXML(icao: String): List<WeatherForecast> {
    Log.v("taget", TARGET.plus(icao))
    val xml = Fuel.get(TARGET.plus(icao))
        .header("X-Gravitee-API-Key", "a7cc3ee4-1921-48b1-b301-40bd185e6b0b")
        .awaitString()
    val inputStream = xml.byteInputStream()

    return XmlParser().parse(inputStream)

}

suspend fun fetchXMLTafmetar(icao: String): List<MeteorologicalAerodromeReport> {
    Log.v("taget", TARGETREPORT.plus(icao))
    val xml = Fuel.get(TARGETREPORT.plus(icao))
        .header("X-Gravitee-API-Key", "a7cc3ee4-1921-48b1-b301-40bd185e6b0b")
        .awaitString()
    val inputStream = xml.byteInputStream()

    return XmlParser().parseReport(inputStream)

}