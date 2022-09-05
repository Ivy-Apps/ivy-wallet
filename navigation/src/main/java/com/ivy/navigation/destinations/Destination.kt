package com.ivy.navigation.destinations

import com.ivy.navigation.destinations.imports.ImportGraph
import com.ivy.navigation.destinations.onboarding.OnboardingGraph
import com.ivy.navigation.destinations.trn.NewTrn
import com.ivy.navigation.destinations.trn.TrnDetails

object Destination {
    val onboarding = OnboardingGraph
    val import = ImportGraph

    val trnDetails = TrnDetails
    val newTrn = NewTrn
}