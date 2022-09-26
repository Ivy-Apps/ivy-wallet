package com.ivy.navigation.graph

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.navigation.NavGraphBuilder

@Immutable
data class TransactionScreens(
    val accountTransactions: @Composable (accountId: String) -> Unit,
    val categoryTransactions: @Composable (categoryId: String) -> Unit,
)

fun NavGraphBuilder.transactionScreens() {

}