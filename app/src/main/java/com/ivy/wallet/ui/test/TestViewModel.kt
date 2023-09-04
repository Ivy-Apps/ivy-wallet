package com.ivy.wallet.ui.test

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.frp.test.TestIdlingResource
import com.ivy.wallet.domain.data.core.User
import com.ivy.wallet.utils.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor() : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user = _user.asLiveData()

    fun start() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            TestIdlingResource.decrement()
        }
    }
}
