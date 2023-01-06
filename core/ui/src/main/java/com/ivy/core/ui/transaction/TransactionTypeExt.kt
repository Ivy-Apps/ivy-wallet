package com.ivy.core.ui.transaction

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ivy.data.transaction.TransactionType
import com.ivy.design.l0_system.color.Green
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.resources.R

@Composable
fun TransactionType.humanText(): String = when (this) {
    TransactionType.Income -> stringResource(R.string.income)
    TransactionType.Expense -> stringResource(R.string.expense)
}

@DrawableRes
fun TransactionType.icon(): Int = when (this) {
    TransactionType.Income -> R.drawable.ic_income
    TransactionType.Expense -> R.drawable.ic_expense
}

fun TransactionType.feeling(): Feeling = when (this) {
    TransactionType.Income -> Feeling.Custom(Green)
    TransactionType.Expense -> Feeling.Negative
}