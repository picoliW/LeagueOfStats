package com.example.lol.ui.activities

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.lol.R
import com.example.lol.ui.components.loadImageFromUrl
import com.example.lol.ui.components.scheduleNotification



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
    val context = LocalContext.current

    val vsImages = listOf(
        R.drawable.lane1_top,
        R.drawable.lane2_jg,
        R.drawable.lane3_mid,
        R.drawable.lane4_adc,
        R.drawable.lane5_sup
    )

//    fetchAllChampions(champions, context = LocalContext.current)

    LaunchedEffect(champions.value) {
        randomChampions.clear()
        randomChampions.addAll(champions.value.shuffled().take(10))
    }

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
                Button(
                    onClick = {
                        randomChampions.clear()
                        randomChampions.addAll(champions.value.shuffled().take(10))

                        val team1Names = randomChampions.take(5).joinToString(", ") { it.name }
                        val team2Names = randomChampions.takeLast(5).joinToString(", ") { it.name }
                        val notificationText = "Time 1: $team1Names\nTime 2: $team2Names"


                        scheduleNotification(context, notificationText)
                    },
                    modifier = Modifier.padding(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(text = stringResource(id = R.string.roll_again))
                }


                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(team1.size) { index ->
                        val champion1 = team1[index]
                        val champion2 = team2[index]

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(4.dp, Color(0xFFC89B3C))
                                .background(Color(0xFF0A1428))
                                .padding(vertical = 16.dp)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ChampionWithDiceIcon(
                                champion = champion1,
                                onDiceClick = {
                                    randomChampions[index] = champions.value.random()
                                }
                            )

                            Image(
                                painter = painterResource(id = vsImages[index]),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(horizontal = 16.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )

                            ChampionWithDiceIcon(
                                champion = champion2,
                                onDiceClick = {
                                    randomChampions[5 + index] = champions.value.random()
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
