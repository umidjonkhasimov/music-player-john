package uz.gita.musicplayer_john.navigation

import cafe.adriel.voyager.androidx.AndroidScreen

typealias AppScreen = AndroidScreen

interface AppNavigator {
    suspend fun navigateTo(screen: AppScreen)
    suspend fun replace(screen: AppScreen)
    suspend fun pop()
}