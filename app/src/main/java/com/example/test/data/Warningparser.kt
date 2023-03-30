package com.example.test.data

import com.example.test.model.Warning
import com.example.test.model.Windshear
import java.util.*
import java.util.regex.Pattern

class Warningparser {
    //Work in progress. Må finne ut av hva som er kordinater for warnings slik at vi kan lage geometriske representasjoner av objektene på kartet.
    fun parse(input: String): List<Any> {
        //Test eksempel fra api. Tre objeketr:

        /*
        val input: String = "ZCZC\n" +
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

         */

        //Lagrer først alle tokens i en liste med scanner-objekt:
        val list: MutableList<String> = mutableListOf()
        val s: Scanner = Scanner(input)
        var newObjectText: String = ""
        while(s.hasNext()) {
            //Ser om linje er en "ZCZC"
            val line = s.nextLine()
            if (line == "ZCZC") {
                var newObjectData: String = ""
                var zczc = true
                //Legger til ny streng i lista for hver "zczc" token:
                while(zczc) {
                    //Det her er wack, men funker. Må ut av siste while iterasjon før filen slutter.
                    if (s.hasNextLine()) {
                        val line2: String = s.nextLine()
                        if (line2.isNotEmpty()) {
                            newObjectData += line2
                        }
                        else {
                            zczc = false
                        }
                    } else
                    {
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
            //Windshear funker ikke enda, går alltid i else her:
            if (warningData[1] == 'W') {
                val new: Windshear = Windshear(warningData, this.parseICAO(warningData))
                objektListe.add(new)

            } else {
                val new: Warning = Warning(warningData, this.parseDSM(warningData))
                objektListe.add(new)
            }
        }
        println("TEST RESULTAT:") //Forventet res: [Warning(content=ZCZCWSN031 ENMI 301915ENOR SIGMET M01 VALID 302000/310000 ENMI-ENOR POLARIS FIR SEV MTW FCST WI N5910 E00730 – N5919 E00550- N6200 E00545 – N6200 E00730 – N5910 E00730 SFC/FL80 STRN NC=, kordinater=[N5910 E00730, N5919 E00550, N6200 E00545, N6200 E00730, N5910 E00730]), Warning(content=ZCZCWANO31 ENMI 160517ENOR AIRMET I01 VALID 160600/161000 ENMI-ENOR POLARIS FIR MOD ICE FCST WI N5820E00845 - N5800 E00755 - N5800 E00645N5850 E00500 - N5900 E00730 - N5820 E008451000FT/FL150 MOV ENE 2OKT INTSF=, kordinater=[N5800 E00755, N5800 E00645, N5850 E00500, N5900 E00730, N5820 E008451000])]
        //(Mangler windshear objektet:()
        println(objektListe)
        return objektListe
    }

    //Finner lat,long punkter for Sigmets og Airmets.
    private fun parseDSM(input: String): List<String> {
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