package com.ivy.donate

import androidx.lifecycle.viewModelScope
import com.ivy.billing.IvyBilling
import com.ivy.billing.Plan
import com.ivy.donate.data.DonateOption
import com.ivy.frp.then
import com.ivy.frp.viewmodel.FRPViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DonateViewModel @Inject constructor(
    private val ivyBilling: IvyBilling
) : FRPViewModel<DonateState, DonateEvent>() {
    override val _state: MutableStateFlow<DonateState> = MutableStateFlow(DonateState.Success)

    val plans = mutableListOf<Plan>()

    override suspend fun handleEvent(event: DonateEvent): suspend () -> DonateState = when (event) {
        is DonateEvent.Load -> load(event)
        is DonateEvent.Donate -> donate(event)
    }

    private fun load(event: DonateEvent.Load) = suspend {
        ivyBilling.init(
            activity = event.activity,
            onReady = {
                viewModelScope.launch {
                    plans.clear()
                    plans.addAll(ivyBilling.fetchOneTimePlans())
                }
            },
            onError = { code, msg ->
                updateStateNonBlocking {
                    DonateState.Error(errMsg = "Google Play Billing error: $code - $msg")
                }
            },
            onPurchases = {}
        )
        DonateState.Success
    }

    private fun donate(event: DonateEvent.Donate) = suspend {
        when (event.option) {
            DonateOption.DONATE_2 -> IvyBilling.DONATE_2
            DonateOption.DONATE_5 -> IvyBilling.DONATE_5
            DonateOption.DONATE_10 -> IvyBilling.DONATE_10
            DonateOption.DONATE_15 -> IvyBilling.DONATE_15
            DonateOption.DONATE_25 -> IvyBilling.DONATE_25
            DonateOption.DONATE_50 -> IvyBilling.DONATE_50
            DonateOption.DONATE_100 -> IvyBilling.DONATE_100
        }
    } then { targetSku ->
        Timber.i("Donating to sku \"$targetSku\"")
        plans.find { it.sku == targetSku }
    } then { plan ->
        if (plan != null) {
            ivyBilling.buy(
                activity = event.activity,
                skuToBuy = plan.skuDetails,
                oldSubscriptionPurchaseToken = null
            )
        }
        stateVal()
    }
}