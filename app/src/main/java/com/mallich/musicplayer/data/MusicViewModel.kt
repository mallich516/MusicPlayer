package com.mallich.musicplayer.data

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mallich.musicplayer.ui.MusicPlayerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val musicDataStore = MusicDataStore(application)

    val currentSongDetails = musicDataStore.readCurrentSongDetails().asLiveData()

    val songStatus = musicDataStore.getSongStatus().asLiveData()

    fun updateCurrentSong(
        title: String,
        album: String,
        artist: String,
        position: Int,
        type: String,
        albumArt: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            musicDataStore.updateCurrentSongDetails(title, album, artist, position, type, albumArt)
        }
    }

    fun updateSongStatus() {
        updateSongStatusInDatabase()
    }

    private fun updateSongStatusInDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            if (MusicRepository.SONG_STATUS == MusicRepository.SONG_PAUSE) {
                MusicRepository.SONG_STATUS = MusicRepository.SONG_PLAY
                if (MusicPlayerActivity.mediaPlayer != null)
                    MusicPlayerActivity.mediaPlayer!!.start()
            } else {
                MusicRepository.SONG_STATUS = MusicRepository.SONG_PAUSE
                if (MusicPlayerActivity.mediaPlayer != null)
                    MusicPlayerActivity.mediaPlayer!!.pause()
            }
            musicDataStore.updateSongStatus(MusicRepository.SONG_STATUS)
        }
    }
}