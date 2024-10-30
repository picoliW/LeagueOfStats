package com.example.lol.ui.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LolTheme {


                ChampionsScreen()
            }
        }
    }
}

fun fetchAllChampions(champions: MutableState<List<ChampionStats>>, context: Context, size: Int, page: Int) {
    CoroutineScope(Dispatchers.IO).launch {
        val db = ChampionDatabase.getDatabase(context)
        val championDao = db.championDao()

        val cachedChampions = championDao.getAllChampions()

        val locale = context.resources.configuration.locales.get(0)
        val isPortuguese = locale.language == "pt"
        Log.d("sexo", "Cached size: ${cachedChampions.size}")
        Log.d("sexo", "size: $size")

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

                    // Insere todos os campeões novos no banco
                    championDao.insertAll(listOf(championStatsEntity))
                }

                withContext(Dispatchers.Main) {
                    champions.value = champions.value + championList.map {
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

fun savePaginationValues(context: Context, size: Int, page: Int) {
    val sharedPreferences = context.getSharedPreferences("LeagueOfStatsPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().apply {
        putInt("SIZE_KEY02", size)
        putInt("PAGE_KEY02", page)
        apply()
    }
}

fun loadPaginationValues(context: Context): Pair<Int, Int> {
    val sharedPreferences = context.getSharedPreferences("LeagueOfStatsPrefs", Context.MODE_PRIVATE)
    val size = sharedPreferences.getInt("SIZE_KEY02", 20)
    val page = sharedPreferences.getInt("PAGE_KEY02", 1)
    return Pair(size, page)
}


@Composable
fun ChampionsScreen() {
    var searchQuery by remember { mutableStateOf("") }
    val champions = remember { mutableStateOf(listOf<ChampionStats>()) }
    val context = LocalContext.current

    // Carregar valores salvos do SharedPreferences
    val (savedSize, savedPage) = loadPaginationValues(context)
    var size by remember { mutableStateOf(savedSize) }
    var page by remember { mutableStateOf(savedPage) }

    val listState = rememberLazyListState()

    LaunchedEffect(size) {
        fetchAllChampions(champions, context , size, page)
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == listState.layoutInfo.totalItemsCount - 1
        }.collect { atEnd ->
            if (atEnd) {
                size += 20
                page += 1
                fetchAllChampions(champions, context , size, page)
                Log.d("ChampionsScreen", "Chegou ao fim da lista! Incrementando variável: $size")

                savePaginationValues(context, size, page)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                )
            )
    ) {
        SearchBar(
            searchQuery = searchQuery,
            onQueryChanged = { searchQuery = it }
        )

        ChampionsList(champions = champions.value, listState = listState)
    }
}

@Composable
fun ChampionsList(champions: List<ChampionStats>, listState: LazyListState) {
    LazyColumn(state = listState) {
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