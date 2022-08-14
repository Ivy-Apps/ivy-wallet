package com.ivy.wallet.domain.deprecated.logic.model

import androidx.compose.ui.graphics.Color
import com.ivy.data.AccountOld
import com.ivy.data.loan.LoanType

data class CreateLoanData(
    val name: String,
    val amount: Double,
    val type: LoanType,
    val color: Color,
    val icon: String?,
    val account: AccountOld? = null,
    val createLoanTransaction: Boolean = false,
)