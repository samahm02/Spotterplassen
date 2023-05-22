package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.test.viewModel.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {

    // This is a requestPermissionLauncher for handling the result of permission requests.
    // If the permission is granted, the device location is fetched.
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // If permission is granted, we are calling getDeviceLocation method of the viewModel.
                viewModel.getDeviceLocation(fusedLocationProviderClient)
            }
        }

    // Checks if location permissions are granted and handles the result.
    private fun askPermissions() = when (PackageManager.PERMISSION_GRANTED) {
        // If permission is granted, getDeviceLocation is called.
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) -> {
            viewModel.getDeviceLocation(fusedLocationProviderClient)
        }
        // If the permission is not granted, then the permission request is launched.
        else -> {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    // FusedLocationProviderClient is an API from Google Play services that simplifies getting the device's location.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // ViewModel instance is created here. This will handle data-related tasks.
    private val viewModel: ViewModel by viewModels()

    // This is the onCreate method, which is called when the activity is created.
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // Initializing the fusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Asking for location permissions.
        askPermissions()
        setContentView(
            ComposeView(this).apply {
                setContent {

                    Navigasjon(viewModel)
                }
            }
        )

    }
}