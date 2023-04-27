package com.example.test

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
import com.example.test.data.loadAirports
import com.example.test.data.PlaneSpottingLocation
import com.example.test.data.loadPlaneSpottingLocation
import com.example.test.screen.AirportScreen
import com.example.test.screen.MainScreen
import com.example.test.screen.SpottingScreen
import com.example.test.viewModel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope

enum class Navigasjon {
    //Klasse for navigasjonsdestinasjoner/skjermer
    Map,
    Airport,
    SpottingLocation
}

@Composable
fun Navigasjon(
    ViewModel: ViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    //Scaffold for navigasjon.
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    var selectedSpottingLocation: String = ""
    var selectedPlaneSpottingLocation: PlaneSpottingLocation = PlaneSpottingLocation("Plattingen ved Dakota Norge", 59.186815, 10.254216, "Plattingen ved Dakota Norge: Sving til venstre inn på Hangarvegen like før\n" +
            "flyplassområdet/parkeringsplassene. Følg Hangarvegen til kryss nr. 2, sving til høyre inn på\n" +
            "Vatakervegen mot Lufthavnvakta. Her er det også skiltet mot Dakota Norge. Gå til høyre langs gjerdet\n" +
            "ved Dakota Norge. Her finner du 3-\u00AD‐4 gamle fly og en platting. Fin utsikt over flyplassområdet, motlys om\n" +
            "morgenen, flott om ettermiddagen. Fin ved landing fra nord, avgang mot sør, men også fine bilder hvis\n" +
            "trafikken går andre veien. Du står litt nord for midten av banen. Gratis parkering.", "ENTO")
    val allPlaneSpottingLocation: List<PlaneSpottingLocation> = loadPlaneSpottingLocation()

    var selectedAirPort: String = ""
    var selectedAirPortData: AirportData = AirportData(644,"Oslo Lufthavn","Oslo","Norway","OSL","ENGM",60.197166,11.099431,681,1,"E","Europe/Oslo","airport","OurAirports")
    val allAirportData: List<AirportData> = loadAirports()

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
                            ViewModel.changeairPortICAO(selectedAirPort)
                            ViewModel.loadWarnings()
                            for (airportData in allAirportData) {
                                if (airportData.ICAO == it) {
                                    selectedAirPortData = airportData
                                }
                            }
                        }
                    },
                    onSpottingButtonClicked = {
                        GlobalScope.launch(Dispatchers.Main) {
                            navController.navigate(Navigasjon.SpottingLocation.name)
                            selectedSpottingLocation = it
                            //ViewModel.changeSelectedSpottingLocation(selectedSpottingLocation)
                            for (spottingLocation in allPlaneSpottingLocation) {
                                if (spottingLocation.Name == it) {
                                    selectedPlaneSpottingLocation = spottingLocation
                                }
                            }
                        }

                    }
                )
                ViewModel.lastInnNyeFly()
                ViewModel.loadWarnings()
            }

            composable(route = Navigasjon.Airport.name) {
                AirportScreen(ViewModel, selectedAirPort, selectedAirPortData)
            }

            composable(route = Navigasjon.SpottingLocation.name) {
                SpottingScreen(spottingLocation = selectedPlaneSpottingLocation)
            }
        }
    }
}