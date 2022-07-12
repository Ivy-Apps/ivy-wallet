package com.ivy.wallet.io.persistence.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration115to116_Account_Include_In_Balance : Migration(115, 116) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE accounts ADD COLUMN includeInBalance INTEGER NOT NULL DEFAULT 1")
    }

}