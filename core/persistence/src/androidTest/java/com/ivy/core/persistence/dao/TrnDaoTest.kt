package com.ivy.core.persistence.dao

import androidx.sqlite.db.SimpleSQLiteQuery
import com.ivy.common.test.AndroidTest
import com.ivy.common.test.epochSeconds
import com.ivy.common.test.testCase
import com.ivy.common.test.uuidString
import com.ivy.core.persistence.IvyWalletDb
import com.ivy.core.persistence.RoomDbTest
import com.ivy.core.persistence.dao.trn.TrnDao
import com.ivy.core.persistence.dummy.trn.dummyTrnEntity
import com.ivy.core.persistence.entity.trn.TrnEntity
import com.ivy.core.persistence.entity.trn.data.TrnTimeType
import com.ivy.data.SyncState
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnState
import com.ivy.data.transaction.TrnType
import io.kotest.matchers.shouldBe
import org.junit.Test
import java.time.Instant

@AndroidTest
class TrnDaoTest : RoomDbTest() {
    private lateinit var dao: TrnDao

    override fun setUp(db: IvyWalletDb) {
        dao = db.trnDao()
    }

    // region save(): Insert/Update
    @Test
    fun save_simpleTrn() = testCase(
        context = dummyTrnEntity(amount = 13.43, type = TrnType.Income),
        given = {
            dao.save(it)
        },
        test = {
            dao.findBySQL(queryFindAll())
        },
        verifyResult = { trn ->
            size shouldBe 1
            first() shouldBe trn
        }
    )

    @Test
    fun save_complexTrn() = testCase(
        context = TrnEntity(
            id = uuidString(),
            accountId = uuidString(),
            type = TrnType.Expense,
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
        ),
        given = {
            dao.save(it)
        },
        test = {
            dao.findBySQL(queryFindAll())
        },
        verifyResult = { trn ->
            size shouldBe 1
            first() shouldBe trn
        }
    )

    @Test
    fun save_trnsList() = testCase(
        context = listOf(dummyTrnEntity(), dummyTrnEntity()),
        given = {
            dao.save(it)
        },
        test = {
            dao.findBySQL(queryFindAll())
        },
        verifyResult = { trns ->
            this shouldBe trns.sortedBy { it.id }
        }
    )
    // endregion

    // region findBySQL(): Query

    // endregion

    // region delete()
    // endregion

    private fun queryFindAll() = SimpleSQLiteQuery(
        "SELECT * FROM transactions ORDER BY id",
        emptyArray()
    )
}