package com.example.lol

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
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.lol.ui.theme.LolTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
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
                fetchAllChampions(champions)

                ChampionsList(champions.value)
            }
        }
    }
}

fun fetchAllChampions(champions: MutableState<List<ChampionStats>>) {
    CoroutineScope(Dispatchers.IO).launch {
        val url = URL("http://girardon.com.br:3001/champions")
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connect()

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(response)
            val championList = mutableListOf<ChampionStats>()

            for (i in 0 until jsonArray.length()) {
                val champion = jsonArray.getJSONObject(i)

                val id = champion.getString("id")
                val key = champion.getString("key")
                val name = champion.getString("name")
                val title = champion.getString("title")
                val description = champion.getString("description")

                val tagsArray = champion.getJSONArray("tags")
                val tags = mutableListOf<String>()
                for (j in 0 until tagsArray.length()) {
                    tags.add(tagsArray.getString(j))
                }

                val statsJson = champion.getJSONObject("stats")
                val stats = Stats(
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
                    attackspeed = statsJson.getDouble("attackspeed")
                )

                val icon = champion.getString("icon").replace("http://", "https://")

                val spriteJson = champion.getJSONObject("sprite")
                val sprite = Sprite(
                    url = spriteJson.getString("url").replace("http://", "https://"),
                    x = spriteJson.getInt("x"),
                    y = spriteJson.getInt("y")
                )

                championList.add(
                    ChampionStats(
                        id = id,
                        key = key,
                        name = name,
                        title = title,
                        tags = tags,
                        stats = stats,
                        icon = icon,
                        sprite = sprite,
                        description = description
                    )
                )
            }

            champions.value = championList
        } finally {
            connection.disconnect()
        }
    }
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
                    val intent = Intent(context, ChampionActivity::class.java).apply {

                    }
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

