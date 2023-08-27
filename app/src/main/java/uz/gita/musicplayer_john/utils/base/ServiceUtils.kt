package uz.gita.musicplayer_john.utils.base

import android.content.Context
import android.content.Intent
import android.os.Build
import uz.gita.musicplayer_john.data.model.CommandEnum
import uz.gita.musicplayer_john.utils.service.MusicService

fun manageMusicService(context: Context, commandEnum: CommandEnum) {
    val intent = Intent(context, MusicService::class.java)
    intent.putExtra("COMMAND", commandEnum)
    context.startForegroundService(intent)
}