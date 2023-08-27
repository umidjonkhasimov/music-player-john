package uz.gita.musicplayer_john.navigation

import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.flow.SharedFlow

typealias NavigationArgs = Navigator.() -> Unit

interface NavigatorHandler {
    val navigatorState: SharedFlow<NavigationArgs>
}

