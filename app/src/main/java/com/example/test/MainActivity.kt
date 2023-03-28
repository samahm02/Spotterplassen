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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = ActivityMapsBinding.inflate(layoutInflater)
        //setContentView(binding.root)
        setContentView(
            ComposeView(this).apply {
                setContent {
                    Navigasjon()
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
}