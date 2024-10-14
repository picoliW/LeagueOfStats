package com.example.lol.ui.activities

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.lol.BuildConfig
import com.example.lol.models.ChampionStats
import com.example.lol.ui.components.ChampionCard
import com.example.lol.ui.components.SearchBar
import com.example.lol.ui.theme.LolTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import com.example.lol.database.ChampionDatabase
import com.example.lol.database.ChampionStatsEntity
import com.example.lol.models.Sprite
import com.example.lol.models.Stats
import com.example.lol.ui.components.NotificationButton
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LolTheme {
                val champions = remember { mutableStateOf(listOf<ChampionStats>()) }
                fetchAllChampions(champions, context = LocalContext.current)

                ChampionsScreen()
            }
        }
    }
}

fun fetchAllChampions(champions: MutableState<List<ChampionStats>>, context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        val db = ChampionDatabase.getDatabase(context)
        val championDao = db.championDao()

        val cachedChampions = championDao.getAllChampions()

        if (cachedChampions.isNotEmpty()) {
            withContext(Dispatchers.Main) {
                champions.value = cachedChampions.map {
                    ChampionStats(
                        id = it.id,
                        key = it.key,
                        name = it.name,
                        title = it.translatedTitle ?: it.title,
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
            val url = URL("http://girardon.com.br:3001/champions")
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
                    champions.value = championList.map {
                        ChampionStats(
                            id = it.id,
                            key = it.key,
                            name = it.name,
                            title = it.translatedTitle ?: it.title,
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




@Composable
fun ChampionsScreen() {
    var searchQuery by remember { mutableStateOf("") }
    val champions = remember { mutableStateOf(listOf<ChampionStats>()) }
    fetchAllChampions(champions, context = LocalContext.current)

    val filteredChampions = champions.value.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.title.contains(searchQuery, ignoreCase = true)
    }

    Column {
        SearchBar(
            searchQuery = searchQuery,
            onQueryChanged = { searchQuery = it }
        )
        NotificationButton()
        ChampionsList(filteredChampions)
    }
}

@Composable
fun ChampionsList(champions: List<ChampionStats>) {
    LazyColumn {
        items(champions) { champion ->
            ChampionCard(champion = champion, onClick = {})
        }
    }
}


fun translateText(text: String, targetLanguage: String): String {

    val apiKey = BuildConfig.GOOGLE_API_KEY

    val translate = TranslateOptions.newBuilder().setApiKey(apiKey).build().service

    val translation = translate.translate(
        text,
        Translate.TranslateOption.targetLanguage(targetLanguage)
    )
    return translation.translatedText
}

