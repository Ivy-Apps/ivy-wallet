package com.ivy.data.db.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Suppress("MagicNumber", "ClassNaming")
class Migration128to129_DeleteIsDeleted : Migration(128, 129) {
    override fun migrate(database: SupportSQLiteDatabase) {
        with(database) {
            deleteDeletedFrom(tableName = "accounts")
            deleteDeletedFrom(tableName = "budgets")
            deleteDeletedFrom(tableName = "categories")
            deleteDeletedFrom(tableName = "loan_records")
            deleteDeletedFrom(tableName = "loans")
            deleteDeletedFrom(tableName = "planned_payment_rules")
            deleteDeletedFrom(tableName = "settings")
            deleteDeletedFrom(tableName = "tags")
            deleteDeletedFrom(tableName = "tags_association")
            deleteDeletedFrom(tableName = "transactions")
        }
    }

    private fun SupportSQLiteDatabase.deleteDeletedFrom(tableName: String) {
        execSQL("DELETE FROM $tableName WHERE isDeleted = 1")
    }
}