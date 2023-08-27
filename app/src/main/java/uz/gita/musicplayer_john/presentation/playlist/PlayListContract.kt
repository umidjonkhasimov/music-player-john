package uz.gita.musicplayer_john.presentation.playlist

import org.orbitmvi.orbit.ContainerHost
import uz.gita.musicplayer_john.data.model.MusicData

interface PlayListContract {

    interface ViewModel : ContainerHost<UIState, SideEffect> {
        fun onEventDispatcher(intent: Intent)
    }

    sealed interface UIState {
        object Loading : UIState
        object IsExistMusic : UIState
        object PreparedData : UIState
    }

    sealed interface SideEffect {
        object StartMusicService : SideEffect
    }

    sealed interface Intent {
        object CheckMusicExistence : Intent
        object LoadMusics : Intent
        object PlayMusic : Intent
        object OpenPlayScreen : Intent
        data class DeleteMusic(val musicData: MusicData) : Intent
    }

    interface Direction {
        suspend fun navigateToPlayScreen()
    }
}