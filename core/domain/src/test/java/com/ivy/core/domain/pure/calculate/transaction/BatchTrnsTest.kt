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
        val trns = listOf(from, to)
        val links = listOf(
            dummyTrnLinkRecordEntity(
                batchId = "1", trnId = from.id.toString()
            ),
            dummyTrnLinkRecordEntity(
                batchId = "1", trnId = to.id.toString()
            )
        )

        val result = batchTrns(trns = trns, links = links)

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

    "batch a transfer with fee" {
        val from = dummyTrn(
            type = TrnType.Expense, purpose = TrnPurpose.TransferFrom
        )
        val to = dummyTrn(
            type = TrnType.Income, purpose = TrnPurpose.TransferTo
        )
        val fee = dummyTrn(
            type = TrnType.Expense, purpose = TrnPurpose.Fee
        )
        val trns = listOf(from, to, fee)
        val links = listOf(
            dummyTrnLinkRecordEntity(
                batchId = "1", trnId = from.id.toString()
            ),
            dummyTrnLinkRecordEntity(
                batchId = "1", trnId = to.id.toString()
            ),
            dummyTrnLinkRecordEntity(
                batchId = "1", trnId = fee.id.toString()
            )
        )

        val result = batchTrns(trns = trns, links = links)

        result shouldBe listOf(
            TrnListItem.Transfer(
                batchId = "1",
                time = from.time,
                from = from,
                to = to,
                fee = fee,
            )
        )
    }
})