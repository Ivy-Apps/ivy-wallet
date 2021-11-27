package com.ivy.wallet.logic.model

import androidx.compose.ui.graphics.Color
import com.ivy.wallet.model.LoanType

data class CreateLoanData(
    val name: String,
    val amount: Double,
    val type: LoanType,
    val color: Color,
    val icon: String?
)