package com.example.lol

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.lol.ui.theme.LolTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LolTheme {
                val champions = remember { mutableStateOf(listOf<Champion>()) }
                fetchAllChampions(champions)

                ChampionsList(champions.value)
            }
        }
    }
}

data class Champion(
    val name: String,
    val icon: String,
    val title: String
)

fun fetchAllChampions(champions: MutableState<List<Champion>>) {
    CoroutineScope(Dispatchers.IO).launch {
        val url = URL("http://girardon.com.br:3001/champions")
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connect()

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(response)
            val championList = mutableListOf<Champion>()

            for (i in 0 until jsonArray.length()) {
                val champion = jsonArray.getJSONObject(i)
                val name = champion.getString("name")
                val image = champion.getString("icon").replace("http://", "https://")
                val title = champion.getString("title")

                println("URL da imagem de $name: $image")

                championList.add(Champion(name, image, title))
            }

            champions.value = championList
        } finally {
            connection.disconnect()
        }
    }
}

@Composable
fun ChampionsList(champions: List<Champion>) {
    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(champions) { champion ->
                ChampionCard(champion)
            }
        }
    }
}

@Composable
fun ChampionCard(champion: Champion) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(champion.icon)
                        .size(Size.ORIGINAL)
                        .build()
                ),
                contentDescription = "${champion.name} icon",
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = champion.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = champion.title, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LolTheme {
        val sampleChampion = Champion("Ahri", "https://example.com/ahri_icon.png", "The Nine-Tailed Fox")
        ChampionCard(sampleChampion)
    }
}
