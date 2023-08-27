package uz.gita.musicplayer_john.presentation.music

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.orbitmvi.orbit.compose.*
import uz.gita.musicplayer_john.presentation.component.MusicItemComponent
import uz.gita.musicplayer_john.utils.base.checkPermissions
import uz.gita.musicplayer_john.utils.base.manageMusicService
import uz.gita.musicplayer_john.MainActivity
import uz.gita.musicplayer_john.R
import uz.gita.musicplayer_john.data.model.CommandEnum
import uz.gita.musicplayer_john.data.model.CursorEnum
import uz.gita.musicplayer_john.navigation.AppScreen
import uz.gita.musicplayer_john.presentation.component.CurrentMusicItemComponent
import uz.gita.musicplayer_john.presentation.component.LoadingComponent
import uz.gita.musicplayer_john.ui.theme.MyMusicPlayerTheme
import uz.gita.musicplayer_john.utils.MyEventBus
import uz.gita.musicplayer_john.utils.base.getMusicDataByPosition
import uz.gita.musicplayer_john.utils.logging

object MusicListScreen : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Music List"
            val icon = rememberVectorPainter(Icons.Default.List)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val activity = LocalContext.current as MainActivity
        val viewModel: MusicListContract.ViewModel = getViewModel<MusicListViewModel>()
        val uiState = viewModel.collectAsState()

        logging("MusicList")

        viewModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                MusicListContract.SideEffect.StartMusicService -> {
                    MyEventBus.currentCursorEnum = CursorEnum.STORAGE

                    manageMusicService(activity, CommandEnum.PLAY)
                }

                MusicListContract.SideEffect.OpenPermissionDialog -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        activity.checkPermissions(
                            arrayListOf(
                                Manifest.permission.POST_NOTIFICATIONS,
                                Manifest.permission.READ_MEDIA_AUDIO,
                                Manifest.permission.READ_PHONE_STATE
                            )
                        ) {
                            viewModel.onEventDispatcher(MusicListContract.Intent.LoadMusics(activity))
                        }
                    } else {
                        activity.checkPermissions(
                            arrayListOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.READ_PHONE_STATE
                            )
                        ) {
                            viewModel.onEventDispatcher(MusicListContract.Intent.LoadMusics(activity))
                        }
                    }
                }
            }
        }

        LifecycleEffect(
            onStarted = { viewModel.onEventDispatcher(MusicListContract.Intent.LoadMusics(activity)) }
        )

        MyMusicPlayerTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                Scaffold(
//                    topBar = { TopBar(viewModel::onEventDispatcher) }
                ) {
                    MusicListContent(
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
 fun MusicListContent(
    uiState: State<MusicListContract.UIState>,
    eventListener: (MusicListContract.Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current as MainActivity

    Surface(modifier = modifier.fillMaxSize()) {
        Box {
            when (uiState.value) {
                MusicListContract.UIState.Loading -> {
                    LoadingComponent()
                    eventListener.invoke(MusicListContract.Intent.RequestPermission)
                }

                MusicListContract.UIState.PreparedData -> {
                    LazyColumn {
                        for (pos in 0 until MyEventBus.storageCursor!!.count) {
                            item {
                                MusicItemComponent(
                                    musicData = MyEventBus.storageCursor!!.getMusicDataByPosition(pos),
                                    onClick = {
                                        MyEventBus.storagePos = pos

                                        eventListener.invoke(MusicListContract.Intent.PlayMusic)
                                        eventListener.invoke(MusicListContract.Intent.OpenPlayScreen)
                                    },
                                    onLongClick = {}
                                )
                            }
                        }
                    }

                    if (MyEventBus.storagePos != -1 ||
                        MyEventBus.storagePos >= MyEventBus.storageCursor!!.count
                    ) {
                        val data = MyEventBus.currentMusicData.collectAsState()
                        CurrentMusicItemComponent(
                            modifier = Modifier.align(Alignment.BottomCenter),
                            musicData = data.value!!,
                            onClick = { eventListener.invoke(MusicListContract.Intent.OpenPlayScreen) },
                            onClickManage = {
                                MyEventBus.currentCursorEnum = CursorEnum.STORAGE
                                manageMusicService(context, CommandEnum.MANAGE)
                            })
                    }
                }
            }
        }
    }
}

@Composable
fun TopBar(eventListener: (MusicListContract.Intent) -> Unit) {
    Row(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(bottomEnd = 12.dp, bottomStart = 12.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.outline)
            .height(56.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .width(0.dp)
                .padding(start = 16.dp)
                .weight(1f),
            fontSize = 22.sp,
            color = Color.Black,
            text = "Music List"
        )

        Image(
            modifier = Modifier
                .padding(end = 16.dp)
                .clickable { eventListener.invoke(MusicListContract.Intent.OpenPlayListScreen) },
            painter = painterResource(id = R.drawable.ic_favorite),
            contentDescription = null
        )
    }
}