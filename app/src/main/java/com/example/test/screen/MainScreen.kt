package com.example.test.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.test.data.loadAirports
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.example.test.R
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.test.viewModel.ViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import kotlinx.coroutines.delay
import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import com.example.test.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint

import com.google.maps.android.compose.*


@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MainScreen(context: MainActivity, onAirportButtonClicked: (icao: String) -> Unit = {}, ViewModel: ViewModel) {
    //Hovedskjerm, onAirportButtonClicked kalles når man trykker på marker sin infoboks og forteller
    // navigator om hvilken flyplass som er trykket på.
    //Camera ved start
    val osloLufthavn = LatLng(60.121,11.0502)
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(osloLufthavn, 11f)
    }


    ///
    // Set properties using MapProperties which you can use to recompose the map
    val mapProperties by remember {
        mutableStateOf(
            MapProperties(maxZoomPreference = 70f, minZoomPreference = 1f)
        )
    }

    //UI-related configurations
    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(mapToolbarEnabled = false)
        )
    }



    Column {

        Box(Modifier.fillMaxSize()) {

            //GoogleMap composable:
            GoogleMap(
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
                //MapEffect der selve GoogleMap o
                MapEffect {
                    while (ViewModel.flyUiState.value.fly.isEmpty()) {
                        delay(100)
                    }
                    val flyStates = ViewModel.flyUiState.value.fly[0].states
                    for (i in flyStates) {


                        if (i[6] != null || i[5] != null) {
                            val long: Double = i[5].toString().toDouble()
                            val lat: Double = i[6].toString().toDouble()

                            val flyPos = LatLng(lat, long)
                            if (i[10] != null && i[1] != null) {

                                it.addMarker(
                                    MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.kindpng_7070085))
                                        .title(i[1].toString())
                                        .position(flyPos)
                                        .anchor(0.5f, 0.5f)
                                        .rotation(i[10].toString().toFloat())
                                )
                            } else {
                                it.addMarker(
                                    MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.kindpng_7070085))
                                        .position(flyPos)
                                )
                            }

                        }
                    }

                }

            }

            /*
            Column {
                Button(onClick = {
                    mapProperties = mapProperties.copy(
                        isBuildingEnabled = !mapProperties.isBuildingEnabled
                    )
                }) {
                    Text(text = "Toggle isBuildingEnabled")
                }
                Button(onClick = {
                    mapUiSettings = mapUiSettings.copy(
                        mapToolbarEnabled = !mapUiSettings.mapToolbarEnabled
                    )
                }) {
                    Text(text = "Toggle mapToolbarEnabled")
                }
            }

             */
        }
    }

    

    /*
    Virker ikke siden supportFragmentManager krever AppCompat tror jeg.
    AppCompat er gammel aktivitet for støtte til gammle android versjoner.
    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    val mapFragment = supportFragmentManager
        .findFragmentById(R.id.map) as SupportMapFragment
    mapFragment.getMapAsync(activity)

    Spacer(Modifier.height(20.dp))
    Text("test")
     */

}