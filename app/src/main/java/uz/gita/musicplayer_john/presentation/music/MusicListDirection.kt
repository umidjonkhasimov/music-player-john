package uz.gita.musicplayer_john.presentation.music

import uz.gita.musicplayer_john.presentation.play.PlayScreen
import uz.gita.musicplayer_john.presentation.playlist.PlayListScreen
import uz.gita.musicplayer_john.navigation.AppNavigator
import javax.inject.Inject

class MusicListDirectionImpl @Inject constructor(
    private val navigator: AppNavigator
) : MusicListContract.MusicListDirection {

    override suspend fun navigateToPlayScreen() {
        navigator.navigateTo(PlayScreen())
    }

    override suspend fun navigateToPlayListScreen() {
//        navigator.navigateTo(PlayListScreen())
    }
}