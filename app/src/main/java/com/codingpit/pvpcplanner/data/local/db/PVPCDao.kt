package com.codingpit.pvpcplanner.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.codingpit.pvpcplanner.data.local.db.model.DeviceEntity
import com.codingpit.pvpcplanner.data.local.db.model.PVPCEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PVPCDao {
    @Query("SELECT * FROM pvpc_entries")
    fun getAll(): List<PVPCEntity>

    @Query("SELECT * FROM pvpc_entries WHERE day LIKE :date")
    fun getPrices(date: String): List<PVPCEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(pvpcEntities: List<PVPCEntity>)

    @Delete
    fun delete(pvpcEntity: PVPCEntity)

    @Query("SELECT * FROM devices")
    fun getDevices(): Flow<List<DeviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(deviceEntity: DeviceEntity)

    @Delete
    suspend fun deleteDevice(deviceEntity: DeviceEntity)

    @Update
    suspend fun updateDevice(deviceEntity: DeviceEntity)
}
