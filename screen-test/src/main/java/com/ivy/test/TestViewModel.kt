package com.ivy.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.frp.test.TestIdlingResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor() : ViewModel() {
    fun start() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            TestIdlingResource.decrement()
        }
    }
}
