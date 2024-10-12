package com.example.lol

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lol.ui.theme.LolTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class TierListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LolTheme {
                TierListScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TierListScreen() {
    val champions = remember { mutableStateOf(listOf<ChampionStats>()) }
    val sortedByDescending = remember { mutableStateOf(false) }
    val tiers = remember { mutableStateMapOf<String, Int>() }

    fetchAllChampions(champions, context = LocalContext.current)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Champion Tier List") },
                actions = {
                    Button(
                        onClick = {
                            sortedByDescending.value = !sortedByDescending.value
                        }
                    ) {
                        Text(if (sortedByDescending.value) "Sort Ascending" else "Sort Descending")
                    }
                }
            )
        }
    ) {
        val sortedChampions = champions.value.sortedWith { a, b ->
            val tierA = tiers.getOrPut(a.name) { Random.nextInt(1, 6) }
            val tierB = tiers.getOrPut(b.name) { Random.nextInt(1, 6) }
            if (sortedByDescending.value) tierB.compareTo(tierA) else tierA.compareTo(tierB)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 48.dp)
        ) {
            items(sortedChampions) { champion ->
                TierListItem(champion, tiers[champion.name] ?: 1)
                Spacer(modifier = Modifier
                    .height(16.dp)
                    )

            }
        }
    }
}

@Composable
fun TierListItem(champion: ChampionStats, tier: Int) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(champion.icon) {
        CoroutineScope(Dispatchers.IO).launch {
            bitmap = loadImageFromUrl(champion.icon)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "${champion.name} icon",
                    modifier = Modifier.size(64.dp),
                    contentScale = ContentScale.Crop
                )
            } ?: Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = champion.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = champion.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))


            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = when (tier) {
                            1 -> Color(0xFFFF0000)
                            2 -> Color(0xFFFFFF00)
                            3 -> Color(0xFF0000FF)
                            4 -> Color(0xFF00FF00)
                            5 -> Color(0xFFFFD700)
                            else -> Color.Gray
                        },
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$tier",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}





