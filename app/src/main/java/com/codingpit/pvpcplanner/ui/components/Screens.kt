package com.codingpit.pvpcplanner.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldScreen(
    modifier: Modifier = Modifier,
    title: String? = null,
    fab: @Composable () -> Unit = {},
    fabPosition: FabPosition = FabPosition.End,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            if (title != null) {
                TopAppBar(title = { Text(title) })
            }
        },
        floatingActionButton = fab,
        floatingActionButtonPosition = fabPosition,
        bottomBar = bottomBar
    ) {
        Surface(modifier = modifier.padding(it), content = content)
    }
}
