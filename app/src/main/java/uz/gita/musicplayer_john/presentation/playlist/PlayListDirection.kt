package uz.gita.musicplayer_john.presentation.playlist

import uz.gita.musicplayer_john.navigation.AppNavigator
import uz.gita.musicplayer_john.presentation.play.PlayScreen
import javax.inject.Inject

class PlayListDirection @Inject constructor(
    private val appNavigator: AppNavigator
) : PlayListContract.Direction {

    override suspend fun navigateToPlayScreen() {
        appNavigator.navigateTo(PlayScreen())
    }
}