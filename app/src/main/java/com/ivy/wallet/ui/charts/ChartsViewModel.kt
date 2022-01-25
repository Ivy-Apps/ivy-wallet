package com.ivy.wallet.ui.charts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartsViewModel @Inject constructor() : ViewModel() {

    private val _balancesByMonths = MutableStateFlow(emptyList<Double>())
    val balancesByMonths = _balancesByMonths.asStateFlow()

    fun start() {
        viewModelScope.launch {

        }
    }
}