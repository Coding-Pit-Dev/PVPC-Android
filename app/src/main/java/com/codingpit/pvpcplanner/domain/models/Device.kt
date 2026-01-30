package com.codingpit.pvpcplanner.domain.models

object DeviceCategory {
    const val APPLIANCES = "appliances"
    const val LIGHTING = "lighting"
    const val HVAC = "hvac"
    const val KITCHEN = "kitchen"
    const val LAUNDRY = "laundry"
    const val ENTERTAINMENT = "entertainment"
    const val COMPUTING = "computing"
    const val MOBILITY = "mobility"
    const val OTHER = "other"
}

data class Device(
    val id: Int = 0,
    val name: String,
    val hours: Int,
    val icon: String,
    val watts: Int = 0,
    val category: String = DeviceCategory.APPLIANCES,
    val notes: String? = null,
)
