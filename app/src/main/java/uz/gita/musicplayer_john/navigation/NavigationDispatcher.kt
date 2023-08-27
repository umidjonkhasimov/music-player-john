package uz.gita.musicplayer_john.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationDispatcher @Inject constructor() : AppNavigator, NavigatorHandler {

    override val navigatorState = MutableSharedFlow<NavigationArgs>()

    private suspend fun navigate(args: NavigationArgs) {
        navigatorState.emit(args)
    }

    override suspend fun pop() = navigate { pop() }
    override suspend fun navigateTo(screen: AppScreen) = navigate { push(screen) }
    override suspend fun replace(screen: AppScreen) = navigate { replace(screen) }
}