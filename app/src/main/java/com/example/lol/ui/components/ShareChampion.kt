package com.example.lol.ui.components

import android.content.Context
import android.content.Intent

fun shareChampion(context: Context, championName: String) {
    val shareMessage = "Venha ver as estatísticas de $championName em League of Stats! \n\n" +
            "https://github.com/picoliW/LeagueOfStats"
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareMessage)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(shareIntent, "Compartilhar via"))
}
