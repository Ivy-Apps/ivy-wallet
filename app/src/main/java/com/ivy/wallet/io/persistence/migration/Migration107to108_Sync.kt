package com.ivy.wallet.io.persistence.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration107to108_Sync : Migration(107, 108) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.addSyncColumns("accounts")
        database.addSyncColumns("categories")
        database.addSyncColumns("settings")
        database.addSyncColumns("transactions")
    }

    private fun SupportSQLiteDatabase.addSyncColumns(tableName: String) {
        execSQL("ALTER TABLE $tableName ADD COLUMN isSynced INTEGER NOT NULL DEFAULT 0")
        execSQL("ALTER TABLE $tableName ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
    }
}