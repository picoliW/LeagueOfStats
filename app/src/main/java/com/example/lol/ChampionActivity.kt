package com.example.lol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

            Text(text = championStats.name, style = MaterialTheme.typography.titleLarge)
            Text(text = championStats.title, style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "HP: ${championStats.stats.hp}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Attack Damage: ${championStats.stats.attackdamage}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Armor: ${championStats.stats.armor}", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
