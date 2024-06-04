package com.example.chineseherbsdetection.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chineseherbsdetection.R
import com.example.chineseherbsdetection.ui.screens.GetImageScreen
import com.example.chineseherbsdetection.ui.screens.ImageViewModel
import com.example.chineseherbsdetection.ui.screens.ResultScreen

/**
 * enum values that represent the screens in the app
 */
enum class MainScreen(@StringRes val title: Int) {
    Start(title = R.string.title),
    Result(title = R.string.title)
}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppBar(
    currentScreen: MainScreen,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(currentScreen.title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = colorResource(R.color.theme)
        ),
        modifier = modifier
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChineseHerbsDetectionApp(
    viewModel: ImageViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = MainScreen.valueOf(
        backStackEntry?.destination?.route ?: MainScreen.Start.name
    )

    Scaffold (
        topBar = {
            MainAppBar(
                currentScreen = currentScreen
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = MainScreen.Start.name,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            composable(route = MainScreen.Start.name) {
                GetImageScreen(
                    onNextButtonClicked = { uri ->
                        // Set the URI in ViewModel
                        viewModel.setUri(uri)
                        navController.navigate(MainScreen.Result.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            composable(route = MainScreen.Result.name) {
                ResultScreen(
                    imageUri = uiState.imageUri,
                    onNextButtonClicked = {
                        navController.navigate(MainScreen.Start.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}