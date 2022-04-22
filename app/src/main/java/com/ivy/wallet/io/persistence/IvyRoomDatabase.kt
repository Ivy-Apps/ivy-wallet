package com.ivy.wallet.io.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ivy.wallet.domain.data.entity.*
import com.ivy.wallet.io.persistence.dao.*
import com.ivy.wallet.io.persistence.migration.*


@Database(
    entities = [
        Account::class, Transaction::class, Category::class,
        Settings::class, PlannedPaymentRule::class,
        User::class, ExchangeRate::class, Budget::class, Loan::class,
        LoanRecord::class
    ],
    version = 120,
    exportSchema = true
)
@TypeConverters(RoomTypeConverters::class)
abstract class IvyRoomDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao

    abstract fun transactionDao(): TransactionDao

    abstract fun categoryDao(): CategoryDao

    abstract fun budgetDao(): BudgetDao

    abstract fun plannedPaymentRuleDao(): PlannedPaymentRuleDao

    abstract fun settingsDao(): SettingsDao

    abstract fun userDao(): UserDao

    abstract fun exchangeRatesDao(): ExchangeRateDao

    abstract fun loanDao(): LoanDao

    abstract fun loanRecordDao(): LoanRecordDao

    companion object {
        const val DB_NAME = "ivywallet.db"

        fun create(applicationContext: Context): IvyRoomDatabase {
            return Room
                .databaseBuilder(
                    applicationContext,
                    IvyRoomDatabase::class.java, DB_NAME
                )
                .addMigrations(
                    Migration105to106_TrnRecurringRules(),
                    Migration106to107_Wishlist(),
                    Migration107to108_Sync(),
                    Migration108to109_Users(),
                    Migration109to110_PlannedPayments(),
                    Migration110to111_PlannedPaymentRule(),
                    Migration111to112_User_testUser(),
                    Migration112to113_ExchangeRates(),
                    Migration113to114_Multi_Currency(),
                    Migration114to115_Category_Account_Icons(),
                    Migration115to116_Account_Include_In_Balance(),
                    Migration116to117_SalteEdgeIntgration(),
                    Migration117to118_Budgets(),
                    Migration118to119_Loans(),
                    Migration119to120_LoanTransactions(),
                    Migration120to121_DropWishlistItem()
                )
                .build()
        }
    }

    fun reset() {
        accountDao().deleteAll()
        transactionDao().deleteAll()
        categoryDao().deleteAll()
        settingsDao().deleteAll()
        plannedPaymentRuleDao().deleteAll()
        userDao().deleteAll()
        budgetDao().deleteAll()
        loanDao().deleteAll()
        loanRecordDao().deleteAll()
    }
}