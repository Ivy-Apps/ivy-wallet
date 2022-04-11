package com.ivy.wallet.io.persistence.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration110to111_PlannedPaymentRule : Migration(110, 111) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE transaction_recurring_rules")

        database.execSQL("CREATE TABLE IF NOT EXISTS `planned_payment_rules` (`startDate` INTEGER, `intervalN` INTEGER, `intervalType` TEXT, `oneTime` INTEGER NOT NULL, `type` TEXT NOT NULL, `accountId` TEXT NOT NULL, `amount` REAL NOT NULL, `categoryId` TEXT, `title` TEXT, `description` TEXT, `isSynced` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, `id` TEXT NOT NULL, PRIMARY KEY(`id`))")
    }

}