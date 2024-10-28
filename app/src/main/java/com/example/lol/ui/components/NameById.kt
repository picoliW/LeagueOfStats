package com.example.lol.ui.components

import com.example.lol.database.ChampionDao

suspend fun getChampionNameById(championId: Int, dao: ChampionDao): String? {
    val champion = dao.getChampionById(championId.toString())
    return champion?.name
}