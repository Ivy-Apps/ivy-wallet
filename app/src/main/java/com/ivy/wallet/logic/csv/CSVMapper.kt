package com.ivy.wallet.logic.csv

import com.ivy.wallet.logic.csv.model.ImportType
import com.ivy.wallet.logic.csv.model.RowMapping

class CSVMapper {

    fun mapping(type: ImportType, headerRow: String?) = when (type) {
        ImportType.IVY -> {
            if (headerRow?.contains("Currency") == true) {
                ivyMappingV2()
            } else {
                ivyMappingV1()
            }
        }
        ImportType.MONEY_MANAGER_PRASE -> moneyManagerPraseMapping()
        ImportType.WALLET_BY_BUDGET_BAKERS -> walletByBudgetBakers()
        ImportType.SPENDEE -> spendee()
        ImportType.ONE_MONEY -> oneMoney()
        ImportType.KTW_MONEY_MANAGER -> ktwRowMapping()
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
    private fun moneyManagerPraseMapping() = RowMapping(
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

    private fun oneMoney() = RowMapping(
        date = 0,
        type = 1,
        account = 2,
        category = 3,
        amount = 4,
        accountCurrency = 5,
        title = 9
    )

    private fun ktwRowMapping() = RowMapping(
        date = 0,
        type = 1,
        category = 2,
        amount = 3,
        accountCurrency = 4,
        title = 5,
        account = 6
    )
}