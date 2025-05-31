package com.codingpit.pvpcplanner.domain.models

data class PVPCModel(
    val day: String,
    val startHour: Int,
    val endHour: Int,
    val pcb: Double,
    val cym: Double,
)