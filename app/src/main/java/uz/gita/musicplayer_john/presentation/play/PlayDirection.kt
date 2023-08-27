package uz.gita.musicplayer_john.presentation.play

import uz.gita.musicplayer_john.navigation.AppNavigator
import javax.inject.Inject

class PlayDirection @Inject constructor(
    private val navigator: AppNavigator
) : PlayContract.Direction{

    override suspend fun back() {
        navigator.pop()
    }
}