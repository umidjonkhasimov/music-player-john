package uz.gita.musicplayer_john.presentation.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import uz.gita.musicplayer_john.R
import uz.gita.musicplayer_john.data.model.MusicData
import uz.gita.musicplayer_john.ui.theme.MyMusicPlayerTheme
import uz.gita.musicplayer_john.utils.MyEventBus

@Composable
fun CurrentMusicItemComponent(
    modifier: Modifier = Modifier,
    musicData: MusicData,
    onClick: () -> Unit,
    onClickManage: () -> Unit
) {
    val isPlaying = MyEventBus.isPlaying.collectAsState()

    val scrollState = rememberScrollState()
    var shouldAnimated by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = shouldAnimated) {
        scrollState.animateScrollTo(
            scrollState.maxValue,
            animationSpec = tween(3000, 2000, easing = CubicBezierEasing(0f, 0f, 0f, 0f))
        )
        scrollState.scrollTo(0)
        shouldAnimated = !shouldAnimated
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = modifier
            .wrapContentHeight()
            .clip(shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .fillMaxWidth()
            .clickable { onClick.invoke() }
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .padding(4.dp)
        )
        {
            Image(
                painter = painterResource(id = R.drawable.ic_music),
                contentDescription = "MusicDisk",
                modifier = Modifier
                    .padding(8.dp)
                    .width(45.dp)
                    .height(45.dp)
                    .align(Alignment.CenterVertically)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = musicData.title ?: "Unknown name",
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    modifier = Modifier.horizontalScroll(scrollState, false)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = musicData.artist ?: "Unknown artist",
                    color = (MaterialTheme.colorScheme.onBackground).copy(alpha = .5f),
                    fontSize = TextUnit(14f, TextUnitType.Sp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }

            Image(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { onClickManage.invoke() }
                    .padding(12.dp),
                painter = painterResource(
                    id = if (isPlaying.value) R.drawable.ic_pause
                    else R.drawable.ic_play
                ),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                contentDescription = null
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun CurrentMusicItemComponentPreview() {
    MyMusicPlayerTheme {
        Surface {
            val musicDate = MusicData(
                0,
                "My artist",
                "Test title",
                null,
                10000
            )

            CurrentMusicItemComponent(
                musicData = musicDate,
                onClick = {},
                onClickManage = {}
            )
        }
    }
}



