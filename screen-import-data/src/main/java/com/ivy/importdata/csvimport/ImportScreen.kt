package com.ivy.importdata.csvimport

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.importdata.csvimport.flow.ImportFrom
import com.ivy.importdata.csvimport.flow.ImportProcessing
import com.ivy.importdata.csvimport.flow.ImportResultUI
import com.ivy.importdata.csvimport.flow.instructions.ImportInstructions
import com.ivy.navigation.ImportScreen
import com.ivy.onboarding.viewmodel.OnboardingViewModel
import com.ivy.wallet.domain.deprecated.logic.csv.model.ImportResult
import com.ivy.legacy.domain.deprecated.logic.csv.model.ImportType

@OptIn(ExperimentalStdlibApi::class)
@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.ImportCSVScreen(screen: ImportScreen) {
    val viewModel: ImportViewModel = viewModel()

    val importStep by viewModel.importStep.observeAsState(ImportStep.IMPORT_FROM)
    val importType by viewModel.importType.observeAsState()
    val importProgressPercent by viewModel.importProgressPercent.observeAsState(0)
    val importResult by viewModel.importResult.observeAsState()

    val onboardingViewModel: OnboardingViewModel = viewModel()

    com.ivy.legacy.utils.onScreenStart {
        viewModel.start(screen)
    }
    val context = LocalContext.current

    UI(
        screen = screen,
        importStep = importStep,
        importType = importType,
        importProgressPercent = importProgressPercent,
        importResult = importResult,

        onChooseImportType = viewModel::setImportType,
        onUploadCSVFile = { viewModel.uploadFile(context) },
        onSkip = {
            viewModel.skip(
                screen = screen,
                onboardingViewModel = onboardingViewModel
            )
        },
        onFinish = {
            viewModel.finish(
                screen = screen,
                onboardingViewModel = onboardingViewModel
            )
        }
    )
}

@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    screen: ImportScreen,

    importStep: ImportStep,
    importType: ImportType?,
    importProgressPercent: Int,
    importResult: ImportResult?,

    onChooseImportType: (ImportType) -> Unit = {},
    onUploadCSVFile: () -> Unit = {},
    onSkip: () -> Unit = {},
    onFinish: () -> Unit = {},
) {
    when (importStep) {
        ImportStep.IMPORT_FROM -> {
            ImportFrom(
                hasSkip = screen.launchedFromOnboarding,
                launchedFromOnboarding = screen.launchedFromOnboarding,
                onSkip = onSkip,
                onImportFrom = onChooseImportType
            )
        }

        ImportStep.INSTRUCTIONS -> {
            ImportInstructions(
                hasSkip = screen.launchedFromOnboarding,
                importType = importType!!,
                onSkip = onSkip,
                onUploadClick = onUploadCSVFile
            )
        }

        ImportStep.LOADING -> {
            ImportProcessing(
                progressPercent = importProgressPercent
            )
        }

        ImportStep.RESULT -> {
            ImportResultUI(
                result = importResult!!,
                launchedFromOnboarding = screen.launchedFromOnboarding,
            ) {
                onFinish()
            }
        }
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview() {
    com.ivy.legacy.IvyWalletPreview {
        UI(
            screen = ImportScreen(launchedFromOnboarding = true),
            importStep = ImportStep.IMPORT_FROM,
            importType = null,
            importProgressPercent = 0,
            importResult = null
        )
    }
}
