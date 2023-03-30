package com.example.test.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.test.data.WeatherForecast
import com.example.test.viewModel.ViewModel

@Composable
fun TafmetarView(
    forecast: List<WeatherForecast>,
    viewModel: ViewModel,
) {
    //viewModel.loadFly()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
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


    }

@Composable
fun TafmetarText(weatherForecastData: WeatherForecast) {
            Text(text = "Tafftext: " + weatherForecastData.tafText)
}


@Composable
fun TafmetarCard(weatherForecastData: WeatherForecast) {
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
            Text("Tafdata:")
            Text(text = "Tafftext: " + weatherForecastData.tafText)
        }
    }
}