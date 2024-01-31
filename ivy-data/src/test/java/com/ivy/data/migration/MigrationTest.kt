package com.ivy.data.migration

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.ivy.data.db.IvyRoomDatabase
import com.ivy.data.db.IvyRoomDatabase.Companion.DB_NAME
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
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.core.spec.style.FunSpec
import org.junit.runner.RunWith


private val TEST_DB = DB_NAME

class MigrationTest : AnnotationSpec(){
    // Array of all migrations.
    private val ALL_MIGRATIONS = arrayOf(
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


//    val helper = MigrationTestHelper(
//        InstrumentationRegistry.getInstrumentation(),
//        IvyRoomDatabase::class.java,
//        listOf(IvyRoomDatabase.DeleteSEMigration()),
//        FrameworkSQLiteOpenHelperFactory()
//    )

    @Test
    fun testAllMigration(){

        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            IvyRoomDatabase::class.java,
            TEST_DB
        ).addMigrations(*ALL_MIGRATIONS).build().apply {
            openHelper.writableDatabase.close()
        }
    }
}
