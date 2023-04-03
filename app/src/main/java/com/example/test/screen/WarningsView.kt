package com.example.test.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.test.data.WeatherForecast
import com.example.test.model.Warning
import com.example.test.model.Windshear
import com.example.test.viewModel.ViewModel

@Composable
fun WarningsView(
    warnings: List<Any>,
    viewModel: ViewModel,
    airPortIcao: String,
    forecast: List<WeatherForecast>
) {
    //viewModel.loadWarnings()
    val windshearForThisAirport: MutableList<Windshear> = mutableListOf<Windshear>()

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        for (warning in warnings) {
            if (warning is Windshear && warning.icao == airPortIcao ) {
                windshearForThisAirport.add(warning)
            }
        }

        item() {
            if(windshearForThisAirport.isEmpty()) {
                Text("No windshear warnings.")
            }
        }

        item(){
            if (!forecast.isEmpty()){
                for (each in forecast){
                    //TafmetarCard(weatherForecastData = each)
                    TafmetarText(each)
                }
            }
            else {
                Text(text = "No taf-data for this Airport")
            }
        }
        //Forrvirrende variabel navn her, skal endres
        items(windshearForThisAirport) { windshareData ->
            WindshearCard(windshearData = windshareData)
        }
    }
}

@Composable
fun TafmetarText(weatherForecastData: WeatherForecast) {
    Text(text = "Tafftext: " + weatherForecastData.tafText)
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
