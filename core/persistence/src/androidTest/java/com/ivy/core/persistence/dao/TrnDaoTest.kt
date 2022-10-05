package com.ivy.core.persistence.dao

import androidx.sqlite.db.SimpleSQLiteQuery
import com.ivy.common.androidtest.AndroidTest
import com.ivy.common.androidtest.epochSeconds
import com.ivy.common.androidtest.uuidString
import com.ivy.core.persistence.IvyWalletCoreDb
import com.ivy.core.persistence.RoomDbTest
import com.ivy.core.persistence.dao.trn.TrnDao
import com.ivy.core.persistence.dummy.trn.dummyTrnEntity
import com.ivy.core.persistence.entity.trn.TrnEntity
import com.ivy.core.persistence.entity.trn.data.TrnTimeType
import com.ivy.data.SyncState
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnState
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.Instant

@AndroidTest
class TrnDaoTest : RoomDbTest() {
    private lateinit var dao: TrnDao

    override fun setUp(db: IvyWalletCoreDb) {
        dao = db.trnDao()
    }

    // region Save
    @Test
    fun save_a_simple_transaction() = saveTestCase(
        transactionEntity = dummyTrnEntity(amount = 13.43, type = TransactionType.Income)
    )

    @Test
    fun save_a_complex_transaction() = saveTestCase(
        transactionEntity = TrnEntity(
            id = uuidString(),
            accountId = uuidString(),
            type = TransactionType.Expense,
            amount = 0.43,
            currency = "USD",
            time = Instant.now().epochSeconds(),
            timeType = TrnTimeType.Due,
            title = "Title",
            description = "a\nb\nc\nd",
            categoryId = uuidString(),
            purpose = TrnPurpose.TransferFrom,
            state = TrnState.Hidden,
            sync = SyncState.Syncing,
        )
    )

    private fun saveTestCase(transactionEntity: TrnEntity) = runBlocking {
        dao.save(transactionEntity)

        val result = dao.findBySQL(queryFindAll())
        result shouldBe listOf(transactionEntity)
    }

    private fun queryFindAll() = SimpleSQLiteQuery(
        "SELECT * FROM transactions ORDER BY id",
        emptyArray()
    )
}