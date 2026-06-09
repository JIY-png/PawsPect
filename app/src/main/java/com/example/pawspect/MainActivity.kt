package com.example.pawspect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.pawspect.ui.navigation.PawspectRoute
import com.example.pawspect.ui.screens.ContributeScreen
import com.example.pawspect.ui.screens.HomeScreen
import com.example.pawspect.ui.screens.ResultsScreen
import com.example.pawspect.ui.theme.PawspectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PawspectTheme {
                PawspectApp()
            }
        }
    }
}

@Composable
fun PawspectApp() {
    val backStack = rememberNavBackStack(PawspectRoute.Home)

    NavDisplay(
        backStack = backStack,
        modifier = Modifier.fillMaxSize(),
        onBack = {
            if (backStack.size > 1) {
                backStack.removeAt(backStack.size - 1)
            }
        },
        entryProvider = entryProvider {
            entry<PawspectRoute.Home> {
                HomeScreen(
                    onNavigateToResults = { uri ->
                        backStack.add(PawspectRoute.Results(uri))
                    },
                    onNavigateToContribute = {
                        backStack.add(PawspectRoute.Contribute())
                    },
                )
            }
            entry<PawspectRoute.Results> { route ->
                ResultsScreen(
                    imageUri = route.imageUri,
                    onNavigateBack = {
                        if (backStack.size > 1) {
                            backStack.removeAt(backStack.size - 1)
                        }
                    },
                    onNavigateToContribute = { uri ->
                        backStack.add(PawspectRoute.Contribute(uri))
                    },
                )
            }
            entry<PawspectRoute.Contribute> { route ->
                ContributeScreen(
                    initialImageUri = route.imageUri,
                    onNavigateBack = {
                        if (backStack.size > 1) {
                            backStack.removeAt(backStack.size - 1)
                        }
                    },
                )
            }
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        )
    )
}
