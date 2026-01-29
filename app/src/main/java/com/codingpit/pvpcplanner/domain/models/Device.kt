package com.codingpit.pvpcplanner.domain.models

data class Device(
    val id: Int = 0,
    val name: String,
    val hours: Int,
    val icon: String,
    val watts: Int = 0,
    val category: String = "appliances",
    val notes: String? = null,
)
