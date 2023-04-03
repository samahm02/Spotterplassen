package com.example.test.model
//Kilde:
//https://github.com/mitchtabian/Google-Maps-Compose

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.MarkerManager

class ZoneClusterManager(
    context: Context,
    googleMap: GoogleMap,
): ClusterManager<ZoneClusterItem>(context, googleMap, MarkerManager(googleMap))