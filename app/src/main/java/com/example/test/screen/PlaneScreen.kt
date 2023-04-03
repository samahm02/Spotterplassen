package com.example.test.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.test.viewModel.ViewModel

@Composable
fun PlaneScreen(ViewModel: ViewModel, planeTitle: String) {
    val flyUiState by ViewModel.flyUiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Plane id(?): $planeTitle")
        Text(
            text = "Test",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 20.sp
        )
    }
}