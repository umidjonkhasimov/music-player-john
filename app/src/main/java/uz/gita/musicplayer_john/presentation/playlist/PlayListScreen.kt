package uz.gita.musicplayer_john.presentation.playlist

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.orbitmvi.orbit.compose.*
import uz.gita.musicplayer_john.utils.base.manageMusicService
import uz.gita.musicplayer_john.MainActivity
import uz.gita.musicplayer_john.R
import uz.gita.musicplayer_john.data.model.CommandEnum
import uz.gita.musicplayer_john.data.model.CursorEnum
import uz.gita.musicplayer_john.navigation.AppScreen
import uz.gita.musicplayer_john.presentation.component.LoadingComponent
import uz.gita.musicplayer_john.presentation.component.MusicItemComponent
import uz.gita.musicplayer_john.ui.theme.MyMusicPlayerTheme
import uz.gita.musicplayer_john.utils.MyEventBus
import uz.gita.musicplayer_john.utils.base.getMusicDataByPosition

object PlayListScreen : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = "Favorites"
            val icon = rememberVectorPainter(Icons.Default.Favorite)

            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val activity = LocalContext.current as MainActivity
        val viewModel: PlayListContract.ViewModel = getViewModel<PlayListViewModel>()
        val uiState = viewModel.collectAsState()

        viewModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                PlayListContract.SideEffect.StartMusicService -> {
                    MyEventBus.currentCursorEnum = CursorEnum.SAVED
                    manageMusicService(activity, CommandEnum.PLAY)
                }
            }
        }

        LifecycleEffect(
            onStarted = { viewModel.onEventDispatcher(PlayListContract.Intent.CheckMusicExistence) }
        )

        MyMusicPlayerTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                Scaffold(
//                    topBar = { TopBar() }
                ) {
                    PlayListScreenContent(
                        uiState = uiState,
                        eventListener = viewModel::onEventDispatcher,
                        modifier = Modifier.padding(it)
                    )
                }
            }
        }
    }
}

@Composable
fun PlayListScreenContent(
    uiState: State<PlayListContract.UIState>,
    eventListener: (PlayListContract.Intent) -> Unit,
    modifier: Modifier = Modifier
) {

    Box(modifier = modifier.fillMaxSize()) {
        when (uiState.value) {

            PlayListContract.UIState.IsExistMusic -> {
                LoadingComponent()
                eventListener.invoke(PlayListContract.Intent.LoadMusics)
            }

            PlayListContract.UIState.Loading -> {
                eventListener.invoke(PlayListContract.Intent.CheckMusicExistence)
            }

            is PlayListContract.UIState.PreparedData -> {
                if (MyEventBus.roomCursor!!.count == 0) {
                    Image(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(180.dp),
                        painter = painterResource(id = R.drawable.ic_music),
                        contentDescription = null
                    )
                } else {
                    LazyColumn {
                        for (pos in 0 until MyEventBus.roomCursor!!.count) {
                            item {
                                MusicItemComponent(
                                    musicData = MyEventBus.roomCursor!!.getMusicDataByPosition(pos),
                                    onClick = {
                                        //MyEventBus.selectMusicPos = pos
                                        MyEventBus.roomPos = pos
                                        eventListener.invoke(PlayListContract.Intent.PlayMusic)
                                        eventListener.invoke(PlayListContract.Intent.OpenPlayScreen)
                                    },
                                    onLongClick = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
//            .background(color = Light_Red)
            .height(56.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            color = Color.Black,
            fontSize = 22.sp,
            text = "My playlist",
            fontWeight = FontWeight.Bold
        )
    }
}