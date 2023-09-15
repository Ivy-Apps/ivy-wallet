package com.ivy.core.data.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.ivy.core.data.db.dao.AccountDao
import com.ivy.core.data.db.dao.BudgetDao
import com.ivy.core.data.db.dao.CategoryDao
import com.ivy.core.data.db.dao.ExchangeRateDao
import com.ivy.core.data.db.dao.LoanDao
import com.ivy.core.data.db.dao.LoanRecordDao
import com.ivy.core.data.db.dao.PlannedPaymentRuleDao
import com.ivy.core.data.db.dao.SettingsDao
import com.ivy.core.data.db.dao.TransactionDao
import com.ivy.core.data.db.dao.UserDao
import com.ivy.core.data.db.entity.AccountEntity
import com.ivy.core.data.db.entity.BudgetEntity
import com.ivy.core.data.db.entity.CategoryEntity
import com.ivy.core.data.db.entity.ExchangeRateEntity
import com.ivy.core.data.db.entity.LoanEntity
import com.ivy.core.data.db.entity.LoanRecordEntity
import com.ivy.core.data.db.entity.PlannedPaymentRuleEntity
import com.ivy.core.data.db.entity.SettingsEntity
import com.ivy.core.data.db.entity.TransactionEntity
import com.ivy.core.data.db.entity.UserEntity
import com.ivy.core.data.db.migration.Migration105to106_TrnRecurringRules
import com.ivy.core.data.db.migration.Migration106to107_Wishlist
import com.ivy.core.data.db.migration.Migration107to108_Sync
import com.ivy.core.data.db.migration.Migration108to109_Users
import com.ivy.core.data.db.migration.Migration109to110_PlannedPayments
import com.ivy.core.data.db.migration.Migration110to111_PlannedPaymentRule
import com.ivy.core.data.db.migration.Migration111to112_User_testUser
import com.ivy.core.data.db.migration.Migration112to113_ExchangeRates
import com.ivy.core.data.db.migration.Migration113to114_Multi_Currency
import com.ivy.core.data.db.migration.Migration114to115_Category_Account_Icons
import com.ivy.core.data.db.migration.Migration115to116_Account_Include_In_Balance
import com.ivy.core.data.db.migration.Migration116to117_SalteEdgeIntgration
import com.ivy.core.data.db.migration.Migration117to118_Budgets
import com.ivy.core.data.db.migration.Migration118to119_Loans
import com.ivy.core.data.db.migration.Migration119to120_LoanTransactions
import com.ivy.core.data.db.migration.Migration120to121_DropWishlistItem
import com.ivy.core.data.db.migration.Migration122to123_ExchangeRates

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
        const val DB_NAME = "ivywallet.db"

        fun create(applicationContext: Context): IvyRoomDatabase {
            return Room
                .databaseBuilder(
                    applicationContext,
                    IvyRoomDatabase::class.java,
                    DB_NAME
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
                    Migration122to123_ExchangeRates()
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
