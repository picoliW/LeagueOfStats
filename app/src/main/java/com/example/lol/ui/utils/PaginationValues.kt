package com.example.lol.ui.utils

import android.content.Context

fun savePaginationValues(context: Context, size: Int, page: Int) {
    val sharedPreferences = context.getSharedPreferences("LeagueOfStatsPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().apply {
        putInt("SIZE_KEY31", size)
        putInt("PAGE_KEY31", page)
        apply()
    }
}

fun loadPaginationValues(context: Context): Pair<Int, Int> {
    val sharedPreferences = context.getSharedPreferences("LeagueOfStatsPrefs", Context.MODE_PRIVATE)
    val size = sharedPreferences.getInt("SIZE_KEY31", 20)
    val page = sharedPreferences.getInt("PAGE_KEY31", 1)
    return Pair(size, page)
}