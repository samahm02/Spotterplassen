package com.example.test.ui

import com.example.test.data.WeatherForecast

sealed class WeatherUiState {
    data class Success(val weatherForecast: List<WeatherForecast>) : WeatherUiState()
}