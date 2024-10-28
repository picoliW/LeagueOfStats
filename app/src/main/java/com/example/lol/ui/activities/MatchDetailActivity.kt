package com.example.lol.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.lol.ui.components.getMatchDetailsWithSummonerNames
import com.example.lol.ui.components.getMatchDetailsWithSummonerNamesAndChampions
import kotlinx.coroutines.launch

class MatchDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val matchId = intent.getStringExtra("match_id") ?: ""

        setContent {
            MatchDetailScreen(matchId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(matchId: String) {
    var playersChampionsAndRoles by remember { mutableStateOf<List<Triple<String, String, String>>>(emptyList()) }

    LaunchedEffect(matchId) {
        try {
            val response = getMatchDetailsWithSummonerNamesAndChampions(matchId, "americas")
            playersChampionsAndRoles = response
        } catch (e: Exception) {
            Log.e("MatchDetailScreen", "Error fetching match details: ${e.message}")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Detalhes da Partida") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text("Invocadores, Campeões e Funções na Partida", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(playersChampionsAndRoles) { (name, champion, position) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$name - $champion ($position)",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}





