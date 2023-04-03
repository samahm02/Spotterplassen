package com.example.test

import android.location.Location
import com.example.test.model.ZoneClusterItem

data class MapState(
    val lastKnownLocation: Location?,
    val clusterItems: List<ZoneClusterItem>,
)