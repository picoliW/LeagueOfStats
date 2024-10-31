package com.example.lol.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "champion_icons")
data class ChampionIconEntity(
    @PrimaryKey val name: String,
    val key: Int,
    val iconUrl: String
)