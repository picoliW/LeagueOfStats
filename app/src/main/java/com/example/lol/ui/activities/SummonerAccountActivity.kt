package com.example.lol.ui.activities

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
import com.example.lol.ui.theme.LolTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

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
    var summonerLevel by remember { mutableStateOf<Int?>(null) }
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

                            summonerLevel = level
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
            Text("Buscar Nível da Conta")
        }

        Spacer(modifier = Modifier.height(24.dp))

    }
}

const val RIOT_API_KEY = "RGAPI-e5e4bc18-970a-41fc-845a-517a3d01679b"

fun provideOkHttpClient(): OkHttpClient {
    val interceptor = Interceptor { chain ->
        val original = chain.request()
        val originalUrl = original.url
        val url = originalUrl.newBuilder()
            .addQueryParameter("api_key", RIOT_API_KEY)
            .build()
        val request = original.newBuilder().url(url).build()
        chain.proceed(request)
    }

    return OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()
}

fun provideRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://americas.api.riotgames.com")
        .client(provideOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideSummonerRetrofit(region: String): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://$region.api.riotgames.com")
        .client(provideOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}


interface RiotAccountApi {
    @GET("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}")
    suspend fun getAccountByRiotId(
        @Path("gameName") gameName: String,
        @Path("tagLine") tagLine: String
    ): AccountResponse
}

interface RiotSummonerApi {
    @GET("/lol/summoner/v4/summoners/by-puuid/{encryptedPUUID}")
    suspend fun getSummonerByPUUID(
        @Path("encryptedPUUID") puuid: String
    ): SummonerResponse
}

data class AccountResponse(val puuid: String)
data class SummonerResponse(val summonerLevel: Int)

suspend fun getAccountPuuid(gameName: String, tagLine: String): String {
    val retrofit = provideRetrofit()
    val accountApi = retrofit.create(RiotAccountApi::class.java)
    return accountApi.getAccountByRiotId(gameName, tagLine).puuid
}

suspend fun getSummonerLevel(puuid: String, region: String): Int {
    val retrofit = provideSummonerRetrofit(region)
    val summonerApi = retrofit.create(RiotSummonerApi::class.java)
    return summonerApi.getSummonerByPUUID(puuid).summonerLevel
}

