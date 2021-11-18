package com.ivy.wallet.logic.csv

import com.ivy.wallet.logic.csv.model.ImportType

class CSVNormalizer {

    fun normalize(rawCSV: String, importType: ImportType): String {
        return when (importType) {
            ImportType.WALLET_BY_BUDGET_BAKERS -> walletByBudgetBakers(rawCSV)
            else -> rawCSV
        }
    }

    private fun walletByBudgetBakers(rawCSV: String): String {
        return rawCSV
            .replace(",", " ")
            .replace(";", ",")
    }
}