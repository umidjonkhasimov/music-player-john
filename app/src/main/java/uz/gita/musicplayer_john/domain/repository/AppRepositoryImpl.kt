package uz.gita.musicplayer_john.domain.repository

import android.database.Cursor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uz.gita.musicplayer_john.data.model.MusicData
import uz.gita.musicplayer_john.data.sources.local.dao.MusicDao
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val dao: MusicDao
) : AppRepository {

    override fun addMusic(musicData: MusicData) {
        dao.addMusic(musicData.toEntity())
    }

    override fun deleteMusic(musicData: MusicData) {
        dao.deleteMusic(musicData.toEntity())
    }

    override fun getAllMusics(): Flow<List<MusicData>> =
        dao.retrieveAllMusics().map { list ->
            list.map { musicEntity ->
                musicEntity.toData()
            }
        }

    override fun getSavedMusics(): Cursor = dao.getSavedMusics()

    override fun checkMusicIsSaved(musicData: MusicData): Boolean {
        val data = dao.checkMusicSaved(musicData.data ?: "")
        return data != null
    }
}