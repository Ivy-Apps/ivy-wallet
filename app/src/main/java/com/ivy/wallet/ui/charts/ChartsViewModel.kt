package com.ivy.wallet.ui.charts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.base.dateNowUTC
import com.ivy.wallet.base.endOfMonth
import com.ivy.wallet.base.getDefaultFIATCurrency
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.logic.WalletLogic
import com.ivy.wallet.model.entity.Transaction
import com.ivy.wallet.persistence.dao.SettingsDao
import com.ivy.wallet.ui.onboarding.model.FromToTimeRange
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


    private val _balanceValues = MutableStateFlow(emptyList<TimeValue>())
    val balanceValues = _balanceValues.asStateFlow()

    private val _incomeValues = MutableStateFlow(emptyList<TimeValue>())
    val incomeValues = _incomeValues.asStateFlow()

    private val _expenseValues = MutableStateFlow(emptyList<TimeValue>())
    val expenseValues = _expenseValues.asStateFlow()

    fun start() {
        viewModelScope.launch {
            _baseCurrencyCode.value = ioThread {
                settingsDao.findFirst().currency
            }

            val lastNMonths = lastNMonths(n = 12)

            _balanceValues.value = ioThread {
                lastNMonths.map { month ->
                    TimeValue(
                        dateTime = month,
                        value = walletLogic.calculateBalance(
                            before = month
                        )
                    )
                }
            }

            _incomeValues.value = ioThread {
                lastNMonths.map { endOfMonthTime ->
                    TimeValue(
                        dateTime = endOfMonthTime,
                        value = walletLogic.calculateIncome(
                            walletLogic.history(
                                range = FromToTimeRange(
                                    from = endOfMonthTime.withDayOfMonth(1),
                                    to = endOfMonthTime
                                )
                            ).filterIsInstance(Transaction::class.java)
                        )
                    )
                }
            }

            _expenseValues.value = ioThread {
                lastNMonths.map { endOfMonthTime ->
                    TimeValue(
                        dateTime = endOfMonthTime,
                        value = walletLogic.calculateExpenses(
                            walletLogic.history(
                                range = FromToTimeRange(
                                    from = endOfMonthTime.withDayOfMonth(1),
                                    to = endOfMonthTime
                                )
                            ).filterIsInstance(Transaction::class.java)
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
                date = endOfMonth(date.withDayOfMonth(10).minusMonths(1).toLocalDate())
            )
        } else {
            //end recursion
            accumulator.reversed()
        }
    }
}

