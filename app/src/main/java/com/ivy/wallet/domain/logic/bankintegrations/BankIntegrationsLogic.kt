package com.ivy.wallet.domain.logic.bankintegrations

import com.ivy.wallet.domain.data.bankintegrations.SEAccount
import com.ivy.wallet.domain.data.bankintegrations.SEConnection
import com.ivy.wallet.domain.data.bankintegrations.SETransaction
import com.ivy.wallet.io.network.IvySession
import com.ivy.wallet.io.network.RestClient
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.ui.IvyActivity

class BankIntegrationsLogic(
    restClient: RestClient,
    private val seTransactionMapper: SaltEdgeTransactionMapper,
    private val ivySession: IvySession,
    private val sharedPrefs: SharedPrefs
) {
    private val bankIntegrationsService = restClient.bankIntegrationsService

    suspend fun connect(
        ivyActivity: IvyActivity
    ) {
        val response = bankIntegrationsService.connectSession()
        ivyActivity.openUrlInBrowser(response.connectUrl)
    }

    suspend fun fetchConnections(): List<SEConnection> {
        if (!hasBankConnection()) return emptyList() //do nothing if the user isn't logged in

        return bankIntegrationsService.getConnections().connections
    }

    suspend fun sync() {
        if (!hasBankConnection()) return //do nothing if the user isn't logged in

        val seAccounts = fetchAccounts()
        val seTransactions = fetchTransactions()

        seTransactionMapper.save(
            seAccounts = seAccounts,
            seTransactions = seTransactions
        )
    }

    private suspend fun hasBankConnection(): Boolean {
        //TODO: Check for SEConnections in table
        return ivySession.isLoggedIn() && sharedPrefs.getBoolean(
            SharedPrefs.ENABLE_BANK_SYNC,
            false
        )
    }

    private suspend fun fetchAccounts(): List<SEAccount> {
        return bankIntegrationsService.getAccounts().accounts
    }

    private suspend fun fetchTransactions(): List<SETransaction> {
        return bankIntegrationsService.getTransactions().transactions
    }

    suspend fun removeCustomer() {
        bankIntegrationsService.removeCustomer()
    }
}