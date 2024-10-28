package com.example.lol.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.lol.ui.components.getAccountPuuid
import com.example.lol.ui.components.getSummonerLevel
import com.example.lol.ui.theme.LolTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LolTheme {
                AccountScreen()
            }
        }
    }
}

@Composable
fun AccountScreen() {
    var gameName by remember { mutableStateOf(TextFieldValue("")) }
    var tagLine by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = gameName,
            onValueChange = { gameName = it },
            label = { Text("Game Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = tagLine,
            onValueChange = { tagLine = it },
            label = { Text("Tag Line") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (gameName.text.isNotEmpty() && tagLine.text.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            Log.d("AccountActivity", "Iniciando requisição para obter PUUID")
                            val puuid = getAccountPuuid(gameName.text, tagLine.text)
                            Log.d("AccountActivity", "PUUID obtido com sucesso: $puuid")

                            Log.d("AccountActivity", "Iniciando requisição para obter nível do invocador")
                            val level = getSummonerLevel(puuid, "br1")
                            Log.d("AccountActivity", "Nível do invocador obtido com sucesso: $level")

                            CoroutineScope(Dispatchers.Main).launch {
                                val intent = Intent(context, SummonerProfileActivity::class.java)
                                intent.putExtra("summoner_level", level)
                                context.startActivity(intent)
                            }
                        } catch (e: Exception) {
                            Log.e("AccountActivity", "Erro ao buscar o nível: ${e.message}", e)
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(
                                    context,
                                    "Erro ao buscar o nível: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Preencha ambos os campos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar Invocador")
        }
    }
}
