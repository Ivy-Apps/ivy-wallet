package com.ivy.wallet.domain.action.charts

import com.ivy.wallet.domain.action.framework.FPAction
import com.ivy.wallet.domain.action.wallet.CalcWalletBalanceAct
import com.ivy.wallet.domain.fp.charts.ChartPeriod
import com.ivy.wallet.domain.fp.charts.SingleChartPoint
import com.ivy.wallet.domain.fp.charts.balanceChart
import javax.inject.Inject

class BalanceChartAct @Inject constructor(
    private val calcWalletBalanceAct: CalcWalletBalanceAct
) : FPAction<BalanceChartAct.Input, List<SingleChartPoint>>() {

    override suspend fun Input.compose(): suspend () -> List<SingleChartPoint> = suspend {
        io {
            balanceChart(
                period = period,
                calcWalletBalance = { range ->
                    calcWalletBalanceAct(
                        CalcWalletBalanceAct.Input(
                            baseCurrency = baseCurrency,
                            range = range
                        )
                    )
                }
            )
        }
    }

    data class Input(
        val baseCurrency: String,
        val period: ChartPeriod
    )
}