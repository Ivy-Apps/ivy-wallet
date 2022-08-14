package com.ivy.screens

import com.ivy.data.transaction.TrnType
import com.ivy.frp.view.navigation.Screen
import java.util.*

data class EditTransaction(
    val initialTransactionId: UUID?,
    val type: TrnType,

    //extras
    val accountId: UUID? = null,
    val categoryId: UUID? = null
) : Screen