package com.example.test.data

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.github.kittinunf.fuel.core.FuelError
import org.xmlpull.v1.XmlPullParserException

/**
 * Url to the TAFMETAR Api through the proxy
 */
private const val TARGET =
    "https://gw-uio.intark.uh-it.no/in2000/weatherapi/tafmetar/1.0/tafmetar.xml?icao="

/**
 * Fetches XML data for the given ICAO code and parses it into a list of WeatherForecast objects.
 */

suspend fun fetchXml(icao: String): List<WeatherForecast> {
    return try {
        val xml = Fuel.get(TARGET.plus(icao))
            .header("X-Gravitee-API-Key", "a7cc3ee4-1921-48b1-b301-40bd185e6b0b")
            .awaitString()
        val inputStream = xml.byteInputStream()
        XmlParser().parse(inputStream)
    } catch(e: FuelError) {
        println("Fuel Error: ${e.message}")
        emptyList()
    } catch(e: XmlPullParserException) {
        println("XML Parsing Error: ${e.message}")
        emptyList()
    } catch(e: Exception) {
        println("General Error: ${e.message}")
        emptyList()
    }
}
/**
 * Fetches XML data for the given ICAO code and parses it into a list of MeteorologicalAerodromeReport objects.
 */
suspend fun fetchXmlTafmetar(icao: String): List<MeteorologicalAerodromeReport> {
    return try {
        val xml = Fuel.get(TARGET.plus(icao))
            .header("X-Gravitee-API-Key", "a7cc3ee4-1921-48b1-b301-40bd185e6b0b")
            .awaitString()
        val inputStream = xml.byteInputStream()
        XmlParser().parseReport(inputStream)
    } catch(e: FuelError) {
        println("Fuel Error: ${e.message}")
        emptyList()
    } catch(e: XmlPullParserException) {
        println("XML Parsing Error: ${e.message}")
        emptyList()
    } catch(e: Exception) {
        println("General Error: ${e.message}")
        emptyList()
    }
}