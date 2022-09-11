package com.ivy.wallet.domain.deprecated.logic.csv

import com.ivy.wallet.domain.deprecated.logic.csv.model.ImportApp

class CSVNormalizer {

    fun normalize(rawCSV: String, importApp: ImportApp): String {
        return when (importApp) {
            ImportApp.WALLET_BY_BUDGET_BAKERS -> walletByBudgetBakers(rawCSV)
            else -> rawCSV
        }
    }

    private fun walletByBudgetBakers(rawCSV: String): String {
        return rawCSV
            .replace(",", " ")
            .replace(";", ",")
    }
}