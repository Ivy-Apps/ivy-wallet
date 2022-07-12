package com.ivy.wallet.io.persistence.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration109to110_PlannedPayments : Migration(109, 110) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE transaction_recurring_rules")

        database.execSQL("CREATE TABLE IF NOT EXISTS `transaction_recurring_rules` (`startDate` INTEGER, `intervalN` INTEGER, `intervalType` TEXT, `oneTime` INTEGER NOT NULL, `type` TEXT NOT NULL, `accountId` TEXT NOT NULL, `amount` REAL NOT NULL, `categoryId` TEXT, `title` TEXT, `description` TEXT, `isSynced` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, `id` TEXT NOT NULL, PRIMARY KEY(`id`))")
    }

    private fun SupportSQLiteDatabase.addSyncColumns(tableName: String) {
        execSQL("ALTER TABLE $tableName ADD COLUMN isSynced INTEGER NOT NULL DEFAULT 0")
        execSQL("ALTER TABLE $tableName ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
    }
}