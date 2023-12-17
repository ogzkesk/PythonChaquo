package com.ogzkesk.testproject.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThirdScreenScaffold(
    viewModel: GraphViewModel,
    onPopBackstack: () -> Unit,
    content: @Composable (PaddingValues, state: AnimContent) -> Unit,
) {

    AnimatedContent(
        label = "",
        targetState = viewModel.state,
        transitionSpec = {
            if (targetState > initialState) {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(600),
                ) togetherWith fadeOut(
                    animationSpec = tween(600)
                )
            } else {
                fadeIn(
                    animationSpec = tween(600),
                ) togetherWith slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(600)
                )

            }
        },
        content = { state ->
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = state.name) },
                        navigationIcon = {
                            IconButton(onClick = {
                                if (state == AnimContent.FIRST) {
                                    onPopBackstack()
                                } else {
                                    val newState = AnimContent.values()[state.ordinal - 1]
                                    viewModel.onStateChanged(newState)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                },
                content = { padd ->
                    content(padd, state)
                }
            )
        }
    )
}


@Composable
fun Third(
    viewModel: GraphViewModel,
    onPopBackstack: () -> Unit,
) {


    ThirdScreenScaffold(
        viewModel = viewModel,
        onPopBackstack = onPopBackstack
    ) { padd, state ->

        Column(
            modifier = Modifier.padding(padd),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            InnerContent(
                modifier = Modifier.weight(1f),
                page = state,
                thirdValue = viewModel.thirdValue,
                secondValue = viewModel.secondValue,
                onSecondValueChanged = viewModel::onSecondValueChanged,
                onThirdValueChanged = viewModel::onThirdValueChanged
            )


            Button(
                modifier = Modifier.padding(bottom = 32.dp),
                onClick = {

                    val newState = if (state == AnimContent.SIXTH) {
                        AnimContent.FIRST
                    } else {
                        AnimContent.values()[state.ordinal + 1]
                    }

                    viewModel.onStateChanged(newState)
                },
                content = {
                    Text(text = if (state == AnimContent.SIXTH) "Previous" else "Next")
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InnerContent(
    modifier: Modifier = Modifier,
    page: AnimContent,
    secondValue: String,
    onSecondValueChanged: (String) -> Unit,
    thirdValue: String,
    onThirdValueChanged: (String) -> Unit,
) {

    when (page) {
        AnimContent.FIRST -> {
            Box(
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(randomColorList.random()),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = page.name,
                    style = MaterialTheme.typography.headlineLarge
                        .copy(fontWeight = FontWeight.Black)
                )
            }

        }

        AnimContent.SECOND -> {
            Column(
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(randomColorList.random()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(value = secondValue, onValueChange = onSecondValueChanged)
                Text(
                    text = page.name,
                    style = MaterialTheme.typography.headlineLarge
                        .copy(fontWeight = FontWeight.Black)
                )
            }
        }

        AnimContent.THIRD -> {
            Column(
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(randomColorList.random()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(value = thirdValue, onValueChange = onThirdValueChanged)

                Text(
                    text = page.name,
                    style = MaterialTheme.typography.headlineLarge
                        .copy(fontWeight = FontWeight.Black)
                )
            }
        }

        AnimContent.FOURTH -> {
            Box(
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(randomColorList.random()),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = page.name,
                    style = MaterialTheme.typography.headlineLarge
                        .copy(fontWeight = FontWeight.Black)
                )
            }
        }

        AnimContent.FIFTH -> {
            Box(
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(randomColorList.random()),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = page.name,
                    style = MaterialTheme.typography.headlineLarge
                        .copy(fontWeight = FontWeight.Black)
                )
            }
        }

        AnimContent.SIXTH -> {
            Box(
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(randomColorList.random()),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = page.name,
                    style = MaterialTheme.typography.headlineLarge
                        .copy(fontWeight = FontWeight.Black)
                )
            }
        }
    }


}

val randomColorList by lazy {
    listOf(
        Color.Black,
        Color.Blue,
        Color.Cyan,
        Color.Gray,
        Color.DarkGray,
        Color.Green,
        Color.Magenta,
        Color.Red,
        Color.Yellow,
    )
}
