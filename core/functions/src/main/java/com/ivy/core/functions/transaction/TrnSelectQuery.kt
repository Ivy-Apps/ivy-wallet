package com.ivy.core.functions.transaction

import arrow.core.NonEmptyList
import com.ivy.common.toEpochMilli
import com.ivy.core.functions.toRange
import com.ivy.core.functions.transaction.TrnWhere.*
import com.ivy.data.Period
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.transaction.TrnType
import java.time.LocalDateTime
import java.util.*

sealed class TrnWhere {
    data class ById(val id: UUID) : TrnWhere()
    data class ByIdIn(val ids: NonEmptyList<UUID>) : TrnWhere()

    data class ByCategory(val category: Category?) : TrnWhere()
    data class ByCategoryIn(val categories: NonEmptyList<Category?>) : TrnWhere()

    data class ByAccount(val account: Account) : TrnWhere()
    data class ByAccountIn(val accs: NonEmptyList<Account>) : TrnWhere()

    data class ByToAccount(val toAccount: Account) : TrnWhere()
    data class ByToAccountIn(val toAccs: NonEmptyList<Account>) : TrnWhere()

    data class ByType(val trnType: TrnType) : TrnWhere()
    data class ByTypeIn(val types: NonEmptyList<TrnType>) : TrnWhere()

    /**
     * Inclusive period [from, to]
     */
    data class DueBetween(val period: Period) : TrnWhere()

    /**
     * Inclusive period [from, to]
     */
    data class ActualBetween(val period: Period) : TrnWhere()

    data class Brackets(val cond: TrnWhere) : TrnWhere()
    data class And(val cond1: TrnWhere, val cond2: TrnWhere) : TrnWhere()
    data class Or(val cond1: TrnWhere, val cond2: TrnWhere) : TrnWhere()
    data class Not(val cond: TrnWhere) : TrnWhere()
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

fun toWhereClause(where: TrnWhere): WhereClause {
    fun placeholders(argsCount: Int): String = when (argsCount) {
        0 -> ""
        1 -> "?"
        else -> "?, " + placeholders(argsCount - 1)
    }

    fun <T> arg(arg: T): List<T> = listOf(arg)
    fun noArg() = arg(EmptyArg)

    fun uuid(id: UUID): String = id.toString()
    fun timestamp(dateTime: LocalDateTime): Long = dateTime.toEpochMilli()
    fun trnType(type: TrnType): String = type.name

    val result = when (where) {
        is ById -> "id = ?" to arg(uuid(where.id))
        is ByIdIn ->
            "id IN (${placeholders(where.ids.size)})" to arg(where.ids.map(::uuid).toList())

        is ByType -> "type = ?" to arg(trnType(where.trnType))
        is ByTypeIn ->
            "type IN (${placeholders(where.types.size)})" to arg(
                where.types.map(::trnType).toList()
            )

        is ByAccount -> "accountId = ?" to arg(uuid(where.account.id))
        is ByAccountIn ->
            "accountId IN (${placeholders(where.accs.size)})" to arg(where.accs.map { uuid(it.id) })
        is ByToAccount -> "toAccountId = ?" to arg(uuid(where.toAccount.id))
        is ByToAccountIn ->
            "toAccountId IN (${placeholders(where.toAccs.size)})" to arg(
                where.toAccs.map { uuid(it.id) }
            )

        is ByCategory -> {
            where.category?.id?.let {
                "categoryId = ?" to arg(uuid(it))
            } ?: ("categoryId IS NULL" to noArg())
        }
        is ByCategoryIn -> {
            val nonNullArgs = where.categories.filterNotNull()
            when (nonNullArgs.size) {
                0 -> "categoryId is NULL" to nonNullArgs
                where.categories.size ->
                    // only non-null args
                    "categoryId IN (${placeholders(nonNullArgs.size)})" to arg(
                        nonNullArgs.map { uuid(it.id) }
                    )
                else ->
                    // non-null args + null
                    "(categoryId IN (${placeholders(nonNullArgs.size)}) OR categoryId IS NULL)" to arg(
                        nonNullArgs.map { uuid(it.id) }
                    )
            }
        }

        is DueBetween -> {
            "(dueDate IS NOT NULL AND dueDate >= ? AND dueDate <= ?)" to arg(
                where.period.toRange().toList().map(::timestamp)
            )
        }
        is ActualBetween -> "(dateTime IS NOT NULL AND dateTime >= ? AND dateTime <= ?)" to arg(
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