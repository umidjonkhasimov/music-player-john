package uz.gita.musicplayer_john.presentation.play

import android.content.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import cafe.adriel.voyager.hilt.getViewModel
import org.orbitmvi.orbit.compose.*
import uz.gita.musicplayer_john.utils.base.manageMusicService
import uz.gita.musicplayer_john.R
import uz.gita.musicplayer_john.data.model.ActionEnum
import uz.gita.musicplayer_john.data.model.CommandEnum
import uz.gita.musicplayer_john.data.model.CursorEnum
import uz.gita.musicplayer_john.data.model.MusicData
import uz.gita.musicplayer_john.navigation.AppScreen
import uz.gita.musicplayer_john.ui.theme.MyMusicPlayerTheme
import uz.gita.musicplayer_john.utils.MyEventBus
import uz.gita.musicplayer_john.utils.base.getMusicDataByPosition
import uz.gita.musicplayer_john.utils.base.getTime
import uz.gita.musicplayer_john.utils.logging
import uz.gita.musicplayer_john.utils.makeToast
import java.util.concurrent.TimeUnit

class PlayScreen : AppScreen() {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val viewModel: PlayContract.ViewModel = getViewModel<PlayViewModel>()
        val uiState = viewModel.collectAsState().value

        val musicData = MyEventBus.currentMusicData.collectAsState(
            initial = if (MyEventBus.currentCursorEnum == CursorEnum.SAVED)
                MyEventBus.roomCursor!!.getMusicDataByPosition(MyEventBus.roomPos)
            else MyEventBus.storageCursor!!.getMusicDataByPosition(MyEventBus.storagePos)
        ).value

        logging("MusicData: $musicData")

        viewModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                is PlayContract.SideEffect.UserAction -> {
                    when (sideEffect.actionEnum) {
                        ActionEnum.MANAGE -> {
                            manageMusicService(context, CommandEnum.MANAGE)
                        }

                        ActionEnum.NEXT -> {
                            manageMusicService(context, CommandEnum.NEXT)
                        }

                        ActionEnum.PREV -> {
                            manageMusicService(context, CommandEnum.PREV)
                        }

                        ActionEnum.UPDATE_SEEKBAR -> {
                            manageMusicService(context, CommandEnum.UPDATE_SEEKBAR)
                        }
                    }
                }
            }
        }

        MyMusicPlayerTheme {
            Surface(color = MaterialTheme.colorScheme.background) {
                Scaffold(
                    topBar = {
                        TopBar(musicData!!, uiState, viewModel::onEventDispatcher)
                    }
                ) {
                    PlayScreenContent(
                        musicData!!,
                        viewModel::onEventDispatcher,
                        modifier = Modifier.padding(it)
                    )
                }
            }
        }
    }
}

@Composable
fun TopBar(
    musicData: MusicData,
    uiState: PlayContract.UIState,
    onEventDispatcher: (PlayContract.Intent) -> Unit
) {

    val context = LocalContext.current
    onEventDispatcher(PlayContract.Intent.CheckMusic(musicData))

    when (uiState) {
        is PlayContract.UIState.CheckMusic -> {
            val isSaved = uiState.isSaved
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.outline)
                    .height(56.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    modifier = Modifier.padding(start = 12.dp),
                    onClick = { onEventDispatcher(PlayContract.Intent.Back) }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = null
                    )
                }

                IconButton(
                    modifier = Modifier.padding(end = 12.dp),
                    onClick = {
                        if (isSaved) {
                            onEventDispatcher.invoke(
                                PlayContract.Intent.DeleteMusic(musicData)
                            )
                            makeToast(context, "Removed from favorites")

                        } else {
                            onEventDispatcher.invoke(
                                PlayContract.Intent.SaveMusic(musicData)
                            )
                            makeToast(context, "Added to favorites")
                        }
                        onEventDispatcher.invoke(PlayContract.Intent.CheckMusic(musicData))
                    }
                ) {
                    Image(
                        painter = painterResource(id = if (isSaved) R.drawable.ic_favorite_filled else R.drawable.ic_favorite),
                        contentDescription = null
                    )
                }
            }
        }

        else -> {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayScreenContent(
    musicData: MusicData,
    eventListener: (PlayContract.Intent) -> Unit,
    modifier: Modifier
) {

    val seekBarState = MyEventBus.currentTimeFlow.collectAsState(initial = 0)
    var seekBarValue by remember { mutableStateOf(seekBarState.value) }
    val mucisIsPlaying = MyEventBus.isPlaying.collectAsState()

    val milliseconds = musicData.duration
    val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
    val minutes = (milliseconds / 1000 / 60) % 60
    val seconds = (milliseconds / 1000) % 60

    val duration = if (hours == 0L) "%02d:%02d".format(minutes, seconds) // 03:45
    else "%02d:%02d:%02d".format(hours, minutes, seconds) // 01:03:45

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(0.dp)
                .weight(1.7f)
        ) {
            Image(
                modifier = Modifier
                    .size(250.dp)
                    .padding(top = 10.dp, bottom = 12.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.ic_music),
                contentDescription = null
            )

            Text(
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                text = musicData.title ?: "Unknown",
                fontSize = 20.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            Text(
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                text = musicData.artist ?: "Unknown",
                fontSize = 18.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.dp)
                .weight(1f)
        ) {
            Slider(
                value = seekBarState.value.toFloat(),
                onValueChange = { newState ->
                    seekBarValue = newState.toInt()
                    eventListener.invoke(PlayContract.Intent.UserAction(ActionEnum.UPDATE_SEEKBAR))
                },
                onValueChangeFinished = {
                    MyEventBus.currentTime.value = seekBarValue
                    eventListener.invoke(PlayContract.Intent.UserAction(ActionEnum.UPDATE_SEEKBAR))
                },
                valueRange = 0f..musicData.duration.toFloat(),
                steps = 1000,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTickColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent,
                )
            )

            // 00:00
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    modifier = Modifier
                        .width(0.dp)
                        .weight(1f),
                    text = getTime(seekBarState.value / 1000)
                )
                // 03:45
                Text(
                    modifier = Modifier
                        .width(0.dp)
                        .weight(1f),
                    textAlign = TextAlign.End,
                    text = duration
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Image(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .clickable {
                            eventListener.invoke(PlayContract.Intent.UserAction(ActionEnum.PREV))
                            seekBarValue = 0
                            eventListener(PlayContract.Intent.CheckMusic(musicData))
                        }
                        .padding(8.dp),
                    painter = painterResource(id = R.drawable.ic_prev),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                    contentDescription = null
                )

                Image(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { eventListener.invoke(PlayContract.Intent.UserAction(ActionEnum.MANAGE)) }
                        .padding(16.dp),
                    painter = painterResource(
                        id = if (mucisIsPlaying.value) R.drawable.ic_pause
                        else R.drawable.ic_play
                    ),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                    contentDescription = null
                )

                Image(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .clickable {
                            eventListener.invoke(PlayContract.Intent.UserAction(ActionEnum.NEXT))
                            seekBarValue = 0
                            eventListener(PlayContract.Intent.CheckMusic(musicData))
                        }
                        .padding(8.dp),
                    painter = painterResource(id = R.drawable.ic_next),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                    contentDescription = null
                )
            }
        }
    }
}