package com.ivy.data.db.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Suppress("MagicNumber", "ClassNaming")
class Migration126to127_LoanRecordType : Migration(126, 127) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE loan_records ADD COLUMN loanRecordType TEXT NOT NULL DEFAULT 'DECREASE'")
    }
}