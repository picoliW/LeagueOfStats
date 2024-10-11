package com.example.lol

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lol.ui.theme.LolTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

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
fun displayImg(url: String, name: String) {
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
    var mediaPlayer: MediaPlayer? = null

    fun playSound(championName: String) {
        val soundFileName = championName
            .lowercase()
            .replace("'", "")
            .replace(" ", "_")
            .replace(".", "")
            .replace("&", "")

        val soundResId = context.resources.getIdentifier(soundFileName, "raw", context.packageName)

        if (soundResId != 0) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, soundResId)
            }
            mediaPlayer?.start()
            println("Som não encontrado para o campeão: $championName, nome do arquivo: $soundFileName")
        } else {
            println("Som não encontrado para o campeão: $championName, nome do arquivo: $soundFileName")
        }
    }

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
                            playSound(championStats.name)
                        }
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
                                text = "HP: ${championStats.stats.hp}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Health per lvl: ${championStats.stats.hpperlevel}",
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
                                text = "Attack Damage: ${championStats.stats.attackdamage}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Text(
                            text = "Attack Damage per lvl: ${championStats.stats.attackdamageperlevel}",
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
                                text = "Armor: ${championStats.stats.armor}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Text(
                            text = "Armor per lvl: ${championStats.stats.armorperlevel}",
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
                                text = "Mana Points: ${championStats.stats.mp}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Text(
                            text = "Mana Points per lvl: ${championStats.stats.mpperlevel}",
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
                                text = "Attack Speed: ${championStats.stats.attackspeed}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Text(
                            text = "Attack Speed per lvl: ${championStats.stats.attackspeedperlevel}",
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
                            text = "Recommended Spells",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            displayImg(
                                url = "https://static.wikia.nocookie.net/leagueoflegends/images/7/74/Flash.png/revision/latest/thumbnail/width/360/height/360?cb=20220324211321&path-prefix=pt-br",
                                name = "Flash icon"
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            displayImg(
                                url = "https://cmsassets.rgpub.io/sanity/images/dsfx7636/news_live/6dc976f3ec2d5f41e14cb9aa94535e9ee2d82077-256x256.png",
                                name = "Teleport icon"
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
                            text = "Recommended Items",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            displayImg(url = "https://leagueofitems.com/images/items/256/3031.webp", name = "IE icon")

                            Spacer(modifier = Modifier.width(16.dp))

                            displayImg(url = "https://static.invenglobal.com/upload/image/2021/10/11/i1633960421449915.png", name = "Goredrinker icon")

                            Spacer(modifier = Modifier.width(16.dp))

                            displayImg(url = "https://cmsassets.rgpub.io/sanity/images/dsfx7636/news_live/a600af61619cdbcc5b3cf6c8d8f5bb49554d7739-512x512.png", name = "Stormsurge icon")
                        }
                    }
                }
            }
        }
    }
}


