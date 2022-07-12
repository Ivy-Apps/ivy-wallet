package com.ivy.wallet.io.persistence.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration118to119_Loans : Migration(118, 119) {
    companion object {
        private const val LOANS_TABLE = "loans"
        private const val LOAN_RECORDS_TABLE = "loan_records"
    }

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `$LOANS_TABLE` (`name` TEXT NOT NULL, `amount` REAL NOT NULL, `type` TEXT NOT NULL, `color` INTEGER NOT NULL, `icon` TEXT, `orderNum` REAL NOT NULL, `isSynced` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, `id` TEXT NOT NULL, PRIMARY KEY(`id`))")

        database.execSQL("CREATE TABLE IF NOT EXISTS `$LOAN_RECORDS_TABLE` (`loanId` TEXT NOT NULL, `amount` REAL NOT NULL, `note` TEXT, `dateTime` INTEGER NOT NULL, `isSynced` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, `id` TEXT NOT NULL, PRIMARY KEY(`id`))")
    }

}