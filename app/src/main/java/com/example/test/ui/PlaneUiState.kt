package com.example.test.ui

import com.example.test.model.PlaneData

/**
 * data class PlaneUiState is for the Opensky API
 * It stores a list of FlyData objects.
 */
data class PlaneUiState (
    val plane: List<PlaneData>
)
