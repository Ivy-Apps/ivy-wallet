package com.ivy.data.db

import android.database.Cursor
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import com.ivy.base.model.TransactionType
import com.ivy.data.db.migration.Migration128to129_DeleteIsDeleted
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.util.UUID

class Migration128to129Test {

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        IvyRoomDatabase::class.java,
        listOf(IvyRoomDatabase.DeleteSEMigration()),
        FrameworkSQLiteOpenHelperFactory()
    )

    private val migration = Migration128to129_DeleteIsDeleted()

    @Test
    fun deletesDeletedTransactions() = migrationTestCase(
        tableName = "transactions",
        dataBeforeMigration = {
            insertTransaction(
                title = "Trn 1",
                isDeleted = false,
            )
            insertTransaction(
                title = "Trn 2",
                isDeleted = true,
            )
        },
        dataAfterMigration = {
            moveToFirst() shouldBe true
            getString(getColumnIndexOrThrow("title")) shouldBe "Trn 1"
            moveToNext() shouldBe false
        }
    )

    private fun SupportSQLiteDatabase.insertTransaction(
        title: String,
        isDeleted: Boolean
    ) {
        val sql = """
        INSERT INTO transactions (
            accountId, type, amount, toAccountId, toAmount, title, 
            description, dateTime, categoryId, dueDate, recurringRuleId, 
            paidForDateTime, attachmentUrl, loanId, loanRecordId, isSynced, 
            isDeleted, id
        ) VALUES (
            ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
        )
    """.trimIndent()
        val statement = this.compileStatement(sql)

        val id = UUID.randomUUID()
        val accountId = UUID.randomUUID()
        val type = TransactionType.INCOME
        val amount = 10.0
        val isSynced = true

        statement.bindString(1, accountId.toString())
        statement.bindString(2, type.name)
        statement.bindDouble(3, amount)
        statement.bindString(4, UUID.randomUUID().toString())
        statement.bindNull(5)
        statement.bindString(6, title)
        statement.bindNull(7)
        statement.bindLong(8, Instant.EPOCH.toEpochMilli()) // Use the correct format for dateTime
        statement.bindNull(9)
        statement.bindNull(10)
        statement.bindNull(11)
        statement.bindNull(12)
        statement.bindNull(13)
        statement.bindNull(14)
        statement.bindNull(15)
        statement.bindLong(16, if (isSynced) 1 else 0)
        statement.bindLong(17, if (isDeleted) 1 else 0)
        statement.bindString(18, id.toString())

        statement.executeInsert()
    }

    @Test
    fun deleteDeletedAccounts() = migrationTestCase(
        tableName = "accounts",
        dataBeforeMigration = {
            insertAccount(
                name = "Acc 1",
                isDeleted = true,
            )
            insertAccount(
                name = "Acc 2",
                isDeleted = false,
            )
        },
        dataAfterMigration = {
            moveToFirst() shouldBe true
            getString(getColumnIndexOrThrow("name")) shouldBe "Acc 2"
            moveToNext() shouldBe false
        }
    )

    private fun SupportSQLiteDatabase.insertAccount(
        name: String,
        isDeleted: Boolean,
    ) {
        val sql = """
        INSERT INTO accounts (
            name, currency, color, icon, orderNum, includeInBalance, 
            isSynced, isDeleted, id
        ) VALUES (
            ?, ?, ?, ?, ?, ?, ?, ?, ?
        )
        """.trimIndent()
        val statement = this.compileStatement(sql)

        val id = UUID.randomUUID()
        val currency = "USD" // Dummy currency
        val color = 0xFFFFFF // Dummy color (white)
        val icon = "default_icon" // Dummy icon
        val orderNum = 1.0
        val includeInBalance = true
        val isSynced = true

        statement.bindString(1, name)
        statement.bindString(2, currency)
        statement.bindLong(3, color.toLong())
        statement.bindString(4, icon)
        statement.bindDouble(5, orderNum)
        statement.bindLong(6, if (includeInBalance) 1 else 0)
        statement.bindLong(7, if (isSynced) 1 else 0)
        statement.bindLong(8, if (isDeleted) 1 else 0)
        statement.bindString(9, id.toString())

        statement.executeInsert()
    }

    private fun migrationTestCase(
        tableName: String,
        dataBeforeMigration: SupportSQLiteDatabase.() -> Unit,
        dataAfterMigration: Cursor.() -> Unit,
    ) {
        // Given
        helper.createDatabase(TestDb, 128).apply {
            dataBeforeMigration()
            close()
        }

        // When
        val newDb = helper.runMigrationsAndValidate(
            TestDb,
            129,
            true,
            migration,
        )

        // Then
        newDb.query("SELECT * FROM $tableName").apply {
            dataAfterMigration()
        }
        newDb.close()
    }

    companion object {
        private const val TestDb = "migration-test"
    }
}