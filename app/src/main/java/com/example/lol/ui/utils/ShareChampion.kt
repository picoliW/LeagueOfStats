package com.example.lol.ui.utils

import android.content.Context
import android.content.Intent
import com.example.lol.R

fun shareChampion(context: Context, championName: String) {
    val shareMessage = context.getString(R.string.share_text, championName.toString())
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareMessage)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(shareIntent, "Compartilhar via"))
}