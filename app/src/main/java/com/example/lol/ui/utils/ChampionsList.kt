package com.example.lol.ui.utils

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.example.lol.data.models.ChampionStats
import com.example.lol.ui.components.ChampionCard

@Composable
fun ChampionsList(champions: List<ChampionStats>, listState: LazyListState) {
    LazyColumn(state = listState) {
        items(champions) { champion ->
            ChampionCard(champion = champion, onClick = {})
        }
    }
}