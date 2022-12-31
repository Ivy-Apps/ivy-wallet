package com.ivy.core.domain.action.transaction

import arrow.core.NonEmptyList
import com.ivy.core.persistence.query.TrnWhere
import com.ivy.data.time.TimeRange
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnPurpose
import java.util.*

sealed interface TrnQuery {
    data class ById(val id: UUID) : TrnQuery
    data class ByIdIn(val ids: NonEmptyList<UUID>) : TrnQuery

    data class ByCategoryId(val categoryId: UUID?) : TrnQuery
    data class ByCategoryIdIn(val categoryIds: NonEmptyList<UUID?>) : TrnQuery

    data class ByAccountId(val accountId: UUID) : TrnQuery
    data class ByAccountIdIn(val accountIds: NonEmptyList<UUID>) : TrnQuery

    data class ByType(val trnType: TransactionType) : TrnQuery
    data class ByTypeIn(val types: NonEmptyList<TransactionType>) : TrnQuery

    data class ByPurpose(val purpose: TrnPurpose?) : TrnQuery
    data class ByPurposeIn(val purposes: NonEmptyList<TrnPurpose>) : TrnQuery

    /**
     * Inclusive period [from, to]
     */
    data class DueBetween(val range: TimeRange) : TrnQuery

    /**
     * Inclusive period [from, to]
     */
    data class ActualBetween(val range: TimeRange) : TrnQuery

    data class Brackets(val cond: TrnQuery) : TrnQuery
    data class And(val cond1: TrnQuery, val cond2: TrnQuery) : TrnQuery
    data class Or(val cond1: TrnQuery, val cond2: TrnQuery) : TrnQuery
    data class Not(val cond: TrnQuery) : TrnQuery
}

fun brackets(cond: TrnQuery): TrnQuery.Brackets = TrnQuery.Brackets(cond)
infix fun TrnQuery.and(cond2: TrnQuery): TrnQuery.And = TrnQuery.And(this, cond2)
infix fun TrnQuery.or(cond2: TrnQuery): TrnQuery.Or = TrnQuery.Or(this, cond2)
fun not(cond: TrnQuery): TrnQuery.Not = TrnQuery.Not(cond)

fun TrnQuery.toTrnWhere(): TrnWhere = when (this) {
    is TrnQuery.ActualBetween -> TrnWhere.ActualBetween(range)
    is TrnQuery.And -> TrnWhere.And(cond1.toTrnWhere(), cond2.toTrnWhere())
    is TrnQuery.Brackets -> TrnWhere.Brackets(cond.toTrnWhere())
    is TrnQuery.ByAccountId -> TrnWhere.ByAccountId(accountId.toString())
    is TrnQuery.ByAccountIdIn -> TrnWhere.ByAccountIdIn(accountIds.map { it.toString() })
    is TrnQuery.ByCategoryId -> TrnWhere.ByCategoryId(categoryId?.toString())
    is TrnQuery.ByCategoryIdIn -> TrnWhere.ByCategoryIdIn(categoryIds.map { it?.toString() })
    is TrnQuery.ById -> TrnWhere.ById(id.toString())
    is TrnQuery.ByIdIn -> TrnWhere.ByIdIn(ids.map { it.toString() })
    is TrnQuery.ByPurpose -> TrnWhere.ByPurpose(purpose)
    is TrnQuery.ByPurposeIn -> TrnWhere.ByPurposeIn(purposes)
    is TrnQuery.ByType -> TrnWhere.ByType(trnType)
    is TrnQuery.ByTypeIn -> TrnWhere.ByTypeIn(types)
    is TrnQuery.DueBetween -> TrnWhere.DueBetween(range)
    is TrnQuery.Not -> TrnWhere.Not(cond.toTrnWhere())
    is TrnQuery.Or -> TrnWhere.Or(cond1.toTrnWhere(), cond2.toTrnWhere())
}