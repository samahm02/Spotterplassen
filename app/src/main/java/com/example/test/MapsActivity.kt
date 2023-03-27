package com.example.test


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.test.data.loadAirports
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.test.databinding.ActivityMapsBinding
import com.example.test.viewModel.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.MarkerInfoWindow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)



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
}