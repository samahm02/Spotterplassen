package com.example.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.material.Text
import androidx.compose.foundation.layout.*
//Lazy er greit i importere manuelt:
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.unit.dp
import com.example.test.model.Warning
import com.example.test.viewModel.ViewModel
import androidx.compose.material.Card
import com.example.test.screen.WarningsView

@Composable
fun AirportScreen(ViewModel: ViewModel) {
    val warningUiState by ViewModel.warningUiState.collectAsState()
    val tafmetarUiState by ViewModel.weatherUiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Warnings og tafmetar",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 20.sp
        )

        //TafmetarView(forecast = ..., viewModel = ViewModel)
        //Temporary text until tafmetar is implemented
        Text("Tafmetar: (not implemented yet)")

        WarningsView(
            warnings = warningUiState.warnings, viewModel = ViewModel
        )
    }
}

