package com.example.test.ui

import com.example.test.data.MeteorologicalAerodromeReport
import com.example.test.data.WeatherForecast

sealed class WeatherUiState {
    data class Success(val weatherForecast: List<WeatherForecast>) : WeatherUiState()
}

sealed class WeatherUiStateReport {
    data class Success(val WeatherReport: List<MeteorologicalAerodromeReport>) : WeatherUiStateReport()
}
