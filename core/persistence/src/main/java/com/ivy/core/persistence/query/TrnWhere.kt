package com.ivy.core.persistence.query

import arrow.core.NonEmptyList
import com.ivy.common.toEpochSeconds
import com.ivy.common.toRange
import com.ivy.core.persistence.entity.trn.data.TrnTimeType
import com.ivy.core.persistence.query.TrnWhere.*
import com.ivy.data.SyncState
import com.ivy.data.time.Period
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnType
import java.time.LocalDateTime
import java.util.*

sealed interface TrnWhere {
    data class ById(val id: String) : TrnWhere
    data class ByIdIn(val ids: NonEmptyList<String>) : TrnWhere

    data class ByCategoryId(val categoryId: String?) : TrnWhere
    data class ByCategoryIdIn(val categoryIds: NonEmptyList<String?>) : TrnWhere

    data class ByAccountId(val accountId: String) : TrnWhere
    data class ByAccountIdIn(val accountIds: NonEmptyList<String>) : TrnWhere

    data class ByType(val trnType: TrnType) : TrnWhere
    data class ByTypeIn(val types: NonEmptyList<TrnType>) : TrnWhere

    data class BySync(val sync: SyncState) : TrnWhere
    data class ByPurpose(val purpose: TrnPurpose?) : TrnWhere

    /**
     * Inclusive period [from, to]
     */
    data class DueBetween(val period: Period) : TrnWhere

    /**
     * Inclusive period [from, to]
     */
    data class ActualBetween(val period: Period) : TrnWhere

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

internal fun toWhereClause(where: TrnWhere): WhereClause {
    fun placeholders(argsCount: Int): String = when (argsCount) {
        0 -> ""
        1 -> "?"
        else -> "?, " + placeholders(argsCount - 1)
    }

    fun <T> arg(arg: T): List<T> = listOf(arg)
    fun noArg() = arg(EmptyArg)

    fun uuid(id: UUID): String = id.toString()
    fun timestamp(dateTime: LocalDateTime): Long = dateTime.toEpochSeconds()
    fun trnType(type: TrnType): Int = type.code

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
                0 -> "categoryId is NULL" to nonNullArgs
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
                where.period.toRange().toList().map(::timestamp)
            )
        }
        is ActualBetween ->
            "(timeType = ${TrnTimeType.Actual.code} AND time >= ? AND time <= ?)" to arg(
                where.period.toRange().toList().map(::timestamp)
            )

        is Brackets -> {
            val clause = toWhereClause(where.cond)
            "(${clause.query})" to clause.args
        }
        is And -> {
            val clause1 = toWhereClause(where.cond1)
            val clause2 = toWhereClause(where.cond2)

            "${clause1.query} AND ${clause2.query}" to (clause1.args + clause2.args)
        }
        is Or -> {
            val clause1 = toWhereClause(where.cond1)
            val clause2 = toWhereClause(where.cond2)

            "${clause1.query} OR ${clause2.query}" to (clause1.args + clause2.args)
        }
        is Not -> {
            val clause = toWhereClause(where.cond)
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