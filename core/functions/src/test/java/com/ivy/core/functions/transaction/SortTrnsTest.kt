package com.ivy.core.functions.transaction

import com.ivy.common.timeNowLocal
import com.ivy.data.transaction.TrnTime
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SortTrnsTest : StringSpec({
    var now = timeNowLocal()

    beforeTest {
        now = timeNowLocal()
    }

    "sort actual transactions" {
        chronological(
            trns = listOf(
                dummyTrn(title = "Trn 4", time = TrnTime.Actual(now.plusSeconds(4))),
                dummyTrn(title = "Trn 1", time = TrnTime.Actual(now)),
                dummyTrn(title = "Trn 3", time = TrnTime.Actual(now.plusSeconds(3))),
                dummyTrn(title = "Trn 2", time = TrnTime.Actual(now.plusSeconds(2))),
            )
        ).map { it.title } shouldBe listOf("Trn 4", "Trn 3", "Trn 2", "Trn 1")
    }

    "sort due transactions" {
        chronological(
            trns = listOf(
                dummyTrn(title = "Due 4", time = TrnTime.Due(now.plusSeconds(4))),
                dummyTrn(title = "Due 3", time = TrnTime.Due(now.plusSeconds(3))),
                dummyTrn(title = "Due 1", time = TrnTime.Due(now)),
                dummyTrn(title = "Due 2", time = TrnTime.Due(now.plusSeconds(2))),
            )
        ).map { it.title } shouldBe listOf("Due 1", "Due 2", "Due 3", "Due 4")
    }

    "sort actual and due transactions" {
        chronological(
            trns = listOf(
                dummyTrn(title = "Due 1", time = TrnTime.Due(now)),
                dummyTrn(title = "Due 4", time = TrnTime.Due(now.plusSeconds(4))),
                dummyTrn(title = "Due 3", time = TrnTime.Due(now.plusSeconds(3))),
                dummyTrn(title = "Due 2", time = TrnTime.Due(now.plusSeconds(2))),
                dummyTrn(title = "Trn 4", time = TrnTime.Actual(now.plusSeconds(4))),
                dummyTrn(title = "Trn 1", time = TrnTime.Actual(now)),
                dummyTrn(title = "Trn 3", time = TrnTime.Actual(now.plusSeconds(3))),
                dummyTrn(title = "Trn 2", time = TrnTime.Actual(now.plusSeconds(2))),
            )
        ).map { it.title } shouldBe listOf(
            "Due 1", "Due 2", "Due 3", "Due 4", "Trn 4", "Trn 3", "Trn 2", "Trn 1"
        )
    }
})