package uz.gita.musicplayer_john.data.sources.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import uz.gita.musicplayer_john.data.model.MusicData

@Entity(tableName = "songs")
data class MusicEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val artist: String?,
    val title: String?,
    val data: String?,
    val duration: Long
) {
    fun toData() = MusicData(id, artist, title, data, duration)
}