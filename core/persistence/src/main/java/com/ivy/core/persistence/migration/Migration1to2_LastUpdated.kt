package com.ivy.core.persistence.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration1to2_LastUpdated : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        val tables = setOf(
            "accounts",
            "account_folders",
            "attachments",
            "categories",
            "exchange_rates_override",
            "tags",
            "transactions",
            "trn_links",
            "trn_metadata",
            "trn_tags",
        )
        tables.forEach {
            database.addLastUpdated(tableName = it)
        }
    }

    private fun SupportSQLiteDatabase.addLastUpdated(tableName: String) {
        execSQL("ALTER TABLE $tableName ADD COLUMN last_updated INTEGER NOT NULL DEFAULT 0")
    }
}