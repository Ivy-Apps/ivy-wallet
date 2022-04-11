package com.ivy.wallet.io.persistence.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration114to115_Category_Account_Icons : Migration(114, 115) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE accounts ADD COLUMN icon TEXT")
        database.execSQL("ALTER TABLE categories ADD COLUMN icon TEXT")
    }

}