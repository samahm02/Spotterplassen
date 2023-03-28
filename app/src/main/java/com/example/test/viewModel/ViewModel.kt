package com.example.test.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.data.DataSourceFly
import com.example.test.ui.FlyUiState
import com.example.test.ui.WarningUiState
import com.example.test.data.fetchXML
import com.example.test.model.Warning
import com.example.test.ui.WeatherUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewModel : ViewModel() {
    private val dataSource = DataSourceFly("https://opensky-network.org/api/states/all?lamin=55.0&lomin=0.5&lamax=80.0&lomax=31.0")
    private val _flyUiState = MutableStateFlow(FlyUiState(fly = listOf()))

    var warnings: List<Warning> = listOf()
    private var endPointWarnings = DataSourceFly("https://api.met.no/weatherapi/sigmets/2.0/")
    private val _warningUiState =  MutableStateFlow((WarningUiState(listOf())))
    val warningUiState: StateFlow<WarningUiState> = _warningUiState.asStateFlow()

    private val _weatherUiState = MutableStateFlow(
        WeatherUiState.Success(
            emptyList()
        ))
    val weatherUiState: StateFlow<WeatherUiState> =  _weatherUiState.asStateFlow()

    val flyUiState: StateFlow<FlyUiState> = _flyUiState.asStateFlow()

    init{
        loadFly()
        loadWarnings()
    }

    fun loadFly(){
        viewModelScope.launch {
            val fly = dataSource.fetchFly()
            val test = listOf(fly)
            _flyUiState.value = FlyUiState(fly = test)

            val forecastList = fetchXML("ENGM")
            _weatherUiState.value = WeatherUiState.Success(forecastList)

            Log.v("----------------------",forecastList[0].issuedTime)

        }
    }

    fun loadWarnings() {
        viewModelScope.launch(Dispatchers.IO) {
            warnings = endPointWarnings.fetchWarning()
        }
    }

}