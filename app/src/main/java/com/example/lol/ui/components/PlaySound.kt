package com.example.lol.ui.components

import android.content.Context
import android.media.MediaPlayer

class SoundManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun playSound(championName: String) {
        val soundFileName = championName
            .lowercase()
            .replace("'", "")
            .replace(" ", "_")
            .replace(".", "")
            .replace("&", "")

        val soundResId = context.resources.getIdentifier(soundFileName, "raw", context.packageName)

        if (soundResId != 0) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, soundResId)
            }
            mediaPlayer?.start()
        } else {
            println("Som não encontrado para o campeão: $championName, nome do arquivo: $soundFileName")
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
