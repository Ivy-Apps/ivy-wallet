package com.ivy.core.domain.pure.calculate.transaction

import com.ivy.core.domain.pure.dummy.dummyTrn
import com.ivy.core.persistence.dummy.trn.dummyTrnLinkRecordEntity
import com.ivy.data.transaction.TrnListItem
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnType
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class BatchTrnsTest : StringSpec({
    "batch a transfer without fee" {
        val from = dummyTrn(
            type = TrnType.Expense, purpose = TrnPurpose.TransferFrom
        )
        val to = dummyTrn(
            type = TrnType.Income, purpose = TrnPurpose.TransferTo
        )
        val links = listOf(
            dummyTrnLinkRecordEntity(
                batchId = "1", trnId = from.id.toString()
            ),
            dummyTrnLinkRecordEntity(
                batchId = "1", trnId = to.id.toString()
            )
        )

        val result = batchTrns(trns = listOf(from, to), links = links)

        result shouldBe listOf(
            TrnListItem.Transfer(
                batchId = "1",
                time = from.time,
                from = from,
                to = to,
                fee = null,
            )
        )
    }
})