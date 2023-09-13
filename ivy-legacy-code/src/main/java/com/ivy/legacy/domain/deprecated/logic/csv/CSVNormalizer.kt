package com.ivy.wallet.domain.deprecated.logic.csv

import com.ivy.legacy.domain.deprecated.logic.csv.model.ImportType
import javax.inject.Inject

class CSVNormalizer @Inject constructor() {

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
