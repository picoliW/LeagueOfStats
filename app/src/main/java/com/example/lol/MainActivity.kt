package com.example.lol

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lol.ui.theme.LolTheme
import database.ChampionDatabase
import database.ChampionStatsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

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

                championList.add(
                    ChampionStatsEntity(
                        id = champion.getString("id"),
                        key = champion.getString("key"),
                        name = champion.getString("name"),
                        title = champion.getString("title"),
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
                )
            }

            val db = ChampionDatabase.getDatabase(context)
            db.championDao().insertAll(championList)

            champions.value = championList.map {
                ChampionStats(
                    id = it.id,
                    key = it.key,
                    name = it.name,
                    title = it.title,
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
        } finally {
            connection.disconnect()
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
        ChampionsList(filteredChampions)
    }
}

@Composable
fun SearchBar(searchQuery: String, onQueryChanged: (String) -> Unit) {
    TextField(
        value = searchQuery,
        onValueChange = onQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = {
            Text(text = "Search Champions")
        }
    )
}

@Composable
fun ChampionsList(champions: List<ChampionStats>) {
    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(champions) { champion ->
                val context = LocalContext.current
                ChampionCard(champion) {
                    val intent = Intent(context, ChampionActivity::class.java)
                    context.startActivity(intent)
                }
            }
        }
    }
}

@Composable
fun ChampionCard(champion: ChampionStats, onClick: () -> Unit) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

LaunchedEffect(champion.icon) {
    CoroutineScope(Dispatchers.IO).launch {
        bitmap = loadImageFromUrl(champion.icon)
    }
}

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                val intent = Intent(context, ChampionActivity::class.java)
                intent.putExtra("championStats", champion)
                context.startActivity(intent)
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "${champion.icon} icon",
                    modifier = Modifier.size(64.dp),
                    contentScale = ContentScale.Crop
                )
            } ?: Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            )

            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = champion.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = champion.title, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

fun loadImageFromUrl(url: String): Bitmap? {
    return try {
        val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val inputStream = connection.inputStream
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

