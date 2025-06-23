package com.codingpit.pvpcplanner.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Battery5Bar
import androidx.compose.material.icons.filled.Blender
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CoffeeMaker
import androidx.compose.material.icons.filled.DesktopMac
import androidx.compose.material.icons.filled.DryCleaning
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.ElectricMoped
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Iron
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Microwave
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.WindPower
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.codingpit.pvpcplanner.R

object Constants {
    const val HOME_SCREEN = "Prices"
    const val DEVICES_SCREEN = "Devices"
    const val SETTINGS_SCREEN = "Settings"

    val ROUTES_NAVIGATION_BOTTOM_BAR =
        mapOf(
            HOME_SCREEN to Icons.Default.Paid,
            DEVICES_SCREEN to Icons.Default.PowerSettingsNew,
            SETTINGS_SCREEN to Icons.Default.Settings,
        )
}

@Composable
fun getIcons(): Map<String, ImageVector> =
    mapOf(
        "Fan" to Icons.Default.WindPower,
        "Battery" to Icons.Default.Battery5Bar,
        "Lightbulb" to Icons.Default.Lightbulb,
        "Electric Car" to Icons.Default.ElectricCar,
        "Electric Motorbike" to Icons.Default.ElectricMoped,
        "Scooter" to Icons.Default.ElectricScooter,
        "Electric bike" to Icons.Default.ElectricBike,
        "Camera" to Icons.Default.Camera,
        "Dry machine" to Icons.Default.DryCleaning,
        "Phone" to Icons.Default.PhoneIphone,
        "Laptop" to Icons.Default.Laptop,
        "Desktop" to Icons.Default.DesktopMac,
        "Tv" to Icons.Default.Tv,
        "Default" to Icons.Default.Power,
        "Console" to Icons.Default.Gamepad,
        "Fridge" to Icons.Default.Kitchen,
        "Vitro" to ImageVector.vectorResource(R.drawable.ic_vitro),
        "Coffee Machine" to Icons.Default.CoffeeMaker,
        "Blender" to Icons.Default.Blender,
        "Microwave" to Icons.Default.Microwave,
        "Oven" to ImageVector.vectorResource(R.drawable.ic_oven),
        "Dishwasher" to ImageVector.vectorResource(R.drawable.ic_dishwasher),
        "Air Conditioner" to Icons.Default.Air,
        "Iron" to Icons.Default.Iron,
        "Laundry" to Icons.Default.LocalLaundryService,
    )
