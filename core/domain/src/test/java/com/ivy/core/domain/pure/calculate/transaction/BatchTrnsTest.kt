package com.ivy.core.domain.pure.calculate.transaction

import com.ivy.core.domain.pure.dummy.dummyTrn
import com.ivy.core.persistence.dummy.trn.dummyTrnLinkRecordEntity
import com.ivy.data.transaction.TrnListItem
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnType
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

class BatchTrnsTest : StringSpec({
    "batching trns into a transfer without a fee" {
        val from = dummyTrn(type = TrnType.Expense, purpose = TrnPurpose.TransferFrom)
        val to = dummyTrn(type = TrnType.Income, purpose = TrnPurpose.TransferTo)
        val trns = listOf(from, to).shuffled()
        val links = listOf(
            dummyTrnLinkRecordEntity(batchId = "1", trnId = from.id.toString()),
            dummyTrnLinkRecordEntity(batchId = "1", trnId = to.id.toString())
        ).shuffled()

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

    "batching trns into a transfer with a fee" {
        val from = dummyTrn(type = TrnType.Expense, purpose = TrnPurpose.TransferFrom)
        val to = dummyTrn(type = TrnType.Income, purpose = TrnPurpose.TransferTo)
        val fee = dummyTrn(type = TrnType.Expense, purpose = TrnPurpose.Fee)
        val trns = listOf(from, to, fee).shuffled()
        val links = listOf(
            dummyTrnLinkRecordEntity(batchId = "1", trnId = from.id.toString()),
            dummyTrnLinkRecordEntity(batchId = "1", trnId = to.id.toString()),
            dummyTrnLinkRecordEntity(batchId = "1", trnId = fee.id.toString())
        ).shuffled()

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

    "batching a transfer while preserving other trns" {
        val from = dummyTrn(type = TrnType.Expense, purpose = TrnPurpose.TransferFrom)
        val to = dummyTrn(type = TrnType.Income, purpose = TrnPurpose.TransferTo)
        val trn1 = dummyTrn(type = TrnType.Expense)
        val trn2 = dummyTrn(type = TrnType.Income)
        val links = listOf(
            dummyTrnLinkRecordEntity(batchId = "1", trnId = from.id.toString()),
            dummyTrnLinkRecordEntity(batchId = "1", trnId = to.id.toString())
        ).shuffled()
        val trns = listOf(from, trn2, to, trn1).shuffled()

        val result = batchTrns(trns = trns, links = links)

        result shouldContainExactlyInAnyOrder listOf(
            TrnListItem.Trn(trn1),
            TrnListItem.Trn(trn2),
            TrnListItem.Transfer(
                batchId = "1",
                time = from.time,
                from = from,
                to = to,
                fee = null,
            )
        )
    }

    "don't batch trns into an unknown batch type" {
        val from = dummyTrn(type = TrnType.Expense)
        val to = dummyTrn(type = TrnType.Income, purpose = TrnPurpose.TransferFrom)
        val trns = listOf(from, to).shuffled()
        val links = listOf(
            dummyTrnLinkRecordEntity(batchId = "1", trnId = from.id.toString()),
            dummyTrnLinkRecordEntity(batchId = "1", trnId = to.id.toString()),
        ).shuffled()

        val result = batchTrns(trns = trns, links = links)

        result shouldContainExactlyInAnyOrder listOf(
            TrnListItem.Trn(from),
            TrnListItem.Trn(to)
        )
    }

    "batching many transfers while preserving other trns" {
        val from1 = dummyTrn(type = TrnType.Expense, purpose = TrnPurpose.TransferFrom)
        val to1 = dummyTrn(type = TrnType.Income, purpose = TrnPurpose.TransferTo)
        val from2 = dummyTrn(type = TrnType.Expense, purpose = TrnPurpose.TransferFrom)
        val to2 = dummyTrn(type = TrnType.Income, purpose = TrnPurpose.TransferTo)
        val trn1 = dummyTrn()
        val trn2 = dummyTrn()
        val trns = listOf(from1, to1, from2, to2, trn1, trn2).shuffled()
        val links = listOf(
            dummyTrnLinkRecordEntity(batchId = "1", trnId = from1.id.toString()),
            dummyTrnLinkRecordEntity(batchId = "1", trnId = to1.id.toString()),
            dummyTrnLinkRecordEntity(batchId = "2", trnId = from2.id.toString()),
            dummyTrnLinkRecordEntity(batchId = "2", trnId = to2.id.toString()),
        ).shuffled()

        val res = batchTrns(trns = trns, links = links)

        res shouldContainExactlyInAnyOrder listOf(
            TrnListItem.Trn(trn1),
            TrnListItem.Trn(trn2),
            TrnListItem.Transfer(
                batchId = "1",
                time = from1.time,
                from = from1,
                to = to1,
                fee = null,
            ),
            TrnListItem.Transfer(
                batchId = "2",
                time = from2.time,
                from = from2,
                to = to2,
                fee = null,
            )
        )
    }
})