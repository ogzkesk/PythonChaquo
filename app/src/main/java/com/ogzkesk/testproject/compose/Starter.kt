package com.ogzkesk.testproject.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage


const val NATURE =
    "https://static.vecteezy.com/system/resources/thumbnails/025/284/015/small_2x/close-up-growing-beautiful-forest-in-glass-ball-and-flying-butterflies-in-nature-outdoors-spring-season-concept-generative-ai-photo.jpg"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Starter(
    onNavigateSecond: () -> Unit,
    viewModel: GraphViewModel,
) {

    val scrollState = rememberScrollState()
    val deneme by viewModel.deneme.collectAsState()
    LaunchedEffect(key1 = Unit, block = {
        println("starter deneme :: $deneme")
    })

    Scaffold { padd ->
        Column(
            modifier = Modifier
                .padding(padd)
                .verticalScroll(scrollState),
        ) {
            (0..30).forEach {
                RowContainer(onNavigateSecond)
                Divider()
            }
        }
    }
}


@Composable
private fun RowContainer(onNavigateSecond: () -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateSecond() }
            .padding(32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = NATURE,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Text(
            text = "NATURE",
            style = MaterialTheme.typography.titleLarge
                .copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

