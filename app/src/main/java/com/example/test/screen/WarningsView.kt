package com.example.test.screen

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.data.MeteorologicalAerodromeReport
import com.example.test.data.WeatherForecast
import com.example.test.model.Windshear

@Composable
fun WarningsView(
    warnings: List<Any>,
    airPortIcao: String,
    forecast: List<WeatherForecast>,
    report: List<MeteorologicalAerodromeReport>

) {
    //viewModel.loadWarnings()
    val windshearForThisAirport: MutableList<Windshear> = mutableListOf()

    for (warning in warnings) {
        //warning.icao funker ikke?
        if (warning is Windshear && warning.content.contains(airPortIcao)) {
            windshearForThisAirport.add(warning)
        }
    }




    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            //.fillMaxSize()
            .fillMaxWidth()
            //.fillMaxHeight()
    ) {

        item {
            Text(text = "Metar-data: (Swipe left to load more...)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
        item() {
            LazyRow(
                modifier = Modifier
                    //.fillMaxSize()
                    .fillMaxWidth()

                //.fillMaxHeight()
            ) {
                item() {
                    if(report.isNotEmpty()){
                        for (each in report){
                            MetarCard(meteorologicalAerodromeReport = each)
                        }

                    }
                    else {
                        Text(text = "No Metar-data for this Airport")
                    }

                }


            }
        }
        item(){
            Text(text = "Windshear-data:", fontWeight = FontWeight.Bold, fontSize = 15.sp)

        }

        item() {
            if(windshearForThisAirport.isEmpty()) {
                Text("No windshear warnings.")
            }
        }

        //Forrvirrende variabel navn her, skal endres
        items(windshearForThisAirport) { windshareData ->
            WindshearCard(windshearData = windshareData)
        }
        item(){
            Text(text = "Taf-data: (Swipe down to load more...)", fontWeight = FontWeight.Bold, fontSize = 15.sp)

        }

        item(){
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
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            //Spacer(modifier = Modifier.height(20.dp).fillMaxWidth())
            Text(
                modifier = Modifier.padding(horizontal = 1.dp, vertical = 5.dp),
                text = "Valid period: " + weatherForecastData.validPeriodStart + " - " + weatherForecastData.validPeriodEnd)
            Text(
                modifier = Modifier.padding(horizontal = 1.dp, vertical = 1.dp),
                text ="Issued time: " + weatherForecastData.issuedTime)
            Text(
                modifier = Modifier.padding(horizontal = 1.dp, vertical = 1.dp),
                text = "Nais header: " + weatherForecastData.naisHeader)
            Text(
                modifier = Modifier,
                text = "Tafftext:")
            Text(
                modifier = Modifier.padding(horizontal = 1.dp, vertical = 0.dp),
                text = weatherForecastData.tafText)
        }
    }
}

@Composable
fun WindshearCard(windshearData: Windshear) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)

    ) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier
                .height(20.dp)
                .fillMaxWidth()
            )
            Text(text = windshearData.content)
        }
    }
}

@Composable
fun MetarCard(meteorologicalAerodromeReport: MeteorologicalAerodromeReport) {
    val configuration = LocalConfiguration.current
    var screenWidth = 450.dp

    if (configuration.screenWidthDp.dp < 450.dp) {
        screenWidth = configuration.screenWidthDp.dp
    }


    //Log.v("width", configuration.screenWidthDp.dp.toString())
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(7.dp),
        shape = RoundedCornerShape(size = 26.dp),
        border = BorderStroke(width = 1.dp, color = Color.Black),
        elevation = 4.dp,
        //backgroundColor = Color.Green.copy(alpha = 0.2f)

    ) {
        Column(
            modifier = Modifier.width(screenWidth),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Spacer(modifier = Modifier.height(20.dp).fillMaxWidth())
            Text(
                modifier = Modifier.padding(horizontal = 1.dp, vertical = 5.dp),
                text = "Time position: " + meteorologicalAerodromeReport.timePosition
            )
            Text(
                modifier = Modifier,
                text = "Metar text:"
            )
            Text(
                modifier = Modifier.padding(horizontal = 1.dp, vertical = 0.dp),
                text = meteorologicalAerodromeReport.metarText
            )

        }
    }
}


