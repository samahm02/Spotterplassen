package com.example.test.screen

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.test.data.loadAirports
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import androidx.compose.foundation.layout.*
import androidx.compose.ui.platform.LocalContext
import com.example.test.MapState
import com.example.test.R
import com.example.test.model.ZoneClusterManager
import com.example.test.viewModel.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.delay

import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MainScreen(
    onAirportButtonClicked: (icao: String) -> Unit = {},
    ViewModel: ViewModel,
    state: MapState,
    setupClusterManager: (Context, GoogleMap) -> ZoneClusterManager,
    calculateZoneViewCenter: () -> LatLngBounds
) {
    //Hovedskjerm, onAirportButtonClicked kalles når man trykker på marker sin infoboks og forteller
    // navigator om hvilken flyplass som er trykket på.
    //Camera ved start
    val osloLufthavn = LatLng(60.121, 11.0502)
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(osloLufthavn, 11f)
    }
    // Set properties using MapProperties which you can use to recompose the map
    var mapProperties by remember {
        mutableStateOf(
            MapProperties(maxZoomPreference = 70f, minZoomPreference = 1f)
        )
    }

    //UI-related configurations
    var mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(mapToolbarEnabled = false)
        )
    }

    //Fra Mitch. Mapstate for å huske lokasjon og clusteritems (brukt til polygons: sigmet)
    Box(Modifier.fillMaxSize()) {
        //GoogleMap composable:
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = mapProperties,
            uiSettings = mapUiSettings,
            cameraPositionState = cameraPositionState
        ) {
            //Preben sin flyplass metode, mMap er erstattet med maps-compose componenter:
            val airports = loadAirports()
            for (airport in airports) {
                Marker(
                    state = MarkerState(position = LatLng(airport.Latitude, airport.Longitude)),
                    title = airport.name,
                    snippet = airport.ICAO,
                    //Navigerer til angitt skjerm når infoboksen trykkes på.
                    //Vi kan gjøre det samme for fly.
                    onInfoWindowClick = { onAirportButtonClicked(airport.ICAO) }
                )
            }
            //mMap.addMarker(MarkerOptions().position(LatLng(airport.Latitude, airport.Longitude)).title(airport.name))
            //MapEffect der selve GoogleMap er it.
            //Mitch (context og scope:)
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            MapEffect { map ->
                val markers = mutableListOf<Marker?>()
                while (true) {
                    val test = ViewModel.flyUiState.value.fly
                    while (ViewModel.flyUiState.value.fly.isEmpty() || ViewModel.flyUiState.value.fly == test) {
                        delay(100)
                    }
                    markers.forEach { it?.remove() }
                    markers.clear()
                    val flyStates = ViewModel.flyUiState.value.fly[0].states
                    for (i in flyStates) {

                        if (i[6] != null || i[5] != null) {
                            val long: Double = i[5].toString().toDouble()
                            val lat: Double = i[6].toString().toDouble()

                            val flyPos = LatLng(lat, long)
                            if (i[10] != null && i[1] != null) {

                                markers.add(
                                    map.addMarker(
                                        MarkerOptions()
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.kindpng_7070085))
                                            .title(i[1].toString())
                                            .position(flyPos)
                                            .anchor(0.5f, 0.5f)
                                            .rotation(i[10].toString().toFloat())
                                    )
                                )
                            } else {
                                markers.add(
                                    map.addMarker(
                                        MarkerOptions()
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.kindpng_7070085))
                                            .position(flyPos)
                                    )
                                )
                            }
                        }
                    }
                    /*
                //Polygon for sigmet/airmet:
                //Funker, men breaks airport skjerm.

                if (state.clusterItems.isNotEmpty()) {
                    val clusterManager = setupClusterManager(context, map)
                    map.setOnCameraIdleListener(clusterManager)
                    map.setOnMarkerClickListener(clusterManager)
                    state.clusterItems.forEach { clusterItem ->
                        map.addPolygon(clusterItem.polygonOptions)
                    }

                    //Flytter kameraet til klusterne
                    /*
                    it.setOnMapLoadedCallback {
                        if (state.clusterItems.isNotEmpty()) {
                            scope.launch {
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.newLatLngBounds(
                                        calculateZoneViewCenter(),
                                        0
                                    ),
                                )
                            }
                        }
                    }
                     */

                 */
                    delay(10000)
                    ViewModel.lastInnNyeFly()

                }
            }
        }
    }
}
