package uz.gita.musicplayer_john.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uz.gita.musicplayer_john.presentation.music.MusicListContract
import uz.gita.musicplayer_john.presentation.music.MusicListDirectionImpl
import uz.gita.musicplayer_john.presentation.play.PlayContract
import uz.gita.musicplayer_john.presentation.play.PlayDirection
import uz.gita.musicplayer_john.presentation.playlist.PlayListContract
import uz.gita.musicplayer_john.presentation.playlist.PlayListDirection

@Module
@InstallIn(ViewModelComponent::class)
interface DirectionModule {

    @Binds
    fun bindMusicListDirection(impl: MusicListDirectionImpl): MusicListContract.MusicListDirection

    @Binds
    fun bindPlayDirection(impl: PlayDirection): PlayContract.Direction

    @Binds
    fun bindPlayListDirection(impl: PlayListDirection): PlayListContract.Direction
}