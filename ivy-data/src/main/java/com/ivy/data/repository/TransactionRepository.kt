package com.ivy.data.repository

import com.ivy.data.source.TransactionDataSource
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val dataSource: TransactionDataSource
) {
    // TODO: Implement
}