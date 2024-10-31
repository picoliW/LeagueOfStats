package com.example.lol.repository

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import com.example.lol.data.database.ChampionDatabase
import com.example.lol.data.database.ChampionStatsEntity
import com.example.lol.data.models.ChampionStats
import com.example.lol.data.models.Sprite
import com.example.lol.data.models.Stats
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import com.example.lol.data.database.ChampionDao
import com.example.lol.data.database.ChampionIconEntity
import com.example.lol.data.models.ChampionIconModel
import com.example.lol.data.models.ItemsModel
import com.example.lol.data.models.Price
import com.example.lol.ui.utils.translateText


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

fun fetchChampionIcons(
    icons: MutableState<List<ChampionIconModel>>,
    context: Context,
    onComplete: () -> Unit
) {
    val database = ChampionDatabase.getDatabase(context)
    CoroutineScope(Dispatchers.IO).launch {
        val cachedIcons = database.championDao().getAllIcons()
        if (cachedIcons.isNotEmpty()) {
            withContext(Dispatchers.Main) {
                icons.value = cachedIcons.map { ChampionIconModel(it.name, it.key, it.iconUrl) }
                onComplete()
            }
        } else {
            val allChampions = mutableListOf<ChampionIconModel>()
            var currentPage = 1
            val size = 20
            var hasMore = true

            while (hasMore) {
                val url = URL("http://girardon.com.br:3001/champions?page=$currentPage&size=$size")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                try {
                    connection.connect()
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonArray = JSONArray(response)

                    if (jsonArray.length() == 0) {
                        hasMore = false
                    } else {
                        for (i in 0 until jsonArray.length()) {
                            val champion = jsonArray.getJSONObject(i)
                            val icon = ChampionIconModel(
                                name = champion.getString("name"),
                                key = champion.getInt("key"),
                                iconUrl = champion.getString("icon").replace("http://", "https://")
                            )
                            allChampions.add(icon)
                        }
                        currentPage++
                    }
                } finally {
                    connection.disconnect()
                }
            }

            val iconEntities = allChampions.map { ChampionIconEntity(it.name, it.key, it.iconUrl) }
            database.championDao().insertIcons(iconEntities)

            withContext(Dispatchers.Main) {
                icons.value = allChampions
                onComplete()
            }
        }
    }
}


fun fetchRandomItems(context: Context, onResult: (List<ItemsModel>) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        val allItems = mutableListOf<ItemsModel>()
        var currentPage = 1
        val size = 20
        var hasMore = true

        while (hasMore) {
            val url = URL("http://girardon.com.br:3001/items?page=$currentPage&size=$size")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            try {
                connection.connect()
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(response)

                if (jsonArray.length() == 0) {
                    hasMore = false
                } else {
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val priceJson = item.getJSONObject("price")
                        val totalPrice = priceJson.getInt("total")

                        if (totalPrice > 2000) {
                            val itemModel = ItemsModel(
                                name = item.getString("name"),
                                description = item.getString("description"),
                                price = Price(
                                    base = priceJson.getInt("base"),
                                    total = totalPrice,
                                    sell = priceJson.getInt("sell")
                                ),
                                purchasable = item.getBoolean("purchasable"),
                                iconUrl = item.getString("icon").replace("http://", "https://")
                            )
                            allItems.add(itemModel)
                        }
                    }
                    currentPage++
                }
            } finally {
                connection.disconnect()
            }
        }

        withContext(Dispatchers.Main) {
            onResult(allItems.shuffled().take(5))
        }
    }
}