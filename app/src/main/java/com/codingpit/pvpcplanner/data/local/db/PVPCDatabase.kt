package com.codingpit.pvpcplanner.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codingpit.pvpcplanner.data.local.db.model.DeviceEntity
import com.codingpit.pvpcplanner.data.local.db.model.PVPCEntity

val MIGRATION_1_2 =
    object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE devices ADD COLUMN watts INTEGER NOT NULL DEFAULT 0")
        }
    }

val MIGRATION_2_3 =
    object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE devices ADD COLUMN category TEXT NOT NULL DEFAULT 'appliances'")
        }
    }

val MIGRATION_3_4 =
    object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE devices ADD COLUMN notes TEXT")
        }
    }

@Database(entities = [PVPCEntity::class, DeviceEntity::class], version = 4)
abstract class PVPCDatabase : RoomDatabase() {
    abstract fun pvpcDao(): PVPCDao
}
