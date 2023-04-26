package com.example.test.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.material.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.test.R
import com.example.test.data.AirportData
import com.example.test.data.MeteorologicalAerodromeReport
import com.example.test.data.WeatherForecast
import com.example.test.viewModel.ViewModel
import com.example.test.ui.WeatherUiState
import com.example.test.ui.WeatherUiStateReport


@Composable
fun AirportScreen(ViewModel: ViewModel, airportIcao: String, airportData: AirportData?) {
    val warningUiState by ViewModel.warningUiState.collectAsState()
    //ViewModel.changeairPortICAO(icao)
    val tafmetarUiState by ViewModel.weatherUiState.collectAsState()
    val metarUiState by ViewModel.weatherUiStateReport.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            Image(painter = painterResource(id = R.drawable.minimalairport),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            )
            if (airportData != null) {
                Text(
                    text = airportData.name,
                    fontSize = 30.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        WarningsView(warnings = warningUiState.warnings, viewModel = ViewModel, airportIcao, tafmetarUiState.getForecastList(),
            metarUiState.getForecastList()
        )
    }
}

fun WeatherUiState.getForecastList(): List<WeatherForecast> {
    return when (this) {
        is WeatherUiState.Success -> weatherForecast
    }
}


fun WeatherUiStateReport.getForecastList(): List<MeteorologicalAerodromeReport> {
    return when (this) {
        is WeatherUiStateReport.Success -> WeatherReport
    }
}