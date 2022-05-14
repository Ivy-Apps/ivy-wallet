package com.ivy.wallet.domain.action.global

import com.ivy.fp.action.FPAction
import com.ivy.fp.monad.Res
import com.ivy.fp.monad.thenR
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.ui.IvyWalletCtx
import javax.inject.Inject

class UpdateStartDayOfMonthAct @Inject constructor(
    private val sharedPrefs: SharedPrefs,
    private val ivyWalletCtx: IvyWalletCtx
) : FPAction<Int, Res<String, Int>>() {

    override suspend fun Int.compose(): suspend () -> Res<String, Int> = suspend {
        val startDay = this

        if (startDay in 1..31) {
            Res.Ok(startDay)
        } else {
            Res.Err("Invalid start day $startDay. Start date must be between 1 and 31.")
        }
    } thenR { startDay ->
        sharedPrefs.putInt(SharedPrefs.START_DATE_OF_MONTH, startDay)
        ivyWalletCtx.setStartDayOfMonth(startDay)
        Res.Ok(startDay)
    } thenR { startDay ->
        ivyWalletCtx.initSelectedPeriodInMemory(
            startDayOfMonth = startDay,
            forceReinitialize = true
        )
        Res.Ok(startDay)
    }
}