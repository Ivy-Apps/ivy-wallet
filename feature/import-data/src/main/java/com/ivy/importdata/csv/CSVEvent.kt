package com.ivy.importdata.csv

import android.net.Uri
import com.ivy.onboarding.viewmodel.OnboardingViewModel

sealed interface CSVEvent {
    data class FilePicked(val uri: Uri) : CSVEvent

    data class MapAmount(val index: Int, val name: String) : CSVEvent
    data class AmountMultiplier(val multiplier: Int) : CSVEvent
    data class MapType(val index: Int, val name: String) : CSVEvent
    data class TypeMetaChange(val meta: TrnTypeMetadata) :
        CSVEvent

    data class MapDate(val index: Int, val name: String) : CSVEvent
    data class DataMetaChange(val meta: DateMetadata) :
        CSVEvent

    data class MapAccount(val index: Int, val name: String) : CSVEvent
    data class MapAccountCurrency(val index: Int, val name: String) :
        CSVEvent

    data class MapToAccount(val index: Int, val name: String) : CSVEvent
    data class MapToAccountCurrency(val index: Int, val name: String) :
        CSVEvent

    data class MapToAmount(val index: Int, val name: String) : CSVEvent
    data class ToAmountMetaChange(val multiplier: Int) : CSVEvent

    data class MapCategory(val index: Int, val name: String) : CSVEvent
    data class MapTitle(val index: Int, val name: String) : CSVEvent
    data class MapDescription(val index: Int, val name: String) : CSVEvent

    data object Continue : CSVEvent
    data object ResetState : CSVEvent

    data class FinishImport(
        val launchedFromOnboarding: Boolean,
        val onboardingViewModel: OnboardingViewModel
    ) : CSVEvent
}
