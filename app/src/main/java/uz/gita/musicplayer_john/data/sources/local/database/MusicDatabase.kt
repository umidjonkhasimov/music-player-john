package uz.gita.musicplayer_john.data.sources.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.gita.musicplayer_john.data.sources.local.dao.MusicDao
import uz.gita.musicplayer_john.data.sources.local.entity.MusicEntity

@Database(entities = [MusicEntity::class], version = 1, exportSchema = false)
abstract class MusicDatabase : RoomDatabase() {

    abstract fun getMusicDao(): MusicDao
}