package com.ivy.data.repository

interface LegalRepository {
    suspend fun isDisclaimerAccepted(): Boolean
    suspend fun setDisclaimerAccepted(accepted: Boolean)
}