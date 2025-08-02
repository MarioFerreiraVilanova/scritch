package com.scritch.app.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.scritch.app.jam.JamScreen
import com.scritch.app.navigation.HomeScreen
import com.scritch.app.solomode.SoloScreen
import org.jetbrains.compose.resources.stringResource
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.solo_mode
import scritch.composeapp.generated.resources.weekly_jam

@Composable
fun HomeScreen(
    onGoToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    var selectedDestination by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                NavigationBarItem(
                    selected = selectedDestination == 0,
                    onClick = {
                        navController.navigate(route = HomeScreen.SoloMode)
                        selectedDestination = 0
                    },
                    icon = {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = stringResource(Res.string.solo_mode)
                        )
                    },
                    label = { Text(stringResource(Res.string.solo_mode)) }
                )
                NavigationBarItem(
                    selected = selectedDestination == 1,
                    onClick = {
                        navController.navigate(route = HomeScreen.WeeklyJam)
                        selectedDestination = 1
                    },
                    icon = {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = stringResource(Res.string.weekly_jam)
                        )
                    },
                    label = { Text(stringResource(Res.string.weekly_jam)) }
                )
            }
        }
    ) { contentPadding ->
        HomeNavGraph(
            navController = navController,
            onGoToSettings = onGoToSettings,
            modifier = Modifier.padding(contentPadding)
        )
    }
}

@Composable
private fun HomeNavGraph(
    navController: NavHostController,
    onGoToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeScreen.SoloMode,
        modifier = modifier,
    ) {
        composable<HomeScreen.SoloMode> {
            SoloScreen(
                onGoToSettings = onGoToSettings,
            )
        }
        composable<HomeScreen.WeeklyJam> {
            JamScreen()
        }
    }
}