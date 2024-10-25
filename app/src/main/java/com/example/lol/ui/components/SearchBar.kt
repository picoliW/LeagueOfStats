package com.example.lol.ui.components

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.res.stringResource
import com.example.lol.R

@Composable
fun SearchBar(searchQuery: String, onQueryChanged: (String) -> Unit) {
    TextField(
        value = searchQuery,
        onValueChange = onQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = {
            Text(text = stringResource(id = R.string.browse_champions_search))
        }
    )
}
