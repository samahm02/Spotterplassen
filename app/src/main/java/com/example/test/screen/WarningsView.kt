package com.example.test.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.data.MeteorologicalAerodromeReport
import com.example.test.data.WeatherForecast
import com.example.test.model.Warning
import com.example.test.model.Windshear
import com.example.test.viewModel.ViewModel

@Composable
fun WarningsView(
    warnings: List<Any>,
    viewModel: ViewModel,
    airPortIcao: String,
    forecast: List<WeatherForecast>,
    report: List<MeteorologicalAerodromeReport>

) {
    //viewModel.loadWarnings()
    val windshearForThisAirport: MutableList<Windshear> = mutableListOf<Windshear>()

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
                    if(!report.isEmpty()){
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
            if (!forecast.isEmpty()){
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
        border = BorderStroke(width = 1.dp, color = Color.Green),
        elevation = 4.dp,
        //backgroundColor = Color.Green.copy(alpha = 0.2f)

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
                modifier = Modifier.padding(horizontal = 1.dp, vertical = 1.dp),
                text = "Tafftext: " + weatherForecastData.tafText)
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(7.dp),
        shape = RoundedCornerShape(size = 26.dp),
        border = BorderStroke(width = 1.dp, color = Color.Green),
        elevation = 4.dp,
        //backgroundColor = Color.Green.copy(alpha = 0.2f)

    ) {
        Column(modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            //Spacer(modifier = Modifier.height(20.dp).fillMaxWidth())
            Text(
                modifier = Modifier.padding(horizontal = 1.dp, vertical = 5.dp),
                text = "Time position: " + meteorologicalAerodromeReport.timePosition)
            Text(
                modifier = Modifier.padding(horizontal = 1.dp, vertical = 1.dp),
                text ="Metar text: " + meteorologicalAerodromeReport.metarText)

        }
    }
}

