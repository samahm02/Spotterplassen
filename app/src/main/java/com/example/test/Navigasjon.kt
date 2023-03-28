package com.example.test

import androidx.compose.foundation.layout.padding
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope

enum class Navigasjon() {
    Map,
    Airport
}

@Composable
fun Navigasjon(
    modifier: Modifier = Modifier,
    //viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    // Get current back stack entry
    //val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    /*
    val currentScreen = PalindromeScreen().valueOf(
        backStackEntry?.destination?.route ?: PalindromeScreen.Start.name
    )
    */
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    androidx.compose.material.Scaffold(
        scaffoldState = scaffoldState
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Navigasjon.Map.name,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(route = Navigasjon.Map.name) {
                MainScreen(
                    onAirportButtonClicked = {
                        //Hentet fra sstackoverflow:
                        //https://stackoverflow.com/questions/70279262/navigating-with-compose-not-working-with-google-maps-on-android
                        GlobalScope.launch(Dispatchers.Main) {
                            navController.navigate(Navigasjon.Airport.name)
                        }
                    }
                )
            }
            composable(route = Navigasjon.Airport.name) {
                AirportScreen()
            }
        }
    }
}