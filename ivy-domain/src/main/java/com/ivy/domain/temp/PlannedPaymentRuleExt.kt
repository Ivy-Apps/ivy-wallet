package com.ivy.domain.temp

import com.ivy.domain.datamodel.PlannedPaymentRule
import com.ivy.persistence.db.entity.PlannedPaymentRuleEntity

fun PlannedPaymentRuleEntity.toDomain(): PlannedPaymentRule = PlannedPaymentRule(
    startDate = startDate,
    intervalN = intervalN,
    intervalType = intervalType,
    oneTime = oneTime,
    type = type,
    accountId = accountId,
    amount = amount,
    categoryId = categoryId,
    title = title,
    description = description,
    isSynced = isSynced,
    isDeleted = isDeleted,
    id = id
)