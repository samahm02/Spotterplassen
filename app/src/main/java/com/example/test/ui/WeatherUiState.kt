package com.example.test.ui

import com.example.test.data.MeteorologicalAerodromeReport
import com.example.test.data.WeatherForecast

/**
 * WeatherUiState is a represents Taf from the TAFMETAR Api
 * It holds a list of weatherForecasts
 */
sealed class WeatherUiState {
    data class Success(val weatherForecast: List<WeatherForecast>) : WeatherUiState()
}

/**
 * WeatherUiStateReport is a represents Metar from the TAFMETAR Api
 * It holds a list of WeatherReports
 */
sealed class WeatherUiStateReport {
    data class Success(val WeatherReport: List<MeteorologicalAerodromeReport>) : WeatherUiStateReport()
}
