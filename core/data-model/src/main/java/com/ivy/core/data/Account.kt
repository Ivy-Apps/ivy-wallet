package com.ivy.core.data

import java.util.*

sealed interface Account : Reorderable {
    val id: UUID
    val asset: AssetCode
    val visuals: ItemVisuals
    val includeInBalance: Boolean
    val folderId: FolderId?
}

sealed interface Asset : Account {
    val liquid: Boolean

    data class Cash(
        override val id: UUID,
        override val asset: AssetCode,
        override val visuals: ItemVisuals,
        override val includeInBalance: Boolean,
        override val folderId: FolderId?,
        override val orderNum: Double,
    ) : Asset {
        override val liquid = true
    }

    data class Bank(
        override val id: UUID,
        override val asset: AssetCode,
        override val visuals: ItemVisuals,
        override val includeInBalance: Boolean,
        override val folderId: FolderId?,
        override val orderNum: Double,
    ) : Asset {
        override val liquid = true
    }

    /**
     * You gave a Loan to someone and they owe you money.
     */
    data class Loan(
        override val id: UUID,
        override val asset: AssetCode,
        override val visuals: ItemVisuals,
        override val includeInBalance: Boolean,
        override val folderId: FolderId?,
        override val orderNum: Double,
    ) : Asset {
        override val liquid = false
    }

    /**
     * Illiquid asset like Stocks, Crypto, Gold.
     */
    data class Investment(
        override val id: UUID,
        override val asset: AssetCode,
        override val visuals: ItemVisuals,
        override val includeInBalance: Boolean,
        override val folderId: FolderId?,
        override val orderNum: Double,
    ) : Asset {
        override val liquid = false
    }

    /**
     * Money put in Savings account. We don't know if they are liquid
     */
    data class Savings(
        override val id: UUID,
        override val asset: AssetCode,
        override val visuals: ItemVisuals,
        override val includeInBalance: Boolean,
        override val folderId: FolderId?,
        override val orderNum: Double,

        override val liquid: Boolean,
    ) : Asset

    data class Other(
        override val id: UUID,
        override val asset: AssetCode,
        override val visuals: ItemVisuals,
        override val includeInBalance: Boolean,
        override val folderId: FolderId?,
        override val orderNum: Double,

        override val liquid: Boolean,
    ) : Asset
}

sealed interface Liability : Account {
    data class CreditCard(
        override val id: UUID,
        override val asset: AssetCode,
        override val visuals: ItemVisuals,
        override val includeInBalance: Boolean,
        override val folderId: FolderId?,
        override val orderNum: Double,

        val limit: Value,
        val billingDate: MonthDate,
        val dueDate: MonthDate,
    ) : Liability

    /**
     * Money that you owe to a bank, friend or other entity.
     */
    data class Loan(
        override val id: UUID,
        override val asset: AssetCode,
        override val visuals: ItemVisuals,
        override val includeInBalance: Boolean,
        override val folderId: FolderId?,
        override val orderNum: Double,

        // TODO: Consider what other attributes will the Loan have
    ) : Liability

    data class Other(
        override val id: UUID,
        override val asset: AssetCode,
        override val visuals: ItemVisuals,
        override val includeInBalance: Boolean,
        override val folderId: FolderId?,
        override val orderNum: Double,
    ) : Liability
}


@JvmInline
value class AccountId(val id: UUID)
