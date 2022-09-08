package com.ivy.reports.actions

import com.ivy.core.action.FlowAction
import com.ivy.core.action.account.AccountsFlow
import com.ivy.reports.data.SelectableAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReportAccountsFlow @Inject constructor(
    private val accountsFlow: AccountsFlow
) : FlowAction<Unit, List<SelectableAccount>>() {

    override fun Unit.createFlow(): Flow<List<SelectableAccount>> = accountsFlow().map {
        it.map { a -> SelectableAccount(a) }
    }
}