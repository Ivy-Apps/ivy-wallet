package com.ivy.onboarding

import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Category
import com.ivy.wallet.domain.data.IvyCurrency
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData

sealed interface OnboardingEvent {

    data object loginWithGoogle : OnboardingEvent
    data object loginOfflineAccount : OnboardingEvent
    data object startImport : OnboardingEvent
    data object importSkip : OnboardingEvent
    data class importFinished(val success: Boolean) : OnboardingEvent
    data object startFresh : OnboardingEvent
    data class setBaseCurrency(val baseCurrency: IvyCurrency) : OnboardingEvent
    data class editAccount(val account: Account, val newBalance: Double) : OnboardingEvent
    data class createAccount(val data: CreateAccountData) : OnboardingEvent
    data object onAddAccountsDone : OnboardingEvent
    data object onAddAccountsSkip : OnboardingEvent
    data class editCategory(val updatedCategory: Category) : OnboardingEvent
    data class createCategory(val data: CreateCategoryData) : OnboardingEvent
    data object onAddCategoriesDone : OnboardingEvent
    data object onAddCategoriesSkip : OnboardingEvent
}