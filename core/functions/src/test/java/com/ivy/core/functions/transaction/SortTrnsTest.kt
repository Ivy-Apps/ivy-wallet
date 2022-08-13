package com.ivy.core.functions.transaction

import com.ivy.common.timeNowLocal
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TrnTime
import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.date.shouldNotBeAfter
import io.kotest.matchers.date.shouldNotBeBefore
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.types.beInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll

class SortTrnsTest : StringSpec({
    var now = timeNowLocal()

    val trnGen = Arb.bind(
        Arb.localDateTime(),
        Arb.boolean(),
    ) { time, actual ->
        dummyTrn(time = if (actual) TrnTime.Actual(time) else TrnTime.Due(time))
    }

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

    "sort empty list" {
        chronological(listOf()) shouldBe listOf()
    }

    "sort 1 element list" {
        val trn = trnGen.next()
        chronological(listOf(trn)) shouldBe listOf(trn)
    }

    "sort 2+ elements list" {
        checkAll(Arb.list(trnGen, 2..100)) { trns ->
            tailrec fun areChronological(trns: List<Transaction>) {
                if (trns.size < 2) {
                    // end recursion
                    return
                } else {
                    val (first, second) = trns.take(2).map { it.time }
                    when (first) {
                        is TrnTime.Due -> {
                            if (second is TrnTime.Due) {
                                // most close due are on top
                                first.due shouldNotBeAfter second.due
                            }
                            // if second is TrnTime.Actual => it's okay
                        }
                        is TrnTime.Actual -> {
                            // Due transactions can't be after Actual
                            second shouldNot beInstanceOf<TrnTime.Due>()
                            second should beInstanceOf<TrnTime.Actual>()

                            // newest transactions show on top
                            val secondActual = (second as TrnTime.Actual).actual
                            first.actual shouldNotBeBefore secondActual
                        }
                        else -> fail("unexpected case")
                    }

                    // recurse
                    areChronological(trns.drop(2))
                }
            }

            val sorted = chronological(trns)
            areChronological(sorted)
        }
    }
})