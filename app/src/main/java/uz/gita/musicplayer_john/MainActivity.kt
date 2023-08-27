package uz.gita.musicplayer_john

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.lifecycle.lifecycleScope
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.gita.musicplayer_john.navigation.NavigatorHandler
import uz.gita.musicplayer_john.presentation.music.MusicListScreen
import uz.gita.musicplayer_john.ui.theme.MyMusicPlayerTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigatorHandler: NavigatorHandler

    @SuppressLint("FlowOperatorInvokedInComposition", "CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMusicPlayerTheme {
                Surface() {
                    Navigator(screen = MainScreen()) { navigator ->
                        navigatorHandler.navigatorState
                            .onEach { it.invoke(navigator) }
                            .launchIn(lifecycleScope)
                        CurrentScreen()
                    }
                }
            }
        }
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        manageMusicService(this, CommandEnum.CLOSE)
//    }
}