package com.example.test

import com.example.test.data.DataSourceFly
import com.example.test.data.Warningparser
import com.example.test.data.fetchXML
import com.example.test.data.fetchXMLTafmetar
import com.example.test.model.Warning
import com.example.test.model.Windshear
import com.google.android.gms.common.api.Response
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ApiTest {

    @Test
    fun testFetchXML() {
        val icao = "ENGM"
        val weatherForecasts = runBlocking { fetchXML(icao) }
        assertNotNull(weatherForecasts)
    }

    @Test
    fun testFetchXMLTafmetar() {
        val icao = "ENGM"
        val metarReports = runBlocking { fetchXMLTafmetar(icao) }
        assertNotNull(metarReports)
    }

    @Test
    fun testFetchFly() {
        val dataSourceFly = DataSourceFly("https://Prebennc:Gruppe21@opensky-network.org/api/states/all")
        val flyData = runBlocking { dataSourceFly.fetchFly() }
        assertNotNull(flyData)
    }

    private val warningparser = Warningparser()

    //private val warningparser = Warningparser()



    //funkee
    @Test
    fun testConvertDDMtoLatLong() {
        val input = "N5910 E00730"
        val expectedOutput = "59.166666666666664 7.5"
        val actualOutput = warningparser.convertDDMtoLatLong(input)
        assertEquals(expectedOutput, actualOutput)
    }

    @Test
    fun convertDDMtoLatLongListTest() {

        val inputList = listOf("N6012 E01012", "N6008 E01009", "N6013 E01011")

        val result = warningparser.convertDDMtoLatLongList(inputList)

        assertNotNull(result)
        assertEquals("60.2 10.2", result[0])
        assertEquals("60.13333333333333 10.15", result[1])
        assertEquals("60.21666666666667 10.183333333333334", result[2])
    }


    //funker
    @Test
    fun parseEmptyInput() {
        val input = ""
        val expectedOutput = emptyList<Any>()
        val actualOutput = warningparser.parse(input)
        assertEquals(expectedOutput, actualOutput)
    }


   /* @Test
    fun parseTest() {
        val warningparser = Warningparser()
        val input = "ZCZC\n" +
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
        val expected = listOf(
            Windshear(
                "WWNO48 ENMI 160712ENBR WS WRNG 01 160712 VALID 160730/161130WS FCST INTSF=",
                "N"
            ),
            Warning(
                "WSN031 ENMI 301915ENOR SIGMET M01 VALID 302000/310000 ENMI-ENOR POLARIS FIR SEV MTW FCST WI N5910 E00730 – N5919 E00550- N6200 E00545 – N6200 E00730 – N5910 E00730 SFC/FL80 STRN NC="
                        ["59.166666666666664 7.5", "59.31666666666667 5.833333333333333", "62.0 5.75", "62.0 7.5", "59.166666666666664 7.5"])
            )
        )

        val result = warningparser.parse(input)
        assertEquals(expected, result)
    }


    [Warning(content=WSN031 ENMI 301915ENOR SIGMET M01 VALID 302000/310000 ENMI-ENOR POLARIS FIR SEV MTW FCST WI N5910 E00730 – N5919 E00550- N6200 E00545 – N6200 E00730 – N5910 E00730 SFC/FL80 STRN NC=, kordinater=[59.166666666666664 7.5, 59.31666666666667 5.833333333333333, 62.0 5.75, 62.0 7.5, 59.166666666666664 7.5]),
    Warning(content=WANO31 ENMI 160517ENOR AIRMET I01 VALID 160600/161000 ENMI-ENOR POLARIS FIR MOD ICE FCST WI N5820E00845 - N5800 E00755 - N5800 E00645N5850 E00500 - N5900 E00730 - N5820 E008451000FT/FL150 MOV ENE 2OKT INTSF=, kordinater=[58.0 7.916666666666667, 58.0 6.75, 58.833333333333336 5.0, 59.0 7.5, 58.333333333333336 8.75]),
    Windshear(content=WWNO48 ENMI 160712ENBR WS WRNG 01 160712 VALID 160730/161130WS FCST INTSF=, icao=N)]*/

}

