package com.example.test.viewModel

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.MapState
import com.example.test.data.DataSourceKtor
import com.example.test.data.fetchXml
import com.example.test.data.fetchXmlTafmetar
import com.example.test.model.*
import com.example.test.ui.FlyUiState
import com.example.test.ui.WarningUiState
import com.example.test.ui.WeatherUiState
import com.example.test.ui.WeatherUiStateReport
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.model.polygonOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewModel : ViewModel() {
    //Ny nøkkel:
//    private val dataSource = DataSourceFly("https://opensky-network.org/api/states/all")
    //Gammel nøkkel:
    // DataSource instances for fetching data from different APIs
    private val dataSource = DataSourceKtor("https://Prebennc:Gruppe21@opensky-network.org/api/states/all?lamin=55.0&lomin=0.5&lamax=80.0&lomax=31.0")
    private val _flyUiState = MutableStateFlow(FlyUiState(fly = listOf()))

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
    val flyUiState: StateFlow<FlyUiState> = _flyUiState.asStateFlow()

    // Default ICAO airport code set to Gardermoen (OSL)
    private var airPortICAO: String = "ENGM"

    // The init block runs after primary constructor. Here, we load initial data
    init{
        loadFly()
        loadWarnings()
    }

    // Fetches data about flights and updates the flyUiState
    private fun loadFly(){
        viewModelScope.launch {
            val fly = dataSource.fetchFly()
            val listPlane = listOf(fly)
            _flyUiState.value = FlyUiState(fly = listPlane)
/*
            val forecastList = fetchXML("ENGM")
            _weatherUiState.value = WeatherUiState.Success(forecastList)
            Log.v("----------------------",forecastList[0].issuedTime)
 */

        }
    }

    // Method to fetch new flight data
    fun loadNewPlanes(){
        loadFly()
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
        viewModelScope.launch(Dispatchers.IO) {
            warnings = endPointWarnings.fetchWarning()
            _warningUiState.value = WarningUiState(warnings = warnings)

            for (warning in warnings) {
                if (warning is Warning) {
                    clusterItems.add(
                        ZoneClusterItem(
                            id = "testid",
                            title = warning.content,
                            snippet = "testsnippet",
                            polygonOptions = polygonOptions {
                                for (kordinatStreng in warning.coordinates) {
                                    val split = kordinatStreng.split(" ")

                                    add(LatLng(split[0].toDouble(), split[1].toDouble()))
                                }
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
            // Show error or something
        }
    }

    /*
    fun setupClusterManager(
        context: Context,
        map: GoogleMap,
    ): ZoneClusterManager {
        val clusterManager = ZoneClusterManager(context, map)
        clusterManager.addItems(state.value.clusterItems)
        return clusterManager
    }



    fun calculateZoneLatLngBounds(): LatLngBounds {
        // Get all the points from all the polygons and calculate the camera view that will show them all.
        val latLngs = state.value.clusterItems.map { it.polygonOptions }
            .map { it.points.map { LatLng(it.latitude, it.longitude) } }.flatten()
        return latLngs.calculateCameraViewPoints().getCenterOfPolygon()
    }
     */
}