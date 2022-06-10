package com.ivy.wallet.ui.donate

import androidx.lifecycle.viewModelScope
import com.ivy.frp.viewmodel.FRPViewModel
import com.ivy.wallet.android.billing.IvyBilling
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DonateViewModel @Inject constructor(
    private val ivyBilling: IvyBilling
) : FRPViewModel<DonateState, DonateEvent>() {
    override val _state: MutableStateFlow<DonateState> = MutableStateFlow(DonateState.Loading)

    override suspend fun handleEvent(event: DonateEvent): suspend () -> DonateState = when (event) {
        is DonateEvent.Buy -> TODO()
        is DonateEvent.Load -> TODO()
    }

    private fun load(event: DonateEvent.Load) = suspend {
        ivyBilling.init(
            activity = event.activity,
            onReady = {
                viewModelScope.launch {
                    val plans = ivyBilling.fetchOneTimePlans()
                        .mapNotNull {
                            when (it.sku) {
                                IvyBilling.DONATE_5 -> "Donate 5" to "Show support"
                                IvyBilling.DONATE_10 -> "Donate 10" to "Give us hope!"
                                IvyBilling.DONATE_15 -> "Donate 15" to ""
                                IvyBilling.DONATE_25 -> "Donate 25" to "Pay our servers for 1 month."
                                IvyBilling.DONATE_50 -> "Donate 50" to "Pay our accountant for 1 month."
                                else -> null
                            }
                        }
                }
            },
            onError = { code, msg ->
                updateStateNonBlocking {
                    DonateState.Error(errMsg = "Google Play Billing error: $code - $msg")
                }
            },
            onPurchases = {}
        )
        stateVal()
    }
}