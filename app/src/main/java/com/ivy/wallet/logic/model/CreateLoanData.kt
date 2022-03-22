package com.ivy.wallet.logic.model

import androidx.compose.ui.graphics.Color
import com.ivy.wallet.model.LoanType
import com.ivy.wallet.model.entity.Account

data class CreateLoanData(
    val name: String,
    val amount: Double,
    val type: LoanType,
    val color: Color,
    val icon: String?,
    val account: Account? = null,
    val createLoanTransaction :Boolean = false,
)