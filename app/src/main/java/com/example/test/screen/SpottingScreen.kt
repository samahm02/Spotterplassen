package com.example.test.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.data.PlaneSpottingLocation

@Composable
fun SpottingScreen(spottingLocation: PlaneSpottingLocation) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Spotterplass",
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 80.dp,start = 30.dp, end = 30.dp),

                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                Text(
                    modifier = Modifier
                        .padding(bottom = 10.dp),
                    text = spottingLocation.Name,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    )
                Text(
                    text = spottingLocation.Description,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    )
            }

        }
    }
}