package com.ivy.core.ui.action

import android.content.Context
import com.ivy.core.domain.action.SharedFlowAction
import com.ivy.core.domain.action.account.AccountsFlow
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.design.l0_system.color.toComposeColor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountsUiFlow @Inject constructor(
    @ApplicationContext
    private val appContext: Context,
    private val accountsFlow: AccountsFlow,
) : SharedFlowAction<Map<String, AccountUi>?>() {
    override fun initialValue(): Map<String, AccountUi>? = null

    override fun createFlow(): Flow<Map<String, AccountUi>?> = accountsFlow().map { accs ->
        accs.associate {
            val id = it.id.toString()
            id to AccountUi(
                id = id,
                name = it.name,
                color = it.color.toComposeColor(),
                icon = itemIcon(
                    appContext = appContext,
                    iconId = it.icon,
                    defaultTo = DefaultTo.Account,
                ),
                excluded = it.excluded,
            )
        }
    }
}