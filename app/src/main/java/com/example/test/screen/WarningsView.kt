package com.example.test.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.data.MeteorologicalAerodromeReport
import com.example.test.data.WeatherForecast
import com.example.test.model.Windshear

/*
* WarningsView checks for warnings, taf- and metardata, if data is present it loop trough
* and uses helper functions to load cards.
* If there is no data to be displayed a simple Text is displayed noticing the user that there is no data*/
@Composable
fun WarningsView(
    warnings: List<Any>,
    airPortIcao: String,
    forecast: List<WeatherForecast>,
    report: List<MeteorologicalAerodromeReport>

) {
    // Makes mutable list of for Windshear
    val windshearForThisAirport: MutableList<Windshear> = mutableListOf()

    // Gets Windshear warning form list of all warnings
    for (warning in warnings) {
        if (warning is Windshear && warning.content.contains(airPortIcao)) {
            windshearForThisAirport.add(warning)
        }
    }



    // Uses a lazyColumn to display cards and text items
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            //.fillMaxSize()
            .fillMaxWidth()
    ) {

        //Title for metar data
        item {
            Text(text = "Metar-data", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        // Adds all metar reports in the form of a card. Typically loads 48, therefore LazyRow is used.
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                item {
                    if(report.isNotEmpty()){
                        for (each in report.reversed()){
                            MetarCard(meteorologicalAerodromeReport = each)
                        }

                    }

                }


            }
        }

        // In case of no Metar data
        item {
            if(report.isEmpty()) {
                Text("No Metar-data for this Airport")
            }
        }

        // Title for Windshear section
        item{
            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Windshear-data", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        }
        // In case of No windshear warnings
        item {
            if(windshearForThisAirport.isEmpty()) {
                Text("No windshear warnings.")
            }
        }

        // Loads all windshear warning data into a WindshearCard
        items(windshearForThisAirport) { windshareData ->
            WindshearCard(windshearData = windshareData)
        }
        // Adds spaces to seperate windshear section from taf section
        item{
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = "Taf-data", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        }

        // Loads taf data into TafmetarCard
        item{
            if (forecast.isNotEmpty()){
                for (each in forecast){
                    TafmetarCard(weatherForecastData = each)
                }
            }
            else {
                Text(text = "No taf-data for this Airport")
            }
        }

    }

}

/*
* Helper function to load cards of taf. All data is stacked vertically.
*/
@Composable
fun TafmetarCard(weatherForecastData: WeatherForecast) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(7.dp),
        shape = RoundedCornerShape(size = 26.dp),
        border = BorderStroke(width = 1.dp, color = Color.Black),
        elevation = 4.dp,

    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color(android.graphics.Color.parseColor("#fff3f3f3")))
            .padding((7.dp)),
            horizontalAlignment = Alignment.CenterHorizontally) {
            // Title and value of data is concatenated into one sting
            Text(
                modifier = Modifier.padding(horizontal = 1.dp, vertical = 5.dp),
                text = "Valid : " + weatherForecastData.validPeriodStart + " - " + weatherForecastData.validPeriodEnd,
                textAlign = TextAlign.Justify,
                softWrap = true)
            Text(
                modifier = Modifier.padding(horizontal = 1.dp, vertical = 1.dp),
                text ="Issued time: " + weatherForecastData.issuedTime)
            Text(
                modifier = Modifier.padding(horizontal = 1.dp, vertical = 1.dp),
                text = "Nais header: " + weatherForecastData.naisHeader)
            Text(
                modifier = Modifier,
                text = "\nTafftext:")
            Text(
                modifier = Modifier.padding(horizontal = 1.dp, vertical = 0.dp),
                text = weatherForecastData.tafText,
                textAlign = TextAlign.Center,
                )
        }
    }
}

// Helper function to display WindshearCard containing windshearData as one Text element
@Composable
fun WindshearCard(windshearData: Windshear) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)

    ) {
        Column(modifier = Modifier.fillMaxSize().background(Color(android.graphics.Color.parseColor("#fff3f3f3"))),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier
                .height(20.dp)
                .fillMaxWidth()
            )
            Text(text = windshearData.content)
        }
    }
}

/*
* Helper function to load cards of taf. All data is stacked vertically.
* MetarCards are adaptable to users screen size.
* If screen width is smaller that 550 dp MetarCard is adjusted to 70% of screen width.
* If screen size is larger than 550 the width is set to 450 dp. This is done to -
* gesture the user that the there is a scrollable row out of view.
* */
@Composable
fun MetarCard(meteorologicalAerodromeReport: MeteorologicalAerodromeReport) {
    val configuration = LocalConfiguration.current
    var screenWidth = 450

    if (configuration.screenWidthDp < 550) {
        screenWidth = (configuration.screenWidthDp * 0.7).toInt()
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(7.dp),
        shape = RoundedCornerShape(size = 26.dp),
        border = BorderStroke(width = 1.dp, color = Color.Black),
        elevation = 4.dp,

    ) {
        Column(
            modifier = Modifier.width(screenWidth.dp).background(Color(android.graphics.Color.parseColor("#fff3f3f3"))),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 1.dp, vertical = 5.dp),
                text = "Time position: " + meteorologicalAerodromeReport.timePosition
            )
            Text(
                modifier = Modifier.padding(top = 1.dp, bottom = 0.dp),
                text = "Metar text:"
            )
            Text(
                modifier = Modifier.padding(horizontal = 1.dp, vertical = 0.dp),
                text = meteorologicalAerodromeReport.metarText,
                textAlign = TextAlign.Center,
            )

        }
    }
}


