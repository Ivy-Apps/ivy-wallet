package com.ivy.core.domain.pure.calculate

import com.ivy.core.domain.pure.dummy.dummyTrn
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnState
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class FilterTransactionsTest : StringSpec({
    "include all" {
        val trn1 = dummyTrn(purpose = TrnPurpose.TransferFrom)
        val trn2 = dummyTrn(purpose = TrnPurpose.TransferTo)
        val trn3 = dummyTrn(state = TrnState.Hidden)
        val trn4 = dummyTrn()
        val trns = listOf(trn1, trn2, trn3, trn4)

        val res = trns.filter(includeTransfers = true, includeHidden = true)

        res shouldBe trns
    }

    "filter transfers" {
        val trn1 = dummyTrn(purpose = TrnPurpose.TransferFrom)
        val trn2 = dummyTrn(purpose = TrnPurpose.TransferTo)
        val trn3 = dummyTrn(state = TrnState.Hidden)
        val trn4 = dummyTrn()
        val trns = listOf(trn1, trn2, trn3, trn4)

        val res = trns.filter(includeTransfers = false, includeHidden = true)

        res shouldBe listOf(trn3, trn4)
    }

    "filter hidden" {
        val trn1 = dummyTrn(purpose = TrnPurpose.TransferFrom)
        val trn2 = dummyTrn(purpose = TrnPurpose.TransferTo)
        val trn3 = dummyTrn(state = TrnState.Hidden)
        val trn4 = dummyTrn()
        val trns = listOf(trn1, trn2, trn3, trn4)

        val res = trns.filter(includeTransfers = true, includeHidden = false)

        res shouldBe listOf(trn1, trn2, trn4)
    }

    "filter transfers and hidden" {
        val trn1 = dummyTrn(purpose = TrnPurpose.TransferFrom)
        val trn2 = dummyTrn(purpose = TrnPurpose.TransferTo)
        val trn3 = dummyTrn(state = TrnState.Hidden)
        val trn4 = dummyTrn()
        val trns = listOf(trn1, trn2, trn3, trn4)

        val res = trns.filter(includeTransfers = false, includeHidden = false)

        res shouldBe listOf(trn4)
    }


})