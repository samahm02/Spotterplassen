
package com.example.test.screen

import android.location.Location
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.test.data.loadAirports
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.test.MapState
import com.example.test.R
import com.example.test.data.loadPlaneSpottingLocation
import com.example.test.data.PlaneSpottingLocation
import com.example.test.viewModel.ViewModel
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.delay
import com.google.maps.android.compose.*

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MainScreen(
    onAirportButtonClicked: (icao: String) -> Unit = {},
    onSpottingButtonClicked: (spottingLocation: String) -> Unit = {},
    ViewModel: ViewModel,
    state: MapState
) {
    //Hovedskjerm, onAirportButtonClicked kalles når man trykker på marker sin infoboks og forteller
    // navigator om hvilken flyplass som er trykket på.
    //Camera ved start
    val osloLufthavn = LatLng(60.121,11.0502)
    val userLocation: Location? = ViewModel.state.value.lastKnownLocation
    val userPosition: LatLng

    //DO NOT LIFT OUT ASSIGNMET (break location)
    if (userLocation != null) {

        val userLatitude = userLocation.latitude
        val userLongitude = userLocation.longitude
        userPosition = LatLng(userLatitude, userLongitude)
        // You can use userPosition here
    } else {
        // Handle the case when user position is not available
        userPosition = osloLufthavn
    }

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        //println("POSISSSSJOOOON" + userPosition)
        position = CameraPosition.fromLatLngZoom(userPosition, 11f)
    }


    // Set properties using MapProperties which you can use to recompose the map
    val mapProperties =
        MapProperties(
            maxZoomPreference = 70f,
            minZoomPreference = 1f,
            isMyLocationEnabled = ViewModel.state.value.lastKnownLocation != null
        )

    //UI-related configurations
    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(mapToolbarEnabled = false)
        )
    }

    Box(Modifier.fillMaxSize()) {
        //GoogleMap composable:
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = mapProperties,
            uiSettings = mapUiSettings,
            cameraPositionState = cameraPositionState,

        ) {

            var spotterBoolean by remember {
                mutableStateOf(false)
            }
            //Preben sin flyplass metode, mMap er erstattet med maps-compose componenter:
            val airports = loadAirports()
            var selectedAirportICAO by remember { mutableStateOf("") }
            for (airport in airports) {
                val planeSpottingLocations = loadPlaneSpottingLocation().filter { it.FlypalssICAO == airport.ICAO }
                Marker(
                    icon= BitmapDescriptorFactory.fromResource(R.drawable.free_airport_location_icon_2959_thumb),
                    state = MarkerState(position = LatLng(airport.Latitude, airport.Longitude)),
                    title = airport.name,
                    snippet = airport.ICAO ,
                    onInfoWindowClick = { onAirportButtonClicked(airport.ICAO) },
                    onClick = {
                        if (airport.ICAO == selectedAirportICAO) {
                            //if the same airport is selected set to false
                            selectedAirportICAO = ""
                            spotterBoolean = false
                        } else {
                            //if new airport is selected set to true
                            selectedAirportICAO = airport.ICAO
                            spotterBoolean = true
                        }
                        false
                    }
                )
                if(spotterBoolean && airport.ICAO == selectedAirportICAO) {
                    SpotterPins(
                        planeSpottingLocations,
                        onSpottingButtonClicked = { onSpottingButtonClicked(it) }
                    )
                }
            }

            //MapEffect der selve GoogleMap er it(map).
            MapEffect { map ->
                map.uiSettings.isRotateGesturesEnabled = false
                map.uiSettings.isMapToolbarEnabled = true
//                map.uiSettings.isTiltGesturesEnabled = false
//                map.uiSettings.isIndoorLevelPickerEnabled = true

                val markers = mutableListOf<Marker?>()
                val poly = mutableListOf<Polygon>()
                while (true) {
                    val test = ViewModel.flyUiState.value.fly
                    while (ViewModel.flyUiState.value.fly.isEmpty() || ViewModel.flyUiState.value.fly == test) {
                        delay(100)
                        //println(1)
                    }
                    poly.forEach { it.remove() }
                    poly.clear()
                    markers.forEach { it?.remove() }
                    markers.clear()
                    val flyStates = ViewModel.flyUiState.value.fly[0].states
                    for (i in flyStates) {

                        if (i[6] != null || i[5] != null) {
                            val long: Double = i[5].toString().toDouble()
                            val lat: Double = i[6].toString().toDouble()

                            //If plane has orientation and name:
                            val flyPos = LatLng(lat, long)
                            if (i[10] != null && i[1] != null) {

                                markers.add(
                                    map.addMarker(
                                        MarkerOptions()
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.fly))
                                            .title("Icao24: " + i[0].toString() + ", Call sign: " + i[1].toString())
                                            .position(flyPos)
                                            .anchor(0.5f, 0.5f)
                                            .rotation(i[10].toString().toFloat())
                                            .snippet("Geo_altitude: " + i[13].toString() + "moh, Velocity: " + i[9].toString() +"m/s" )

                                    )
                                )
                            } else {
                                markers.add(
                                    map.addMarker(
                                        MarkerOptions()
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.fly))
                                            .position(flyPos)
                                    )
                                )
                            }
                        }
                    }

                    //Polygon for sigmet/airmet:
                    if (state.clusterItems.isNotEmpty()) {
                        /*
                        Mitch sine markers. onClick funker ikke med .map
                        val clusterManager = setupClusterManager(context, map)
                        map.setOnCameraIdleListener(clusterManager)
                        map.setOnMarkerClickListener(clusterManager)

                         */
                        state.clusterItems.forEach { clusterItem ->
                            poly.add(
                                map.addPolygon(clusterItem.polygonOptions)
                            )
                        }
                    }

                    delay(10000)
                    ViewModel.lastInnNyeFly()
                }
            }

            //Adds marker in the center of each polygon:
            if (state.clusterItems.isNotEmpty()) {
                state.clusterItems.forEach { clusterItem ->
                    PolygonMarker(polygonCenter = clusterItem.position, title = clusterItem.title)
                }
            }
        }
    }
    TopBar(title = "Kart")
}

@Composable
fun SpotterPins(
    spotterLocations: List<PlaneSpottingLocation>,
    onSpottingButtonClicked: (spottingLocation: String) -> Unit = {}
) {
    for (spottingLocation in spotterLocations) {
        Marker(
            state = MarkerState(position = LatLng(spottingLocation.Latitude, spottingLocation.Longitude)),
            icon= BitmapDescriptorFactory.fromResource(R.drawable.binoculars_2),
            title = spottingLocation.Name,
            onInfoWindowClick = { onSpottingButtonClicked(spottingLocation.Name) }
        )
    }
}

@Composable
fun PolygonMarker(polygonCenter: LatLng, title: String) {
    Marker(
        state = MarkerState(position = polygonCenter),
        title = title
    )
}


@Composable
fun TopBar(title: String) {
    TopAppBar(
        title = {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    )
}



