package com.ivy.navigation.graph

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ivy.navigation.destinations.transaction.*

@Immutable
data class TransactionScreens(
    val accountTransactions: @Composable (accountId: String) -> Unit,
    val categoryTransactions: @Composable (categoryId: String) -> Unit,
    val newTransaction: @Composable (NewTransaction.Arg) -> Unit,
    val transaction: @Composable (trnId: String) -> Unit,
    val newTransfer: @Composable () -> Unit,
    val transfer: @Composable (batchId: String) -> Unit,
)

fun NavGraphBuilder.transactionScreens(
    screens: TransactionScreens
) {
    composable(AccountTransactions.route) {
        screens.accountTransactions(AccountTransactions.parse(it))
    }
    composable(CategoryTransactions.route) {
        screens.categoryTransactions(CategoryTransactions.parse(it))
    }
    composable(NewTransaction.route) {
        screens.newTransaction(NewTransaction.parse(it))
    }
    composable(Transaction.route) {
        screens.transaction(Transaction.parse(it))
    }
    composable(NewTransfer.route) {
        screens.newTransfer()
    }
    composable(Transfer.route) {
        screens.transfer(Transfer.parse(it))
    }
}