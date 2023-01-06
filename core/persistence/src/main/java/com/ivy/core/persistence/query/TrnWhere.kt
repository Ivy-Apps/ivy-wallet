package com.ivy.core.persistence.query

import arrow.core.NonEmptyList
import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.toEpochSeconds
import com.ivy.common.time.toPair
import com.ivy.core.persistence.entity.trn.data.TrnTimeType
import com.ivy.core.persistence.query.TrnWhere.*
import com.ivy.data.SyncState
import com.ivy.data.time.TimeRange
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnPurpose
import java.time.LocalDateTime

sealed interface TrnWhere {
    data class ById(val id: String) : TrnWhere
    data class ByIdIn(val ids: NonEmptyList<String>) : TrnWhere

    data class ByCategoryId(val categoryId: String?) : TrnWhere
    data class ByCategoryIdIn(val categoryIds: NonEmptyList<String?>) : TrnWhere

    data class ByAccountId(val accountId: String) : TrnWhere
    data class ByAccountIdIn(val accountIds: NonEmptyList<String>) : TrnWhere

    data class ByType(val trnType: TransactionType) : TrnWhere
    data class ByTypeIn(val types: NonEmptyList<TransactionType>) : TrnWhere

    data class BySync(val sync: SyncState) : TrnWhere
    data class ByPurpose(val purpose: TrnPurpose?) : TrnWhere
    data class ByPurposeIn(val purposes: NonEmptyList<TrnPurpose>) : TrnWhere

    /**
     * Inclusive period [from, to]
     */
    data class DueBetween(val range: TimeRange) : TrnWhere

    /**
     * Inclusive period [from, to]
     */
    data class ActualBetween(val range: TimeRange) : TrnWhere

    data class Brackets(val cond: TrnWhere) : TrnWhere
    data class And(val cond1: TrnWhere, val cond2: TrnWhere) : TrnWhere
    data class Or(val cond1: TrnWhere, val cond2: TrnWhere) : TrnWhere
    data class Not(val cond: TrnWhere) : TrnWhere
}

fun brackets(cond: TrnWhere): Brackets = Brackets(cond)
infix fun TrnWhere.and(cond2: TrnWhere): And = And(this, cond2)
infix fun TrnWhere.or(cond2: TrnWhere): Or = Or(this, cond2)
fun not(cond: TrnWhere): Not = Not(cond)

data class WhereClause(
    val query: String,
    val args: List<Any?>
)

private object EmptyArg

internal fun generateWhereClause(
    where: TrnWhere,
    timeProvider: TimeProvider
): WhereClause {
    fun placeholders(argsCount: Int): String = when (argsCount) {
        0 -> ""
        1 -> "?"
        else -> "?, " + placeholders(argsCount - 1)
    }

    fun <T> arg(arg: T): List<T> = listOf(arg)
    fun noArg() = arg(EmptyArg)

    fun timestamp(dateTime: LocalDateTime): Long =
        dateTime.toEpochSeconds(timeProvider)

    fun trnType(type: TransactionType): Int = type.code

    val result = when (where) {
        is ById -> "id = ?" to arg(where.id)
        is ByIdIn ->
            "id IN (${placeholders(where.ids.size)})" to arg(where.ids.toList())

        is ByType -> "type = ?" to arg(trnType(where.trnType))
        is ByTypeIn ->
            "type IN (${placeholders(where.types.size)})" to arg(
                where.types.map(::trnType).toList()
            )

        is BySync -> "sync = ?" to arg(where.sync.code)

        is ByPurpose -> where.purpose?.let {
            "purpose = ?" to arg(where.purpose.code)
        } ?: ("purpose IS NULL" to noArg())
        is ByPurposeIn ->
            "purpose IN (${placeholders(where.purposes.size)})" to arg(
                where.purposes.map { it.code }.toList()
            )

        is ByAccountId -> "accountId = ?" to arg(where.accountId)
        is ByAccountIdIn ->
            "accountId IN (${placeholders(where.accountIds.size)})" to arg(
                where.accountIds.toList()
            )

        is ByCategoryId -> {
            where.categoryId?.let {
                "categoryId = ?" to arg(it)
            } ?: ("categoryId IS NULL" to noArg())
        }
        is ByCategoryIdIn -> {
            val nonNullArgs = where.categoryIds.filterNotNull()
            when (nonNullArgs.size) {
                0 -> "categoryId IS NULL" to nonNullArgs
                where.categoryIds.size ->
                    // only non-null args
                    "categoryId IN (${placeholders(nonNullArgs.size)})" to
                            arg(nonNullArgs)
                else ->
                    // non-null args + null
                    "(categoryId IN (${placeholders(nonNullArgs.size)}) OR categoryId IS NULL)" to
                            arg(nonNullArgs)
            }
        }

        is DueBetween -> {
            "(timeType = ${TrnTimeType.Due.code} AND time >= ? AND time <= ?)" to arg(
                where.range.toPair().toList().map(::timestamp)
            )
        }
        is ActualBetween ->
            "(timeType = ${TrnTimeType.Actual.code} AND time >= ? AND time <= ?)" to arg(
                where.range.toPair().toList().map(::timestamp)
            )

        is Brackets -> {
            val clause = generateWhereClause(where.cond, timeProvider)
            "(${clause.query})" to clause.args
        }
        is And -> {
            val clause1 = generateWhereClause(where.cond1, timeProvider)
            val clause2 = generateWhereClause(where.cond2, timeProvider)

            "${clause1.query} AND ${clause2.query}" to (clause1.args + clause2.args)
        }
        is Or -> {
            val clause1 = generateWhereClause(where.cond1, timeProvider)
            val clause2 = generateWhereClause(where.cond2, timeProvider)

            "${clause1.query} OR ${clause2.query}" to (clause1.args + clause2.args)
        }
        is Not -> {
            val clause = generateWhereClause(where.cond, timeProvider)
            "NOT(${clause.query})" to clause.args
        }
    }

    val args = flatten(result.second.filter { it !is EmptyArg })

    return WhereClause(
        query = result.first,
        args = args
    )
}

@Suppress("UNCHECKED_CAST")
private fun flatten(list: List<Any?>): List<Any?> {
    val result = mutableListOf<Any?>()

    for (item in list) {
        if (item is List<*>) {
            result.addAll(item as List<Any?>)
        } else {
            result.add(item)
        }
    }

    return result
}