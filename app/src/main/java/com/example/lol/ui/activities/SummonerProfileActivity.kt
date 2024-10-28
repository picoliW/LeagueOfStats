package com.example.lol.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.lol.R
import com.example.lol.database.ChampionDao
import com.example.lol.database.ChampionDatabase
import com.example.lol.ui.components.ChampionMasteryResponse
import com.example.lol.ui.components.MatchDetailsResponse
import com.example.lol.ui.components.getChampionNameById
import com.example.lol.ui.components.getMatchDetails
import com.example.lol.ui.components.getMatchIds
import com.example.lol.ui.components.getTopChampionMasteries
import com.example.lol.ui.components.shareChampion
import com.example.lol.ui.theme.LolTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SummonerProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val summonerLevel = intent.getIntExtra("summoner_level", 0)
        val puuid = intent.getStringExtra("puuid") ?: ""
        val summonerName = intent.getStringExtra("summoner_name") ?: "Invocador"

        setContent {
            LolTheme {
                SummonerProfileScreen(summonerLevel, puuid, summonerName)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummonerProfileScreen(summonerLevel: Int, puuid: String, summonerName: String) {
    var masteries by remember { mutableStateOf<List<ChampionMasteryResponse>>(emptyList()) }
    var championNames by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var recentMatches by remember { mutableStateOf<List<MatchDetailsResponse>>(emptyList()) }

    val context = LocalContext.current
    val db = ChampionDatabase.getDatabase(context)

    LaunchedEffect(puuid) {
        if (puuid.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val masteryList = getTopChampionMasteries(puuid, "br1")
                    masteries = masteryList

                    val names = mutableMapOf<Int, String>()
                    for (mastery in masteryList) {
                        val championName = getChampionNameById(mastery.championId, db.championDao())
                        if (championName != null) {
                            names[mastery.championId] = championName
                        }
                    }
                    championNames = names
                    val matchIds = getMatchIds(puuid, "americas")
                    val matches = matchIds.take(5).map { matchId ->
                        getMatchDetails(matchId, "americas")
                    }
                    recentMatches = matches
                } catch (e: Exception) {
                    Log.e("SummonerProfileScreen", "Erro ao buscar dados: ${e.message}")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = summonerName) },
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.summoner_lvl, summonerLevel.toString()),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (masteries.isNotEmpty()) {
                items(masteries) { mastery ->
                    val championName = championNames[mastery.championId] ?: "Desconhecido"
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.teste222),
                                contentDescription = championName,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = championName,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                                )
                                Text(
                                    text = stringResource(
                                        id = R.string.show_mastery,
                                        mastery.championLevel.toString(),
                                        mastery.championPoints.toString()
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            } else {
                item {
                    Text("Carregando maestrias...", color = Color.White)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Partidas Recentes",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(recentMatches) { match ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            val intent = Intent(context, MatchDetailActivity::class.java).apply {
                                putExtra("match_id", match.metadata.matchId)
                            }
                            context.startActivity(intent)
                        },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ID do Jogo: ${match.metadata.matchId}",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                            )
                            Text(
                                text = "Duração: ${match.info.gameDuration / 60} minutos",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Text(
                                text = "Modo de Jogo: ${match.info.gameMode}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

        }
    }
}



