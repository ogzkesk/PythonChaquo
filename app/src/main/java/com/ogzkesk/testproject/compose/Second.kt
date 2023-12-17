package com.ogzkesk.testproject.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Second(
    onPopBackstack: () -> Unit,
    onNavigateToThird: () -> Unit,
    viewModel: GraphViewModel,
) {

//    val deneme by viewModel.deneme.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Second") },
                navigationIcon = {
                    IconButton(onClick = {
                        onPopBackstack()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padd ->

        Column(
            modifier = Modifier.padding(padd),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            AsyncImage(
                model = NATURE,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .padding(64.dp)
                    .clip(shape = RoundedCornerShape(24.dp))
                    .fillMaxWidth()
                    .height(150.dp)

            )

            Text(
                text = "Nature",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
            )

            Button(onClick = onNavigateToThird) {
                Text(text = "Navigate Third")
            }
        }
    }
}