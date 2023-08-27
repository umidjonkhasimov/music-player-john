package uz.gita.musicplayer_john.utils.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import android.widget.RemoteViews
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uz.gita.musicplayer_john.MainActivity
import uz.gita.musicplayer_john.R
import uz.gita.musicplayer_john.data.model.CommandEnum
import uz.gita.musicplayer_john.data.model.CursorEnum
import uz.gita.musicplayer_john.data.model.MusicData
import uz.gita.musicplayer_john.ui.theme.md_theme_light_background
import uz.gita.musicplayer_john.ui.theme.md_theme_light_outline
import uz.gita.musicplayer_john.utils.MyEventBus
import uz.gita.musicplayer_john.utils.base.getMusicDataByPosition
import uz.gita.musicplayer_john.utils.broadcast.CallReceiver
import uz.gita.musicplayer_john.utils.logging

class MusicService : Service() {

    companion object {
        const val CHANNEL_ID = "My music player"
        const val CHANNEL_NAME = "Music player"
    }

    private val callReceiver = CallReceiver()

    private var _musicPlayer: MediaPlayer? = null
    private val musicPlayer get() = _musicPlayer!!
    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
    private var job: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null


    override fun onCreate() {
        super.onCreate()
        createChannel()
        val intentFilter = IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        registerReceiver(callReceiver, intentFilter)
    }

    private fun createChannel() {
        val importance = NotificationManager.IMPORTANCE_LOW
        val mChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    private fun createNotification(musicData: MusicData) {

        val myIntent = Intent(this, MainActivity::class.java).apply {
            Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val myPendingIntent =
            PendingIntent.getActivity(this, 1, myIntent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_music)
            .setCustomContentView(createRemoteView(musicData))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentIntent(myPendingIntent)
            .setColor(md_theme_light_outline.toArgb())
            .setColorized(true)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
        startForeground(1, notificationBuilder.build())
    }

    private fun createRemoteView(musicData: MusicData): RemoteViews {
        val view = RemoteViews(this.packageName, R.layout.remote_view)
        view.setTextViewText(R.id.textMusicName, musicData.title)
        view.setTextViewText(R.id.textArtistName, musicData.artist)

        if (_musicPlayer != null && !musicPlayer.isPlaying) {
            view.setImageViewResource(R.id.buttonManage, R.drawable.ic_play)
        } else {
            view.setImageViewResource(R.id.buttonManage, R.drawable.ic_pause)
        }

        view.setOnClickPendingIntent(R.id.buttonPrev, createPendingIntent(CommandEnum.PREV))
        view.setOnClickPendingIntent(R.id.buttonManage, createPendingIntent(CommandEnum.MANAGE))
        view.setOnClickPendingIntent(R.id.buttonNext, createPendingIntent(CommandEnum.NEXT))
        view.setOnClickPendingIntent(R.id.buttonCancel, createPendingIntent(CommandEnum.CLOSE))
        return view
    }

    private fun createPendingIntent(commandEnum: CommandEnum): PendingIntent {
        val intent = Intent(this, MusicService::class.java)
        intent.putExtra("COMMAND", commandEnum)
        return PendingIntent.getService(
            this,
            commandEnum.amount,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (MyEventBus.currentCursorEnum == CursorEnum.STORAGE &&
            (MyEventBus.storageCursor == null || MyEventBus.storagePos == -1)
        ) return START_NOT_STICKY
        else if (MyEventBus.currentCursorEnum == CursorEnum.SAVED &&
            (MyEventBus.roomCursor == null || MyEventBus.roomPos == -1)
        ) return START_NOT_STICKY

        val command = intent?.extras?.getSerializable("COMMAND") as CommandEnum
        doneCommand(command)
        if (command.name != CommandEnum.CLOSE.name && MyEventBus.currentCursorEnum == CursorEnum.SAVED) {
            createNotification(MyEventBus.roomCursor!!.getMusicDataByPosition(MyEventBus.roomPos))
        } else if (command.name != CommandEnum.CLOSE.name && MyEventBus.currentCursorEnum == CursorEnum.STORAGE) {
            createNotification(MyEventBus.storageCursor!!.getMusicDataByPosition(MyEventBus.storagePos))
        }
        return START_NOT_STICKY
    }

    private fun doneCommand(commandEnum: CommandEnum) {
        when (commandEnum) {
            CommandEnum.MANAGE -> {
                if (musicPlayer.isPlaying) doneCommand(CommandEnum.PAUSE)
                else doneCommand(CommandEnum.CONTINUE)
            }

            CommandEnum.CONTINUE -> {
                job = moveProgress().onEach { MyEventBus.currentTimeFlow.emit(it) }.launchIn(scope)
                musicPlayer.seekTo(MyEventBus.currentTime.value)
                scope.launch { MyEventBus.isPlaying.emit(true) }
                musicPlayer.start()
            }

            CommandEnum.UPDATE_SEEKBAR -> {
                if (musicPlayer.isPlaying) {
                    job?.cancel()
                    musicPlayer.seekTo(MyEventBus.currentTime.value)
                    job = moveProgress().onEach { MyEventBus.currentTimeFlow.emit(it) }
                        .launchIn(scope)
                } else {
                    musicPlayer.seekTo(MyEventBus.currentTime.value)
                    job = moveProgress().onEach { MyEventBus.currentTimeFlow.emit(it) }
                        .launchIn(scope)
                    job?.cancel()
                }
            }

            CommandEnum.PREV -> {
                if (MyEventBus.currentCursorEnum == CursorEnum.SAVED) {
                    if (MyEventBus.roomPos - 1 == -1) {
                        MyEventBus.roomPos = MyEventBus.roomCursor!!.count - 1
                    } else if (MyEventBus.roomPos == MyEventBus.roomCursor!!.count) {
                        MyEventBus.currentCursorEnum = CursorEnum.STORAGE
                        MyEventBus.storagePos = 0
                    } else {
                        --MyEventBus.roomPos
                    }
                } else {
                    if (MyEventBus.storagePos - 1 == -1) {
                        MyEventBus.storagePos = MyEventBus.storageCursor!!.count - 1
                    } else {
                        --MyEventBus.storagePos
                    }
                }
                doneCommand(CommandEnum.PLAY)
            }

            CommandEnum.NEXT -> {
                if (MyEventBus.currentCursorEnum == CursorEnum.SAVED) {
                    if (MyEventBus.roomPos + 1 == MyEventBus.roomCursor!!.count) {
                        MyEventBus.roomPos = 0
                    } else if (MyEventBus.roomPos == MyEventBus.roomCursor!!.count) {
                        MyEventBus.currentCursorEnum = CursorEnum.STORAGE
                        MyEventBus.storagePos = 0
                    } else {
                        ++MyEventBus.roomPos
                    }
                } else {
                    if (MyEventBus.storagePos + 1 == MyEventBus.storageCursor!!.count) {
                        MyEventBus.storagePos = 0
                    } else {
                        ++MyEventBus.storagePos
                    }
                }
                doneCommand(CommandEnum.PLAY)
            }

            CommandEnum.PLAY -> {
                val data =
                    if (MyEventBus.currentCursorEnum == CursorEnum.SAVED) MyEventBus.roomCursor!!.getMusicDataByPosition(
                        MyEventBus.roomPos
                    ) else MyEventBus.storageCursor!!.getMusicDataByPosition(MyEventBus.storagePos)

                scope.launch { MyEventBus.currentMusicData.emit(data) }

                MyEventBus.currentTime.value = 0
                MyEventBus.totalTime = data.duration.toInt()
                _musicPlayer?.stop()
                _musicPlayer = MediaPlayer.create(this, Uri.parse(data.data))
                musicPlayer.seekTo(MyEventBus.currentTime.value)
                musicPlayer.setOnCompletionListener { doneCommand(CommandEnum.NEXT) }

                job?.cancel()
                job = moveProgress().onEach { MyEventBus.currentTimeFlow.emit(it) }.launchIn(scope)
                scope.launch { MyEventBus.isPlaying.emit(true) }

                musicPlayer.start()
            }

            CommandEnum.PAUSE -> {
                musicPlayer.pause()
                MyEventBus.currentTime.value = MyEventBus.currentTimeFlow.value
                musicPlayer.seekTo(MyEventBus.currentTime.value)
                job?.cancel()
                scope.launch { MyEventBus.isPlaying.emit(false) }
            }

            CommandEnum.CLOSE -> {
                musicPlayer.pause()
                job?.cancel()
                scope.launch { MyEventBus.isPlaying.emit(false) }
                ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
            }
        }
    }

    private fun moveProgress(): Flow<Int> = flow {
        for (i in MyEventBus.currentTime.value until MyEventBus.totalTime step 1000) {
            emit(i)
            delay(1000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(callReceiver)
    }
}