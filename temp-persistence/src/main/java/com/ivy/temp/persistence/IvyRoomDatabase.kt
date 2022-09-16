package com.ivy.wallet.io.persistence

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.ivy.temp.persistence.ExchangeRateDao
import com.ivy.temp.persistence.ExchangeRateEntity
import com.ivy.wallet.io.persistence.dao.*
import com.ivy.wallet.io.persistence.data.*
import com.ivy.wallet.io.persistence.migration.*


@Deprecated("don't use! it'll be deleted after data migration")
@Database(
    entities = [
        AccountEntity::class, TransactionEntity::class, CategoryEntity::class,
        SettingsEntity::class, PlannedPaymentRuleEntity::class,
        UserEntity::class, ExchangeRateEntity::class, BudgetEntity::class,
        LoanEntity::class, LoanRecordEntity::class
    ],
    autoMigrations = [
        AutoMigration(
            from = 121,
            to = 122,
            spec = IvyRoomDatabase.DeleteSEMigration::class
        )
    ],
    version = 123,
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
        private const val DB_NAME = "ivywallet.db"

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
                    Migration120to121_DropWishlistItem(),
                    Migration122to123_SubCategories()
                )
                .build()
        }
    }

    suspend fun reset() {
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

    @DeleteColumn(tableName = "accounts", columnName = "seAccountId")
    @DeleteColumn(tableName = "transactions", columnName = "seTransactionId")
    @DeleteColumn(tableName = "transactions", columnName = "seAutoCategoryId")
    @DeleteColumn(tableName = "categories", columnName = "seCategoryName")
    class DeleteSEMigration : AutoMigrationSpec
}