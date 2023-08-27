package uz.gita.musicplayer_john.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.gita.musicplayer_john.navigation.AppNavigator
import uz.gita.musicplayer_john.navigation.NavigationDispatcher
import uz.gita.musicplayer_john.navigation.NavigatorHandler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NavigatorModule {

    @[Binds Singleton]
    fun bindAppNavigator(impl: NavigationDispatcher): AppNavigator

    @[Binds Singleton]
    fun bindNavigatorHandler(impl: NavigationDispatcher): NavigatorHandler

}

