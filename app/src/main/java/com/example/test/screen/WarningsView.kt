package com.example.test.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.test.model.Warning
import com.example.test.model.Windshear
import com.example.test.viewModel.ViewModel

@Composable
fun WarningsView(
    warnings: List<Any>,
    viewModel: ViewModel,
    airPortIcao: String
) {
    //viewModel.loadWarnings()
    val windshearForThisAirport: MutableList<Windshear> = mutableListOf<Windshear>()
    Log.d("Debug", "TestWview: $warnings")

    for (warning in warnings) {
        Log.d("Debug", "warning.icao: ${warning} Airport: $airPortIcao")
        if (warning is Windshear && warning.content.contains(airPortIcao)) {
            windshearForThisAirport.add(warning)
        }
    }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        item() {
            if(windshearForThisAirport.isEmpty()) {
                Text("No warnings")
            }
        }
        //Forrvirrende variabel navn her, skal endres
        items(windshearForThisAirport) { windshareData ->
            WindshearCard(windshearData = windshareData)
        }
    }
}

@Composable
fun WindshearCard(windshearData: Windshear) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)

    ) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier
                .height(20.dp)
                .fillMaxWidth()
            )
            Text(text = windshearData.content)
        }
    }
}