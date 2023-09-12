package com.ivy.importdata.csv

import android.net.Uri
import com.ivy.onboarding.viewmodel.OnboardingViewModel

sealed interface CSVEvent {
    data class FilePicked(val uri: Uri) : com.ivy.importdata.csv.CSVEvent

    data class MapAmount(val index: Int, val name: String) : com.ivy.importdata.csv.CSVEvent
    data class AmountMultiplier(val multiplier: Int) : com.ivy.importdata.csv.CSVEvent
    data class MapType(val index: Int, val name: String) : com.ivy.importdata.csv.CSVEvent
    data class TypeMetaChange(val meta: com.ivy.importdata.csv.TrnTypeMetadata) :
        com.ivy.importdata.csv.CSVEvent
    data class MapDate(val index: Int, val name: String) : com.ivy.importdata.csv.CSVEvent
    data class DataMetaChange(val meta: com.ivy.importdata.csv.DateMetadata) :
        com.ivy.importdata.csv.CSVEvent
    data class MapAccount(val index: Int, val name: String) : com.ivy.importdata.csv.CSVEvent
    data class MapAccountCurrency(val index: Int, val name: String) :
        com.ivy.importdata.csv.CSVEvent

    data class MapToAccount(val index: Int, val name: String) : com.ivy.importdata.csv.CSVEvent
    data class MapToAccountCurrency(val index: Int, val name: String) :
        com.ivy.importdata.csv.CSVEvent
    data class MapToAmount(val index: Int, val name: String) : com.ivy.importdata.csv.CSVEvent
    data class ToAmountMetaChange(val multiplier: Int) : com.ivy.importdata.csv.CSVEvent

    data class MapCategory(val index: Int, val name: String) : com.ivy.importdata.csv.CSVEvent
    data class MapTitle(val index: Int, val name: String) : com.ivy.importdata.csv.CSVEvent
    data class MapDescription(val index: Int, val name: String) : com.ivy.importdata.csv.CSVEvent

    object Continue : com.ivy.importdata.csv.CSVEvent
    object ResetState : com.ivy.importdata.csv.CSVEvent

    data class FinishImport(
        val launchedFromOnboarding: Boolean,
        val onboardingViewModel: OnboardingViewModel
    ) : com.ivy.importdata.csv.CSVEvent
}
