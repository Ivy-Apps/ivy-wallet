package com.ivy.wallet.domain.action.charts

import com.ivy.frp.action.FPAction
import com.ivy.wallet.domain.action.wallet.CalcWalletBalanceAct
import com.ivy.wallet.domain.pure.charts.ChartPeriod
import com.ivy.wallet.domain.pure.charts.SingleChartPoint
import com.ivy.wallet.domain.pure.charts.balanceChart
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