package com.ivy.wallet.logic.csv.model

data class ImportResult(
    val rowsFound: Int,
    val transactionsImported: Int,
    val accountsImported: Int,
    val categoriesImported: Int,
    val failedRows: List<CSVRow>,
)