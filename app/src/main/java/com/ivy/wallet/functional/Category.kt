package com.ivy.wallet.functional

//suspend fun calculateAccountValues(
//    accountId: UUID,
//    retrieveAccountTransactions: suspend (UUID) -> List<Transaction>,
//    retrieveToAccountTransfers: suspend (UUID) -> List<Transaction>,
//    valueFunctions: NonEmptyList<(FPTransaction, accountId: UUID) -> BigDecimal>
//): NonEmptyList<BigDecimal> {
//    val accountTransactions = retrieveAccountTransactions(accountId)
//        .plus(retrieveToAccountTransfers(accountId))
//        .map { it.toFPTransaction() }
//
//    return calculateValueFunctionsSum(
//        categoryId = accountId,
//        transactions = accountTransactions,
//        valueFunctions = valueFunctions
//    )
//}
