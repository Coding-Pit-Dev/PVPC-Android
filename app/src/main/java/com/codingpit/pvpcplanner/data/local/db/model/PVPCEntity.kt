package com.codingpit.pvpcplanner.data.local.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pvpc_entries")
data class PVPCEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "day") val day: String,
    @ColumnInfo(name = "startHour") val startHour: Int,
    @ColumnInfo(name = "endHour") val endHour: Int,
    @ColumnInfo(name = "pcb") val pcb: Double,
    @ColumnInfo(name = "cym") val cym: Double,
)
