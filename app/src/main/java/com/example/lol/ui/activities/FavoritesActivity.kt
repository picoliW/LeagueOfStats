package com.example.lol.ui.activities

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lol.R
import com.example.lol.data.models.ChampionStats
import com.example.lol.ui.components.ChampionCard
import com.example.lol.ui.theme.LolTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.lol.data.database.ChampionDatabase
import com.example.lol.data.models.Sprite
import com.example.lol.data.models.Stats
import com.example.lol.ui.utils.FavoriteChampionsList
import com.example.lol.ui.utils.NoFavoritesMessage

class FavoriteChampionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LolTheme {
                val favoriteChampions = remember { mutableStateOf(listOf<ChampionStats>()) }
                fetchFavoriteChampions(favoriteChampions, context = LocalContext.current)

                FavoriteChampionsScreen(favoriteChampions.value)
            }
        }
    }
}

fun fetchFavoriteChampions(favoriteChampions: MutableState<List<ChampionStats>>, context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        val db = ChampionDatabase.getDatabase(context)
        val championDao = db.championDao()

        val cachedChampions = championDao.getAllChampions().filter { it.isFavorited }

        withContext(Dispatchers.Main) {
            favoriteChampions.value = cachedChampions.map {
                ChampionStats(
                    id = it.id,
                    key = it.key,
                    name = it.name,
                    title = it.title,
                    tags = it.tags.split(","),
                    stats = Stats(
                        hp = it.hp,
                        hpperlevel = it.hpperlevel,
                        mp = it.mp,
                        mpperlevel = it.mpperlevel,
                        movespeed = it.movespeed,
                        armor = it.armor,
                        armorperlevel = it.armorperlevel,
                        spellblock = it.spellblock,
                        spellblockperlevel = it.spellblockperlevel,
                        attackrange = it.attackrange,
                        hpregen = it.hpregen,
                        hpregenperlevel = it.hpregenperlevel,
                        mpregen = it.mpregen,
                        mpregenperlevel = it.mpregenperlevel,
                        crit = it.crit,
                        critperlevel = it.critperlevel,
                        attackdamage = it.attackdamage,
                        attackdamageperlevel = it.attackdamageperlevel,
                        attackspeedperlevel = it.attackspeedperlevel,
                        attackspeed = it.attackspeed
                    ),
                    icon = it.icon,
                    sprite = Sprite(
                        url = it.spriteUrl,
                        x = it.spriteX,
                        y = it.spriteY
                    ),
                    description = it.description,
                    isFavorited = it.isFavorited
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteChampionsScreen(favoriteChampions: List<ChampionStats>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.favorites_topbar)) },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                    )
                )
        ) {
            if (favoriteChampions.isEmpty()) {
                NoFavoritesMessage()
            } else {
                FavoriteChampionsList(favoriteChampions)
            }
        }
    }
}





