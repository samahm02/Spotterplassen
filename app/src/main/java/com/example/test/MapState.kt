package com.example.test

import android.location.Location
import com.example.test.model.ZoneClusterItem

// Define a data class named MapState to represent the state of a map
data class MapState(
    // Represents the last known location on the map, nullable because it may be unknown or not available
    val lastKnownLocation: Location?,
    // Represents a list of cluster items on the map, such as markers or points of interest
    val clusterItems: List<ZoneClusterItem>,
)