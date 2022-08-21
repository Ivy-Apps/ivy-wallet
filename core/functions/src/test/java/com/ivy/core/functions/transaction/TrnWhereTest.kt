package com.ivy.core.functions.transaction

import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf
import com.ivy.common.endOfIvyTime
import com.ivy.common.timeNowUTC
import com.ivy.common.toEpochMilli
import com.ivy.core.functions.account.dummyAcc
import com.ivy.core.functions.category.dummyCategory
import com.ivy.core.functions.toRange
import com.ivy.core.functions.transaction.TrnWhere.*
import com.ivy.data.Period
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
        ById(id)
    }

    val genCategory = arbitrary {
        dummyCategory()
    }

    val genByCategory = listOf(ByCategory(null), ByCategory(genCategory.next()))
        .exhaustive()

    val genAcc = arbitrary {
        dummyAcc()
    }

    val genByAccount = arbitrary {
        ByAccount(genAcc.bind())
    }

    val genByToAccount = arbitrary {
        ByToAccount(genAcc.bind())
    }

    val genTrnType = Arb.enum<TrnType>()

    val genByType = arbitrary {
        ByType(
            trnType = genTrnType.bind()
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
        ByIdIn(Arb.nonEmptyList(Arb.uuid(), 1..20).bind())
    }

    val genByAccountIn = arbitrary {
        ByAccountIn(Arb.nonEmptyList(genAcc, 1..10).bind())
    }

    val genByToAccountIn = arbitrary {
        ByToAccountIn(Arb.nonEmptyList(genAcc, 1..10).bind())
    }

    val genByCategoryIn = arbitrary {
        val categories = Arb.nonEmptyList(genCategory, 1..10).bind()
        val maybeNullableCats = if (Arb.boolean().bind())
            NonEmptyList.fromListUnsafe(categories.plus(null).shuffled()) else categories
        ByCategoryIn(maybeNullableCats)
    }

    val genByTypeIn = arbitrary {
        ByTypeIn(Arb.nonEmptyList(genTrnType, 1..3).bind())
    }

    val genSimpleQuery = Arb.choice(
        genById,
        genByType,
        genByAccount,
        genByToAccount,
        genByCategory.toArb(),
        genActualBetween,
        genDueBetween,
        genByIdIn,
        genByAccountIn,
        genByToAccountIn,
        genByCategoryIn,
        genByTypeIn
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
        val category = dummyCategory()

        val query = DueBetween(Period.After(theFuture)) and ByCategory(category)
        val where = toWhereClause(query)

        where.query shouldBe "(dueDate IS NOT NULL AND dueDate >= ? AND dueDate <= ?) AND categoryId = ?"
        where.args shouldBe listOf(
            theFuture.toEpochMilli(),
            endOfIvyTime().toEpochMilli(),
            category.id.toString()
        )
    }

    "case complex query" {
        val acc1 = dummyAcc()
        val acc2 = dummyAcc()
        val acc3 = dummyAcc()
        val cat = dummyCategory()
        val dueStart = timeNowUTC()
        val dueEnd = dueStart.plusYears(3)
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val id3 = UUID.randomUUID()

        val query = brackets(
            ByAccountIn(
                nonEmptyListOf(acc1, acc2)
            ) and not(ByToAccount(acc3))
        ) or brackets(
            ByCategory(cat) and ActualBetween(Period.FromTo(dueStart, dueEnd))
        ) or ByIdIn(nonEmptyListOf(id1, id2, id3))

        val where = toWhereClause(query)

        where.query shouldBe "(accountId IN (?, ?) AND NOT(toAccountId = ?)) OR " +
                "(categoryId = ? AND (dateTime IS NOT NULL AND dateTime >= ? AND dateTime <= ?)) OR " +
                "id IN (?, ?, ?)"
        where.args shouldBe listOf(
            acc1.id.toString(), acc2.id.toString(), acc3.id.toString(),
            cat.id.toString(), dueStart.toEpochMilli(), dueEnd.toEpochMilli(),
            id1.toString(), id2.toString(), id3.toString()
        )
    }

    "case 'ByCategoryIdIn' happy path 1" {
        val cat1 = dummyCategory()
        val cat2 = dummyCategory()
        val cat3 = dummyCategory()

        val where = toWhereClause(
            ByCategoryIn(nonEmptyListOf(cat1, cat2, cat3, null)) and ByType(TrnType.EXPENSE)
        )

        where.query shouldBe "(categoryId IN (?, ?, ?) OR categoryId IS NULL) AND type = ?"
        where.args shouldBe listOf(
            cat1.id.toString(), cat2.id.toString(), cat3.id.toString(), "EXPENSE"
        )
    }

    "case 'ByCategoryIdIn' happy path 2" {
        val cat1 = dummyCategory()
        val cat2 = dummyCategory()
        val cat3 = dummyCategory()

        val where = toWhereClause(ByCategoryIn(nonEmptyListOf(cat1, cat2, cat3)))

        where.query shouldBe "categoryId IN (?, ?, ?)"
        where.args shouldBe listOf(cat1.id.toString(), cat2.id.toString(), cat3.id.toString())
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
            val category = byCategory.category
            if (category == null) {
                where.query shouldBe "categoryId IS NULL"
                where.args shouldBe emptyList()
            } else {
                where.query shouldBe "categoryId = ?"
                where.args shouldBe listOf(category.id.toString())
            }
        }
    }

    "generate ByAccount" {
        val byAccount = genByAccount.next()
        toWhereClause(byAccount) shouldBe WhereClause(
            query = "accountId = ?",
            args = listOf(byAccount.account.id.toString())
        )
    }

    "generate ByToAccount" {
        val byToAccount = genByToAccount.next()
        toWhereClause(byToAccount) shouldBe WhereClause(
            query = "toAccountId = ?",
            args = listOf(byToAccount.toAccount.id.toString())
        )
    }

    "generate ByType" {
        checkAll(genByType) { byType ->
            val where = toWhereClause(byType)
            where.query shouldBe "type = ?"
            where.args shouldBe listOf(byType.trnType.name)
        }
    }

    "generate ActualBetween" {
        checkAll(genActualBetween) { actualBetween ->
            val where = toWhereClause(actualBetween)
            where.query shouldBe "(dateTime IS NOT NULL AND dateTime >= ? AND dateTime <= ?)"
            where.args.size shouldBe 2
            where.args shouldBe actualBetween.period.toRange().toList().map { it.toEpochMilli() }
        }
    }

    "generate DueBetween" {
        checkAll(genDueBetween) { dueBetween ->
            val where = toWhereClause(dueBetween)
            where.query shouldBe "(dueDate IS NOT NULL AND dueDate >= ? AND dueDate <= ?)"
            where.args.size shouldBe 2
            where.args shouldBe dueBetween.period.toRange().toList().map { it.toEpochMilli() }
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
            where.query shouldBe "accountId IN (${placeholders(byAccountIn.accs.size)})"
            where.args shouldBe byAccountIn.accs.map { it.id.toString() }.toList()
        }
    }

    "generate ByToAccountIn" {
        checkAll(genByToAccountIn) { byToAccountIn ->
            val where = toWhereClause(byToAccountIn)
            where.query shouldBe "toAccountId IN (${placeholders(byToAccountIn.toAccs.size)})"
            where.args shouldBe byToAccountIn.toAccs.map { it.id.toString() }.toList()
        }
    }

    "generate ByCategoryIn" {
        checkAll(genByCategoryIn) { byCategoryIn ->
            val where = toWhereClause(byCategoryIn)
            val categories = byCategoryIn.categories
            val nonNullCategories = categories.filterNotNull()

            val expectedQuery = when {
                categories.size == 1 && categories.first() == null -> {
                    "categoryId IS NULL"
                }
                categories.size == nonNullCategories.size -> {
                    "categoryId IN (${placeholders(byCategoryIn.categories.size)})"
                }
                else -> {
                    "(categoryId IN (${placeholders(nonNullCategories.size)}) OR categoryId IS NULL)"
                }
            }
            where.query shouldBe expectedQuery
            where.args shouldBe byCategoryIn.categories.mapNotNull { it?.id?.toString() }
        }
    }

    "generate ByTypeIn" {
        checkAll(genByTypeIn) { byTypeIn ->
            val where = toWhereClause(byTypeIn)
            where.query shouldBe "type IN (${placeholders(byTypeIn.types.size)})"
            where.args shouldBe byTypeIn.types.toList().map { it.name }
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
                    is String, is Long, is Boolean -> true
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