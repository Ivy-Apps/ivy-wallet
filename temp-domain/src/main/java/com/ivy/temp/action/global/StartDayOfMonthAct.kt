package com.ivy.wallet.domain.action.global

import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import com.ivy.wallet.io.persistence.SharedPrefs
import javax.inject.Inject

@Deprecated("Use StartDayOfMonthAct from `:core:actions`")
class StartDayOfMonthAct @Inject constructor(
    private val sharedPrefs: SharedPrefs,
    private val ivyWalletCtx: com.ivy.core.ui.temp.IvyWalletCtx
) : FPAction<Unit, Int>() {

    override suspend fun Unit.compose(): suspend () -> Int = suspend {
        sharedPrefs.getInt(SharedPrefs.START_DATE_OF_MONTH, 1)
    } then { startDay ->
        ivyWalletCtx.setStartDayOfMonth(startDay)
        startDay
    }
}