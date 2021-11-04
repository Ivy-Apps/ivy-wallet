package com.ivy.wallet.network.service

import com.ivy.wallet.network.request.bankintegrations.BankAccountsResponse
import com.ivy.wallet.network.request.bankintegrations.BankConnectionSessionResponse
import com.ivy.wallet.network.request.bankintegrations.BankConnectionsResponse
import com.ivy.wallet.network.request.bankintegrations.BankTransactionsResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface BankIntegrationsService {
    @POST("wallet/bank-integrations/connect")
    suspend fun connectSession(): BankConnectionSessionResponse

    @GET("wallet/bank-integrations/connections")
    suspend fun getConnections(): BankConnectionsResponse

    @GET("wallet/bank-integrations/accounts")
    suspend fun getAccounts(): BankAccountsResponse

    @GET("wallet/bank-integrations/transactions")
    suspend fun getTransactions(): BankTransactionsResponse

    @DELETE("wallet/bank-integrations/remove-customer")
    suspend fun removeCustomer()
}