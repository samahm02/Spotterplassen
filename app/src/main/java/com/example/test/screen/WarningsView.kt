package com.example.test.screen

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
import com.example.test.viewModel.ViewModel

@Composable
fun WarningsView(
    warnings: List<Warning>,
    viewModel: ViewModel,
) {
    viewModel.loadWarnings()
    println("test3")
    println(warnings)

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        item() {
            if(warnings.isEmpty()) {
                Text("No warnings")
            }
        }
        items(warnings) { sigmetData ->
            WarningCard(warningData = sigmetData)
            println("test3")
        }
    }
}

@Composable
fun WarningCard(warningData: Warning) {
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
            Text(text = warningData.content)
        }
    }
}