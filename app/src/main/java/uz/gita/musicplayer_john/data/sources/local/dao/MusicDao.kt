package uz.gita.musicplayer_john.data.sources.local.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.gita.musicplayer_john.data.sources.local.entity.MusicEntity

@Dao
interface MusicDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addMusic(musicEntity: MusicEntity)

    @Query("SELECT * FROM songs WHERE data = :data")
    fun checkMusicSaved(data: String): MusicEntity?

    @Delete
    fun deleteMusic(musicEntity: MusicEntity)

    @Query("Select * from songs")
    fun retrieveAllMusics(): Flow<List<MusicEntity>>

    @Query("Select * from songs")
    fun getSavedMusics(): Cursor
}