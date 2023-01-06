package com.ivy.core.persistence.query

import arrow.core.nonEmptyListOf
import com.ivy.common.test.testTimeProvider
import com.ivy.common.time.endOfIvyTime
import com.ivy.common.time.toEpochSeconds
import com.ivy.common.toNonEmptyList
import com.ivy.core.persistence.entity.trn.data.TrnTimeType
import com.ivy.core.persistence.query.TrnWhere.*
import com.ivy.data.SyncState
import com.ivy.data.time.TimeRange
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnPurpose
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

/**
 * This test suite is a bit more complicated that it should be and
 * should NOT serve as an example. For a better example refer to **TimeConversionTest**.
 */
class TrnWhereTest : StringSpec({
    val timeProvider = testTimeProvider()

    // region generators
    val genById = arbitrary { ById(Arb.uuid().bind().toString()) }

    val genId = Arb.uuid().map { it.toString() }

    val genByCategory = listOf(
        ByCategoryId(null), ByCategoryId(genId.next())
    ).exhaustive()

    val genByAccount = arbitrary { ByAccountId(genId.bind()) }

    val genTrnType = Arb.enum<TransactionType>()
    val genTrnPurpose = Arb.enum<TrnPurpose>()

    val genByType = arbitrary { ByType(trnType = genTrnType.bind()) }
    val genByPurpose = arbitrary { ByPurpose(purpose = genTrnPurpose.bind()) }

    val genBySync = arbitrary { BySync(sync = Arb.enum<SyncState>().bind()) }

    val genRange = arbitrary {
        TimeRange(Arb.localDateTime().bind(), Arb.localDateTime().bind())
    }
    val genActualBetween = arbitrary { ActualBetween(range = genRange.bind()) }
    val genDueBetween = arbitrary { DueBetween(range = genRange.bind()) }

    val genByIdIn = arbitrary {
        ByIdIn(Arb.nonEmptyList(Arb.uuid().map { it.toString() }, 1..20).bind())
    }

    val genByAccountIn = arbitrary {
        ByAccountIdIn(Arb.nonEmptyList(genId, 1..10).bind())
    }

    val genByTypeIn = arbitrary {
        ByTypeIn(Arb.nonEmptyList(genTrnType, 1..3).bind())
    }

    val genByPurposeIn = arbitrary {
        ByPurposeIn(Arb.nonEmptyList(genTrnPurpose, 1..3).bind())
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
    // endregion

    // region concrete test cases
    "generate 'upcoming by category' query" {
        val theFuture = timeProvider.timeNow().plusSeconds(1)
        val categoryId = genId.next()

        val query = DueBetween(
            TimeRange(from = theFuture, to = endOfIvyTime())
        ) and ByCategoryId(categoryId)
        val where = generateWhereClause(query, timeProvider = timeProvider)

        where.query shouldBe "(timeType = ${TrnTimeType.Due.code}" +
                " AND time >= ? AND time <= ?) AND categoryId = ?"
        where.args shouldBe listOf(
            theFuture.toEpochSeconds(timeProvider),
            endOfIvyTime().toEpochSeconds(timeProvider),
            categoryId
        )
    }

    "generate a complex query" {
        // Arrange
        val accId1 = genId.next()
        val accId2 = genId.next()
        val purpose = TrnPurpose.TransferFrom
        val catId = genId.next()
        val startDate = timeProvider.timeNow()
        val endDate = startDate.plusYears(3)
        val id1 = UUID.randomUUID().toString()
        val id2 = UUID.randomUUID().toString()
        val id3 = UUID.randomUUID().toString()

        val query = brackets(
            ByAccountIdIn(
                nonEmptyListOf(accId1, accId2)
            ) and not(ByPurpose(purpose))
        ) or brackets(
            ByCategoryId(catId) and ActualBetween(TimeRange(startDate, endDate))
        ) or ByIdIn(nonEmptyListOf(id1, id2, id3))

        // Act
        val where = generateWhereClause(query, timeProvider = timeProvider)

        // Assert
        where.query shouldBe "(accountId IN (?, ?) AND NOT(purpose = ?)) OR " +
                "(categoryId = ? AND (timeType = ${TrnTimeType.Actual.code} " +
                "AND time >= ? AND time <= ?)) OR " +
                "id IN (?, ?, ?)"
        where.args shouldBe listOf(
            accId1, accId2, purpose.code, catId,
            startDate.toEpochSeconds(timeProvider),
            endDate.toEpochSeconds(timeProvider),
            id1, id2, id3
        )
    }

    "generate 'ByCategoryIdIn' query with null category" {
        val catId1 = genId.next()
        val catId2 = genId.next()
        val catId3 = genId.next()

        val where = generateWhereClause(
            ByCategoryIdIn(
                nonEmptyListOf(catId1, catId2, catId3, null)
            ) and ByType(TransactionType.Expense),
            timeProvider = timeProvider
        )

        where.query shouldBe "(categoryId IN (?, ?, ?) OR categoryId IS NULL) AND type = ?"
        where.args shouldBe listOf(
            catId1, catId2, catId3, TransactionType.Expense.code
        )
    }

    "generate 'ByCategoryIdIn' without non-null categories" {
        val catId1 = genId.next()
        val catId2 = genId.next()
        val catId3 = genId.next()

        val where = generateWhereClause(
            ByCategoryIdIn(nonEmptyListOf(catId1, catId2, catId3)),
            timeProvider = timeProvider
        )

        where.query shouldBe "categoryId IN (?, ?, ?)"
        where.args shouldBe listOf(catId1, catId2, catId3)
    }
    // endregion

    // region property-based
    "generate ById" {
        val byId = genById.next()

        val res = generateWhereClause(byId, timeProvider = timeProvider)

        res shouldBe WhereClause(
            query = "id = ?",
            args = listOf(byId.id)
        )
    }

    // region ByCategoryId
    "generate ByCategoryId null" {
        val byCategory = ByCategoryId(categoryId = null)

        val res = generateWhereClause(byCategory, timeProvider = timeProvider)

        res.query shouldBe "categoryId IS NULL"
        res.args shouldBe emptyList()
    }

    "generate ByCategoryId non-null" {
        val byCategory = ByCategoryId(categoryId = genId.next())

        val res = generateWhereClause(byCategory, timeProvider = timeProvider)

        res.query shouldBe "categoryId = ?"
        res.args shouldBe listOf(byCategory.categoryId)
    }
    // endregion

    "generate ByAccountId" {
        val byAccount = genByAccount.next()

        val res = generateWhereClause(byAccount, timeProvider = timeProvider)

        res shouldBe WhereClause(
            query = "accountId = ?",
            args = listOf(byAccount.accountId)
        )
    }

    "generate ByType" {
        checkAll(genByType) { byType ->
            val res = generateWhereClause(byType, timeProvider = timeProvider)

            res.query shouldBe "type = ?"
            res.args shouldBe listOf(byType.trnType.code)
        }
    }

    "generate ByPurpose" {
        checkAll(genByPurpose) { byPurpose ->
            val res = generateWhereClause(byPurpose, timeProvider = timeProvider)

            res.query shouldBe "purpose = ?"
            res.args shouldBe listOf(byPurpose.purpose?.code)
        }
    }

    "generate BySync" {
        checkAll(genBySync) { bySync ->
            val res = generateWhereClause(bySync, timeProvider = timeProvider)

            res.query shouldBe "sync = ?"
            res.args shouldBe listOf(bySync.sync.code)
        }
    }

    "generate ActualBetween" {
        checkAll(genActualBetween) { actualBetween ->
            val res = generateWhereClause(actualBetween, timeProvider = timeProvider)

            res.query shouldBe "(timeType = ${TrnTimeType.Actual.code}" +
                    " AND time >= ? AND time <= ?)"
            res.args shouldBe listOf(
                actualBetween.range.from.toEpochSeconds(timeProvider),
                actualBetween.range.to.toEpochSeconds(timeProvider),
            )
        }
    }

    "generate DueBetween" {
        checkAll(genDueBetween) { dueBetween ->
            val res = generateWhereClause(dueBetween, timeProvider = timeProvider)

            res.query shouldBe "(timeType = ${TrnTimeType.Due.code}" +
                    " AND time >= ? AND time <= ?)"
            res.args shouldBe listOf(
                dueBetween.range.from.toEpochSeconds(timeProvider),
                dueBetween.range.to.toEpochSeconds(timeProvider),
            )
        }
    }

    fun placeholders(n: Int) = "?, ".repeat(n - 1) + "?"

    "generate ByIdIn" {
        checkAll(genByIdIn) { byIdIn ->
            val res = generateWhereClause(byIdIn, timeProvider = timeProvider)

            res.query shouldBe "id IN (${placeholders(byIdIn.ids.size)})"
            res.args shouldBe byIdIn.ids.toList().map { it }
        }
    }

    "generate ByAccountIn" {
        checkAll(genByAccountIn) { byAccountIn ->
            val res = generateWhereClause(byAccountIn, timeProvider = timeProvider)

            res.query shouldBe "accountId IN (${placeholders(byAccountIn.accountIds.size)})"
            res.args shouldBe byAccountIn.accountIds.toList()
        }
    }

    // region ByCategoryIn
    "generate ByCategoryIdIn non-null categories only" {
        val gen = arbitrary {
            ByCategoryIdIn(categoryIds = Arb.nonEmptyList(genId, 1..10).bind())
        }
        checkAll(gen) { byCategoryIn ->
            val categoryIds = byCategoryIn.categoryIds

            val res = generateWhereClause(byCategoryIn, timeProvider = timeProvider)

            res.query shouldBe "categoryId IN (${placeholders(byCategoryIn.categoryIds.size)})"
            res.args shouldBe categoryIds
        }
    }

    "generate ByCategoryIdIn null category only" {
        val res = generateWhereClause(
            where = ByCategoryIdIn(nonEmptyListOf(null)),
            timeProvider = timeProvider
        )

        res.query shouldBe "categoryId IS NULL"
        res.args shouldBe emptyList()
    }

    "generate ByCategoryIdIn nullable categories" {
        val gen = arbitrary {
            ByCategoryIdIn(
                categoryIds = Arb.nonEmptyList(genId, 1..10).bind()
                    .plus(null)
                    .toNonEmptyList()
            )
        }
        checkAll(gen) { byCategoryIn ->
            val nonNullCategoryIds = byCategoryIn.categoryIds.filterNotNull()

            val res = generateWhereClause(byCategoryIn, timeProvider = timeProvider)

            res.query shouldBe "(categoryId IN (${placeholders(nonNullCategoryIds.size)}) " +
                    "OR categoryId IS NULL)"
            res.args shouldBe nonNullCategoryIds
        }
    }
    // endregion

    "generate ByTypeIn" {
        checkAll(genByTypeIn) { byTypeIn ->
            val types = byTypeIn.types

            val res = generateWhereClause(byTypeIn, timeProvider = timeProvider)

            res.query shouldBe "type IN (${placeholders(types.size)})"
            res.args shouldBe types.toList().map { it.code }
        }
    }

    "generate ByPurposeIn" {
        checkAll(genByPurposeIn) { byPurposeIn ->
            val purposes = byPurposeIn.purposes

            val res = generateWhereClause(byPurposeIn, timeProvider = timeProvider)

            res.query shouldBe "purpose IN (${placeholders(purposes.size)})"
            res.args shouldBe purposes.toList().map { it.code }
        }
    }


    "generate Brackets" {
        checkAll(genBrackets) { brackets ->
            val condWhere = generateWhereClause(brackets, timeProvider = timeProvider)

            val res = generateWhereClause(brackets(brackets), timeProvider = timeProvider)

            res.query shouldBe "(${condWhere.query})"
            res.args shouldBe condWhere.args
        }
    }

    "generate Not" {
        checkAll(genNot) { not ->
            val condWhere = generateWhereClause(not.cond, timeProvider = timeProvider)

            val res = generateWhereClause(not, timeProvider = timeProvider)

            res.query shouldBe "NOT(${condWhere.query})"
            res.args shouldBe condWhere.args
        }
    }

    "generate non-recursive And" {
        checkAll(genNonRecursiveAnd) { and ->
            val whereCond1 = generateWhereClause(and.cond1, timeProvider = timeProvider)
            val whereCond2 = generateWhereClause(and.cond2, timeProvider = timeProvider)

            val res = generateWhereClause(and, timeProvider = timeProvider)

            res.query shouldBe "${whereCond1.query} AND ${whereCond2.query}"
            res.args shouldBe (whereCond1.args + whereCond2.args)
        }
    }

    "generate non-recursive Or" {
        checkAll(genNonRecursiveOr) { or ->
            val whereCond1 = generateWhereClause(or.cond1, timeProvider = timeProvider)
            val whereCond2 = generateWhereClause(or.cond2, timeProvider = timeProvider)

            val res = generateWhereClause(or, timeProvider = timeProvider)

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
            // Arrange
            val whereCond1 = generateWhereClause(and.cond1, timeProvider = timeProvider)
            val whereCond2 = generateWhereClause(and.cond2, timeProvider = timeProvider)

            // Act
            val res = generateWhereClause(and, timeProvider = timeProvider)

            // Assert
            res.query shouldBe "${whereCond1.query} AND ${whereCond2.query}"
            res.args shouldBe (whereCond1.args + whereCond2.args)

            // Log
            labelRecursive("cond2", and.cond2)
            collect("args_count", res.args.size)
        }
    }

    "generate recursive Or" {
        checkAll(genRecursiveOr) { or ->
            // Arrange
            val whereCond1 = generateWhereClause(or.cond1, timeProvider = timeProvider)
            val whereCond2 = generateWhereClause(or.cond2, timeProvider = timeProvider)

            // Act
            val res = generateWhereClause(or, timeProvider = timeProvider)

            // Assert
            res.query shouldBe "${whereCond1.query} OR ${whereCond2.query}"
            res.args shouldBe (whereCond1.args + whereCond2.args)

            // Log
            labelRecursive("cond2", or.cond2)
            collect("args_count", res.args.size)
        }
    }

    "every query has only allowed argument types" {
        checkAll(genComplexQuery) { complexQuery ->
            val res = generateWhereClause(complexQuery, timeProvider = timeProvider)

            // Every query must have only allowed types as arguments
            res.args.forEach {
                val acceptedType = when (it) {
                    is String, is Long, is Boolean, is Int -> true
                    else -> false
                }
                if (!acceptedType) {
                    fail("$it (${it?.javaClass?.name}) is not accepted type.")
                }
            }
        }
    }
    //endregion
})