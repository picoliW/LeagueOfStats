package com.example.lol.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.lol.data.models.ChampionStats
import com.example.lol.repository.fetchAllChampions
import com.example.lol.ui.components.SearchBar
import com.example.lol.ui.theme.LolTheme
import com.example.lol.ui.utils.ChampionsList
import com.example.lol.ui.utils.loadPaginationValues
import com.example.lol.ui.utils.savePaginationValues


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
