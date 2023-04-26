package com.example.test

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.test.data.AirportData
import com.example.test.screen.AirportScreen
import com.example.test.screen.MainScreen
import com.example.test.viewModel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope

enum class Navigasjon {
    //Klasse for navigasjonsdestinasjoner/skjermer
    Map,
    Airport
}

@Composable
fun Navigasjon(
    ViewModel: ViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    //Scaffold for navigasjon.
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    var selectedAirPort: String = ""
    var selectedAirPortData: AirportData = AirportData(644,"Oslo Lufthavn","Oslo","Norway","OSL","ENGM",60.197166,11.099431,681,1,"E","Europe/Oslo","airport","OurAirports")

    //Scaffold med ønsket parameter
    androidx.compose.material.Scaffold(
        scaffoldState = scaffoldState
    ) { innerPadding ->
        //NavHost som holder oversikt over composables
        NavHost(
            navController = navController,
            startDestination = Navigasjon.Map.name,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(route = Navigasjon.Map.name) {
                MainScreen(
                    state = ViewModel.state.value,
                    ViewModel = ViewModel,
                    onAirportButtonClicked = {
                        //Hentet fra stackoverflow:
                        //https://stackoverflow.com/questions/70279262/navigating-with-compose-not-working-with-google-maps-on-android
                        GlobalScope.launch(Dispatchers.Main) {
                            navController.navigate(Navigasjon.Airport.name)
                            selectedAirPort = it
                            Log.d("User", "it: $it")
                            //Hvordan endre airportData? Hente fra load airports?
                            ViewModel.changeairPortICAO(selectedAirPort)
                            ViewModel.loadWarnings()
                        }
                    }

                )
                ViewModel.lastInnNyeFly()
            }

            composable(route = Navigasjon.Airport.name) {
                AirportScreen(ViewModel, selectedAirPort, selectedAirPortData)
            }
        }
    }
}