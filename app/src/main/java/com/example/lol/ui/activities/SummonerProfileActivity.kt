package com.example.lol.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lol.ui.theme.LolTheme

class SummonerProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val summonerLevel = intent.getIntExtra("summoner_level", 0)

        setContent {
            LolTheme {
                SummonerLevelScreen(summonerLevel)
            }
        }
    }
}

@Composable
fun SummonerLevelScreen(summonerLevel: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Nível do Invocador: $summonerLevel", style = MaterialTheme.typography.bodyLarge)
    }
}
