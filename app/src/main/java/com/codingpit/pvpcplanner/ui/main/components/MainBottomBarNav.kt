package com.codingpit.pvpcplanner.ui.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codingpit.pvpcplanner.utils.Constants.ROUTES_NAVIGATION_BOTTOM_BAR

@Composable
fun MainBottomBarNav(
    selectedScreen: String,
    onScreenSelected: (String) -> Unit,
    iconSize: Int = 22,
    fontSize: Int = 14,
) {
    NavigationBar(
        modifier = Modifier.background(Color.Red),
    ) {
        ROUTES_NAVIGATION_BOTTOM_BAR.forEach { screen ->
            NavigationBarItem(
                selected = selectedScreen == screen.key,
                onClick = { onScreenSelected(screen.key) },
                icon = {
                    Icon(
                        imageVector = screen.value,
                        contentDescription = screen.key,
                        modifier = Modifier.size(iconSize.dp),
                    )
                },
                label = {
                    Text(
                        text = screen.key,
                        fontSize = fontSize.sp,
                    )
                },
            )
        }
    }
}

@Preview
@Composable
private fun BottomBar_Preview() {
    MainBottomBarNav(
        selectedScreen = "HomeScreen",
        onScreenSelected = {},
    )
}
