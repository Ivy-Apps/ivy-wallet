package com.ivy.wallet.ui.charts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.base.dateNowUTC
import com.ivy.wallet.base.endOfMonth
import com.ivy.wallet.base.getDefaultFIATCurrency
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.logic.WalletLogic
import com.ivy.wallet.persistence.dao.SettingsDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ChartsViewModel @Inject constructor(
    private val walletLogic: WalletLogic,
    private val settingsDao: SettingsDao
) : ViewModel() {

    private val _baseCurrencyCode = MutableStateFlow(getDefaultFIATCurrency().currencyCode)
    val baseCurrencyCode = _baseCurrencyCode.asStateFlow()


    private val _balanceValues = MutableStateFlow(emptyList<MonthValue>())
    val balanceValues = _balanceValues.asStateFlow()

    fun start() {
        viewModelScope.launch {
            _baseCurrencyCode.value = ioThread {
                settingsDao.findFirst().currency
            }

            _balanceValues.value = ioThread {
                lastNMonths(n = 12)
                    .map { month ->
                        MonthValue(
                            month = month,
                            value = walletLogic.calculateBalance(
                                before = month
                            )
                        )
                    }
            }
        }
    }

    private fun lastNMonths(
        n: Int,
        accumulator: List<LocalDateTime> = emptyList(),
        date: LocalDateTime = endOfMonth(dateNowUTC())
    ): List<LocalDateTime> {
        return if (accumulator.size < n) {
            //recurse
            lastNMonths(
                n = n,
                accumulator = accumulator.plus(date),
                date = date.minusMonths(1)
            )
        } else {
            //end recursion
            accumulator.reversed()
        }
    }
}

