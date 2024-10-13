package com.example.lol.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChampionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(champions: List<ChampionStatsEntity>)

    @Query("SELECT * FROM champions")
    suspend fun getAllChampions(): List<ChampionStatsEntity>
}
