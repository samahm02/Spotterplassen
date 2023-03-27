package com.example.test.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.data.DataSourceFly
import com.example.test.data.FlyUiState
import com.example.test.data.fetchXML
import com.example.test.ui.WeatherUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewModel : ViewModel() {
    private val dataSource = DataSourceFly("https://opensky-network.org/api/states/all?lamin=55.0&lomin=0.5&lamax=80.0&lomax=31.0")
    private val _flyUiState = MutableStateFlow(FlyUiState(fly = listOf()))

    private val _uiState = MutableStateFlow(
        WeatherUiState.Success(
            emptyList()
        ))
    val uiState: StateFlow<WeatherUiState> =  _uiState.asStateFlow()

    val flyUiState: StateFlow<FlyUiState> = _flyUiState.asStateFlow()

    init{
        loadFly()
    }

    private fun loadFly(){
        viewModelScope.launch {
            val fly = dataSource.fetchFly()
            val test = listOf(fly)
            _flyUiState.value = FlyUiState(fly = test)

            val forecastList = fetchXML("ENGM")
            _uiState.value = WeatherUiState.Success(forecastList)

            Log.v("----------------------",forecastList[0].issuedTime)

        }
    }

}