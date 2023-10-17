package com.ivy.onboarding

import androidx.compose.runtime.Immutable
import com.ivy.legacy.data.model.AccountBalance
import com.ivy.legacy.datamodel.Category
import com.ivy.wallet.domain.data.IvyCurrency
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class OnboardingDetailState(
    val currency: IvyCurrency,
    val accounts: ImmutableList<AccountBalance>,
    val accountSuggestions: ImmutableList<CreateAccountData>,
    val categories: ImmutableList<Category>,
    val categorySuggestions: ImmutableList<CreateCategoryData>
)
