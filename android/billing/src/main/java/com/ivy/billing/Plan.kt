package com.ivy.billing

import com.android.billingclient.api.SkuDetails

data class Plan(
    val sku: String,
    val type: PlanType,
    val price: String,
    val skuDetails: SkuDetails
) {

    fun parsePrice(): AmountCurrency? {
        try {
            val currency = price.take(3)
            val amount = price
                .removeRange(0, 4)
                .replace(",", "")
                .replace(" ", "")
                .toDoubleOrNull() ?: return null

            return AmountCurrency(
                amount = amount,
                currency = currency
            )
        } catch (e: Exception) {
            return null
        }

    }

    fun freePeriod(): String = when (skuDetails.freeTrialPeriod) {
        "P3D" -> "3-days for free"
        "P1W" -> "7-days for free"
        "P7D" -> "7-days for free"
        "P2W" -> "14-days for free"
        "P14D" -> "14-days for free"
        else -> "for free"
    }

    data class AmountCurrency(
        val amount: Double,
        val currency: String
    )
}