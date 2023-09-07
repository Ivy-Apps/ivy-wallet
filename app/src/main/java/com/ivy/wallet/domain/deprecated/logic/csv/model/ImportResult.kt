package com.ivy.wallet.domain.deprecated.logic.csv.model

import kotlinx.collections.immutable.ImmutableList

data class ImportResult(
    val rowsFound: Int,
    val transactionsImported: Int,
    val accountsImported: Int,
    val categoriesImported: Int,
    val failedRows: ImmutableList<CSVRow>,
)
