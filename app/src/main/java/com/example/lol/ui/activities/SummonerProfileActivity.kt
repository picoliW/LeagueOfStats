package com.example.lol.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.lol.database.ChampionDao
import com.example.lol.database.ChampionDatabase
import com.example.lol.ui.components.ChampionMasteryResponse
import com.example.lol.ui.components.getTopChampionMasteries
import com.example.lol.ui.theme.LolTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SummonerProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val summonerLevel = intent.getIntExtra("summoner_level", 0)
        val puuid = intent.getStringExtra("puuid") ?: ""

        setContent {
            LolTheme {
                SummonerProfileScreen(summonerLevel, puuid)
            }
        }
    }
}

@Composable
fun SummonerProfileScreen(summonerLevel: Int, puuid: String) {
    var masteries by remember { mutableStateOf<List<ChampionMasteryResponse>>(emptyList()) }
    var championNames by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }

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
                } catch (e: Exception) {
                    Log.e("SummonerProfileScreen", "Erro ao buscar maestrias: ${e.message}")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Nível do Invocador: $summonerLevel", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (masteries.isNotEmpty()) {
            masteries.forEach { mastery ->
                val championName = championNames[mastery.championId] ?: "Desconhecido"
                Text(
                    text = "Campeão: $championName, Nível: ${mastery.championLevel}, Pontos: ${mastery.championPoints}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            Text("Carregando maestrias...")
        }
    }
}


suspend fun getChampionNameById(championId: Int, dao: ChampionDao): String? {
    val champion = dao.getChampionById(championId.toString())
    Log.d("ChampionDebug", "ID: $championId, Campeão: $champion")
    return champion?.name
}


