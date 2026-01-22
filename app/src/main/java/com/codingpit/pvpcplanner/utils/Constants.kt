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

data class DeviceIcon(
    val id: String,
    val icon: ImageVector,
    val labelRes: Int
)

@Composable
fun getDeviceIcons(): List<DeviceIcon> =
    listOf(
        DeviceIcon("Fan", Icons.Default.WindPower, R.string.device_fan),
        DeviceIcon("Battery", Icons.Default.Battery5Bar, R.string.device_battery),
        DeviceIcon("Lightbulb", Icons.Default.Lightbulb, R.string.device_lightbulb),
        DeviceIcon("Electric Car", Icons.Default.ElectricCar, R.string.device_electric_car),
        DeviceIcon(
            "Electric Motorbike",
            Icons.Default.ElectricMoped,
            R.string.device_electric_motorbike
        ),
        DeviceIcon("Scooter", Icons.Default.ElectricScooter, R.string.device_scooter),
        DeviceIcon("Electric bike", Icons.Default.ElectricBike, R.string.device_electric_bike),
        DeviceIcon("Camera", Icons.Default.Camera, R.string.device_camera),
        DeviceIcon("Dry machine", Icons.Default.DryCleaning, R.string.device_dry_machine),
        DeviceIcon("Phone", Icons.Default.PhoneIphone, R.string.device_phone),
        DeviceIcon("Laptop", Icons.Default.Laptop, R.string.device_laptop),
        DeviceIcon("Desktop", Icons.Default.DesktopMac, R.string.device_desktop),
        DeviceIcon("Tv", Icons.Default.Tv, R.string.device_tv),
        DeviceIcon("Default", Icons.Default.Power, R.string.device_default),
        DeviceIcon("Console", Icons.Default.Gamepad, R.string.device_console),
        DeviceIcon("Fridge", Icons.Default.Kitchen, R.string.device_fridge),
        DeviceIcon("Vitro", ImageVector.vectorResource(R.drawable.ic_vitro), R.string.device_vitro),
        DeviceIcon("Coffee Machine", Icons.Default.CoffeeMaker, R.string.device_coffee_machine),
        DeviceIcon("Blender", Icons.Default.Blender, R.string.device_blender),
        DeviceIcon("Microwave", Icons.Default.Microwave, R.string.device_microwave),
        DeviceIcon("Oven", ImageVector.vectorResource(R.drawable.ic_oven), R.string.device_oven),
        DeviceIcon(
            "Dishwasher",
            ImageVector.vectorResource(R.drawable.ic_dishwasher),
            R.string.device_dishwasher
        ),
        DeviceIcon("Air Conditioner", Icons.Default.Air, R.string.device_air_conditioner),
        DeviceIcon("Iron", Icons.Default.Iron, R.string.device_iron),
        DeviceIcon("Laundry", Icons.Default.LocalLaundryService, R.string.device_laundry),
    )

@Composable
fun getIcons(): Map<String, ImageVector> =
    getDeviceIcons().associate { it.id to it.icon }
