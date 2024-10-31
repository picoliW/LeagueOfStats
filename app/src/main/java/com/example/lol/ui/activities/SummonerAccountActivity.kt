package com.example.lol.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.lol.R
import com.example.lol.data.network.*
import com.example.lol.repository.RiotRepository
import com.example.lol.ui.theme.LolTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountActivity : ComponentActivity() {
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

        setContent {
            LolTheme {
                AccountScreen(riotRepository)
            }
        }
    }
}


@Composable
fun AccountScreen(riotRepository: RiotRepository) {
    var gameName by remember { mutableStateOf(TextFieldValue("")) }
    var tagLine by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = gameName,
            onValueChange = { gameName = it },
            label = { Text(text = stringResource(id = R.string.summoner_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = tagLine,
            onValueChange = { tagLine = it },
            label = { Text(text = stringResource(id = R.string.summoner_tag)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (gameName.text.isNotEmpty() && tagLine.text.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val puuid = riotRepository.getAccountPuuid(gameName.text, tagLine.text)
                            val level = riotRepository.getSummonerLevel(puuid)

                            CoroutineScope(Dispatchers.Main).launch {
                                val intent = Intent(context, SummonerProfileActivity::class.java)
                                intent.putExtra("summoner_level", level)
                                intent.putExtra("puuid", puuid)
                                intent.putExtra("summoner_name", gameName.text)
                                context.startActivity(intent)
                            }
                        } catch (e: Exception) {
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(
                                    context,
                                    "Erro ao buscar o invocador: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Preencha ambos os campos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(stringResource(id = R.string.search_summoner))
        }
    }
}
