package com.ivy.core.data

import androidx.annotation.FloatRange
import com.ivy.core.data.common.*
import java.time.LocalDateTime
import java.util.*

// TODO: Support attachments to Accounts? Some users wanted to attach loan documents
sealed interface Account : Reorderable, Archiveable, Syncable {
    val id: UUID
    val asset: AssetCode
    val name: String
    val description: String?
    val iconId: ItemIconId
    val color: IvyColor
    val includeInBalance: Boolean
    val folderId: AccountFolderId?
}

sealed interface Asset : Account {
    data class Cash(
        override val id: UUID,
        override val asset: AssetCode,
        override val name: String,
        override val description: String?,
        override val iconId: ItemIconId,
        override val color: IvyColor,
        override val includeInBalance: Boolean,
        override val folderId: AccountFolderId?,
        override val orderNum: Double,
        override val archived: Boolean,
        override val lastUpdated: LocalDateTime,
    ) : Asset

    data class Bank(
        override val id: UUID,
        override val asset: AssetCode,
        override val name: String,
        override val description: String?,
        override val iconId: ItemIconId,
        override val color: IvyColor,
        override val includeInBalance: Boolean,
        override val folderId: AccountFolderId?,
        override val orderNum: Double,
        override val archived: Boolean,
        override val lastUpdated: LocalDateTime,
    ) : Asset

    /**
     * You gave a Loan to someone and they owe you money.
     */
    data class Loan(
        override val id: UUID,
        override val asset: AssetCode,
        override val name: String,
        override val description: String?,
        override val iconId: ItemIconId,
        override val color: IvyColor,
        override val includeInBalance: Boolean,
        override val folderId: AccountFolderId?,
        override val orderNum: Double,
        override val archived: Boolean,
        override val lastUpdated: LocalDateTime,

        /**
         * The lent amount
         */
        val principal: Value,
        @FloatRange(from = 0.0, to = 1.0)
        val interest: Float,
    ) : Asset

    /**
     * Illiquid asset like Stocks, Crypto, Gold.
     */
    data class Investment(
        override val id: UUID,
        override val asset: AssetCode,
        override val name: String,
        override val description: String?,
        override val iconId: ItemIconId,
        override val color: IvyColor,
        override val includeInBalance: Boolean,
        override val folderId: AccountFolderId?,
        override val orderNum: Double,
        override val archived: Boolean,
        override val lastUpdated: LocalDateTime,
    ) : Asset

    /**
     * Money put in Savings account. We don't know if they are liquid
     */
    data class Savings(
        override val id: UUID,
        override val asset: AssetCode,
        override val name: String,
        override val description: String?,
        override val iconId: ItemIconId,
        override val color: IvyColor,
        override val includeInBalance: Boolean,
        override val folderId: AccountFolderId?,
        override val orderNum: Double,
        override val archived: Boolean,
        override val lastUpdated: LocalDateTime,
    ) : Asset

    data class Other(
        override val id: UUID,
        override val asset: AssetCode,
        override val name: String,
        override val description: String?,
        override val iconId: ItemIconId,
        override val color: IvyColor,
        override val includeInBalance: Boolean,
        override val folderId: AccountFolderId?,
        override val orderNum: Double,
        override val archived: Boolean,
        override val lastUpdated: LocalDateTime,
    ) : Asset
}

sealed interface Liability : Account {
    data class CreditCard(
        override val id: UUID,
        override val asset: AssetCode,
        override val name: String,
        override val description: String?,
        override val iconId: ItemIconId,
        override val color: IvyColor,
        override val includeInBalance: Boolean,
        override val folderId: AccountFolderId?,
        override val orderNum: Double,
        override val archived: Boolean,
        override val lastUpdated: LocalDateTime,

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
        override val name: String,
        override val description: String?,
        override val iconId: ItemIconId,
        override val color: IvyColor,
        override val includeInBalance: Boolean,
        override val folderId: AccountFolderId?,
        override val orderNum: Double,
        override val archived: Boolean,
        override val lastUpdated: LocalDateTime,

        /**
         * The borrowed amount
         */
        val principal: Value,
        @FloatRange(from = 0.0, to = 1.0)
        val interest: Float,
    ) : Liability

    data class Other(
        override val id: UUID,
        override val asset: AssetCode,
        override val name: String,
        override val description: String?,
        override val iconId: ItemIconId,
        override val color: IvyColor,
        override val includeInBalance: Boolean,
        override val folderId: AccountFolderId?,
        override val orderNum: Double,
        override val archived: Boolean,
        override val lastUpdated: LocalDateTime,
    ) : Liability
}


@JvmInline
value class AccountId(val id: UUID)

@JvmInline
value class AccountFolderId(val id: UUID)

data class AccountFolder(
    val id: UUID,
    val asset: AssetCode,
    val name: String,
    val description: String?,
    val iconId: ItemIconId,
    val color: IvyColor,
    override val orderNum: Double,
    override val lastUpdated: LocalDateTime,
) : Reorderable, Syncable
