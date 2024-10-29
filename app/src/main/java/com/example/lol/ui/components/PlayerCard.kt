package com.example.lol.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.lol.R

@Composable
fun PlayerCard(player: ParticipantData, championIcons: Map<String, Bitmap?>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    championIcons[player.championName]?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "${player.championName} Icon",
                            modifier = Modifier.size(40.dp),
                            contentScale = ContentScale.Crop
                        )
                    } ?: Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    )

                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color.White, shape = MaterialTheme.shapes.small)
                            .align(Alignment.BottomStart),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = player.champLevel.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                val kda = "${player.kills}/${player.deaths}/${player.assists}"
                Column {
                    Text(
                        text = player.riotIdGameName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                    Text(
                        text = player.championName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Text(
                        text = kda,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            val positionIconRes = when (player.individualPosition) {
                "TOP" -> R.drawable.lane1_top
                "JUNGLE" -> R.drawable.lane2_jg
                "MIDDLE" -> R.drawable.lane3_mid
                "BOTTOM" -> R.drawable.lane4_adc
                "UTILITY" -> R.drawable.lane5_sup
                else -> R.drawable.teste222
            }

            Image(
                painter = painterResource(id = positionIconRes),
                contentDescription = "${player.individualPosition} Icon",
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}


