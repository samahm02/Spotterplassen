package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import com.example.test.data.loadAirports
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.test.viewModel.ViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.runtime.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*


//class MapsActivity : AppCompatActivity(), OnMapReadyCallback
//class MapsActivity : ComponentActivity(), OnMapReadyCallback
class MainActivity : ComponentActivity() {
    //private lateinit var mMap: GoogleMap
    //private lateinit var binding: ActivityMapsBinding
    val ViewModel = ViewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = ActivityMapsBinding.inflate(layoutInflater)
        //setContentView(binding.root)
        setContentView(
            ComposeView(this).apply {
                setContent {
                    MainScreen()
                }
            }
        )



        /*
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

         */
    }

    @Composable
    fun MainScreen() {
        //Camera
        val osloLufthavn = LatLng(60.121,11.0502)
        val cameraPositionState: CameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(osloLufthavn, 11f)
        }

        // Set properties using MapProperties which you can use to recompose the map
        var mapProperties by remember {
            mutableStateOf(
                MapProperties(maxZoomPreference = 10f, minZoomPreference = 5f)
            )
        }
        var mapUiSettings by remember {
            mutableStateOf(
                MapUiSettings(mapToolbarEnabled = false)
            )
        }
        Box(Modifier.fillMaxSize()) {
            GoogleMap(
                properties = mapProperties,
                uiSettings = mapUiSettings,
                cameraPositionState = cameraPositionState
            ) {
                val airports = loadAirports()
                for (airport in airports) {
                    Marker(
                        state = MarkerState(position = LatLng(airport.Latitude, airport.Longitude)),
                        title = airport.name,
                        snippet = airport.ICAO
                    )
                    //mMap.addMarker(MarkerOptions().position(LatLng(airport.Latitude, airport.Longitude)).title(airport.name))

                    MapEffect {
                        while (ViewModel.flyUiState.value.fly.isEmpty()){
                            delay(100)
                        }
                        val flyStates = ViewModel.flyUiState.value.fly[0].states
                        for (i in flyStates){


                            if (i[6] != null|| i[5] != null){
                                val long : Double= i[5].toString().toDouble()
                                val lat : Double = i[6].toString().toDouble()

                                val flyPos = LatLng(lat, long)
                                if (i[10].toString().toFloat() != null && i[1].toString() != null){

                                    it.addMarker(MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.kindpng_7070085))
                                        .title(i[1].toString())
                                        .position(flyPos)
                                        .anchor(0.5f, 0.5f)
                                        .rotation(i[10].toString().toFloat()))





                                }
                                else{
                                    it.addMarker(MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.kindpng_7070085))
                                        .position(flyPos))
                                }

                            }
                        }

                    }
                }


            }
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
        }



        /*
        Virker ikke siden supportFragmentManager krever AppCompat tror jeg.
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(activity)

        Spacer(Modifier.height(20.dp))
        Text("test")
         */
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    /*
    Other onReady:

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val ViewModel = ViewModel()
        val airports = loadAirports()
        for (airport in airports) {
            mMap.addMarker(MarkerOptions().position(LatLng(airport.Latitude, airport.Longitude)).title(airport.name))
        }

        lifecycleScope.launch {
            while (ViewModel.flyUiState.value.fly.isEmpty()){
                delay(100)
            }
            val flyStates = ViewModel.flyUiState.value.fly[0].states
            for (i in flyStates){


                if (i[6] != null|| i[5] != null){
                    val long : Double= i[5].toString().toDouble()
                    val lat : Double = i[6].toString().toDouble()

                    val flyPos = LatLng(lat, long)
                    if (i[10].toString().toFloat() != null && i[1].toString() != null){
                        mMap.addMarker(MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.kindpng_7070085))
                            .title(i[1].toString())
                            .position(flyPos)
                            .anchor(0.5f, 0.5f)
                            .rotation(i[10].toString().toFloat()))





                    }
                    else{
                        mMap.addMarker(MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.kindpng_7070085))
                            .position(flyPos))
                    }

                }
            }
        }
        val osloLufthavn = LatLng(60.121,11.0502)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(osloLufthavn))
    }
     */
}