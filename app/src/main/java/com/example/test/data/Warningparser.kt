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
                            newObjectData += line2
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
            println("TEST WW:")
            println(warningData)

            if (warningData[1] == 'W') {
                //Windshear:
                val new: Windshear = Windshear(warningData, this.parseICAO(warningData))
                objektListe.add(new)

            } else {
                //Airmet/Sigmet
                val new: Warning = Warning(warningData, this.parseDSM(warningData))
                objektListe.add(new)
            }
        }
        println("TEST RESULTAT før:") //Forventet res: [Warning(content=ZCZCWSN031 ENMI 301915ENOR SIGMET M01 VALID 302000/310000 ENMI-ENOR POLARIS FIR SEV MTW FCST WI N5910 E00730 – N5919 E00550- N6200 E00545 – N6200 E00730 – N5910 E00730 SFC/FL80 STRN NC=, kordinater=[N5910 E00730, N5919 E00550, N6200 E00545, N6200 E00730, N5910 E00730]), Warning(content=ZCZCWANO31 ENMI 160517ENOR AIRMET I01 VALID 160600/161000 ENMI-ENOR POLARIS FIR MOD ICE FCST WI N5820E00845 - N5800 E00755 - N5800 E00645N5850 E00500 - N5900 E00730 - N5820 E008451000FT/FL150 MOV ENE 2OKT INTSF=, kordinater=[N5800 E00755, N5800 E00645, N5850 E00500, N5900 E00730, N5820 E008451000])]
        //Windshear funker
        println(objektListe)


        for (warning in objektListe) {
            if (warning is Warning) {
                warning.kordinater = this.convertDDMtoLatLongList(warning.kordinater)
            }
        }

        println("TEST RESULTAT ETTER DD KONVERTERING:")
        println(objektListe)
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
        Log.d("User", "Latstring: $lat")
        Log.d("User", "Longstring: $long")
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
        val stringlist = input.split("")
        return stringlist[3]
    }
}