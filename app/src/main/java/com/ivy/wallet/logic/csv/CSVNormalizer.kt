package com.ivy.wallet.logic.csv

import com.ivy.wallet.logic.csv.model.ImportType

class CSVNormalizer {

    fun normalize(rawCSV: String, importType: ImportType): String {
        return when (importType) {
            ImportType.IVY -> rawCSV
            ImportType.MONEY_MANAGER_PRASE -> rawCSV
            ImportType.WALLET_BY_BUDGET_BAKERS -> walletByBudgetBakers(rawCSV)
            ImportType.SPENDEE -> rawCSV
            ImportType.ONE_MONEY -> rawCSV
            ImportType.KTW_MONEY_MANAGER -> rawCSV
            ImportType.FORTUNE_CITY -> rawCSV
        }
    }

    private fun walletByBudgetBakers(rawCSV: String): String {
        return rawCSV
            .replace(",", " ")
            .replace(";", ",")
    }
}