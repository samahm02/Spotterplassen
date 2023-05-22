package com.example.test.data

import android.text.TextUtils
import android.util.Log
import com.example.test.model.Warning
import com.example.test.model.Windshear
import java.util.*

class Warningparser {
    //Work in progress. Må finne ut av hva som er kordinater for warnings slik at vi kan lage geometriske representasjoner av objektene på kartet.
    fun parse(input: String): List<Any> {
        //Hardcoded testinput:
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


        //Lagrer først alle tokens i en liste med scanner-objekt:
        val list: MutableList<String> = mutableListOf()
        //Change Scanner parameter to test for testing:
        val s = Scanner(input)
        while (s.hasNext()) {
            //Ser om linje er en "ZCZC"
            val line = s.nextLine()
            if (line == "ZCZC") {
                var newObjectData = ""
                var zczc = true
                //Legger til ny streng i lista for hver "zczc" token:
                while (zczc) {
                    //Det her er wack, men funker. Må ut av siste while iterasjon før filen slutter.
                    if (s.hasNextLine()) {
                        val line2: String = s.nextLine()
                        if (line2.isNotEmpty()) {
                            newObjectData += " $line2"
                        } else {
                            zczc = false
                        }
                    } else {
                        break
                    }
                }
                list.add(newObjectData)
            }
        }

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

        for (warning in objectList) {
            if (warning is Warning) {
                warning.kordinater = this.convertDDMtoLatLongList(warning.kordinater)
            }
        }
        return objectList
    }

    fun convertDDMtoLatLongList(input: List<String>): MutableList<String> {
        val retur = mutableListOf<String>()
        Log.d("User", "inputlistsize: ${input.size}")
        for (latLongString in input) {
            retur += this.convertDDMtoLatLong(latLongString)
        }
        return retur
    }

    //Konverterer DDM til DD altså "Nxxxx Exxxxx" til "x.x x.x"
    fun convertDDMtoLatLong(input: String): String {
        val inputList = TextUtils.split(input, " ")
        val lat: String = inputList[0]
        val long: String = inputList[1]
        val inputDegreesLat = lat[1].toString() + lat[2].toString()
        val inputMinutesLat = lat[3].toString() + lat[4].toString()
        //Antar at første tall i Exxxxx alltid er 0!
        val inputDegreesLong = long[2].toString() + long[3].toString()
        val inputMinutesLong = long[4].toString() + long[5].toString()

        val latDD: Double = inputDegreesLat.toDouble() + inputMinutesLat.toDouble() / 60
        val longDD: Double = inputDegreesLong.toDouble() + inputMinutesLong.toDouble() / 60
        return "$latDD $longDD"
    }

    //Finner lat,long punkter for Sigmets og Airmets.
    private fun parseDSM(input: String): MutableList<String> {
        val coordinates = mutableListOf<String>()
        val regex = Regex("N\\d+\\sE\\d+") // pattern to match coordinates

        regex.findAll(input).forEach { matchResult ->
            coordinates.add(matchResult.value)
        }
        return(coordinates)
    }

    //Finner ICAO for windshear
    fun parseICAO(input: String): String {
        val stringlist = input.split(" ")
        return stringlist[4]
    }
}