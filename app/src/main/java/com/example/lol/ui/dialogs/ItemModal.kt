package com.example.lol.ui.dialogs

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.lol.R
import com.example.lol.data.models.ChampionIconModel
import com.example.lol.data.models.ItemsModel
import com.example.lol.ui.components.loadImageFromUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ItemModal(champion: ChampionIconModel, items: List<ItemsModel>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.items_for, champion.name) ,
                color = Color.White
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .background(Color.Black)
                    .padding(8.dp)
            ) {
                items.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        var bitmap by remember { mutableStateOf<Bitmap?>(null) }

                        LaunchedEffect(item.iconUrl) {
                            CoroutineScope(Dispatchers.IO).launch {
                                bitmap = loadImageFromUrl(item.iconUrl)
                            }
                        }

                        bitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "${item.name} Icon",
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(end = 8.dp),
                                contentScale = ContentScale.Crop
                            )
                        } ?: Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.Gray)
                        )

                        Text(
                            text = item.name,
                            color = Color.White
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar", color = Color.White)
            }
        },
        containerColor = Color.Black
    )
}
