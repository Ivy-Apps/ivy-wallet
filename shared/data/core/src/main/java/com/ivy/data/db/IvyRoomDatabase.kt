package com.ivy.data.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.dao.read.BudgetDao
import com.ivy.data.db.dao.read.CategoryDao
import com.ivy.data.db.dao.read.ExchangeRatesDao
import com.ivy.data.db.dao.read.LoanDao
import com.ivy.data.db.dao.read.LoanRecordDao
import com.ivy.data.db.dao.read.PlannedPaymentRuleDao
import com.ivy.data.db.dao.read.SettingsDao
import com.ivy.data.db.dao.read.TagDao
import com.ivy.data.db.dao.read.TagAssociationDao
import com.ivy.data.db.dao.read.TransactionDao
import com.ivy.data.db.dao.read.UserDao
import com.ivy.data.db.dao.write.WriteAccountDao
import com.ivy.data.db.dao.write.WriteBudgetDao
import com.ivy.data.db.dao.write.WriteCategoryDao
import com.ivy.data.db.dao.write.WriteExchangeRatesDao
import com.ivy.data.db.dao.write.WriteLoanDao
import com.ivy.data.db.dao.write.WriteLoanRecordDao
import com.ivy.data.db.dao.write.WritePlannedPaymentRuleDao
import com.ivy.data.db.dao.write.WriteSettingsDao
import com.ivy.data.db.dao.write.WriteTagDao
import com.ivy.data.db.dao.write.WriteTagAssociationDao
import com.ivy.data.db.dao.write.WriteTransactionDao
import com.ivy.data.db.entity.AccountEntity
import com.ivy.data.db.entity.BudgetEntity
import com.ivy.data.db.entity.CategoryEntity
import com.ivy.data.db.entity.ExchangeRateEntity
import com.ivy.data.db.entity.LoanEntity
import com.ivy.data.db.entity.LoanRecordEntity
import com.ivy.data.db.entity.PlannedPaymentRuleEntity
import com.ivy.data.db.entity.SettingsEntity
import com.ivy.data.db.entity.TagEntity
import com.ivy.data.db.entity.TagAssociationEntity
import com.ivy.data.db.entity.TransactionEntity
import com.ivy.data.db.entity.UserEntity
import com.ivy.data.db.migration.Migration123to124_LoanIncludeDateTime
import com.ivy.data.db.migration.Migration124to125_LoanEditDateTime
import com.ivy.data.db.migration.Migration126to127_LoanRecordType
import com.ivy.data.db.migration.Migration127to128_PaidForDateRecord
import com.ivy.data.db.migration.Migration128to129_DeleteIsDeleted
import com.ivy.data.db.migration.Migration129to130_LoanIncludeNote
import com.ivy.domain.db.RoomTypeConverters
import com.ivy.domain.db.migration.Migration105to106_TrnRecurringRules
import com.ivy.domain.db.migration.Migration106to107_Wishlist
import com.ivy.domain.db.migration.Migration107to108_Sync
import com.ivy.domain.db.migration.Migration108to109_Users
import com.ivy.domain.db.migration.Migration109to110_PlannedPayments
import com.ivy.domain.db.migration.Migration110to111_PlannedPaymentRule
import com.ivy.domain.db.migration.Migration111to112_User_testUser
import com.ivy.domain.db.migration.Migration112to113_ExchangeRates
import com.ivy.domain.db.migration.Migration113to114_Multi_Currency
import com.ivy.domain.db.migration.Migration114to115_Category_Account_Icons
import com.ivy.domain.db.migration.Migration115to116_Account_Include_In_Balance
import com.ivy.domain.db.migration.Migration116to117_SalteEdgeIntgration
import com.ivy.domain.db.migration.Migration117to118_Budgets
import com.ivy.domain.db.migration.Migration118to119_Loans
import com.ivy.domain.db.migration.Migration119to120_LoanTransactions
import com.ivy.domain.db.migration.Migration120to121_DropWishlistItem
import com.ivy.domain.db.migration.Migration122to123_ExchangeRates
import com.ivy.domain.db.migration.Migration125to126_Tags

@Database(
    entities = [
        AccountEntity::class, TransactionEntity::class, CategoryEntity::class,
        SettingsEntity::class, PlannedPaymentRuleEntity::class,
        UserEntity::class, ExchangeRateEntity::class, BudgetEntity::class,
        LoanEntity::class, LoanRecordEntity::class, TagEntity::class, TagAssociationEntity::class
    ],
    autoMigrations = [
        AutoMigration(
            from = 121,
            to = 122,
            spec = IvyRoomDatabase.DeleteSEMigration::class
        )
    ],
    version = 130,
    exportSchema = true
)
@TypeConverters(RoomTypeConverters::class)
abstract class IvyRoomDatabase : RoomDatabase() {
    abstract val accountDao: AccountDao
    abstract val transactionDao: TransactionDao
    abstract val categoryDao: CategoryDao
    abstract val budgetDao: BudgetDao
    abstract val plannedPaymentRuleDao: PlannedPaymentRuleDao
    abstract val settingsDao: SettingsDao
    abstract val userDao: UserDao
    abstract val exchangeRatesDao: ExchangeRatesDao
    abstract val loanDao: LoanDao
    abstract val loanRecordDao: LoanRecordDao
    abstract val tagDao: TagDao
    abstract val tagAssociationDao: TagAssociationDao

    abstract val writeAccountDao: WriteAccountDao
    abstract val writeTransactionDao: WriteTransactionDao
    abstract val writeCategoryDao: WriteCategoryDao
    abstract val writeBudgetDao: WriteBudgetDao
    abstract val writePlannedPaymentRuleDao: WritePlannedPaymentRuleDao
    abstract val writeSettingsDao: WriteSettingsDao
    abstract val writeExchangeRatesDao: WriteExchangeRatesDao
    abstract val writeLoanDao: WriteLoanDao
    abstract val writeLoanRecordDao: WriteLoanRecordDao
    abstract val writeTagDao: WriteTagDao
    abstract val writeTagAssociationDao: WriteTagAssociationDao

    companion object {
        const val DB_NAME = "ivywallet.db"

        fun migrations() = arrayOf(
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
            Migration122to123_ExchangeRates(),
            Migration123to124_LoanIncludeDateTime(),
            Migration124to125_LoanEditDateTime(),
            Migration125to126_Tags(),
            Migration126to127_LoanRecordType(),
            Migration127to128_PaidForDateRecord(),
            Migration128to129_DeleteIsDeleted(),
            Migration129to130_LoanIncludeNote()
        )

        @Suppress("SpreadOperator")
        fun create(applicationContext: Context): IvyRoomDatabase {
            return Room
                .databaseBuilder(
                    applicationContext,
                    IvyRoomDatabase::class.java,
                    DB_NAME
                )
                .addMigrations(*migrations())
                .build()
        }
    }

    @DeleteColumn(tableName = "accounts", columnName = "seAccountId")
    @DeleteColumn(tableName = "transactions", columnName = "seTransactionId")
    @DeleteColumn(tableName = "transactions", columnName = "seAutoCategoryId")
    @DeleteColumn(tableName = "categories", columnName = "seCategoryName")
    class DeleteSEMigration : AutoMigrationSpec
}
