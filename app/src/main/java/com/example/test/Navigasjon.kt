package com.example.test

import androidx.compose.foundation.layout.padding
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.test.screen.AirportScreen
import com.example.test.screen.MainScreen
import com.example.test.viewModel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope

enum class Navigasjon() {
    //Klasse for navigasjonsdestinasjoner/skjermer
    Map,
    Airport
}

@Composable
fun Navigasjon(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    //Scaffold for navigasjon. Lager viewModel her, kansjke gjøre det et annet sted?
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val ViewModel = ViewModel()
    var selectedAirPort: String = ""

    //Scaffold med ønsket paramet.
    androidx.compose.material.Scaffold(
        scaffoldState = scaffoldState
    ) { innerPadding ->
        //NavHos som holder oversikt over composables
        NavHost(
            navController = navController,
            startDestination = Navigasjon.Map.name,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(route = Navigasjon.Map.name) {
                ViewModel.loadWarnings()
                MainScreen(
                    state = ViewModel.state.value,
                    ViewModel = ViewModel,
                    setupClusterManager = ViewModel::setupClusterManager,
                    calculateZoneViewCenter = ViewModel::calculateZoneLatLngBounds,
                    onAirportButtonClicked = {
                        //Hentet fra stackoverflow:
                        //https://stackoverflow.com/questions/70279262/navigating-with-compose-not-working-with-google-maps-on-android
                        GlobalScope.launch(Dispatchers.Main) {
                            navController.navigate(Navigasjon.Airport.name)
                            selectedAirPort = it
                        }
                    }
                )
            }
            composable(route = Navigasjon.Airport.name) {
                AirportScreen(ViewModel, selectedAirPort)
            }
        }
    }
}