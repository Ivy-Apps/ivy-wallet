package com.ivy.core.functions.transaction

import com.ivy.core.functions.toRange
import com.ivy.core.functions.transaction.TrnQuery.*
import com.ivy.data.Period
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.transaction.TrnType
import java.util.*

sealed class TrnQuery {
    data class ById(val id: UUID) : TrnQuery()
    data class ByCategory(val category: Category?) : TrnQuery()
    data class ByAccount(val account: Account) : TrnQuery()
    data class ByType(val trnType: TrnType) : TrnQuery()

    /**
     * Inclusive period [from, to]
     */
    data class DueBetween(val period: Period) : TrnQuery()

    /**
     * Inclusive period [from, to]
     */
    data class ActualBetween(val period: Period) : TrnQuery()

    data class Brackets(val cond: TrnQuery) : TrnQuery()
    data class And(val cond1: TrnQuery, val cond2: TrnQuery) : TrnQuery()
    data class Or(val cond1: TrnQuery, val cond2: TrnQuery) : TrnQuery()
}

infix fun TrnQuery.and(cond2: TrnQuery): And = And(this, cond2)
infix fun TrnQuery.or(cond2: TrnQuery): And = And(this, cond2)

data class WhereClause(
    val query: String,
    val args: List<Any>
)

private object EmptyArg

fun toWhereClause(query: TrnQuery): WhereClause {
    fun <T> arg(arg: T): List<T> = listOf(arg)
    fun noArg() = arg(EmptyArg)

    val result = when (query) {
        is ActualBetween -> "(dateTime >= ? AND dateTime <= ?)" to arg(
            query.period.toRange().toList()
        )
        is ByAccount -> "accountId = ?" to arg(query.account.id)
        is ByCategory -> {
            query.category?.id?.let {
                "categoryId = ?" to arg(it)
            } ?: ("categoryId IS NULL" to noArg())
        }
        is ById -> "id = ?" to arg(query.id)
        is ByType -> "type = ?" to arg(query.trnType)
        is DueBetween -> {
            "(dueDate >= ? AND dueDate <= ?)" to arg(query.period.toRange().toList())
        }
        is Brackets -> {
            val clause = toWhereClause(query.cond)
            "(${clause.query})" to clause.args
        }
        is And -> {
            val clause1 = toWhereClause(query.cond1)
            val clause2 = toWhereClause(query.cond2)

            "${clause1.query} AND ${clause2.query}" to (clause1.args + clause2.args)
        }
        is Or -> {
            val clause1 = toWhereClause(query.cond1)
            val clause2 = toWhereClause(query.cond2)

            "${clause1.query} OR ${clause2.query}" to (clause1.args + clause2.args)
        }
    }

    val args = flatten(result.second.filter { it !is EmptyArg })

    return WhereClause(
        query = result.first,
        args = args
    )
}

@Suppress("UNCHECKED_CAST")
private fun flatten(list: List<Any>): List<Any> {
    val result = mutableListOf<Any>()

    for (item in list) {
        if (item is List<*>) {
            result.addAll(item as List<Any>)
        } else {
            result.add(item)
        }
    }

    return result
}