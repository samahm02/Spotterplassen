package com.example.test.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.MapState
import com.example.test.data.DataSourceFly
import com.example.test.ui.FlyUiState
import com.example.test.ui.WarningUiState
import com.example.test.data.fetchXML
import com.example.test.model.*
import com.example.test.ui.WeatherUiState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.ktx.model.polygonOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewModel : ViewModel() {
    //Ny nøkkel:
    private val dataSource = DataSourceFly("https://Prebennc:Gruppe21@opensky-network.org/api/states/all?lamin=55.0&lomin=0.5&lamax=80.0&lomax=31.0")
    //Gammel nøkkel:
    //private val dataSource = DataSourceFly("https://opensky-network.org/api/states/all?lamin=55.0&lomin=0.5&lamax=80.0&lomax=31.0")
    private val _flyUiState = MutableStateFlow(FlyUiState(fly = listOf()))


    private var endPointWarnings = DataSourceFly("https://api.met.no/weatherapi/sigmets/2.0/")
    private val _warningUiState =  MutableStateFlow((WarningUiState(warnings = listOf())))
    val warningUiState: StateFlow<WarningUiState> = _warningUiState.asStateFlow()

    private val _weatherUiState = MutableStateFlow(
        WeatherUiState.Success(
            emptyList()
        ))
    val weatherUiState: StateFlow<WeatherUiState> =  _weatherUiState.asStateFlow()

    val flyUiState: StateFlow<FlyUiState> = _flyUiState.asStateFlow()

    private var airPortICAO: String = "ENGM"

    init{
        airPortICAO = "ENGM"
        loadFly()
        loadWarnings()
        laodTafData()
    }

    fun loadFly(){
        viewModelScope.launch {
            val fly = dataSource.fetchFly()
            val test = listOf(fly)
            _flyUiState.value = FlyUiState(fly = test)
/*
            val forecastList = fetchXML("ENGM")
            _weatherUiState.value = WeatherUiState.Success(forecastList)
            Log.v("----------------------",forecastList[0].issuedTime)
 */

        }
    }

    private fun laodTafData(){
        viewModelScope.launch {
            val forecastList = fetchXML(airPortICAO)
            _weatherUiState.value = WeatherUiState.Success(forecastList)
            //Log.v("----------------------",forecastList[0].issuedTime)
        }
    }

    fun changeairPortICAO(ICAO: String){
        airPortICAO = ICAO
        laodTafData()
    }

    var warnings = listOf<Any>()
    private var clusterItems = mutableListOf<ZoneClusterItem>()
    fun loadWarnings() {
        viewModelScope.launch(Dispatchers.IO) {
            warnings = endPointWarnings.fetchWarning()
            _warningUiState.value = WarningUiState(warnings = warnings)

            for (warning in warnings) {
                if (warning is Warning) {
                    clusterItems.add(
                        ZoneClusterItem(
                            id = "testid",
                            title = "testtitle",
                            snippet = "testsnippet",
                            polygonOptions = polygonOptions {
                                for (kordinatStreng in warning.kordinater) {
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

    //Mitch sin WM:
    val state: MutableState<MapState> = mutableStateOf(
        //Midlertidig clusteritem test:
        MapState(
            lastKnownLocation = null,
            clusterItems = clusterItems
        )
    )

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



    companion object {
        private val POLYGON_FILL_COLOR = Color.parseColor("#ABF44336")
    }
}