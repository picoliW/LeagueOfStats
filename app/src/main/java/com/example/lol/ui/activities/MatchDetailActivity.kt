package com.example.lol.ui.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.lol.R
import com.example.lol.data.database.ChampionDao
import com.example.lol.data.database.ChampionDatabase
import com.example.lol.data.network.ParticipantData
import com.example.lol.data.network.RiotAccountApi
import com.example.lol.data.network.RiotChampionMasteryApi
import com.example.lol.data.network.RiotMatchApi
import com.example.lol.data.network.RiotSummonerApi
import com.example.lol.data.network.provideRetrofit
import com.example.lol.data.network.provideSummonerRetrofit
import com.example.lol.repository.RiotRepository
import com.example.lol.ui.components.PlayerCard
import com.example.lol.ui.components.loadImageFromUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MatchDetailActivity : ComponentActivity() {
    private lateinit var riotRepository: RiotRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val riotAccountApi = provideRetrofit().create(RiotAccountApi::class.java)
        val riotSummonerApi = provideSummonerRetrofit("br1").create(RiotSummonerApi::class.java)
        val riotChampionMasteryApi = provideSummonerRetrofit("br1").create(RiotChampionMasteryApi::class.java)
        val riotMatchApi = provideRetrofit().create(RiotMatchApi::class.java)

        riotRepository = RiotRepository(
            riotAccountApi,
        riotSummonerApi,
        riotChampionMasteryApi,
        riotMatchApi
        )

        val matchId = intent.getStringExtra("match_id") ?: ""

        val db = ChampionDatabase.getDatabase(this)
        val championDao = db.championDao()

        setContent {
            MatchDetailScreen(matchId, championDao, riotRepository)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    matchId: String,
    championDao: ChampionDao,
    riotRepository: RiotRepository
) {
    var playersChampionsAndRoles by remember { mutableStateOf<List<ParticipantData>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val championIcons = remember { mutableStateMapOf<String, Bitmap?>() }

    LaunchedEffect(matchId) {
        try {
            val response = riotRepository.getMatchDetailsWithSummonerNamesAndChampions(matchId)
            playersChampionsAndRoles = response

            response.forEach { playerData ->
                coroutineScope.launch(Dispatchers.IO) {
                    val iconUrl = championDao.getChampionIconByChampionName(playerData.championName)
                    if (iconUrl != null) {
                        championIcons[playerData.championName] = loadImageFromUrl(iconUrl)
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


