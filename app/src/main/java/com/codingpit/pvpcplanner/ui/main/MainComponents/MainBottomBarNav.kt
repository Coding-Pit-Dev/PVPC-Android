package com.codingpit.pvpcplanner.ui.main.MainComponents

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
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

@Composable
fun MainBottomBarNav(
    selectedScreen: String,
    onScreenSelected: (String) -> Unit,
    selectedIconColor: Color,
    unselectedIconColor: Color,
    iconSize: Int = 22,
    fontSize: Int = 14,
) {
    NavigationBar{
        NavigationBarItem(
            selected = selectedScreen == "HomeScreen",
            onClick = { onScreenSelected("HomeScreen") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "icon home",
                    modifier = Modifier.size(iconSize.dp),
                    tint = if (selectedScreen == "HomeScreen") selectedIconColor else unselectedIconColor
                )
            },
            label = { Text(
                text = "Home",
                fontSize = fontSize.sp,
            ) }
        )
        NavigationBarItem(
            selected = selectedScreen == "NotificationScreen",
            onClick = { onScreenSelected("NotificationScreen") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "icon notification",
                    modifier = Modifier.size(iconSize.dp),
                    tint = if (selectedScreen == "NotificationScreen") selectedIconColor else unselectedIconColor
                )
            },
            label = { Text(
                text = "Notification",
                fontSize = fontSize.sp,
            ) }
        )
        NavigationBarItem(
            selected = selectedScreen == "SettingScreen",
            onClick = { onScreenSelected("SettingScreen") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "icon setting",
                    modifier = Modifier.size(iconSize.dp),
                    tint = if (selectedScreen == "SettingScreen") selectedIconColor else unselectedIconColor
                )
            },
            label = { Text(
                text = "Setting",
                fontSize = fontSize.sp,
            ) }
        )
    }
}

@Preview
@Composable
private fun BottomBar_Preview() {
    MainBottomBarNav(
        selectedScreen = "HomeScreen",
        onScreenSelected = {},
        selectedIconColor = Color.Black,
        unselectedIconColor = Color.Black,
    )
}
