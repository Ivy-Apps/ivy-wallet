package com.ivy.screens

import com.ivy.data.transaction.TrnTypeOld
import com.ivy.frp.view.navigation.Screen
import java.util.*

data class EditTransaction(
    val initialTransactionId: UUID?,
    val type: TrnTypeOld,

    //extras
    val accountId: UUID? = null,
    val categoryId: UUID? = null
) : Screen