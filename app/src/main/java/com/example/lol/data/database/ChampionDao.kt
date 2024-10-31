package com.example.lol.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChampionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(champions: List<ChampionStatsEntity>)

    @Query("SELECT * FROM champions WHERE [key] = :key LIMIT 1")
    suspend fun getChampionById(key: String): ChampionStatsEntity?

    @Query("SELECT * FROM champions")
    suspend fun getAllChampions(): List<ChampionStatsEntity>

    @Query("UPDATE champions SET isFavorited = :isFavorited WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorited: Boolean)

    @Query("SELECT * FROM champions WHERE isFavorited = 1")
    suspend fun getFavoritedChampions(): List<ChampionStatsEntity>

    @Query("SELECT icon FROM champions WHERE name = :championName LIMIT 1")
    suspend fun getChampionIconByChampionName(championName: String): String?

    @Query("SELECT icon FROM champions WHERE [key] = :key")
    suspend fun getChampionIconByChampionId(key: String): String?

    @Query("SELECT iconUrl FROM champion_icons WHERE [key] = :key")
    suspend fun getChampionIconByChampionId2(key: String): String?

    @Query("SELECT iconUrl FROM champion_icons WHERE name = :championName LIMIT 1")
    suspend fun getChampionIconByChampionName2(championName: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIcons(icons: List<ChampionIconEntity>)

    @Query("SELECT * FROM champion_icons")
    suspend fun getAllIcons(): List<ChampionIconEntity>

    @Query("SELECT * FROM champion_icons WHERE [key] = :key LIMIT 1")
    suspend fun getChampionById2(key: String): ChampionIconEntity?
}
