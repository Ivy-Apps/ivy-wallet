package com.ivy.wallet.domain.action.global

import com.ivy.frp.action.FPAction
import com.ivy.frp.monad.Res
import com.ivy.frp.monad.thenIfSuccess
import com.ivy.wallet.io.persistence.SharedPrefs
import javax.inject.Inject

class UpdateStartDayOfMonthAct @Inject constructor(
    private val sharedPrefs: SharedPrefs,
    private val ivyWalletCtx: com.ivy.core.ui.temp.IvyWalletCtx
) : FPAction<Int, Res<String, Int>>() {

    override suspend fun Int.compose(): suspend () -> Res<String, Int> = suspend {
        val startDay = this

        if (startDay in 1..31) {
            Res.Ok(startDay)
        } else {
            Res.Err("Invalid start day $startDay. Start date must be between 1 and 31.")
        }
    } thenIfSuccess { startDay ->
        sharedPrefs.putInt(SharedPrefs.START_DATE_OF_MONTH, startDay)
        ivyWalletCtx.setStartDayOfMonth(startDay)
        Res.Ok(startDay)
    } thenIfSuccess { startDay ->
        ivyWalletCtx.initSelectedPeriodInMemory(
            startDayOfMonth = startDay,
            forceReinitialize = true
        )
        Res.Ok(startDay)
    }
}