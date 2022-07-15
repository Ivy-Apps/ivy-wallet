package com.ivy.base

interface Reorderable {
    fun getItemOrderNum(): Double

    fun withNewOrderNum(newOrderNum: Double): Reorderable
}