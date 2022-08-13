package com.ivy.core.functions.transaction

import com.ivy.core.functions.account.dummyAcc
import com.ivy.core.functions.category.dummyCategory
import com.ivy.core.functions.transaction.TrnQuery.*
import com.ivy.data.Period
import com.ivy.data.transaction.TrnType
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive

class TrnSelectQueryTest : StringSpec({
    val genById = arbitrary {
        val id = Arb.uuid().bind()
        ById(id)
    }

    val genByCategory = listOf(ByCategory(null), ByCategory(dummyCategory()))
        .exhaustive()

    val genByAccount = arbitrary {
        ByAccount(dummyAcc())
    }

    val genByType = arbitrary {
        ByType(
            trnType = Arb.enum<TrnType>().bind()
        )
    }

    val genPeriod = arbitrary {
        when (Arb.int(1..3).bind()) {
            1 -> Period.Before(Arb.localDateTime().bind())
            2 -> Period.After(Arb.localDateTime().bind())
            else -> Period.FromTo(Arb.localDateTime().bind(), Arb.localDateTime().bind())
        }
    }

    val genActualBetween = arbitrary {
        ActualBetween(period = genPeriod.bind())
    }

    val genDueBetween = arbitrary {
        DueBetween(period = genPeriod.bind())
    }

    "generate ById" {
        val byId = genById.next()
        toWhereClause(byId) shouldBe WhereClause(
            query = "id = ?",
            args = listOf(byId.id)
        )
    }

    "generate ByCategory" {
        checkAll(genByCategory) { byCategory ->
            val where = toWhereClause(byCategory)
            val category = byCategory.category
            if (category == null) {
                where.query shouldBe "categoryId IS NULL"
                where.args shouldBe emptyList()
            } else {
                where.query shouldBe "categoryId = ?"
                where.args shouldBe listOf(category.id)
            }
        }
    }

    "generate ByAccount" {
        val byAccount = genByAccount.next()
        toWhereClause(byAccount) shouldBe WhereClause(
            query = "accountId = ?",
            args = listOf(byAccount.account.id)
        )
    }

    "generate ByType" {
        checkAll(genByType) { byType ->
            val where = toWhereClause(byType)
            where.query shouldBe "type = ?"
            where.args shouldBe listOf(byType.trnType)
        }
    }

    "generate ActualBetween" {
        checkAll(genActualBetween) { actualBetween ->
            val where = toWhereClause(actualBetween)
            where.query shouldBe "(dateTime >= ? AND dateTime <= ?)"
            where.args.size shouldBe 2
            where.args shouldBe listOf(where.args[0], where.args[1])
        }
    }

    "generate DueBetween" {
        checkAll(genDueBetween) { dueBetween ->
            val where = toWhereClause(dueBetween)
            where.query shouldBe "(dueDate >= ? AND dueDate <= ?)"
            where.args.size shouldBe 2
            where.args shouldBe listOf(where.args[0], where.args[1])
        }
    }
})