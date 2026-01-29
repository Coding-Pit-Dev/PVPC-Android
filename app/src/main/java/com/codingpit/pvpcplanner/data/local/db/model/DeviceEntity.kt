package com.codingpit.pvpcplanner.data.local.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val hours: Int,
    val icon: String,
    val watts: Int = 0,
    val category: String = "appliances",
    val notes: String? = null,
)
