package com.ivy.backup.base.data

data class FaultTolerantList<T>(
    val items: List<T>,
    val faulty: Int,
)