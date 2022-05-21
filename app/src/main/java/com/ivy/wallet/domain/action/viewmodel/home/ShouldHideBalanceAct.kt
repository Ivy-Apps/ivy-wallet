package com.ivy.wallet.domain.action.viewmodel.home

import com.ivy.frp.action.FPAction
import com.ivy.wallet.io.persistence.SharedPrefs
import javax.inject.Inject

class ShouldHideBalanceAct @Inject constructor(
    private val sharedPrefs: SharedPrefs
) : FPAction<Unit, Boolean>() {
    override suspend fun Unit.compose(): suspend () -> Boolean = {
        sharedPrefs.getBoolean(
            SharedPrefs.HIDE_CURRENT_BALANCE,
            false
        )
    }
}