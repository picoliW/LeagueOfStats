package com.example.lol.ui.activities

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lol.R
import com.example.lol.ui.theme.LolTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.lol.models.ChampionStats
import com.example.lol.ui.components.loadImageFromUrl
import com.example.lol.ui.utils.SoundManager

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

@Composable
fun DisplayImg(url: String, name: String) {
    var teleportBitmap by remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            teleportBitmap = loadImageFromUrl(url)
        }
    }

    teleportBitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = name,
            modifier = Modifier.size(64.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChampionDetailsScreen(championStats: ChampionStats) {
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }

    fun shareChampion() {
        val shareMessage = "Venha ver as estatísticas de ${championStats.name} em League of Stats!"
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(shareIntent, "Compartilhar via"))

    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = championStats.name) },
                actions = {
                    IconButton(onClick = { shareChampion() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.compartilhar),
                            contentDescription = "Compartilhar ${championStats.name}",
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                    )
                )
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                var iconBitmap by remember { mutableStateOf<Bitmap?>(null) }

                LaunchedEffect(championStats.icon) {
                    CoroutineScope(Dispatchers.IO).launch {
                        iconBitmap = loadImageFromUrl(championStats.icon)
                    }
                }

                iconBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "${championStats.name} icon",
                        modifier = Modifier
                            .size(128.dp)
                            .padding(16.dp),
                        contentScale = ContentScale.Crop
                    )
                }


                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Image(
                    painter = painterResource(id = R.drawable.altofalante),
                    contentDescription = "Speaker Icon",
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            soundManager.playSound(championStats.name)
                        }
                        .padding(16.dp),
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Dados do Campeão",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.hpicon),
                                contentDescription = "HP icon",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pontos de Vida: ${championStats.stats.hp}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pontos de Vida por lvl: ${championStats.stats.hpperlevel}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.atkdmgicon),
                                contentDescription = "Attack Damage icon",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Dano de Ataque: ${championStats.stats.attackdamage}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Text(
                            text = "Dano de Ataque por lvl: ${championStats.stats.attackdamageperlevel}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.armoricon),
                                contentDescription = "Armor icon",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Armadura: ${championStats.stats.armor}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Text(
                            text = "Armadura por lvl: ${championStats.stats.armorperlevel}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.mpicon),
                                contentDescription = "Mana Points icon",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pontos de Mana: ${championStats.stats.mp}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Text(
                            text = "Pontos de Mana por lvl: ${championStats.stats.mpperlevel}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.atkspeedicon),
                                contentDescription = "Attack Speed icon",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Velocidade de Ataque: ${championStats.stats.attackspeed}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Text(
                            text = "Velocidade de Ataque por lvl: ${championStats.stats.attackspeedperlevel}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Feitiços Recomendados",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            DisplayImg(
                                url = "https://cmsassets.rgpub.io/sanity/images/dsfx7636/news_live/6dc976f3ec2d5f41e14cb9aa94535e9ee2d82077-256x256.png",
                                name = "Teleport icon"
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            DisplayImg(
                                url = "https://static.wikia.nocookie.net/leagueoflegends/images/7/74/Flash.png/revision/latest/thumbnail/width/360/height/360?cb=20220324211321&path-prefix=pt-br",
                                name = "Flash icon"
                            )


                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Itens Recomendados",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DisplayImg(url = "https://leagueofitems.com/images/items/256/3031.webp", name = "IE icon")

                            Spacer(modifier = Modifier.width(16.dp))

                            DisplayImg(url = "https://static.invenglobal.com/upload/image/2021/10/11/i1633960421449915.png", name = "Goredrinker icon")

                            Spacer(modifier = Modifier.width(16.dp))

                            DisplayImg(url = "https://cmsassets.rgpub.io/sanity/images/dsfx7636/news_live/a600af61619cdbcc5b3cf6c8d8f5bb49554d7739-512x512.png", name = "Stormsurge icon")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(IntrinsicSize.Min),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Ordem de Habilidades",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.White)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Q",
                                    color = Color.Black,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ">",
                                    color = Color.Black,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.White)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "W",
                                    color = Color.Black,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ">",
                                    color = Color.Black,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.White)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "E",
                                    color = Color.Black,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }

            }
        }
    }
}




