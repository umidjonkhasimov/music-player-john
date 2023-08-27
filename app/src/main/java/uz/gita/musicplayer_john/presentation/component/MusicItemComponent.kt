package uz.gita.musicplayer_john.presentation.component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import uz.gita.musicplayer_john.R
import uz.gita.musicplayer_john.data.model.MusicData
import uz.gita.musicplayer_john.ui.theme.MyMusicPlayerTheme

@OptIn(ExperimentalUnitApi::class, ExperimentalFoundationApi::class)
@Composable
fun MusicItemComponent(
    musicData: MusicData,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {

    Surface(modifier = Modifier
        .wrapContentHeight()
        .fillMaxWidth()
        .padding(vertical = 8.dp, horizontal = 4.dp)
        .combinedClickable(
            onClick = { onClick.invoke() },
            onLongClick = { onLongClick.invoke() }
        )
    ) {
        Row(modifier = Modifier.wrapContentHeight()) {

            Image(
                painter = painterResource(id = R.drawable.ic_music),
                contentDescription = "MusicDisk",
                modifier = Modifier
                    .width(56.dp)
                    .height(56.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = musicData.title ?: "Unknown name",
                    fontSize = TextUnit(16f, TextUnitType.Sp),
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = musicData.artist ?: "Unknown artist",
                    color = Color(0XFF988E8E),
                    fontSize = TextUnit(14f, TextUnitType.Sp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MusicItemComponentPreview() {
    MyMusicPlayerTheme {
        val musicDate = MusicData(
            0,
            "My artist",
            "Test title",
            null,
            10000
        )

        MusicItemComponent(
            musicData = musicDate,
            onClick = {}, {}
        )
    }
}