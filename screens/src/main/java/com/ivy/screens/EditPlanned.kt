package com.ivy.screens

import com.ivy.data.transaction.TrnTypeOld
import com.ivy.frp.view.navigation.Screen
import java.util.*

data class EditPlanned(
    val plannedPaymentRuleId: UUID?,
    val type: TrnTypeOld,
    val amount: Double? = null,
    val accountId: UUID? = null,
    val categoryId: UUID? = null,
    val title: String? = null,
    val description: String? = null,
) : Screen {
    fun mandatoryFilled(): Boolean {
        return amount != null && amount > 0.0
                && accountId != null
    }
}