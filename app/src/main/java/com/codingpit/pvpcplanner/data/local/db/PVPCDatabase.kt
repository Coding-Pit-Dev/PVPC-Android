package com.codingpit.pvpcplanner.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.codingpit.pvpcplanner.data.local.db.model.DeviceEntity
import com.codingpit.pvpcplanner.data.local.db.model.PVPCEntity

@Database(entities = [PVPCEntity::class, DeviceEntity::class], version = 1)
abstract class PVPCDatabase : RoomDatabase() {
    abstract fun pvpcDao(): PVPCDao
}