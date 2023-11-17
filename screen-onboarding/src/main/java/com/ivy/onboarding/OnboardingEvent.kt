package com.ivy.onboarding

import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Category
import com.ivy.wallet.domain.data.IvyCurrency
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData

sealed interface OnboardingEvent {

    data object LoginWithGoogle : OnboardingEvent
    data object LoginOfflineAccount : OnboardingEvent
    data object StartImport : OnboardingEvent
    data object ImportSkip : OnboardingEvent
    data class ImportFinished(val success: Boolean) : OnboardingEvent
    data object StartFresh : OnboardingEvent
    data class SetBaseCurrency(val baseCurrency: IvyCurrency) : OnboardingEvent
    data class EditAccount(val account: Account, val newBalance: Double) : OnboardingEvent
    data class CreateAccount(val data: CreateAccountData) : OnboardingEvent
    data object OnAddAccountsDone : OnboardingEvent
    data object OnAddAccountsSkip : OnboardingEvent
    data class EditCategory(val updatedCategory: Category) : OnboardingEvent
    data class CreateCategory(val data: CreateCategoryData) : OnboardingEvent
    data object OnAddCategoriesDone : OnboardingEvent
    data object OnAddCategoriesSkip : OnboardingEvent
}