package com.example.lol

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.lol.ui.theme.LolTheme

class ChampionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val championStats = intent.getSerializableExtra("championStats") as ChampionStats

        setContent {
            LolTheme {
                ChampionDetailsScreen(championStats)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChampionDetailsScreen(championStats: ChampionStats) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = championStats.name) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(championStats.icon)
                            .size(Size.ORIGINAL)
                            .build()
                    ),
                    contentDescription = "${championStats.name} icon",
                    modifier = Modifier
                        .size(128.dp)
                        .padding(16.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Base Champion Stats",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            item {
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = championStats.name, style = MaterialTheme.typography.titleLarge)
                        Text(text = championStats.title, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = "HP: ${championStats.stats.hp}", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Health per lvl: ${championStats.stats.hpperlevel}", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = "Attack Damage: ${championStats.stats.attackdamage}", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Attack Damage per lvl: ${championStats.stats.attackdamageperlevel}", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = "Armor: ${championStats.stats.armor}", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Armor per lvl: ${championStats.stats.armorperlevel}", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = "Mana Points: ${championStats.stats.mp}", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Mana Points per lvl: ${championStats.stats.mpperlevel}", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = "Attack Speed: ${championStats.stats.attackspeed}", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Attack Speed per lvl: ${championStats.stats.attackspeedperlevel}", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Recommended Spells",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center, // Centraliza os itens
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("https://static.wikia.nocookie.net/leagueoflegends/images/7/74/Flash.png/revision/latest/thumbnail/width/360/height/360?cb=20220324211321&path-prefix=pt-br")
                                    .size(Size.ORIGINAL)
                                    .build()
                            ),
                            contentDescription = "flash icon",
                            modifier = Modifier.size(64.dp),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(32.dp))

                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("https://cmsassets.rgpub.io/sanity/images/dsfx7636/news_live/6dc976f3ec2d5f41e14cb9aa94535e9ee2d82077-256x256.png")
                                    .size(Size.ORIGINAL)
                                    .build()
                            ),
                            contentDescription = "teleport icon",
                            modifier = Modifier.size(64.dp),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(16.dp))
                    }
                }
            }
        }
    }
}
