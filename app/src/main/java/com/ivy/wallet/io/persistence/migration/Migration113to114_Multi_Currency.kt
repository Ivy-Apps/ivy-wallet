package com.ivy.wallet.io.persistence.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration113to114_Multi_Currency : Migration(113, 114) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE accounts ADD COLUMN currency TEXT")
        database.execSQL("ALTER TABLE transactions ADD COLUMN toAmount REAL")
    }

}