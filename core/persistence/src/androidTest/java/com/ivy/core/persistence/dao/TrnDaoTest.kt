package com.ivy.core.persistence.dao

import androidx.sqlite.db.SimpleSQLiteQuery
import com.ivy.common.test.AndroidTest
import com.ivy.common.test.testCase
import com.ivy.core.persistence.IvyWalletDb
import com.ivy.core.persistence.RoomDbTest
import com.ivy.core.persistence.dummy.trn.dummyTrnEntity
import io.kotest.matchers.shouldBe
import org.junit.Test

@AndroidTest
class TrnDaoTest : RoomDbTest() {

    private lateinit var dao: TrnDao

    override fun setUp(db: IvyWalletDb) {
        dao = db.trnDao()
    }

    @Test
    fun save_simpleTrn() = testCase(
        context = dummyTrnEntity(amount = 13.43),
        given = { trn ->
            dao.save(trn)
        },
        executeTest = {
            dao.findBySQL(queryFindAll())
        },
        verifyResult = { trn ->
            size shouldBe 1
            first() shouldBe trn
        }
    )

    private fun queryFindAll() = SimpleSQLiteQuery(
        "SELECT * FROM transactions_v2",
        emptyArray()
    )
}