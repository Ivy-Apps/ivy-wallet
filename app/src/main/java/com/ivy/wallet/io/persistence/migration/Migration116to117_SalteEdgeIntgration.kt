package com.ivy.wallet.io.persistence.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration116to117_SalteEdgeIntgration : Migration(116, 117) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE accounts ADD COLUMN seAccountId TEXT")
        database.execSQL("ALTER TABLE categories ADD COLUMN seCategoryName TEXT")
        database.execSQL("ALTER TABLE transactions ADD COLUMN seTransactionId TEXT")
        database.execSQL("ALTER TABLE transactions ADD COLUMN seAutoCategoryId TEXT")
    }

}