package com.ivy.data.db

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class IvyRoomDatabaseMigrationTest {

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        IvyRoomDatabase::class.java,
        listOf(IvyRoomDatabase.DeleteSEMigration()),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        // Create earliest version of the database:
        // for Ivy Wallet versions below 106 are broken :/
        helper.createDatabase(TestDb, 106).apply {
            close()
        }

        // Open latest version of the database.
        // Room validates and executes all migrations.
        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            IvyRoomDatabase::class.java,
            TestDb
        ).addMigrations(*IvyRoomDatabase.migrations()).build().apply {
            openHelper.writableDatabase.close()
        }
    }

    companion object {
        private const val TestDb = "migration-test"
    }
}