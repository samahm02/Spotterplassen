package com.example.test.model

/**
 * data class Warning is for the SIGMETS API that are shown as Polygons
 */
data class Warning(val content: String, var coordinates: List<String>)