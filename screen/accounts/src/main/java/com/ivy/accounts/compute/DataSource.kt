package com.ivy.accounts.compute

import com.ivy.data.model.Transaction

sealed interface DataSource {
    data object DB : DataSource
    data class Custom(val transactions: List<Transaction>) : DataSource
}