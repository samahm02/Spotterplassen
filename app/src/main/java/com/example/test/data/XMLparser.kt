package com.example.test.data

import android.content.ContentValues
import android.util.Log
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

private val ns: String? = null

class XmlParser {

    /**
     * Parses the XML input stream and returns a list of WeatherForecast objects.
     */
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): List<WeatherForecast> {
        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            try {
                parser.nextTag()
            } catch (e: XmlPullParserException) {
                return emptyList()
            }
            return readFeed(parser)
        }
    }

    /**
     * Parses the XML input stream and returns a list of MeteorologicalAerodromeReport objects.
     */
    @Throws(XmlPullParserException::class, IOException::class)
    fun parseReport(inputStream: InputStream): List<MeteorologicalAerodromeReport> {
        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            try {
                parser.nextTag()
            } catch (e: XmlPullParserException) {
                return emptyList()
            }
            return readFeedReport(parser)
        }
    }

    /**
     * Reads the XML feed and returns a list of MeteorologicalAerodromeReport objects.
     */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeedReport(parser: XmlPullParser): List<MeteorologicalAerodromeReport> {
        val entries = mutableListOf<MeteorologicalAerodromeReport>()
        parser.require(XmlPullParser.START_TAG, ns, "metno:aviationProducts")
        while (parser.next() != 1) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            while (parser.name == "metno:meteorologicalAerodromeReport") {
                entries.add(readEntryReport(parser))
                parser.nextTag()
            }
        }
        return entries
    }

    /**
     * Reads a single MeteorologicalAerodromeReport entry.
     */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readEntryReport(parser: XmlPullParser): MeteorologicalAerodromeReport {
        parser.require(XmlPullParser.START_TAG, ns, "metno:meteorologicalAerodromeReport")
        var timePosition = ""
        var metarText = ""

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "metno:validTime" -> timePosition = readTimePos(parser)
                "metno:metarText" -> metarText = readMetarText(parser)
                else -> {
                    skip(parser)
                }
            }
        }
        return MeteorologicalAerodromeReport(timePosition, metarText)
    }

    /**
     * Reads the validTime tag and returns the time position.
     */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readTimePos(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, null, "metno:validTime")
        var timePosition = ""
        Log.v("Tafmetar time", timePosition)
        try {
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }

                if (parser.name == "gml:TimeInstant") {
                    timePosition = readTimePositionString(parser)
                } else {
                    skip(parser)
                }
            }
        } catch (e: XmlPullParserException) {
            Log.e(ContentValues.TAG, "XmlPullParserException while reading issuedTime: ${e.message}")
        } catch (e: IOException) {
            Log.e(ContentValues.TAG, "IOException while reading issuedTime: ${e.message}")
        }
        parser.require(XmlPullParser.END_TAG, null, "metno:validTime")
        return timePosition
    }

    /**
     * Reads the time position string within the gml:TimeInstant tag.
     */
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readTimePositionString(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, null, "gml:TimeInstant")
        var time = ""

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            if (parser.name == "gml:timePosition") {
                time = readText(parser)
            } else {
                skip(parser)
            }
        }

        parser.require(XmlPullParser.END_TAG, null, "gml:TimeInstant")
        return time
    }

    /**
     * Reads the metarText tag and returns the METAR text.
     */
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readMetarText(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "metno:metarText")
        val metarText = readText(parser)
        Log.v("Tafmetar metarText", metarText)

        parser.require(XmlPullParser.END_TAG, ns, "metno:metarText")
        return metarText
    }

    /**
     * Reads the XML feed and returns a list of WeatherForecast objects.
     */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): List<WeatherForecast> {
        val entries = mutableListOf<WeatherForecast>()

        parser.require(XmlPullParser.START_TAG, ns, "metno:aviationProducts")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            while (parser.name == "metno:terminalAerodromeForecast") {
                entries.add(readEntry(parser))
                parser.nextTag()
            }
        }
        return entries
    }

    /**
     * Reads a single WeatherForecast entry.
     */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readEntry(parser: XmlPullParser): WeatherForecast {
        parser.require(XmlPullParser.START_TAG, ns, "metno:terminalAerodromeForecast")
        var issuedTime = ""
        var validPeriod = arrayOf(" ", " ")
        var tafText = ""
        var naisHeader = ""

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "metno:issuedTime" -> issuedTime = readIssuedTime(parser)
                "metno:validPeriod" -> validPeriod = readValidPeriod(parser)
                "metno:naisHeader" -> naisHeader = readNaisHeader(parser)
                "metno:tafText" -> tafText = readTafText(parser)
                else -> {
                    skip(parser)
                }
            }
        }
        return WeatherForecast(issuedTime, validPeriod[0], validPeriod[1], tafText, naisHeader)
    }

    /**
     * Reads the issuedTime tag and returns the issued time as a string.
     */
    @Throws(XmlPullParserException::class, IOException::class)
    fun readIssuedTime(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, null, "metno:issuedTime")
        var issuedTime = ""
        try {
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }

                if (parser.name == "gml:TimeInstant") {
                    issuedTime = readTimePosition(parser)
                } else {
                    skip(parser)
                }
            }
        } catch (e: XmlPullParserException) {
            Log.e(ContentValues.TAG, "XmlPullParserException while reading issuedTime: ${e.message}")
        } catch (e: IOException) {
            Log.e(ContentValues.TAG, "IOException while reading issuedTime: ${e.message}")
        }
        parser.require(XmlPullParser.END_TAG, null, "metno:issuedTime")
        return issuedTime
    }

    /**
     * Reads the time position within the gml:TimeInstant tag.
     */
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readTimePosition(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, null, "gml:TimeInstant")
        var time = ""

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            if (parser.name == "gml:timePosition") {
                time = readText(parser)
            } else {
                skip(parser)
            }
        }

        parser.require(XmlPullParser.END_TAG, null, "gml:TimeInstant")
        return time
    }

    /**
     * Reads the validPeriod tag and returns the valid period as an array of strings.
     */
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readValidPeriod(parser: XmlPullParser): Array<String> {
        parser.require(XmlPullParser.START_TAG, null, "metno:validPeriod")
        val validPeriod = arrayOf(" ", " ")

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            if (parser.name == "gml:beginPosition") {
                validPeriod[0] = readText(parser)
            } else if (parser.name == "gml:endPosition") {
                validPeriod[1] = readText(parser)
            } else {
                skip(parser)
            }
        }

        parser.require(XmlPullParser.END_TAG, null, "metno:validPeriod")
        return validPeriod
    }

    /**
     * Reads the tafText tag and returns the TAF text.
     */
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readTafText(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "metno:tafText")
        val tafText = readText(parser)

        parser.require(XmlPullParser.END_TAG, ns, "metno:tafText")
        return tafText
    }

    /**
     * Reads the naisHeader tag and returns the NAIS header.
     */
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readNaisHeader(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "metno:naisHeader")
        val naisHeader = readText(parser)

        parser.require(XmlPullParser.END_TAG, ns, "metno:naisHeader")
        return naisHeader
    }

    /**
     * Extracts the text value from a tag.
     */
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    /**
     * Skips irrelevant tags.
     */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}

/**
 * data class WeatherForecast is for Taf ( weather forcast for the last 24 hours every 3 hours)
 */
data class WeatherForecast(val issuedTime : String, val validPeriodStart : String, val validPeriodEnd : String,
                           val tafText : String, val naisHeader: String) {
}

/**
 * data class MeteorologicalAerodromeReport is for Metar (weather reports for the last 24 hours every 30 mins)
 */
data class MeteorologicalAerodromeReport(val timePosition: String, val metarText: String)