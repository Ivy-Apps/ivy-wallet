package com.ivy.wallet.ui.loan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.base.TestIdlingResource
import com.ivy.wallet.base.getDefaultFIATCurrency
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.logic.LoanCreator
import com.ivy.wallet.logic.model.CreateLoanData
import com.ivy.wallet.persistence.dao.LoanDao
import com.ivy.wallet.persistence.dao.LoanRecordDao
import com.ivy.wallet.persistence.dao.SettingsDao
import com.ivy.wallet.sync.item.LoanSync
import com.ivy.wallet.ui.loan.data.DisplayLoan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoanViewModel @Inject constructor(
    private val loanDao: LoanDao,
    private val loanRecordDao: LoanRecordDao,
    private val settingsDao: SettingsDao,
    private val loanSync: LoanSync,
    private val loanCreator: LoanCreator
) : ViewModel() {

    private val _baseCurrencyCode = MutableStateFlow(getDefaultFIATCurrency().currencyCode)
    val baseCurrencyCode = _baseCurrencyCode.asStateFlow()

    private val _loans = MutableStateFlow(emptyList<DisplayLoan>())
    val loans = _loans.asStateFlow()

    fun start() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            _baseCurrencyCode.value = ioThread {
                settingsDao.findFirst().currency
            }

            _loans.value = ioThread {
                loanDao.findAll()
                    .map { loan ->
                        DisplayLoan(
                            loan = loan,
                            amountPaid = loanRecordDao.findAllByLoanId(loanId = loan.id)
                                .sumOf { loanRecord ->
                                    loanRecord.amount
                                }
                        )
                    }
            }

            TestIdlingResource.decrement()
        }
    }

    fun createLoan(data: CreateLoanData) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            loanCreator.create(data) {
                start()
            }

            TestIdlingResource.decrement()
        }
    }

    fun reorder(newOrder: List<DisplayLoan>) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            ioThread {
                newOrder.forEachIndexed { index, item ->
                    loanDao.save(
                        item.loan.copy(
                            orderNum = index.toDouble(),
                            isSynced = false
                        )
                    )
                }
            }
            start()

            ioThread {
                loanSync.sync()
            }

            TestIdlingResource.decrement()
        }
    }
}