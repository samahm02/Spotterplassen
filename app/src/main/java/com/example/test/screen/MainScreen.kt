
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
    // Main screen, onAirportButtonClicked is called when clicking the marker infobox, and
    // navigator which airport is clicked
    //Default positions
    val osloLufthavn = LatLng(60.197166,11.099431)
    val userLocation: Location? = ViewModel.state.value.lastKnownLocation

    //DO NOT LIFT OUT ASSIGNMENT (break location)
    val userPosition: LatLng = if (userLocation != null) {

        val userLatitude = userLocation.latitude
        val userLongitude = userLocation.longitude
        LatLng(userLatitude, userLongitude)
        // You can use userPosition here
    } else {
        // Handle the case when user position is not available
        osloLufthavn
    }

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        // Default camera position
        position = CameraPosition.fromLatLngZoom(userPosition, 11f)
    }


    // Set properties using MapProperties which you can use to recompose the map
    val mapProperties =
        MapProperties(
            maxZoomPreference = 70f,
            minZoomPreference = 1f,
            // Shows user position on map
            isMyLocationEnabled = state.lastKnownLocation != null,
        )
    //println("LASTKNOWNLOCATION: ${state.lastKnownLocation}")
    //UI-related configurations
    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(mapToolbarEnabled = false)
        )
    }

    //Creates a box layout and fills the full mainScreen
    Box(Modifier.fillMaxSize()) {
        //GoogleMap composable with location variables as parameters:
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = mapProperties,
            uiSettings = mapUiSettings,
            cameraPositionState = cameraPositionState,

        ) {

            // Boolean to show markers for spotting locations
            var spotterBoolean by remember {
                mutableStateOf(false)
            }
            //Loads a list of airport data from airportData.kt
            val airports = loadAirports()

            // By remember keeping track of airport marker clicks
            var selectedAirportICAO by remember { mutableStateOf("") }

            // Loops through all airport, adds markers, if spotterBoolean add sportting markers
            for (airport in airports) {
                val planeSpottingLocations = loadPlaneSpottingLocation().filter { it.flyplassIcao == airport.icao }
                Marker(
                    icon= BitmapDescriptorFactory.fromResource(R.drawable.free_airport_location_icon_2959_thumb),
                    state = MarkerState(position = LatLng(airport.latitude, airport.longitude)),
                    title = airport.name,
                    snippet = airport.icao ,
                    onInfoWindowClick = { onAirportButtonClicked(airport.icao) },
                    onClick = {
                        if (airport.icao == selectedAirportICAO) {
                            //if the same airport is selected set to false
                            selectedAirportICAO = ""
                            spotterBoolean = false
                        } else {
                            //if new airport is selected set to true
                            selectedAirportICAO = airport.icao
                            spotterBoolean = true
                        }
                        false
                    }
                )
                if(spotterBoolean && airport.icao == selectedAirportICAO) {
                    SpotterPins(
                        planeSpottingLocations,
                        onSpottingButtonClicked = { onSpottingButtonClicked(it) }
                    )
                }
            }

            // MapEffect where the GoogleMap itself is referred to as 'map'
            MapEffect { map ->
                // Disables map rotation by gesture
                map.uiSettings.isRotateGesturesEnabled = false
                // Adds maps toolbar
                map.uiSettings.isMapToolbarEnabled = true
//                map.uiSettings.isTiltGesturesEnabled = false
//                map.uiSettings.isIndoorLevelPickerEnabled = true

                // List for markers
                val markers = mutableListOf<Marker?>()
                // List from polygons
                val poly = mutableListOf<Polygon>()
                // Function waits at this point until fly in flyUiState isn't empty or fly is changed
                while (true) {
                    val test = ViewModel.flyUiState.value.fly
                    while (ViewModel.flyUiState.value.fly.isEmpty() || ViewModel.flyUiState.value.fly == test) {
                        delay(100)
                    }

                    // Removes polygons from map
                    poly.forEach { it.remove() }
                    // Removes all elements in poly list
                    poly.clear()
                    // removes markers from map
                    markers.forEach { it?.remove() }
                    // Empties / clear markers list
                    markers.clear()

                    // Retrieves states of planes
                    val flyStates = ViewModel.flyUiState.value.fly[0].states
                    // Loops plane state data and creates plane emoticons
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

                    // Checks for sigmet/airmet:
                    if (state.clusterItems.isNotEmpty()) {
                        /*
                        Mitch's markers. onClick doesn't work with .map
                        val clusterManager = setupClusterManager(context, map)
                        map.setOnCameraIdleListener(clusterManager)
                        map.setOnMarkerClickListener(clusterManager)

                         */
                        // Adds polygons to map
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
    // Uses TopBar function to display title of screen
    TopBar(title = "Kart")
}

// Puts all spotterPins in the spotterLocations list on the map
@Composable
fun SpotterPins(
    spotterLocations: List<PlaneSpottingLocation>,
    onSpottingButtonClicked: (spottingLocation: String) -> Unit = {}
) {
    for (spottingLocation in spotterLocations) {
        Marker(
            state = MarkerState(position = LatLng(spottingLocation.latitude, spottingLocation.longitude)),
            icon= BitmapDescriptorFactory.fromResource(R.drawable.binoculars_2),
            title = spottingLocation.name,
            onInfoWindowClick = { onSpottingButtonClicked(spottingLocation.name) }
        )
    }
}

// Function for marker at the center of polygon
@Composable
fun PolygonMarker(polygonCenter: LatLng, title: String) {
    Marker(
        state = MarkerState(position = polygonCenter),
        icon= BitmapDescriptorFactory.fromResource(R.drawable._a81af7d9123fa7bcc9b0793),
        title = title
    )
}

// Displays text input as TopAppBar
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



