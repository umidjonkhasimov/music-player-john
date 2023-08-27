package uz.gita.musicplayer_john.data.model

import uz.gita.musicplayer_john.data.sources.local.entity.MusicEntity

data class MusicData(
    val id: Int,
    val artist: String?,
    val title: String?,
    val data: String?,
    val duration: Long
) {
    fun toEntity() = MusicEntity(id, artist, title, data, duration)
}