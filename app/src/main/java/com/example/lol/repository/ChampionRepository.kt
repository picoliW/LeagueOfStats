package com.example.lol.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.MutableState
import com.example.lol.data.database.ChampionDatabase
import com.example.lol.data.database.ChampionStatsEntity
import com.example.lol.data.models.ChampionStats
import com.example.lol.data.models.Sprite
import com.example.lol.data.models.Stats
import com.example.lol.ui.activities.translateText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import com.example.lol.data.database.ChampionDao
import android.media.MediaPlayer
import com.example.lol.R

fun fetchAllChampions(champions: MutableState<List<ChampionStats>>, context: Context, size: Int, page: Int) {
    CoroutineScope(Dispatchers.IO).launch {
        val db = ChampionDatabase.getDatabase(context)
        val championDao = db.championDao()

        val cachedChampions = championDao.getAllChampions()

        val locale = context.resources.configuration.locales.get(0)
        val isPortuguese = locale.language == "pt"
        Log.d("sexo", "Cached size: ${cachedChampions.size}")
        Log.d("sexo", "size: $size")
        Log.d("sexo", "page: $page")

        if (cachedChampions.size >= size) {
            withContext(Dispatchers.Main) {
                champions.value += cachedChampions.map {
                    ChampionStats(
                        id = it.id,
                        key = it.key,
                        name = it.name,
                        title = if (isPortuguese) it.translatedTitle ?: it.title else it.title,
                        tags = it.tags.split(","),
                        stats = Stats(
                            hp = it.hp,
                            hpperlevel = it.hpperlevel,
                            mp = it.mp,
                            mpperlevel = it.mpperlevel,
                            movespeed = it.movespeed,
                            armor = it.armor,
                            armorperlevel = it.armorperlevel,
                            spellblock = it.spellblock,
                            spellblockperlevel = it.spellblockperlevel,
                            attackrange = it.attackrange,
                            hpregen = it.hpregen,
                            hpregenperlevel = it.hpregenperlevel,
                            mpregen = it.mpregen,
                            mpregenperlevel = it.mpregenperlevel,
                            crit = it.crit,
                            critperlevel = it.critperlevel,
                            attackdamage = it.attackdamage,
                            attackdamageperlevel = it.attackdamageperlevel,
                            attackspeedperlevel = it.attackspeedperlevel,
                            attackspeed = it.attackspeed
                        ),
                        icon = it.icon,
                        sprite = Sprite(
                            url = it.spriteUrl,
                            x = it.spriteX,
                            y = it.spriteY
                        ),
                        description = it.description
                    )
                }
            }
        } else {
            val url = URL("http://girardon.com.br:3001/champions?page=${page}&size=20")
            val connection = url.openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "GET"
                connection.connect()

                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(response)
                val championList = mutableListOf<ChampionStatsEntity>()

                for (i in 0 until jsonArray.length()) {
                    val champion = jsonArray.getJSONObject(i)
                    val statsJson = champion.getJSONObject("stats")
                    val spriteJson = champion.getJSONObject("sprite")

                    val championId = champion.getString("id")
                    val existingChampion = championDao.getChampionById(championId)

                    val originalTitle = champion.getString("title")
                    val translatedTitle: String = if (existingChampion?.translatedTitle != null) {
                        existingChampion.translatedTitle
                    } else {
                        val translated = translateText(originalTitle, "pt") ?: originalTitle
                        translated
                    }

                    val championStatsEntity = ChampionStatsEntity(
                        id = champion.getString("id"),
                        key = champion.getString("key"),
                        name = champion.getString("name"),
                        title = originalTitle,
                        translatedTitle = translatedTitle,
                        tags = champion.getJSONArray("tags").toString(),
                        hp = statsJson.getInt("hp"),
                        hpperlevel = statsJson.getInt("hpperlevel"),
                        mp = statsJson.getInt("mp"),
                        mpperlevel = statsJson.getInt("mpperlevel"),
                        movespeed = statsJson.getInt("movespeed"),
                        armor = statsJson.getDouble("armor"),
                        armorperlevel = statsJson.getDouble("armorperlevel"),
                        spellblock = statsJson.getDouble("spellblock"),
                        spellblockperlevel = statsJson.getDouble("spellblockperlevel"),
                        attackrange = statsJson.getInt("attackrange"),
                        hpregen = statsJson.getDouble("hpregen"),
                        hpregenperlevel = statsJson.getDouble("hpregenperlevel"),
                        mpregen = statsJson.getDouble("mpregen"),
                        mpregenperlevel = statsJson.getDouble("mpregenperlevel"),
                        crit = statsJson.getDouble("crit"),
                        critperlevel = statsJson.getDouble("critperlevel"),
                        attackdamage = statsJson.getDouble("attackdamage"),
                        attackdamageperlevel = statsJson.getDouble("attackdamageperlevel"),
                        attackspeedperlevel = statsJson.getDouble("attackspeedperlevel"),
                        attackspeed = statsJson.getDouble("attackspeed"),
                        icon = champion.getString("icon").replace("http://", "https://"),
                        spriteUrl = spriteJson.getString("url").replace("http://", "https://"),
                        spriteX = spriteJson.getInt("x"),
                        spriteY = spriteJson.getInt("y"),
                        description = champion.getString("description")
                    )

                    championList.add(championStatsEntity)

                    championDao.insertAll(listOf(championStatsEntity))
                }

                withContext(Dispatchers.Main) {
                    champions.value += championList.map {
                        ChampionStats(
                            id = it.id,
                            key = it.key,
                            name = it.name,
                            title = if (isPortuguese) it.translatedTitle ?: it.title else it.title,
                            tags = it.tags.split(","),
                            stats = Stats(
                                hp = it.hp,
                                hpperlevel = it.hpperlevel,
                                mp = it.mp,
                                mpperlevel = it.mpperlevel,
                                movespeed = it.movespeed,
                                armor = it.armor,
                                armorperlevel = it.armorperlevel,
                                spellblock = it.spellblock,
                                spellblockperlevel = it.spellblockperlevel,
                                attackrange = it.attackrange,
                                hpregen = it.hpregen,
                                hpregenperlevel = it.hpregenperlevel,
                                mpregen = it.mpregen,
                                mpregenperlevel = it.mpregenperlevel,
                                crit = it.crit,
                                critperlevel = it.critperlevel,
                                attackdamage = it.attackdamage,
                                attackdamageperlevel = it.attackdamageperlevel,
                                attackspeedperlevel = it.attackspeedperlevel,
                                attackspeed = it.attackspeed
                            ),
                            icon = it.icon,
                            sprite = Sprite(
                                url = it.spriteUrl,
                                x = it.spriteX,
                                y = it.spriteY
                            ),
                            description = it.description
                        )
                    }
                }
            } finally {
                connection.disconnect()
            }
        }
    }
}



suspend fun getChampionNameById(championId: Int, dao: ChampionDao): String? {
    val champion = dao.getChampionById(championId.toString())
    return champion?.name
}



class SoundManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun playSound(championName: String) {
        val soundFileName = championName
            .lowercase()
            .replace("'", "")
            .replace(" ", "_")
            .replace(".", "")
            .replace("&", "")

        val soundResId = context.resources.getIdentifier(soundFileName, "raw", context.packageName)

        if (soundResId != 0) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, soundResId)
            }
            mediaPlayer?.start()
        } else {
            println("Som não encontrado para o campeão: $championName, nome do arquivo: $soundFileName")
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

fun shareChampion(context: Context, championName: String) {
    val shareMessage = context.getString(R.string.share_text, championName.toString())
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareMessage)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(shareIntent, "Compartilhar via"))
}
