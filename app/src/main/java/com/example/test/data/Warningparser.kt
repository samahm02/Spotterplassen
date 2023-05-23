package com.example.test.data

import android.text.TextUtils
import com.example.test.model.Warning
import com.example.test.model.Windshear
import java.util.*

class Warningparser {
    fun parse(input: String): List<Any> {
        /*
        * Hardcoded testinput. 2 sigmet/airmet objects at westcoast. windshear at Flesland (Bergen)
        * To use testinput instead of the api input parameter, change Scanner parameter from
        * input to test. (at line 37)
         */
        val test = "ZCZC\n" +
        "WSN031 ENMI 301915\n" +
        "ENOR SIGMET M01 VALID 302000/310000 ENMI-\n" +
        "ENOR POLARIS FIR SEV MTW FCST WI N5910 E00730 – N5919 E00550\n" +
        "- N6200 E00545 – N6200 E00730 – N5910 E00730 SFC/FL80 STRN NC=\n" +
        "\n" +
        "ZCZC\n" +
        "WANO31 ENMI 160517\n" +
        "ENOR AIRMET I01 VALID 160600/161000 ENMI-\n" +
        "ENOR POLARIS FIR MOD ICE FCST WI N5820\n" +
        "E00845 - N5800 E00755 - N5800 E00645\n" +
        "N5850 E00500 - N5900 E00730 - N5820 E00845\n" +
        "1000FT/FL150 MOV ENE 2OKT INTSF=\n" +
        "\n" +
        "ZCZC\n" +
        "WWNO48 ENMI 160712\n" +
        "ENBR WS WRNG 01 160712 VALID 160730/161130\n" +
        "WS FCST INTSF="

        val list: MutableList<String> = mutableListOf()
        //Change Scanner parameter from input to test to use hardcoded testinput
        val s = Scanner(input)
        /*
        * Iterate through the entire inputstring and store objects (seperated by "ZCZC")
        * and store them in a list
        */
        while (s.hasNext()) {
            //Check if line is "ZCZC"
            val line = s.nextLine()
            if (line == "ZCZC") {
                var newObjectData = ""
                var createNewObject = true

                //Create new objectdata string
                while (createNewObject) {
                    //Makes sure the file stil has lines
                    if (s.hasNextLine()) {
                        val line2: String = s.nextLine()
                        if (line2.isNotEmpty()) {
                            newObjectData += " $line2"
                        } else {
                            //If an empty line, finish creating new object
                            createNewObject = false
                        }
                    } else {
                        //Make sure the loop breaks if end of file is reached
                        break
                    }
                }
                list.add(newObjectData)
            }
        }

        //Determine object types
        val objectList: MutableList<Any> = mutableListOf()
        for (warningData in list) {
            if (warningData[2] == 'W') {
                //Windshear:
                val new = Windshear(warningData, this.parseICAO(warningData))
                objectList.add(new)

            } else {
                //Airmet/Sigmet
                val new = Warning(warningData, this.parseDSM(warningData))
                objectList.add(new)
            }
        }

        //Convert coordinates for sigmet and airmet objects
        for (warning in objectList) {
            if (warning is Warning) {
                warning.coordinates = this.convertDDMtoLatLongList(warning.coordinates)
            }
        }
        return objectList
    }

    //Support function for converting a list of coordinates
    fun convertDDMtoLatLongList(input: List<String>): MutableList<String> {
        val retur = mutableListOf<String>()
        for (latLongString in input) {
            retur += this.convertDDMtoLatLong(latLongString)
        }
        return retur
    }
    /*
    Converts lat, long coordinates from DDM (Degrees and Decimal Minutes) format
    to DD (Decimal Degrees)
    Example: ("Nxxxx Exxxxx" til "x.x x.x")
     */
    fun convertDDMtoLatLong(input: String): String {
        val inputList = TextUtils.split(input, " ")
        val lat: String = inputList[0]
        val long: String = inputList[1]
        val inputDegreesLat = lat[1].toString() + lat[2].toString()
        val inputMinutesLat = lat[3].toString() + lat[4].toString()
        //Assumes that the first number in the DDM format, Exxxxx, is always 0
        val inputDegreesLong = long[2].toString() + long[3].toString()
        val inputMinutesLong = long[4].toString() + long[5].toString()

        val latDD: Double = inputDegreesLat.toDouble() + inputMinutesLat.toDouble() / 60
        val longDD: Double = inputDegreesLong.toDouble() + inputMinutesLong.toDouble() / 60
        return "$latDD $longDD"
    }

    //Isolates lat,long coordinates in the data string for sigmet and airmet objects
    private fun parseDSM(input: String): MutableList<String> {
        val coordinates = mutableListOf<String>()
        //Pattern to match coordinates
        val regex = Regex("N\\d+\\sE\\d+")

        regex.findAll(input).forEach { matchResult ->
            coordinates.add(matchResult.value)
        }
        return(coordinates)
    }

    //Determines airport ICAO for windshear data string
    fun parseICAO(input: String): String {
        val stringlist = input.split(" ")
        return stringlist[4]
    }
}