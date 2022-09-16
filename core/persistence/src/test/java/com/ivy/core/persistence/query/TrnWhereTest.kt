package com.ivy.core.persistence.query

import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf
import com.ivy.common.endOfIvyTime
import com.ivy.common.timeNowUTC
import com.ivy.common.toEpochSeconds
import com.ivy.common.toRange
import com.ivy.core.persistence.entity.trn.data.TrnTimeType
import com.ivy.core.persistence.query.TrnWhere.*
import com.ivy.data.SyncState
import com.ivy.data.time.Period
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnType
import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropertyContext
import io.kotest.property.arbitrary.*
import io.kotest.property.arrow.nonEmptyList
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive
import java.util.*

class TrnWhereTest : StringSpec({
    //region generators
    val genById = arbitrary {
        val id = Arb.uuid().bind()
        ById(id.toString())
    }

    val genId = Arb.uuid().map { it.toString() }

    val genByCategory = listOf(ByCategoryId(null), ByCategoryId(genId.next()))
        .exhaustive()

    val genByAccount = arbitrary { ByAccountId(genId.bind()) }


    val genTrnType = Arb.enum<TrnType>()

    val genByType = arbitrary {
        ByType(
            trnType = genTrnType.bind()
        )
    }

    val genBySync = arbitrary {
        BySync(
            sync = Arb.enum<SyncState>().bind()
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

    val genByIdIn = arbitrary {
        ByIdIn(
            Arb.nonEmptyList(
                Arb.uuid().map { it.toString() }, 1..20
            ).bind()
        )
    }

    val genByAccountIn = arbitrary {
        ByAccountIdIn(Arb.nonEmptyList(genId, 1..10).bind())
    }

    val genByCategoryIn = arbitrary {
        val categories = Arb.nonEmptyList(genId, 1..10).bind()
        val maybeNullableCats = if (Arb.boolean().bind())
            NonEmptyList.fromListUnsafe(categories.plus(null).shuffled()) else categories
        ByCategoryIdIn(maybeNullableCats)
    }

    val genByTypeIn = arbitrary {
        ByTypeIn(Arb.nonEmptyList(genTrnType, 1..3).bind())
    }

    val genSimpleQuery = Arb.choice(
        genById,
        genByType,
        genByAccount,
        genByCategory.toArb(),
        genActualBetween,
        genDueBetween,
        genByIdIn,
        genByAccountIn,
        genByCategoryIn,
        genByTypeIn,
        genBySync,
    )

    val genNonRecursiveAnd = arbitrary {
        And(
            cond1 = genSimpleQuery.bind(),
            cond2 = genSimpleQuery.bind()
        )
    }

    val genNonRecursiveOr = arbitrary {
        Or(
            cond1 = genSimpleQuery.bind(),
            cond2 = genSimpleQuery.bind()
        )
    }

    fun <T : TrnWhere> recursiveQuery(
        gen: Arb<TrnWhere>,
        block: (TrnWhere, TrnWhere) -> T
    ): Arb<T> {
        fun build(
            cond: TrnWhere,
            block: (TrnWhere, TrnWhere) -> T
        ): Arb<T> = arbitrary {
            if (Arb.boolean().bind()) {
                // recurse
                block(cond, build(gen.bind(), block).bind())
            } else {
                // stop recursion
                block(cond, gen.bind())
            }
        }

        return build(gen.next(), block)
    }

    val genRecursiveAnd = recursiveQuery(gen = genSimpleQuery, block = ::And)
    val genRecursiveOr = recursiveQuery(gen = genSimpleQuery, ::Or)
    val genBrackets = arbitrary {
        brackets(
            Arb.choice(genRecursiveAnd, genRecursiveOr).bind()
        )
    }
    val genNot = arbitrary {
        not(
            Arb.choice(genSimpleQuery, genBrackets).bind()
        )
    }

    val genComplexQuery = Arb.choice(
        genSimpleQuery,
        genRecursiveAnd,
        genRecursiveOr,
        genNot,
        genBrackets
    )
    //endregion

    //region test cases
    "case 'Upcoming By Category' query" {
        val theFuture = timeNowUTC().plusSeconds(1)
        val categoryId = genId.next()

        val query = DueBetween(Period.After(theFuture)) and ByCategoryId(categoryId)
        val where = toWhereClause(query)

        where.query shouldBe "(timeType = ${TrnTimeType.Due.code}" +
                " AND time >= ? AND time <= ?) AND categoryId = ?"
        where.args shouldBe listOf(
            theFuture.toEpochSeconds(),
            endOfIvyTime().toEpochSeconds(),
            categoryId
        )
    }

    "case complex query" {
        val accId1 = genId.next()
        val accId2 = genId.next()
        val purpose = TrnPurpose.TransferFrom
        val catId = genId.next()
        val dueStart = timeNowUTC()
        val dueEnd = dueStart.plusYears(3)
        val id1 = UUID.randomUUID().toString()
        val id2 = UUID.randomUUID().toString()
        val id3 = UUID.randomUUID().toString()

        val query = brackets(
            ByAccountIdIn(
                nonEmptyListOf(accId1, accId2)
            ) and not(ByPurpose(purpose))
        ) or brackets(
            ByCategoryId(catId) and ActualBetween(Period.FromTo(dueStart, dueEnd))
        ) or ByIdIn(nonEmptyListOf(id1, id2, id3))

        val where = toWhereClause(query)

        where.query shouldBe "(accountId IN (?, ?) AND NOT(purpose = ?)) OR " +
                "(categoryId = ? AND (timeType = ${TrnTimeType.Actual.code} " +
                "AND time >= ? AND time <= ?)) OR " +
                "id IN (?, ?, ?)"
        where.args shouldBe listOf(
            accId1, accId2, purpose.code,
            catId, dueStart.toEpochSeconds(), dueEnd.toEpochSeconds(),
            id1, id2, id3
        )
    }

    "case 'ByCategoryIdIn' happy path 1" {
        val catId1 = genId.next()
        val catId2 = genId.next()
        val catId3 = genId.next()

        val where = toWhereClause(
            ByCategoryIdIn(
                nonEmptyListOf(catId1, catId2, catId3, null)
            ) and ByType(TrnType.Expense)
        )

        where.query shouldBe "(categoryId IN (?, ?, ?) OR categoryId IS NULL) AND type = ?"
        where.args shouldBe listOf(
            catId1, catId2, catId3, TrnType.Expense.code
        )
    }

    "case 'ByCategoryIdIn' happy path 2" {
        val catId1 = genId.next()
        val catId2 = genId.next()
        val catId3 = genId.next()

        val where = toWhereClause(ByCategoryIdIn(nonEmptyListOf(catId1, catId2, catId3)))

        where.query shouldBe "categoryId IN (?, ?, ?)"
        where.args shouldBe listOf(catId1, catId2, catId3)
    }
    //endregion

    //region property-based
    "generate ById" {
        val byId = genById.next()
        toWhereClause(byId) shouldBe WhereClause(
            query = "id = ?",
            args = listOf(byId.id.toString())
        )
    }

    "generate ByCategory" {
        checkAll(genByCategory) { byCategory ->
            val where = toWhereClause(byCategory)
            val categoryId = byCategory.categoryId
            if (categoryId == null) {
                where.query shouldBe "categoryId IS NULL"
                where.args shouldBe emptyList()
            } else {
                where.query shouldBe "categoryId = ?"
                where.args shouldBe listOf(categoryId)
            }
        }
    }

    "generate ByAccount" {
        val byAccount = genByAccount.next()
        toWhereClause(byAccount) shouldBe WhereClause(
            query = "accountId = ?",
            args = listOf(byAccount.accountId)
        )
    }

    "generate ByType" {
        checkAll(genByType) { byType ->
            val where = toWhereClause(byType)
            where.query shouldBe "type = ?"
            where.args shouldBe listOf(byType.trnType.code)
        }
    }

    "generate BySync" {
        checkAll(genBySync) { bySync ->
            val where = toWhereClause(bySync)
            where.query shouldBe "sync = ?"
            where.args shouldBe listOf(bySync.sync.code)
        }
    }

    "generate ActualBetween" {
        checkAll(genActualBetween) { actualBetween ->
            val where = toWhereClause(actualBetween)
            where.query shouldBe "(timeType = ${TrnTimeType.Actual.code}" +
                    " AND time >= ? AND time <= ?)"
            where.args.size shouldBe 2
            where.args shouldBe actualBetween.period.toRange().toList().map { it.toEpochSeconds() }
        }
    }

    "generate DueBetween" {
        checkAll(genDueBetween) { dueBetween ->
            val where = toWhereClause(dueBetween)
            where.query shouldBe "(timeType = ${TrnTimeType.Due.code}" +
                    " AND time >= ? AND time <= ?)"
            where.args.size shouldBe 2
            where.args shouldBe dueBetween.period.toRange().toList().map { it.toEpochSeconds() }
        }
    }

    fun placeholders(n: Int) = "?, ".repeat(n - 1) + "?"

    "generate ByIdIn" {
        checkAll(genByIdIn) { byIdIn ->
            val where = toWhereClause(byIdIn)
            where.query shouldBe "id IN (${placeholders(byIdIn.ids.size)})"
            where.args shouldBe byIdIn.ids.toList().map { it.toString() }
        }
    }

    "generate ByAccountIn" {
        checkAll(genByAccountIn) { byAccountIn ->
            val where = toWhereClause(byAccountIn)
            where.query shouldBe "accountId IN (${placeholders(byAccountIn.accountIds.size)})"
            where.args shouldBe byAccountIn.accountIds.toList()
        }
    }

    "generate ByCategoryIn" {
        checkAll(genByCategoryIn) { byCategoryIn ->
            val where = toWhereClause(byCategoryIn)
            val categoryIds = byCategoryIn.categoryIds
            val nonNullCategoryIds = categoryIds.filterNotNull()

            val expectedQuery = when {
                categoryIds.size == 1 && categoryIds.first() == null -> {
                    "categoryId IS NULL"
                }
                categoryIds.size == nonNullCategoryIds.size -> {
                    "categoryId IN (${placeholders(byCategoryIn.categoryIds.size)})"
                }
                else -> {
                    "(categoryId IN (${placeholders(nonNullCategoryIds.size)}) OR categoryId IS NULL)"
                }
            }
            where.query shouldBe expectedQuery
            where.args shouldBe byCategoryIn.categoryIds.filterNotNull()
        }
    }

    "generate ByTypeIn" {
        checkAll(genByTypeIn) { byTypeIn ->
            val where = toWhereClause(byTypeIn)
            where.query shouldBe "type IN (${placeholders(byTypeIn.types.size)})"
            where.args shouldBe byTypeIn.types.toList().map { it.code }
        }
    }

    "generate Brackets" {
        checkAll(genBrackets) { brackets ->
            val res = toWhereClause(brackets(brackets))
            val condWhere = toWhereClause(brackets)
            res.query shouldBe "(${condWhere.query})"
            res.args shouldBe condWhere.args
        }
    }

    "generate Not" {
        checkAll(genNot) { not ->
            val res = toWhereClause(not)
            val condWhere = toWhereClause(not.cond)
            res.query shouldBe "NOT(${condWhere.query})"
            res.args shouldBe condWhere.args
        }
    }

    "generate non-recursive And" {
        checkAll(genNonRecursiveAnd) { and ->
            val res = toWhereClause(and)
            val whereCond1 = toWhereClause(and.cond1)
            val whereCond2 = toWhereClause(and.cond2)

            res.query shouldBe "${whereCond1.query} AND ${whereCond2.query}"
            res.args shouldBe (whereCond1.args + whereCond2.args)
        }
    }

    "generate non-recursive Or" {
        checkAll(genNonRecursiveOr) { or ->
            val res = toWhereClause(or)
            val whereCond1 = toWhereClause(or.cond1)
            val whereCond2 = toWhereClause(or.cond2)

            res.query shouldBe "${whereCond1.query} OR ${whereCond2.query}"
            res.args shouldBe (whereCond1.args + whereCond2.args)
        }
    }

    fun PropertyContext.labelRecursive(label: String, trnWhere: TrnWhere) {
        collect(
            label, when (trnWhere) {
                is And, is Brackets, is Or -> "RECURSIVE"
                else -> "NON-RECURSIVE"
            }
        )
    }

    "generate recursive And" {
        checkAll(genRecursiveAnd) { and ->
            val res = toWhereClause(and)
            val whereCond1 = toWhereClause(and.cond1)
            val whereCond2 = toWhereClause(and.cond2)

            res.query shouldBe "${whereCond1.query} AND ${whereCond2.query}"
            res.args shouldBe (whereCond1.args + whereCond2.args)

            labelRecursive("cond2", and.cond2)
            collect("args_count", res.args.size)
        }
    }

    "generate recursive Or" {
        checkAll(genRecursiveOr) { or ->
            val res = toWhereClause(or)
            val whereCond1 = toWhereClause(or.cond1)
            val whereCond2 = toWhereClause(or.cond2)

            res.query shouldBe "${whereCond1.query} OR ${whereCond2.query}"
            res.args shouldBe (whereCond1.args + whereCond2.args)

            labelRecursive("cond2", or.cond2)
            collect("args_count", res.args.size)
        }
    }

    "query args property" {
        checkAll(genComplexQuery) { complexQuery ->
            val where = toWhereClause(complexQuery)

            // Things that query args can be:
            where.args.forEach {
                val acceptedType = when (it) {
                    is String, is Long, is Boolean, is Int -> true
                    else -> false
                }
                if (!acceptedType) {
                    collect("not_accepted", it)
                    fail("$it (${it?.javaClass?.name}) is not accepted type.")
                }
            }
        }
    }
    //endregion
})