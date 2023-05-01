package com.example.test.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.test.R
import com.example.test.data.AirportData
import com.example.test.data.MeteorologicalAerodromeReport
import com.example.test.data.WeatherForecast
import com.example.test.viewModel.ViewModel
import com.example.test.ui.WeatherUiState
import com.example.test.ui.WeatherUiStateReport


@OptIn(ExperimentalTextApi::class)
@Composable
fun AirportScreen(ViewModel: ViewModel, airportIcao: String, airportData: AirportData?) {
    val warningUiState by ViewModel.warningUiState.collectAsState()
    val tafmetarUiState by ViewModel.weatherUiState.collectAsState()
    val metarUiState by ViewModel.weatherUiStateReport.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        TopAppBar(
            title = {
                Text(
                    text = "Flyplass",
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Image(painter = painterResource(id = R.drawable.minimalairport),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    //.clip(RoundedCornerShape(8.dp))
            )
            //Title
            if (airportData != null) {
                Text(
                    text = airportData.name,
                    fontSize = 38.sp,
                    color = Color.White,
                    //fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
                //Title (outlined tekst workaround)
                Text(
                    text = airportData.name,
                    fontSize = 38.sp,
                    color = Color.Black,
                    //fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.BottomCenter),
                    style = TextStyle.Default.copy(
                        fontSize = 64.sp,
                        drawStyle = Stroke(
                            miter = 10f,
                            width = 5f,
                            join = StrokeJoin.Round
                        )
                    )

                )
            }
        }

        WarningsView(
            warnings = warningUiState.warnings,
            airportIcao,
            tafmetarUiState.getForecastList(),
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

