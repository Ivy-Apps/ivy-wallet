package com.ivy.legacy.datamodel.temp

import com.ivy.legacy.datamodel.PlannedPaymentRule
import com.ivy.data.db.entity.PlannedPaymentRuleEntity

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