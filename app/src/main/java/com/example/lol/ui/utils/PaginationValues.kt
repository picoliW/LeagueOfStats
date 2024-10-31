package com.example.lol.ui.utils

import android.content.Context

fun savePaginationValues(context: Context, size: Int, page: Int) {
    val sharedPreferences = context.getSharedPreferences("LeagueOfStatsPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().apply {
        putInt("SIZE_KEY24", size)
        putInt("PAGE_KEY24", page)
        apply()
    }
}

fun loadPaginationValues(context: Context): Pair<Int, Int> {
    val sharedPreferences = context.getSharedPreferences("LeagueOfStatsPrefs", Context.MODE_PRIVATE)
    val size = sharedPreferences.getInt("SIZE_KEY24", 20)
    val page = sharedPreferences.getInt("PAGE_KEY24", 1)
    return Pair(size, page)
}