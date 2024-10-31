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
import com.example.lol.data.database.ChampionDatabase
import com.example.lol.data.network.ChampionMasteryResponse
import com.example.lol.data.network.MatchDetailsResponse
import com.example.lol.data.network.RiotAccountApi
import com.example.lol.data.network.RiotChampionMasteryApi
import com.example.lol.data.network.RiotMatchApi
import com.example.lol.data.network.RiotSummonerApi
import com.example.lol.data.network.provideRetrofit
import com.example.lol.data.network.provideSummonerRetrofit
import com.example.lol.repository.RiotRepository
import com.example.lol.repository.getChampionNameById
import com.example.lol.ui.theme.LolTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SummonerProfileActivity : ComponentActivity() {
    private lateinit var riotRepository: RiotRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val riotAccountApi = provideRetrofit().create(RiotAccountApi::class.java)
        val riotSummonerApi = provideSummonerRetrofit("br1").create(RiotSummonerApi::class.java)
        val riotChampionMasteryApi = provideSummonerRetrofit("br1").create(RiotChampionMasteryApi::class.java)
        val riotMatchApi = provideRetrofit().create(RiotMatchApi::class.java)

        riotRepository = RiotRepository(riotAccountApi, riotSummonerApi, riotChampionMasteryApi, riotMatchApi)

        val summonerLevel = intent.getIntExtra("summoner_level", 0)
        val puuid = intent.getStringExtra("puuid") ?: ""
        val summonerName = intent.getStringExtra("summoner_name") ?: "Invocador"

        setContent {
            LolTheme {
                SummonerProfileScreen(summonerLevel, puuid, summonerName, riotRepository)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummonerProfileScreen(summonerLevel: Int, puuid: String, summonerName: String, riotRepository: RiotRepository) {
    var masteries by remember { mutableStateOf<List<ChampionMasteryResponse>>(emptyList()) }
    var championNames by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var recentMatches by remember { mutableStateOf<List<MatchDetailsResponse>>(emptyList()) }

    val context = LocalContext.current
    val db = ChampionDatabase.getDatabase(context)

    LaunchedEffect(puuid) {
        if (puuid.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val masteryList = riotRepository.getTopChampionMasteries(puuid)
                    masteries = masteryList

                    val names = mutableMapOf<Int, String>()
                    for (mastery in masteryList) {
                        val championName = getChampionNameById(mastery.championId, db.championDao())
                        if (championName != null) {
                            names[mastery.championId] = championName
                        }
                    }
                    championNames = names

                    val matchIds = riotRepository.getMatchIds(puuid, start = 0, count = 5)
                    val matches = matchIds.map { matchId ->
                        riotRepository.getMatchDetails(matchId)
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
                    text = stringResource(id = R.string.recent_matches),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
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
                            val gameDuration2 = match.info.gameDuration / 60
                            Text(
                                text = stringResource(id = R.string.game_id, match.metadata.matchId.toString()),
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                            )
                            Text(
                                text = stringResource(id = R.string.duration, gameDuration2.toString()),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Text(
                                text = stringResource(id = R.string.game_mode, match.info.gameMode.toString()),
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



