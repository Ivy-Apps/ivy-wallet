package com.ivy.db_persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ivy.db_persistence.dao.*
import com.ivy.db_persistence.data.*

@Database(
    entities = [
        AccountEntity::class, TransactionEntity::class, CategoryEntity::class,
        PlannedPaymentRuleEntity::class,
        UserEntity::class, BudgetEntity::class,
        LoanEntity::class, LoanRecordEntity::class
    ],
    version = 1,
    exportSchema = false //TODO: Fix that
)
@TypeConverters(RoomTypeConverters::class)
abstract class IvyRoomDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao

    abstract fun transactionDao(): TransactionDao

    abstract fun categoryDao(): CategoryDao

    abstract fun budgetDao(): BudgetDao

    abstract fun plannedPaymentRuleDao(): PlannedPaymentRuleDao

    abstract fun userDao(): UserDao

    abstract fun loanDao(): LoanDao

    abstract fun loanRecordDao(): LoanRecordDao

    companion object {
        const val DB_NAME = "db_persistence.db"

        fun create(applicationContext: Context): IvyRoomDatabase {
            return Room
                .databaseBuilder(
                    applicationContext,
                    IvyRoomDatabase::class.java, DB_NAME
                )
                .build()
        }
    }

    suspend fun reset() {
        accountDao().deleteAll()
        transactionDao().deleteAll()
        categoryDao().deleteAll()
        plannedPaymentRuleDao().deleteAll()
        userDao().deleteAll()
        budgetDao().deleteAll()
        loanDao().deleteAll()
        loanRecordDao().deleteAll()
    }
}