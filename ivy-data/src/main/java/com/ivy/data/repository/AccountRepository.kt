package com.ivy.data.repository

import com.ivy.data.source.AccountDataSource
import javax.inject.Inject

class AccountRepository @Inject constructor(
    private val dataSource: AccountDataSource
) {
    // TODO: Implement
}