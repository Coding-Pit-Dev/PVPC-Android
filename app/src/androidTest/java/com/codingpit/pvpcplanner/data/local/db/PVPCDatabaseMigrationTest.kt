package com.codingpit.pvpcplanner.data.local.db

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PVPCDatabaseMigrationTest {
    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper =
        MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            PVPCDatabase::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory(),
        )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        var db =
            helper.createDatabase(TEST_DB, 1).apply {
                // Insert version 1 data
                execSQL("INSERT INTO devices (name, hours, icon) VALUES ('Washer', 2, 'washer_icon')")
                close()
            }

        // Run migration 1 -> 2
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)

        // Validate that the new column 'watts' exists and has default value 0
        val cursor = db.query("SELECT * FROM devices WHERE name = 'Washer'")
        assertTrue(cursor.moveToFirst())
        val wattsColumnIndex = cursor.getColumnIndex("watts")
        assertTrue(wattsColumnIndex != -1)
        assertEquals(0, cursor.getInt(wattsColumnIndex))
        cursor.close()
    }

    @Test
    @Throws(IOException::class)
    fun migrate2To3() {
        var db =
            helper.createDatabase(TEST_DB, 2).apply {
                // Insert version 2 data (includes watts)
                execSQL("INSERT INTO devices (name, hours, icon, watts) VALUES ('Oven', 1, 'oven_icon', 2000)")
                close()
            }

        // Run migration 2 -> 3
        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, MIGRATION_2_3)

        // Validate that the new column 'category' exists and has default value 'appliances'
        val cursor = db.query("SELECT * FROM devices WHERE name = 'Oven'")
        assertTrue(cursor.moveToFirst())
        val categoryColumnIndex = cursor.getColumnIndex("category")
        assertTrue(categoryColumnIndex != -1)
        assertEquals("appliances", cursor.getString(categoryColumnIndex))
        cursor.close()
    }

    @Test
    @Throws(IOException::class)
    fun migrate3To4() {
        var db =
            helper.createDatabase(TEST_DB, 3).apply {
                // Insert version 3 data (includes category)
                execSQL("INSERT INTO devices (name, hours, icon, watts, category) VALUES ('Fridge', 24, 'fridge_icon', 100, 'kitchen')")
                close()
            }

        // Run migration 3 -> 4
        db = helper.runMigrationsAndValidate(TEST_DB, 4, true, MIGRATION_3_4)

        // Validate that the new column 'notes' exists and is null by default (or as inserted if we could insert it, but we are migrating so it should be null)
        val cursor = db.query("SELECT * FROM devices WHERE name = 'Fridge'")
        assertTrue(cursor.moveToFirst())
        val notesColumnIndex = cursor.getColumnIndex("notes")
        assertTrue(notesColumnIndex != -1)
        assertTrue(cursor.isNull(notesColumnIndex))
        cursor.close()
    }
}
