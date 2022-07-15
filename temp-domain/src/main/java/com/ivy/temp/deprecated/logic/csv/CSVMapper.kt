package com.ivy.wallet.domain.deprecated.logic.csv

import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.wallet.domain.deprecated.logic.csv.model.ImportType
import com.ivy.wallet.domain.deprecated.logic.csv.model.JoinResult
import com.ivy.wallet.domain.deprecated.logic.csv.model.RowMapping
import com.ivy.wallet.utils.toLowerCaseLocal

class CSVMapper {

    @ExperimentalStdlibApi
    fun mapping(type: ImportType, headerRow: String?) = when (type) {
        ImportType.IVY -> {
            if (headerRow?.contains("Currency") == true) {
                ivyMappingV2()
            } else {
                ivyMappingV1()
            }
        }
        ImportType.MONEY_MANAGER -> moneyManager()
        ImportType.WALLET_BY_BUDGET_BAKERS -> walletByBudgetBakers()
        ImportType.SPENDEE -> spendee()
        ImportType.MONEFY -> monefy()
        ImportType.ONE_MONEY -> oneMoney()
        ImportType.BLUE_COINS -> blueCoins()
        ImportType.KTW_MONEY_MANAGER -> ktwMoneyManager()
        ImportType.FORTUNE_CITY -> fortuneCity()
        ImportType.FINANCISTO -> financisto()
        ImportType.MONEY_WALLET -> moneyWallet()
    }

    private fun ivyMappingV1() = RowMapping(
        date = 0,
        title = 1,
        category = 2,
        account = 3,
        amount = 4,
        type = 5,
        transferAmount = 6,
        toAccount = 7,
        description = 8,
        dueDate = 9,

        id = null //Don't map because it fcks up the sync with Insufficient Permission error
    )

    private fun ivyMappingV2() = RowMapping(
        date = 0,
        title = 1,
        category = 2,
        account = 3,
        amount = 4,
        accountCurrency = 5,
        type = 6,
        transferAmount = 7,
        //Transfer Currency - 8
        toAccount = 9,
        toAmount = 10,
        toAccountCurrency = 11,
        //skip "Receive Currency"
        description = 12,
        dueDate = 13,

        id = null, //14 - Don't map because it fcks up the sync with Insufficient Permission error

        accountColor = 15,
        accountOrderNum = 16,
        accountIcon = 21,
        categoryColor = 17,
        categoryOrderNum = 18,
        categoryIcon = 22,
        toAccountColor = 19,
        toAccountOrderNum = 20,
        toAccountIcon = 23
    )

    //Praseto - https://play.google.com/store/apps/details?id=com.realbyteapps.moneymanagerfree&hl=en&gl=US
    private fun moneyManager() = RowMapping(
        type = 6,
        amount = 8,
        account = 1,

        date = 0,
        dueDate = null,

        category = 2,
        title = 4,
        description = 7,
        accountCurrency = 9,

        transferAmount = null,
        toAccount = null,

        id = null
    )

    //Wallet By BudgetBakers - https://play.google.com/store/apps/details?id=com.droid4you.application.wallet&hl=en&gl=US
    private fun walletByBudgetBakers() = RowMapping(
        type = 5,
        amount = 3,
        account = 0,
        accountCurrency = 2,

        date = 9,
        dueDate = null,

        category = 1,
        title = 8,
        description = null,

        transferAmount = null,
        toAccount = null,

        id = null
    )

    private fun spendee() = RowMapping(
        date = 0,
        account = 1,
        type = 2,
        category = 3,
        amount = 4,
        accountCurrency = 5,
        title = 6
    )

    private fun monefy() = RowMapping(
        date = 0,
        dateOnlyFormat = "dd/MM/yyyy",
        account = 1,
        category = 2,
        amount = 3,
        accountCurrency = 4,
        //converted amount = 5
        //currency = 6
        title = 7,
        defaultTypeToExpense = true, //Monefy doesn't have transaction type, it uses amount +/- sign,

        transformTransaction = { transaction, _, csvAmount ->
            //Monefy doesn't have transaction type, it uses amount +/- sign
            transaction.copy(
                type = if (csvAmount > 0) TransactionType.INCOME else TransactionType.EXPENSE
            )
        }
    )

    private fun oneMoney() = RowMapping(
        date = 0,
        type = 1,
        account = 2,
        category = 3,
        amount = 4,
        accountCurrency = 5,
        title = 9
    )

    private fun ktwMoneyManager() = RowMapping(
        date = 0,
        type = 1,
        category = 2,
        amount = 3,
        accountCurrency = 4,
        title = 5,
        account = 6
    )


    private fun fortuneCity() = RowMapping(
        account = 0,
        category = 1,
        amount = 2,
        accountCurrency = 3,
        date = 4,
        title = 5,

        transformTransaction = { transaction, category, _ ->
            transaction.copy(
                type = if (category?.name?.toLowerCaseLocal() == "income")
                    TransactionType.INCOME else TransactionType.EXPENSE
            )
        }
    )

    private fun blueCoins() = RowMapping(
        type = 0,
        date = 1,
        //set time = 2
        title = 3,
        amount = 4,
        accountCurrency = 5,
        //exchangeRate = 6
        //category group = 7
        category = 8,
        account = 9,
        description = 10
    )

    @ExperimentalStdlibApi
    private fun financisto() = RowMapping(
        date = 0,
        timeOnly = 1,
        account = 2,
        amount = 3,
        transferAmount = 3,
        accountCurrency = 4,
        // original currency amount = 5
        // original currency = 6
        category = 7,
        // parent transaction = 8
        title = 9,
        type = 10,
        defaultTypeToExpense = true,
        // project = 11
        description = 12,

        transformTransaction = { transaction, _, csvAmount ->
            transaction.copy(
                // Financisto exports expenses with a negative sign and incoming as positive values
                type = if (csvAmount > 0 && transaction.type == TransactionType.EXPENSE) {
                    TransactionType.INCOME
                } else {
                    transaction.type
                }
            )
        },

        joinTransactions = { transactions ->
            var mergedCount = 0
            JoinResult(
                transactions = buildList {
                    val it = transactions.listIterator()
                    while (it.hasNext()) {
                        val t = it.next()
                        if (t.type == TransactionType.TRANSFER && it.hasNext()) {
                            val t2 = it.next()
                            val new = Transaction(
                                id = t.id,
                                type = TransactionType.TRANSFER,
                                amount = t.amount,
                                accountId = t.accountId,
                                toAccountId = t2.accountId,
                                toAmount = t2.amount,
                                dateTime = t.dateTime,
                                dueDate = t.dueDate,
                                categoryId = t.categoryId,
                                title = t.title,
                                description = t.description
                            )
                            mergedCount++
                            add(new)
                        } else {
                            add(t)
                        }
                    }
                },
                mergedCount = mergedCount
            )
        }
    )

    private fun moneyWallet() = RowMapping(
        account = 0,
        accountCurrency = 1,
        category = 2,
        date = 3,
        dateTimeFormat = "yyyy-MM-dd HH:mm:ss",
        amount = 4,
        title = 5,
        description = 9,

        transformTransaction = { transaction, category, csvAmount ->
            // MoneyWallet will export transfer transaction as a pair of income/expense.
            // The category is a "system category" and user cannot change it.
            // However the name depends on system language when the wallet is created.
            // So we have added a note in the import step to ensure user uses the English local
            // for the category name.
            val isTransfer = category?.name == "Transfer"

            transaction.copy(
                type = if (isTransfer)
                    TransactionType.TRANSFER
                else if (csvAmount > 0) TransactionType.INCOME
                else TransactionType.EXPENSE,
                categoryId = if (isTransfer) null else transaction.categoryId
            )
        },


        joinTransactions = { transactions ->
            var mergedCount = 0
            JoinResult(
                transactions = buildList {
                    val it = transactions.listIterator()
                    while (it.hasNext()) {
                        val t = it.next()
                        if (t.type == TransactionType.TRANSFER && it.hasNext()) {
                            val t2 = it.next()
                            val new = Transaction(
                                id = t.id,
                                type = TransactionType.TRANSFER,
                                amount = t.amount,
                                accountId = t.accountId,
                                toAccountId = t2.accountId,
                                toAmount = t2.amount,
                                dateTime = t.dateTime,
                                dueDate = t.dueDate,
                                categoryId = t.categoryId,
                                title = t.title,
                                description = t.description
                            )
                            mergedCount++
                            add(new)
                        } else {
                            add(t)
                        }
                    }
                },
                mergedCount = mergedCount
            )
        }
    )
}