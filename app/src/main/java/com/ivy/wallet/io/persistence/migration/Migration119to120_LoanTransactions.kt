package com.ivy.wallet.io.persistence.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration119to120_LoanTransactions : Migration(119, 120) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE transactions ADD COLUMN loanId TEXT")
        database.execSQL("ALTER TABLE transactions ADD COLUMN loanRecordId TEXT")

        database.execSQL("ALTER TABLE loan_records ADD COLUMN interest INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE loan_records ADD COLUMN accountId TEXT")
        database.execSQL("ALTER TABLE loan_records ADD COLUMN convertedAmount REAL")
        database.execSQL("ALTER TABLE loans ADD COLUMN accountId TEXT")
    }
}