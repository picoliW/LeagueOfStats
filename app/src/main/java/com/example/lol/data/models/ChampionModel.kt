package com.example.lol.data.models

import java.io.Serializable



data class ChampionStats(
    val id: String,
    val key: String,
    val name: String,
    val title: String,
    val tags: List<String>,
    val stats: Stats,
    val icon: String,
    val sprite: Sprite,
    val description: String,
    var isFavorited: Boolean = false
) : Serializable

data class Stats(
    val hp: Int,
    val hpperlevel: Int,
    val mp: Int,
    val mpperlevel: Int,
    val movespeed: Int,
    val armor: Double,
    val armorperlevel: Double,
    val spellblock: Double,
    val spellblockperlevel: Double,
    val attackrange: Int,
    val hpregen: Double,
    val hpregenperlevel: Double,
    val mpregen: Double,
    val mpregenperlevel: Double,
    val crit: Double,
    val critperlevel: Double,
    val attackdamage: Double,
    val attackdamageperlevel: Double,
    val attackspeedperlevel: Double,
    val attackspeed: Double
) : Serializable

data class Sprite(
    val url: String,
    val x: Int,
    val y: Int
) : Serializable
