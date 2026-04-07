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
    private val testDb = "migration-test"

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
            helper.createDatabase(testDb, 1).apply {
                // Insert version 1 data
                execSQL("INSERT INTO devices (name, hours, icon) VALUES ('Washer', 2, 'washer_icon')")
                close()
            }

        // Run migration 1 -> 2
        db = helper.runMigrationsAndValidate(testDb, 2, true, MIGRATION_1_2)

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
            helper.createDatabase(testDb, 2).apply {
                // Insert version 2 data (includes watts)
                execSQL("INSERT INTO devices (name, hours, icon, watts) VALUES ('Oven', 1, 'oven_icon', 2000)")
                close()
            }

        // Run migration 2 -> 3
        db = helper.runMigrationsAndValidate(testDb, 3, true, MIGRATION_2_3)

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
            helper.createDatabase(testDb, 3).apply {
                // Insert version 3 data (includes category)
                execSQL("INSERT INTO devices (name, hours, icon, watts, category) VALUES ('Fridge', 24, 'fridge_icon', 100, 'kitchen')")
                close()
            }

        // Run migration 3 -> 4
        db = helper.runMigrationsAndValidate(testDb, 4, true, MIGRATION_3_4)

        // Validate that the new column 'notes' exists and is null by default (or as inserted if we could insert it, but we are migrating so it should be null)
        val cursor = db.query("SELECT * FROM devices WHERE name = 'Fridge'")
        assertTrue(cursor.moveToFirst())
        val notesColumnIndex = cursor.getColumnIndex("notes")
        assertTrue(notesColumnIndex != -1)
        assertTrue(cursor.isNull(notesColumnIndex))
        cursor.close()
    }

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        helper.createDatabase(testDb, 1).apply {
            execSQL("INSERT INTO devices (name, hours, icon) VALUES ('Washer', 2, 'washer_icon')")
            close()
        }

        // Run all migrations
        val db =
            helper.runMigrationsAndValidate(
                testDb,
                4,
                true,
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
            )

        // Validate final schema has all columns
        val cursor = db.query("SELECT * FROM devices WHERE name = 'Washer'")
        assertTrue(cursor.moveToFirst())
        val wattsIndex = cursor.getColumnIndex("watts")
        val categoryIndex = cursor.getColumnIndex("category")
        val notesIndex = cursor.getColumnIndex("notes")
        assertTrue("watts column should exist", wattsIndex != -1)
        assertTrue("category column should exist", categoryIndex != -1)
        assertTrue("notes column should exist", notesIndex != -1)
        assertEquals(0, cursor.getInt(wattsIndex))
        assertEquals("appliances", cursor.getString(categoryIndex))
        assertTrue(cursor.isNull(notesIndex))
        cursor.close()
    }
}
