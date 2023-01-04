package com.ivy.data.transaction

import com.ivy.data.Value

data class DueSection(
    val income: Value,
    val expense: Value,
    val trns: List<TrnListItem>,
)