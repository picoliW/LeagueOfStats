package com.example.lol.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "champions")
data class ChampionStatsEntity(
    @PrimaryKey val id: String,
    val key: String,
    val name: String,
    val title: String,
    val tags: String,
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
    val attackspeed: Double,
    val icon: String,
    val spriteUrl: String,
    val spriteX: Int,
    val spriteY: Int,
    val description: String
)
