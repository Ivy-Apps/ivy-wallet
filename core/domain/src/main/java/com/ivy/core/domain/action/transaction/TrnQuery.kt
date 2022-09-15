package com.ivy.core.domain.action.transaction

import arrow.core.NonEmptyList
import com.ivy.core.persistence.query.TrnWhere
import com.ivy.data.time.Period
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnType

sealed interface TrnQuery {
    data class ById(val id: String) : TrnQuery
    data class ByIdIn(val ids: NonEmptyList<String>) : TrnQuery

    data class ByCategoryId(val categoryId: String?) : TrnQuery
    data class ByCategoryIdIn(val categoryIds: NonEmptyList<String?>) : TrnQuery

    data class ByAccountId(val accountId: String) : TrnQuery
    data class ByAccountIdIn(val accountIds: NonEmptyList<String>) : TrnQuery

    data class ByType(val trnType: TrnType) : TrnQuery
    data class ByTypeIn(val types: NonEmptyList<TrnType>) : TrnQuery

    data class ByPurpose(val purpose: TrnPurpose?) : TrnQuery

    /**
     * Inclusive period [from, to]
     */
    data class DueBetween(val period: Period) : TrnQuery

    /**
     * Inclusive period [from, to]
     */
    data class ActualBetween(val period: Period) : TrnQuery

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
    is TrnQuery.ActualBetween -> TrnWhere.ActualBetween(period)
    is TrnQuery.And -> TrnWhere.And(cond1.toTrnWhere(), cond2.toTrnWhere())
    is TrnQuery.Brackets -> TrnWhere.Brackets(cond.toTrnWhere())
    is TrnQuery.ByAccountId -> TrnWhere.ByAccountId(accountId)
    is TrnQuery.ByAccountIdIn -> TrnWhere.ByAccountIdIn(accountIds)
    is TrnQuery.ByCategoryId -> TrnWhere.ByCategoryId(categoryId)
    is TrnQuery.ByCategoryIdIn -> TrnWhere.ByCategoryIdIn(categoryIds)
    is TrnQuery.ById -> TrnWhere.ById(id)
    is TrnQuery.ByIdIn -> TrnWhere.ByIdIn(ids)
    is TrnQuery.ByPurpose -> TrnWhere.ByPurpose(purpose)
    is TrnQuery.ByType -> TrnWhere.ByType(trnType)
    is TrnQuery.ByTypeIn -> TrnWhere.ByTypeIn(types)
    is TrnQuery.DueBetween -> TrnWhere.DueBetween(period)
    is TrnQuery.Not -> TrnWhere.Not(cond.toTrnWhere())
    is TrnQuery.Or -> TrnWhere.Or(cond1.toTrnWhere(), cond2.toTrnWhere())
}