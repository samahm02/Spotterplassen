package com.example.test.ui

import com.example.test.model.FlyData

/**
 * data class FlyUiState is for the Opensky API
 * It stores a list of FlyData objects.
 */
data class FlyUiState (
    val fly: List<FlyData>
)
