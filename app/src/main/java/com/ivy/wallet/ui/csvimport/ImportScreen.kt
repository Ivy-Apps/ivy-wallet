package com.ivy.wallet.ui.csvimport

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.wallet.base.onScreenStart
import com.ivy.wallet.logic.csv.model.ImportResult
import com.ivy.wallet.logic.csv.model.ImportType
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.csvimport.flow.ImportFrom
import com.ivy.wallet.ui.csvimport.flow.ImportProcessing
import com.ivy.wallet.ui.csvimport.flow.ImportResultUI
import com.ivy.wallet.ui.csvimport.flow.instructions.ImportInstructions
import com.ivy.wallet.ui.onboarding.viewmodel.OnboardingViewModel

@OptIn(ExperimentalStdlibApi::class)
@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.ImportCSVScreen(screen: Screen.Import) {
    val viewModel: ImportViewModel = viewModel()

    val importStep by viewModel.importStep.observeAsState(ImportStep.IMPORT_FROM)
    val importType by viewModel.importType.observeAsState()
    val importProgressPercent by viewModel.importProgressPercent.observeAsState(0)
    val importResult by viewModel.importResult.observeAsState()

    val onboardingViewModel: OnboardingViewModel = viewModel()

    onScreenStart {
        viewModel.start(screen)
    }

    UI(
        screen = screen,
        importStep = importStep,
        importType = importType,
        importProgressPercent = importProgressPercent,
        importResult = importResult,

        onChooseImportType = viewModel::setImportType,
        onUploadCSVFile = viewModel::uploadFile,
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
    screen: Screen.Import,

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
                result = importResult!!
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
    IvyAppPreview {
        UI(
            screen = Screen.Import(launchedFromOnboarding = true),
            importStep = ImportStep.IMPORT_FROM,
            importType = null,
            importProgressPercent = 0,
            importResult = null
        )
    }
}
