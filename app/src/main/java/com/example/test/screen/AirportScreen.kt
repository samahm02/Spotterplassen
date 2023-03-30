package com.example.test.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.material.Text
import com.example.test.data.WeatherForecast
import com.example.test.viewModel.ViewModel
import com.example.test.screen.WarningsView
import com.example.test.ui.WeatherUiState


@Composable
fun AirportScreen(ViewModel: ViewModel, icao: String) {
    val warningUiState by ViewModel.warningUiState.collectAsState()
    ViewModel.changeairPortICAO(icao)
    val tafmetarUiState by ViewModel.weatherUiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Flyplass ICAO: $icao")
        Text(
            text = "Warnings og tafmetar",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 20.sp
        )

        //TafmetarView(forecast = ..., viewModel = ViewModel)
        //Temporary text until tafmetar is implemented
        //Text("Tafmetar: (not implemented yet)")

        //Warnings. Obs Airmet, Sigmet og windshear er på samme API. Her vises Windshear
        WarningsView(warnings = warningUiState.warnings, viewModel = ViewModel, icao, tafmetarUiState.getForecastList())
        //TafmetarView(tafmetarUiState.getForecastList(), ViewModel)
    }


}

fun WeatherUiState.getForecastList(): List<WeatherForecast> {
    return when (this) {
        is WeatherUiState.Success -> weatherForecast
    }
}

