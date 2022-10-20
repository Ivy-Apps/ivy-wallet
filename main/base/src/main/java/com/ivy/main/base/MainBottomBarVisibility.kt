package com.ivy.main.base

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainBottomBarVisibility @Inject constructor() {
    val visible = MutableStateFlow(false)
}