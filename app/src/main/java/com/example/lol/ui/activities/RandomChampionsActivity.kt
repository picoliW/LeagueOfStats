package com.example.lol.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lol.models.ChampionStats
import com.example.lol.ui.theme.LolTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.lol.R

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
    val champions = remember { mutableStateOf(listOf<ChampionStats>()) }
    val randomChampions = remember { mutableStateListOf<ChampionStats>() }

    fetchAllChampions(champions, context = LocalContext.current)

    LaunchedEffect(champions.value) {
        randomChampions.clear()
        randomChampions.addAll(champions.value.shuffled().take(10))
    }

    if (randomChampions.size == 10) {
        val team1 = randomChampions.take(5)
        val team2 = randomChampions.takeLast(5)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    randomChampions.clear()
                    randomChampions.addAll(champions.value.shuffled().take(10))
                },
                modifier = Modifier.padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(text = "Rolar Novamente")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    team1.forEachIndexed { index, champion ->
                        ChampionWithDiceIcon(
                            champion = champion,
                            onDiceClick = {
                                randomChampions[index] = champions.value.random()
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                Text(
                    text = "VS",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    team2.forEachIndexed { index, champion ->
                        ChampionWithDiceIcon(
                            champion = champion,
                            onDiceClick = {
                                randomChampions[5 + index] = champions.value.random()
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun ChampionWithDiceIcon(champion: ChampionStats, onDiceClick: () -> Unit) {
    Box(
        modifier = Modifier.size(96.dp)
    ) {
        ChampionIcon(champion = champion)

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White, shape = CircleShape)
                .align(Alignment.BottomEnd),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onDiceClick,
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
fun ChampionIcon(champion: ChampionStats) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(champion.icon) {
        CoroutineScope(Dispatchers.IO).launch {
            bitmap = loadImageFromUrl(champion.icon)
        }
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "${champion.name} Icon",
            modifier = Modifier
                .size(96.dp)
                .shadow(8.dp, shape = MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop
        )
    } ?: Box(
        modifier = Modifier
            .size(96.dp)
            .background(Color.Gray, shape = MaterialTheme.shapes.medium)
    )
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
