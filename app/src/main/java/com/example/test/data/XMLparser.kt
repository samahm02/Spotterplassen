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

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): List<WeatherForecast> {
            inputStream.use {
                val parser: XmlPullParser = Xml.newPullParser()
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                parser.setInput(it, null)
                try {
                    parser.nextTag()
                }
                catch (e: XmlPullParserException) {
                    return emptyList()
                }
                return readFeed(parser)
            }


    }

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
                Log.v("name test",parser.name)
                parser.nextTag()
            } /*else {
                skip(parser)
            }*/
        }
        return entries
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readEntry(parser: XmlPullParser): WeatherForecast {
        parser.require(XmlPullParser.START_TAG, ns, "metno:terminalAerodromeForecast")
        var issuedTime : String = ""
        //var validPeriod : Array<Int> = emptyArray()
        var validPeriod = arrayOf(" "," ")
        var tafText : String = ""
        var naisHeader =""

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "metno:issuedTime" -> issuedTime = readIssuedTime(parser)
                "metno:validPeriod" -> validPeriod = readValidPeriod(parser)
                //"metno:validPeriod" -> Log.v("ksdjf", parser.name)
                "metno:naisHeader" -> naisHeader = readNaisHeader(parser)
                "metno:tafText" -> tafText = readTafText(parser)
                else -> {
                    Log.v("ksdjf", parser.name)
                    skip(parser)}
            }

        }
        Log.v("AIDSS:", issuedTime+validPeriod[0]+validPeriod[1] + "TESTSSSSS" +tafText + naisHeader)
        return WeatherForecast(issuedTime, validPeriod[0], validPeriod[1], tafText, naisHeader)
    }

    /*
    gammel
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readIssuedTime(parser: XmlPullParser): String {
        while (parser.name != "gml:timePosition") {
            skip(parser)
        }
        parser.require(XmlPullParser.START_TAG, ns, "gml:timePosition")
        val issuedTime = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "gml:timePosition")
        return issuedTime
    }*/

    /*
    Nyy
    @Throws(IOException::class, XmlPullParserException::class)
     private fun readIssuedTime(parser: XmlPullParser): String {
         parser.require(XmlPullParser.START_TAG, null, "metno:issuedTime")
         var time = ""

         while (parser.next() != XmlPullParser.END_TAG) {
             if (parser.eventType != XmlPullParser.START_TAG) {
                 continue
             }

             if (parser.name == "gml:TimeInstant") {
                 time = readTimePosition(parser)
             } else {
                 skip(parser)
             }
         }

         parser.require(XmlPullParser.END_TAG, null, "metno:issuedTime")
         return time
     }*/
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



    /*@Throws(IOException::class, XmlPullParserException::class)
    private fun readValidPeriod(parser: XmlPullParser): Array<String> {
        parser.require(XmlPullParser.START_TAG, null, "metno:validPeriod")
        val validPeriod = arrayOf(" ", " ")
        parser.nextTag()
        Log.v("Parsernavne i ", parser.name)
        while (parser.name != "gml:beginPosition") {
            skip(parser)
        }
        parser.require(XmlPullParser.START_TAG, ns, "gml:beginPosition")
        validPeriod[0] = readText(parser)
        //Log.v("vLIDPERIOD ", validPeriod[0])
        Log.v("parser", validPeriod[0].toString())
        parser.require(XmlPullParser.END_TAG, ns, "gml:beginPosition")

        parser.nextTag()
        Log.v("Parsernavne i ", parser.name)
        while (parser.name != "gml:endPosition") {
            skip(parser)
        }

        parser.require(XmlPullParser.START_TAG, ns, "gml:endPosition")
        validPeriod[1] = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "gml:endPosition")

        parser.require(XmlPullParser.END_TAG, null, "metno:issuedTime")
        return validPeriod
    }*/

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
            } else if(parser.name == "gml:endPosition") {
                validPeriod[1] = readText(parser)
            } else{
                skip(parser)
            }
        }

        parser.require(XmlPullParser.END_TAG, null, "metno:validPeriod")
        return validPeriod
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readTafText(parser: XmlPullParser): String {
        Log.v("TAftext test", "tafText")
        parser.require(XmlPullParser.START_TAG, ns, "metno:tafText")
        val tafText = readText(parser)

        parser.require(XmlPullParser.END_TAG, ns, "metno:tafText")
        return tafText
    }

    /*@Throws(IOException::class, XmlPullParserException::class)
    private fun readTafText(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, null, "metno:tafText")
        var tafText = ""

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            if (parser.name == "metno:tafText") {
                tafText = readText(parser)
            } else {
                skip(parser)
            }
        }

        parser.require(XmlPullParser.END_TAG, null, "metno:tafText")
        return tafText
    }*/

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readNaisHeader(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ns, "metno:naisHeader")
        val naisHeader = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "metno:naisHeader")
        return naisHeader
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    /*@Throws(XmlPullParserException::class, IOException::class)
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
    }*/

    @Throws(XmlPullParserException::class, IOException::class)
    fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            Log.e("XmlParser", "Unexpected eventType: ${parser.eventType}")
            Log.e("XmlParser", "startTag: ${XmlPullParser.START_TAG}")
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


data class WeatherForecast(val issuedTime : String, val validPeriodStart : String, val validPeriodEnd : String,
                           val tafText : String, val naisHeader: String) {
    constructor() : this("", "", "", "", " ")
}