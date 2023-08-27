package uz.gita.musicplayer_john.presentation.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import uz.gita.musicplayer_john.domain.repository.AppRepository
import uz.gita.musicplayer_john.presentation.playlist.PlayListContract
import uz.gita.musicplayer_john.presentation.playlist.PlayListDirection
import uz.gita.musicplayer_john.utils.MyEventBus
import uz.gita.musicplayer_john.utils.base.checkMusics
import javax.inject.Inject

@HiltViewModel
class PlayListViewModel @Inject constructor(
    private val direction: PlayListDirection,
    private val appRepository: AppRepository
) : ViewModel(), PlayListContract.ViewModel {

    override val container =
        container<PlayListContract.UIState, PlayListContract.SideEffect>(PlayListContract.UIState.Loading)


    override fun onEventDispatcher(intent: PlayListContract.Intent) {
        when (intent) {
            PlayListContract.Intent.CheckMusicExistence -> {
                viewModelScope.launch {
                    MyEventBus.roomCursor = appRepository.getSavedMusics()

                    checkMusics(this@PlayListViewModel::onEventDispatcher)
                    intent { reduce { PlayListContract.UIState.IsExistMusic } }
                }
            }

            PlayListContract.Intent.LoadMusics -> {
                intent { reduce { PlayListContract.UIState.PreparedData } }
            }

            is PlayListContract.Intent.DeleteMusic -> {
                appRepository.deleteMusic(intent.musicData)
            }

            PlayListContract.Intent.PlayMusic -> {
                intent { postSideEffect(PlayListContract.SideEffect.StartMusicService) }
            }

            PlayListContract.Intent.OpenPlayScreen -> {
                viewModelScope.launch { direction.navigateToPlayScreen() }
            }
        }
    }
}