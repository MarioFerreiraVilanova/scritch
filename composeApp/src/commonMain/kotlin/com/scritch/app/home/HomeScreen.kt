package com.scritch.app.home

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.scritch.app.jam.ui.JamScreen
import com.scritch.app.jam.ui.JamArchivesScreen
import com.scritch.app.navigation.HomeScreen
import com.scritch.app.solomode.SoloScreen
import com.scritch.app.theme.NavigationBarItemColors
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.solo_mode
import scritch.composeapp.generated.resources.users
import scritch.composeapp.generated.resources.weekly_jam
import scritch.composeapp.generated.resources.zap

@Composable
fun HomeScreen(
    onGoToSettings: () -> Unit,
    onOpenCamera: () -> Unit,
    onImagePathReceived: () -> Unit,
    modifier: Modifier = Modifier,
    chosenImagePath: String? = null,
) {
    val rootNavController = rememberNavController()
    var selectedDestination by rememberSaveable { mutableIntStateOf(0) }
    val navigationBarItemColors = NavigationBarItemColors()

    fun onBottomTabClick(
        index: Int,
    ) {
        rootNavController.navigate(
            route = when (index) {
                0 -> HomeScreen.SoloMode
                1 -> HomeScreen.WeeklyJam
                else -> throw IllegalArgumentException("Invalid index: $index")
            },
        ) {
            popUpTo(HomeScreen.SoloMode) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
        selectedDestination = index
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(
                windowInsets = NavigationBarDefaults.windowInsets,
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ) {
                NavigationBarItem(
                    selected = selectedDestination == 0,
                    onClick = {
                        onBottomTabClick(0)
                    },
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.zap),
                            contentDescription = stringResource(Res.string.solo_mode)
                        )
                    },
                    label = { Text(stringResource(Res.string.solo_mode)) },
                    colors = navigationBarItemColors,
                )
                NavigationBarItem(
                    selected = selectedDestination == 1,
                    onClick = {
                        onBottomTabClick(1)
                    },
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.users),
                            contentDescription = stringResource(Res.string.weekly_jam),
                        )
                    },
                    label = { Text(stringResource(Res.string.weekly_jam)) },
                    colors = navigationBarItemColors,
                )
            }
        }
    ) { contentPadding ->
        HomeNavGraph(
            navController = rootNavController,
            chosenImagePath = chosenImagePath,
            onGoToSettings = onGoToSettings,
            onOpenCamera = onOpenCamera,
            onImagePathReceived = onImagePathReceived,
            modifier = Modifier
                .consumeWindowInsets(contentPadding)
                .padding(contentPadding)
        )
    }
}

@Composable
private fun HomeNavGraph(
    navController: NavHostController,
    chosenImagePath: String?,
    onGoToSettings: () -> Unit,
    onOpenCamera: () -> Unit,
    onImagePathReceived: () -> Unit,
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
            JamScreen(
                chosenImagePath = chosenImagePath,
                onOpenCamera = onOpenCamera,
                onImagePathReceived = onImagePathReceived,
                onNavigateToArchives = {
                    navController.navigate(HomeScreen.JamArchives)
                },
            )
        }

        composable<HomeScreen.JamArchives> {
            JamArchivesScreen(
                onBackPress = {
                    navController.popBackStack()
                },
                onJamClick = { jamId ->
                    navController.navigate(HomeScreen.ViewPastJam(jamId))
                }
            )
        }

        composable<HomeScreen.ViewPastJam> {
            JamScreen(
                chosenImagePath = null,
                onOpenCamera = { },
                onImagePathReceived = { },
                onNavigateToArchives = { },
                // The jamId will be picked up from SavedStateHandle by the ViewModel
            )
        }
    }
}