package com.example.lol.ui.activities

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.lol.models.ChampionStats
import com.example.lol.ui.theme.LolTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.graphics.Bitmap
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.lol.R
import com.example.lol.models.ChampionIconModel
import com.example.lol.models.ItemsModel
import com.example.lol.models.Price
import com.example.lol.ui.components.loadImageFromUrl
import com.example.lol.ui.components.scheduleNotification
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL


class RandomChampionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LolTheme {
                RandomChampionsScreen()
            }
        }
    }
}

@Composable
fun RandomChampionsScreen() {
    val champions = remember { mutableStateOf(listOf<ChampionIconModel>()) }
    val randomChampions = remember { mutableStateListOf<ChampionIconModel>() }
    val context = LocalContext.current

    var selectedChampion by remember { mutableStateOf<Pair<ChampionIconModel, List<ItemsModel>>?>(null) }

    LaunchedEffect(Unit) {
        fetchChampionIcons(champions, context)
    }

    LaunchedEffect(champions.value) {
        randomChampions.clear()
        randomChampions.addAll(champions.value.shuffled().take(10))
    }

    selectedChampion?.let { (champion, items) ->
        ItemModal(champion, items) {
            selectedChampion = null
        }
    }

    val vsImages = listOf(
        R.drawable.lane1_top,
        R.drawable.lane2_jg,
        R.drawable.lane3_mid,
        R.drawable.lane4_adc,
        R.drawable.lane5_sup
    )

    if (randomChampions.size == 10) {
        val team1 = randomChampions.take(5)
        val team2 = randomChampions.takeLast(5)

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.background1),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(team1.size) { index ->
                        val champion1 = team1[index]
                        val champion2 = team2[index]
                        val vsImage = vsImages.getOrNull(index) ?: R.drawable.test

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(4.dp, Color(0xFFC89B3C))
                                .background(Color(0xFF0A1428))
                                .padding(vertical = 16.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ChampionWithDiceIcon(
                                champion = champion1,
                                onChampionClick = {
                                    fetchRandomItems(context) { randomItems ->
                                        selectedChampion = champion1 to randomItems
                                    }
                                }
                            )

                            Image(
                                painter = painterResource(id = vsImage),
                                contentDescription = "VS Icon",
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(horizontal = 16.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )

                            ChampionWithDiceIcon(
                                champion = champion2,
                                onChampionClick = {
                                    fetchRandomItems(context) { randomItems ->
                                        selectedChampion = champion2 to randomItems
                                    }
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}



@Composable
fun ItemModal(champion: ChampionIconModel, items: List<ItemsModel>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Itens para ${champion.name}") },
        text = {
            Column {
                items.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        // Carregar a imagem do item
                        var bitmap by remember { mutableStateOf<Bitmap?>(null) }

                        LaunchedEffect(item.iconUrl) {
                            CoroutineScope(Dispatchers.IO).launch {
                                bitmap = loadImageFromUrl(item.iconUrl)
                            }
                        }

                        bitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "${item.name} Icon",
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(end = 8.dp),
                                contentScale = ContentScale.Crop
                            )
                        } ?: Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.Gray)
                        )

                        Text(text = item.name)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar")
            }
        }
    )
}



@Composable
fun ChampionWithDiceIcon(champion: ChampionIconModel, onChampionClick: () -> Unit) {
    Box(
        modifier = Modifier.size(96.dp)
    ) {
        ChampionIcon(champion = champion, onClick = onChampionClick)

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White, shape = CircleShape)
                .align(Alignment.BottomEnd),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {  },
                modifier = Modifier.size(32.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.shuffle_icon),
                    contentDescription = "Sorteio Aleatório"
                )
            }
        }
    }
}

@Composable
fun ChampionIcon(champion: ChampionIconModel, onClick: () -> Unit) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(champion.iconUrl) {
        CoroutineScope(Dispatchers.IO).launch {
            bitmap = loadImageFromUrl(champion.iconUrl)
        }
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "${champion.name} Icon",
            modifier = Modifier
                .size(96.dp)
                .shadow(8.dp, shape = MaterialTheme.shapes.medium)
                .clickable(onClick = onClick), // Abre o modal ao clicar no ícone
            contentScale = ContentScale.Crop
        )
    } ?: Box(
        modifier = Modifier
            .size(96.dp)
            .background(Color.Gray, shape = MaterialTheme.shapes.medium)
    )
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





fun fetchChampionIcons(icons: MutableState<List<ChampionIconModel>>, context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
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

        withContext(Dispatchers.Main) {
            icons.value = allChampions
        }
    }
}
