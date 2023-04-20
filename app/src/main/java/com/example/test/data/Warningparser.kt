package com.example.test.data

import android.text.TextUtils
import android.util.Log
import com.example.test.model.Warning
import com.example.test.model.Windshear
import java.util.*
import java.util.regex.Pattern

class Warningparser {
    //Work in progress. Må finne ut av hva som er kordinater for warnings slik at vi kan lage geometriske representasjoner av objektene på kartet.
    fun parse(input: String): List<Any> {
        //Lagrer først alle tokens i en liste med scanner-objekt:
        val list: MutableList<String> = mutableListOf()
        val s: Scanner = Scanner(input)
        var newObjectText: String = ""
        while (s.hasNext()) {
            //Ser om linje er en "ZCZC"
            val line = s.nextLine()
            if (line == "ZCZC") {
                var newObjectData: String = ""
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

        val objektListe: MutableList<Any> = mutableListOf()
        for (warningData in list) {
            if (warningData[2] == 'W') {
                //Windshear:
                val new: Windshear = Windshear(warningData, this.parseICAO(warningData))
                objektListe.add(new)

            } else {
                //Airmet/Sigmet
                val new: Warning = Warning(warningData, this.parseDSM(warningData))
                objektListe.add(new)
            }
        }

        for (warning in objektListe) {
            if (warning is Warning) {
                warning.kordinater = this.convertDDMtoLatLongList(warning.kordinater)
            }
        }
        return objektListe
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
    private fun parseICAO(input: String): String {
        val stringlist = input.split(" ")
        return stringlist[4]
    }
}