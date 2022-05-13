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
) : FPAction<Int, Res<UpdateStartDayOfMonthAct.InvalidStartDay, Int>>() {

    override suspend fun Int.compose(): suspend () -> Res<InvalidStartDay, Int> = suspend {
        val startDay = this

        if (startDay in 1..31) {
            Res.Ok<InvalidStartDay, Int>(startDay)
        } else {
            Res.Err<InvalidStartDay, Int>(InvalidStartDay)
        }
    } thenR { startDay ->
        sharedPrefs.putInt(SharedPrefs.START_DATE_OF_MONTH, startDay)
        ivyWalletCtx.setStartDayOfMonth(startDay)
        Res.Ok<InvalidStartDay, Int>(startDay)
    } thenR { startDay ->
        ivyWalletCtx.initSelectedPeriodInMemory(
            startDayOfMonth = startDay,
            forceReinitialize = true
        )
        Res.Ok<InvalidStartDay, Int>(startDay)
    }

    object InvalidStartDay
}