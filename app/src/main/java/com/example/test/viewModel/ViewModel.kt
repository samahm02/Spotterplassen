package com.example.test.viewModel

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.data.MapState
import com.example.test.data.DataSourceKtor
import com.example.test.data.fetchXml
import com.example.test.data.fetchXmlTafmetar
import com.example.test.model.*
import com.example.test.ui.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.model.polygonOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewModel : ViewModel() {
    // DataSource instances for fetching data from different APIs
    // Possible credential leak addressed in the report section: 5.7 and 7.2
    private val dataSource = DataSourceKtor("https://Prebennc:Gruppe21@opensky-network.org/api/states/all?lamin=55.0&lomin=0.5&lamax=80.0&lomax=31.0")
    private val _planeUiState = MutableStateFlow(PlaneUiState(plane = listOf()))

    // MutableStateFlow instances for storing and exposing UI state
    private var endPointWarnings = DataSourceKtor("https://gw-uio.intark.uh-it.no/in2000/weatherapi/sigmets/2.0/")
    private val _warningUiState =  MutableStateFlow((WarningUiState(warnings = listOf())))
    val warningUiState: StateFlow<WarningUiState> = _warningUiState.asStateFlow()
    private val _weatherUiState = MutableStateFlow(
        WeatherUiState.Success(
            emptyList()
        ))
    private val _weatherUiStateReport = MutableStateFlow(
        WeatherUiStateReport.Success(
            emptyList()
        ))

    // Exposed read-only StateFlow instances
    val weatherUiStateReport: StateFlow<WeatherUiStateReport> =  _weatherUiStateReport.asStateFlow()
    val weatherUiState: StateFlow<WeatherUiState> =  _weatherUiState.asStateFlow()
    val planeUiState: StateFlow<PlaneUiState> = _planeUiState.asStateFlow()

    // Default ICAO airport code set to Gardermoen (OSL)
    private var airPortICAO: String = "ENGM"

    // The init block runs after primary constructor. Here, we load initial data
    init{
        loadPlane()
        loadWarnings()
    }

    // Fetches data about flights and updates the flyUiState
    private fun loadPlane(){
        viewModelScope.launch {
            val fly = dataSource.fetchPlane()
            val listPlane = listOf(fly)
            _planeUiState.value = PlaneUiState(plane = listPlane)
        }
    }

    // Method to fetch new flight data
    fun loadNewPlanes(){
        loadPlane()
    }

    // Fetches forecast data for a given ICAO code and updates weatherUiState
    private fun loadTafMetarData(){
        viewModelScope.launch {
            val forecastList = fetchXml(airPortICAO)
            val reportList = fetchXmlTafmetar(airPortICAO)

            _weatherUiState.value = WeatherUiState.Success(forecastList)
            _weatherUiStateReport.value = WeatherUiStateReport.Success(reportList)
        }
    }

    // Changes the ICAO code and reloads Taf data
    fun changeairPortICAO(ICAO: String){
        airPortICAO = ICAO
        loadTafMetarData()
    }

    // List to hold warning data and cluster items
    private var warnings = listOf<Any>()
    private var clusterItems = mutableListOf<ZoneClusterItem>()
    // Fetches warning data and updates the warningUiState
    fun loadWarnings() {
        // This function is responsible for loading warnings.
        // It is executed on the viewModelScope coroutine in the IO dispatcher.
        viewModelScope.launch(Dispatchers.IO) {
            // Fetch warnings from the endPointWarnings
            warnings = endPointWarnings.fetchWarning()

            // Update the value of _warningUiState LiveData with the fetched warnings
            _warningUiState.value = WarningUiState(warnings = warnings)

            // Iterate through each warning in the fetched warnings
            for (warning in warnings) {
                // Check if the warning is of type Warning
                if (warning is Warning) {
                    // Create a ZoneClusterItem and add it to the clusterItems list
                    clusterItems.add(
                        ZoneClusterItem(
                            id = "Unused",
                            title = warning.content,
                            snippet = "Unused",
                            polygonOptions = polygonOptions {
                                // Iterate through each coordinate string in the warning's coordinates
                                for (coordinateString in warning.coordinates) {
                                    // Split the coordinate string by a space to extract latitude and longitude values
                                    val split = coordinateString.split(" ")

                                    // Create a LatLng object with the extracted latitude and longitude values
                                    add(LatLng(split[0].toDouble(), split[1].toDouble()))
                                }
                                // Set the fill color of the polygonOptions to a specified value
                                fillColor(POLYGON_FILL_COLOR)
                            }
                        )
                    )
                }
            }
        }
    }

    // Computed property for polygon color
    companion object {
        private val POLYGON_FILL_COLOR = Color.parseColor("#1AF44336")
    }

    // MutableState for MapState. This is used to handle state related to the map
    val state: MutableState<MapState> = mutableStateOf(
        MapState(
            lastKnownLocation = null,
            clusterItems = clusterItems
        )
    )

    // Gets the device's last known location and updates the state
    @SuppressLint("MissingPermission")
    fun getDeviceLocation(
        fusedLocationProviderClient: FusedLocationProviderClient
    ) {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    state.value = state.value.copy(
                        lastKnownLocation = task.result,
                    )
                }
            }
        } catch (e: SecurityException) {
            // Show error in terminal for debugging
            println(e)
        }
    }

}