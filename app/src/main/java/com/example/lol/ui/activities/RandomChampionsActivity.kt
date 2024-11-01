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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.lol.ui.theme.LolTheme
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.lol.R
import com.example.lol.data.models.ChampionIconModel
import com.example.lol.data.models.ItemsModel
import com.example.lol.repository.fetchChampionIcons
import com.example.lol.repository.fetchRandomItems
import com.example.lol.ui.components.ChampionWithDiceIcon
import com.example.lol.ui.components.CustomCircularProgressIndicator
import com.example.lol.ui.dialogs.ItemModal
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
    val champions = remember { mutableStateOf(listOf<ChampionIconModel>()) }
    val randomChampions = remember { mutableStateListOf<ChampionIconModel>() }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var selectedChampion by remember { mutableStateOf<Pair<ChampionIconModel, List<ItemsModel>>?>(null) }

    val team1 = remember { mutableStateListOf<Pair<ChampionIconModel, List<ItemsModel>>>() }
    val team2 = remember { mutableStateListOf<Pair<ChampionIconModel, List<ItemsModel>>>() }

    LaunchedEffect(Unit) {
        isLoading = true
        fetchChampionIcons(champions, context) {
            isLoading = false
        }
    }

    LaunchedEffect(champions.value) {
        randomChampions.clear()
        randomChampions.addAll(champions.value.shuffled().take(10))

        team1.clear()
        team2.clear()

        randomChampions.take(5).forEach { champion ->
            fetchRandomItems(context) { items ->
                team1.add(champion to items)
            }
        }
        randomChampions.takeLast(5).forEach { champion ->
            fetchRandomItems(context) { items ->
                team2.add(champion to items)
            }
        }
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

    Image(
        painter = painterResource(id = R.drawable.background1),
        contentDescription = null,
        contentScale = ContentScale.Crop
    )

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CustomCircularProgressIndicator(
                color = Color.White,
                strokeWidth = 4.dp
            )
        }
    } else if (team1.size == 5 && team2.size == 5) {
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
                        isLoading = true
                        randomChampions.clear()
                        randomChampions.addAll(champions.value.shuffled().take(10))

                        team1.clear()
                        team2.clear()

                        randomChampions.take(5).forEach { champion ->
                            fetchRandomItems(context) { items ->
                                team1.add(champion to items)
                                if (team1.size == 5 && team2.size == 5) isLoading = false
                            }
                        }
                        randomChampions.takeLast(5).forEach { champion ->
                            fetchRandomItems(context) { items ->
                                team2.add(champion to items)
                                if (team1.size == 5 && team2.size == 5) isLoading = false
                            }
                        }

                        val team1Names = team1.joinToString(", ") { it.first.name }
                        val team2Names = team2.joinToString(", ") { it.first.name }
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
                        val (champion1, items1) = team1[index]
                        val (champion2, items2) = team2[index]
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
                                onChampionClick = { selectedChampion = champion1 to items1 },
                                onDiceClick = {
                                    var newChampion: ChampionIconModel
                                    do {
                                        newChampion = champions.value.shuffled().first()
                                    } while (newChampion == champion1)
                                    fetchRandomItems(context) { newItems ->
                                        team1[index] = newChampion to newItems
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
                                onChampionClick = { selectedChampion = champion2 to items2 },
                                onDiceClick = {
                                    var newChampion: ChampionIconModel
                                    do {
                                        newChampion = champions.value.shuffled().first()
                                    } while (newChampion == champion2)
                                    fetchRandomItems(context) { newItems ->
                                        team2[index] = newChampion to newItems
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






