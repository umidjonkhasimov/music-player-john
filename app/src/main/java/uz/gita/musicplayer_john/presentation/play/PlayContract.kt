package uz.gita.musicplayer_john.presentation.play

import org.orbitmvi.orbit.ContainerHost
import uz.gita.musicplayer_john.data.model.ActionEnum
import uz.gita.musicplayer_john.data.model.MusicData

sealed interface PlayContract {

    interface ViewModel : ContainerHost<UIState, SideEffect> {
        fun onEventDispatcher(intent: Intent)
    }

    sealed interface UIState {
        object InitState : UIState
        data class CheckMusic(val isSaved: Boolean) : UIState
    }

    sealed interface SideEffect {
        data class UserAction(val actionEnum: ActionEnum) : SideEffect
    }

    sealed interface Intent {
        data class UserAction(val actionEnum: ActionEnum) : Intent
        data class SaveMusic(val musicData: MusicData) : Intent
        data class DeleteMusic(val musicData: MusicData) : Intent
        data class CheckMusic(val musicData: MusicData) : Intent
        object Back : Intent
    }

    interface Direction {
        suspend fun back()
    }
}