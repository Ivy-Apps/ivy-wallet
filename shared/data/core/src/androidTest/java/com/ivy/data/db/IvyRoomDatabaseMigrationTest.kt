package com.ivy.data.db

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.ivy.data.db.migration.Migration123to124_LoanIncludeDateTime
import com.ivy.data.db.migration.Migration124to125_LoanEditDateTime
import com.ivy.data.db.migration.Migration126to127_LoanRecordType
import com.ivy.data.db.migration.Migration129to130_LoanIncludeNote
import com.ivy.data.model.LoanType
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class IvyRoomDatabaseMigrationTest {

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        IvyRoomDatabase::class.java,
        listOf(IvyRoomDatabase.DeleteSEMigration()),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate129to130_LoanIncludeNote() {
        helper.createDatabase(TestDb, 129).apply {
            val insertSql = """
                INSERT INTO loans (name, amount, type, color, icon, orderNum, accountId, isSynced, isDeleted, dateTime, id) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """.trimIndent()

            val preparedStatement = compileStatement(insertSql).apply {
                // Bind values
                bindString(1, "Loan 1")
                bindDouble(2, 123.50)
                bindString(3, LoanType.BORROW.name) // Assuming you store enum as name
                bindLong(4, 13)
                bindString(5, "ic")
                bindDouble(6, 3.14)
                bindString(7, UUID.randomUUID().toString())
                bindLong(8, 1)
                bindLong(9, 0)
                bindString(10, "")
                bindString(11, UUID.randomUUID().toString())
            }
            preparedStatement.executeInsert()
            close()
        }

        val newDb = helper.runMigrationsAndValidate(
            TestDb,
            130,
            true,
            Migration129to130_LoanIncludeNote()
        )

        newDb.query("SELECT * FROM loans").apply {
            moveToFirst() shouldBe true
            getString(0) shouldBe "Loan 1"
            getDouble(1) shouldBe 123.50
            getString(2) shouldBe LoanType.BORROW.name
        }
        newDb.close()
    }

    @Test
    fun migrate123to125_LoanDateTime() {
        // given
        helper.createDatabase(TestDb, 123).apply {
            // Database has schema version 1. Insert some data using SQL queries.
            // You can't use DAO classes because they expect the latest schema.
            val insertSql = """
                INSERT INTO loans (name, amount, type, color, icon, orderNum, accountId, isSynced, isDeleted, id) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """.trimIndent()

            // Assuming you have an instance of LoanEntity named loanEntity
            val preparedStatement = compileStatement(insertSql).apply {
                // Bind the values from your LoanEntity instance to the prepared statement
                bindString(1, "Loan 1")
                bindDouble(2, 123.50)
                bindString(3, LoanType.BORROW.name) // Assuming you store enum as name
                bindLong(4, 13)
                bindString(5, "ic")
                bindDouble(6, 3.14)
                bindString(7, UUID.randomUUID().toString())
                bindLong(8, 1)
                bindLong(9, 0)
                bindString(10, UUID.randomUUID().toString())
            }
            preparedStatement.executeInsert()
            close()
        }

        // when
        helper.runMigrationsAndValidate(
            TestDb,
            124,
            true,
            Migration123to124_LoanIncludeDateTime()
        )
        val newDb = helper.runMigrationsAndValidate(
            TestDb,
            125,
            true,
            Migration124to125_LoanEditDateTime()
        )

        // then
        newDb.query("SELECT * FROM loans").apply {
            moveToFirst() shouldBe true
            getString(0) shouldBe "Loan 1"
            getDouble(1) shouldBe 123.50
            getString(2) shouldBe LoanType.BORROW.name
        }
        newDb.close()
    }

    @Test
    fun migrate126to127_LoanRecordType() {
        // given
        val loanId = UUID.randomUUID().toString()
        val noteString = "here is your note"
        helper.createDatabase(TestDb, 126).apply {
            // Database has schema version 1. Insert some data using SQL queries.
            // You can't use DAO classes because they expect the latest schema.
            val insertSql = """
                INSERT INTO loan_records (loanId, amount, note, dateTime, interest, accountId, convertedAmount, isSynced, isDeleted, id) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """.trimIndent()
            // Assuming you have an instance of LoanRecordEntity named loanRecordEntity
            val preparedStatement = compileStatement(insertSql).apply {
                // Bind the values from your LoanRecordEntity instance to the prepared statement
                bindString(1, loanId)
                bindDouble(2, 123.50)
                bindString(3, noteString)
                bindString(4, "this will fail, LocalDateTimeNeeded")
                bindLong(5, 0) // interest
                bindString(6, UUID.randomUUID().toString())
                bindDouble(7, 3.14) // convertedAmount
                bindLong(8, 1)
                bindLong(9, 0)
                bindString(10, UUID.randomUUID().toString())
            }
            preparedStatement.executeInsert()
            close()
        }

        // when
        val newDb = helper.runMigrationsAndValidate(
            TestDb,
            127,
            true,
            Migration126to127_LoanRecordType()
        )

        // then
        newDb.query("SELECT * FROM loan_records").apply {
            moveToFirst() shouldBe true
            getString(0) shouldBe loanId
            getDouble(1) shouldBe 123.50
            getString(2) shouldBe noteString
            getString(10) shouldBe "DECREASE"
        }
        newDb.close()
    }

    @Test
    fun migrateAll() {
        // given:
        // Create earliest version of the database:
        // for Ivy Wallet versions below 106 are broken :/
        helper.createDatabase(TestDb, 106).apply {
            close()
        }

        // then:
        // Open latest version of the database.
        // Room validates and executes all migrations.
        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            IvyRoomDatabase::class.java,
            TestDb
        ).addMigrations(*IvyRoomDatabase.migrations()).build().apply {
            openHelper.writableDatabase.close()
        }
    }

    companion object {
        private const val TestDb = "migration-test"
    }
}