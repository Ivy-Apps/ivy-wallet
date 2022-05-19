package com.ivy.wallet.ui.test

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.frp.test.TestIdlingResource
import com.ivy.wallet.domain.data.core.User
import com.ivy.wallet.domain.deprecated.logic.notification.TransactionReminderLogic
import com.ivy.wallet.domain.deprecated.sync.item.CategorySync
import com.ivy.wallet.io.network.IvySession
import com.ivy.wallet.io.persistence.dao.UserDao
import com.ivy.wallet.utils.asLiveData
import com.ivy.wallet.utils.ioThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val categorySync: CategorySync,
    private val userDao: UserDao,
    private val ivySession: IvySession,
    private val transactionReminderLogic: TransactionReminderLogic
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user = _user.asLiveData()

    fun start() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            _user.value = ioThread {
                val userId = ivySession.getUserIdSafe()
                if (userId != null) userDao.findById(userId)?.toDomain() else null
            }

            TestIdlingResource.decrement()
        }
    }

    fun syncCategories() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            ioThread {
                categorySync.sync()
            }

            TestIdlingResource.decrement()
        }
    }

    fun testWorker() {
        transactionReminderLogic.testNow()
    }
}