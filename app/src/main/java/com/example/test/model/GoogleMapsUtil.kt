package com.example.test.model
//Source:
//https://github.com/mitchtabian/Google-Maps-Compose

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

//Calculates center of polygons, given a list of coordinates
fun List<LatLng>.getCenterOfPolygon(): LatLngBounds {
    val centerBuilder: LatLngBounds.Builder = LatLngBounds.builder()
    forEach { centerBuilder.include(LatLng(it.latitude, it.longitude)) }
    return centerBuilder.build()
}
