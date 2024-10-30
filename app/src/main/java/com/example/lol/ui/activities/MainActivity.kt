package com.example.lol.ui.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.lol.BuildConfig
import com.example.lol.models.ChampionStats
import com.example.lol.ui.components.ChampionCard
import com.example.lol.ui.components.SearchBar
import com.example.lol.ui.theme.LolTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import com.example.lol.database.ChampionDatabase
import com.example.lol.database.ChampionStatsEntity
import com.example.lol.models.Sprite
import com.example.lol.models.Stats
import com.example.lol.ui.components.fetchAllChampions
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LolTheme {


                ChampionsScreen()
            }
        }
    }
}



fun savePaginationValues(context: Context, size: Int, page: Int) {
    val sharedPreferences = context.getSharedPreferences("LeagueOfStatsPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().apply {
        putInt("SIZE_KEY18", size)
        putInt("PAGE_KEY18", page)
        apply()
    }
}

fun loadPaginationValues(context: Context): Pair<Int, Int> {
    val sharedPreferences = context.getSharedPreferences("LeagueOfStatsPrefs", Context.MODE_PRIVATE)
    val size = sharedPreferences.getInt("SIZE_KEY18", 20)
    val page = sharedPreferences.getInt("PAGE_KEY18", 1)
    return Pair(size, page)
}


@Composable
fun ChampionsScreen() {
    var searchQuery by remember { mutableStateOf("") }
    val champions = remember { mutableStateOf(listOf<ChampionStats>()) }
    val context = LocalContext.current

    val (savedSize, savedPage) = loadPaginationValues(context)
    var size by remember { mutableStateOf(savedSize) }
    var page by remember { mutableStateOf(savedPage) }

    val listState = rememberLazyListState()

    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (champions.value.isEmpty() && !isLoaded) {
            fetchAllChampions(champions, context, size, page)
            isLoaded = true
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == listState.layoutInfo.totalItemsCount - 1
        }.collect { atEnd ->
            if (atEnd && size < 152) {
                size = minOf(size + 20, 152)
                page += 1

                fetchAllChampions(champions, context, size, page)
                Log.d("ChampionsScreen", "Carregando mais campeões. Tamanho atual: $size")

                savePaginationValues(context, size, page)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                )
            )
    ) {
        SearchBar(
            searchQuery = searchQuery,
            onQueryChanged = { searchQuery = it }
        )

        ChampionsList(champions = champions.value, listState = listState)
    }
}


@Composable
fun ChampionsList(champions: List<ChampionStats>, listState: LazyListState) {
    LazyColumn(state = listState) {
        items(champions) { champion ->
            ChampionCard(champion = champion, onClick = {})
        }
    }
}



fun translateText(text: String, targetLanguage: String): String {

    val apiKey = BuildConfig.GOOGLE_API_KEY

    val translate = TranslateOptions.newBuilder().setApiKey(apiKey).build().service

    val translation = translate.translate(
        text,
        Translate.TranslateOption.targetLanguage(targetLanguage)
    )
    return translation.translatedText
}