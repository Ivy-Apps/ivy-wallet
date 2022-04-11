package com.ivy.wallet.io.persistence.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration112to113_ExchangeRates : Migration(112, 113) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `exchange_rates` (`baseCurrency` TEXT NOT NULL, `currency` TEXT NOT NULL, `rate` REAL NOT NULL, PRIMARY KEY(`baseCurrency`, `currency`))")
    }

}