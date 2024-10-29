package com.example.lol.ui.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.lol.R
import com.example.lol.database.ChampionDao
import com.example.lol.database.ChampionDatabase
import com.example.lol.ui.components.ParticipantData
import com.example.lol.ui.components.PlayerCard
import com.example.lol.ui.components.getMatchDetailsWithSummonerNamesAndChampions
import com.example.lol.ui.components.loadImageFromUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

class MatchDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val matchId = intent.getStringExtra("match_id") ?: ""

        val db = ChampionDatabase.getDatabase(this)
        val championDao = db.championDao()

        setContent {
            MatchDetailScreen(matchId, championDao)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(matchId: String, championDao: ChampionDao) {
    var playersChampionsAndRoles by remember { mutableStateOf<List<ParticipantData>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val championIcons = remember { mutableStateMapOf<String, Bitmap?>() }

    LaunchedEffect(matchId) {
        try {
            val response = getMatchDetailsWithSummonerNamesAndChampions(matchId, "americas")
            playersChampionsAndRoles = response

            response.forEach { (_, champion, _, _) ->
                coroutineScope.launch(Dispatchers.IO) {
                    val iconUrl = championDao.getChampionIconByChampionName(champion)
                    if (iconUrl != null) {
                        championIcons[champion] = loadImageFromUrl(iconUrl)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MatchDetailScreen", "Error fetching match details: ${e.message}")
        }
    }

    val team100 = playersChampionsAndRoles.filter { it.teamId == "100" }
    val team200 = playersChampionsAndRoles.filter { it.teamId == "200" }

    val backgroundGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Detalhes da Partida") },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(stringResource(id = R.string.blue_side), style = MaterialTheme.typography.headlineSmall, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(team100) { player ->
                    PlayerCard(player, championIcons)
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(id = R.string.red_side), style = MaterialTheme.typography.headlineSmall, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(team200) { player ->
                    PlayerCard(player, championIcons)
                }
            }
        }
    }
}



