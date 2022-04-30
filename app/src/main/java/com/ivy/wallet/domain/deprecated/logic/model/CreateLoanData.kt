package com.ivy.wallet.domain.deprecated.logic.model

import androidx.compose.ui.graphics.Color
import com.ivy.wallet.domain.data.LoanType
import com.ivy.wallet.domain.data.core.Account

data class CreateLoanData(
    val name: String,
    val amount: Double,
    val type: LoanType,
    val color: Color,
    val icon: String?,
    val account: Account? = null,
    val createLoanTransaction :Boolean = false,
)