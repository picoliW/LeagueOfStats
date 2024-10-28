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
    var summonerNames by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(matchId) {
        try {
            Log.d("MatchDetailScreen", "Fetching details for match ID: $matchId")
            val response = getMatchDetailsWithSummonerNames(matchId, "americas")
            Log.d("MatchDetailScreen", "API response: $response")

            summonerNames = response.second.filter { it.isNotEmpty() }
            Log.d("MatchDetailScreen", "Summoner Names: $summonerNames")

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
                Text("Invocadores na partida", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(summonerNames) { name ->
                Log.d("caralho", "Summoner Names: $name")
                Text(
                    text = name,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}



