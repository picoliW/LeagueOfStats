package com.example.lol.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.lol.ui.components.loadImageFromUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DisplayImg(url: String, name: String) {
    var teleportBitmap by remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            teleportBitmap = loadImageFromUrl(url)
        }
    }

    teleportBitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = name,
            modifier = Modifier.size(64.dp),
            contentScale = ContentScale.Crop
        )
    }
}
