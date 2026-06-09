package com.example.pawspect.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface PawspectRoute : NavKey {
    @Serializable
    data object Home : PawspectRoute

    @Serializable
    data class Results(val imageUri: String) : PawspectRoute

    @Serializable
    data class Contribute(val imageUri: String? = null) : PawspectRoute
}
