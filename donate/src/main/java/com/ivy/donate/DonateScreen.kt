package com.ivy.donate

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.base.IvyWalletPreview
import com.ivy.base.R
import com.ivy.design.l0_system.Black
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.White
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.ColumnRoot
import com.ivy.design.l1_buildingBlocks.IvyText
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l1_buildingBlocks.data.Background
import com.ivy.design.l2_components.IconButton
import com.ivy.design.utils.padding
import com.ivy.donate.data.DonateOption
import com.ivy.frp.view.navigation.navigation
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.screens.DonateScreen
import com.ivy.wallet.ui.theme.Gradient
import com.ivy.wallet.ui.theme.components.IvyButton

@Composable
fun BoxWithConstraintsScope.DonateScreen(screen: DonateScreen) {
    //TODO: For some weird reason FRP<>() crashes, so I workaround it
//    FRP<DonateState, DonateEvent, DonateViewModel>(
//        initialEvent = DonateEvent.Load(LocalContext.current as Activity)
//    ) { _, onEvent ->
//        UI(onEvent)
//    }
    val viewModel: DonateViewModel = viewModel()
//    val state by viewModel.state().collectAsState()

    val activity = LocalContext.current as Activity
    onScreenStart {
        viewModel.onEvent(DonateEvent.Load(activity))
    }

    UI(viewModel::onEvent)
}

@Composable
private fun BoxWithConstraintsScope.UI(
    onEvent: (DonateEvent) -> Unit
) {
    var donateOption by remember { mutableStateOf(DonateOption.DONATE_5) }

    Column {
        Image(
            modifier = Modifier.fillMaxWidth(),
            painter = painterResource(id = R.drawable.donate_illustration),
            contentDescription = "rocket illustration",
            contentScale = ContentScale.FillWidth
        )

        ScreenContent()
    }

    ColumnRoot {
        SpacerVer(height = 16.dp)

        val nav = navigation()
        IconButton(
            modifier = Modifier.padding(start = 16.dp),
            icon = R.drawable.ic_back_android,
            background = Background.Outlined(
                width = 2.dp,
                color = White,
                shape = CircleShape,
                padding = padding(all = 12.dp)
            )
        ) {
            nav.back()
        }

        SpacerVer(height = 16.dp)

        IvyText(
            modifier = Modifier.padding(start = 24.dp),
            text = "Donate",
            typo = UI.typo.h2.style(
                fontWeight = FontWeight.Bold,
                color = White
            )
        )

        SpacerVer(height = 4.dp)

        DonateOptionPicker(option = donateOption) {
            donateOption = it
        }
    }

    val context = LocalView.current.context
    DonateButton {
        onEvent(DonateEvent.Donate(context as Activity, donateOption))
    }
}

@Composable
private fun DonateOptionPicker(
    option: DonateOption,
    onSelect: (DonateOption) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        SpacerHor(width = 16.dp)

        if (option != DonateOption.DONATE_2) {
            OptionPickerButton(
                icon = R.drawable.ic_donate_minus,
                contentDescription = "btn_minus"
            ) {
                val newOption = when (option) {
                    DonateOption.DONATE_2 -> DonateOption.DONATE_2
                    DonateOption.DONATE_5 -> DonateOption.DONATE_2
                    DonateOption.DONATE_10 -> DonateOption.DONATE_5
                    DonateOption.DONATE_15 -> DonateOption.DONATE_10
                    DonateOption.DONATE_25 -> DonateOption.DONATE_15
                    DonateOption.DONATE_50 -> DonateOption.DONATE_25
                    DonateOption.DONATE_100 -> DonateOption.DONATE_50
                }
                onSelect(newOption)
            }
        }

        SpacerHor(width = 12.dp)

        IvyText(
            modifier = Modifier.testTag("donation_amount"),
            text = "$${
                when (option) {
                    DonateOption.DONATE_2 -> 2
                    DonateOption.DONATE_5 -> 5
                    DonateOption.DONATE_10 -> 10
                    DonateOption.DONATE_15 -> 15
                    DonateOption.DONATE_25 -> 25
                    DonateOption.DONATE_50 -> 50
                    DonateOption.DONATE_100 -> 100
                }
            }",
            typo = UI.typo.nH1.style(
                fontWeight = FontWeight.Bold,
                color = White
            )
        )

        SpacerHor(width = 12.dp)

        if (option != DonateOption.DONATE_100) {
            OptionPickerButton(
                icon = R.drawable.ic_donate_plus,
                contentDescription = "btn_plus"
            ) {
                val newOption = when (option) {
                    DonateOption.DONATE_2 -> DonateOption.DONATE_5
                    DonateOption.DONATE_5 -> DonateOption.DONATE_10
                    DonateOption.DONATE_10 -> DonateOption.DONATE_15
                    DonateOption.DONATE_15 -> DonateOption.DONATE_25
                    DonateOption.DONATE_25 -> DonateOption.DONATE_50
                    DonateOption.DONATE_50 -> DonateOption.DONATE_100
                    DonateOption.DONATE_100 -> DonateOption.DONATE_100
                }
                onSelect(newOption)
            }
        }
    }
}

@Composable
private fun OptionPickerButton(
    @DrawableRes icon: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Image(
        modifier = Modifier
            .clip(UI.shapes.r4)
            .background(Black)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        painter = painterResource(icon),
        contentDescription = contentDescription,
    )
}

@Composable
private fun ScreenContent() {
    LazyColumn {
        item {
            SpacerVer(height = 32.dp)

            IvyText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                text = "It seems like you enjoy free and open-source software. We too!",
                typo = UI.typo.b1.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        item {
            SpacerVer(height = 12.dp)

            IvyText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                text = "BIG THANKS to all Ivy contributors who made Ivy Wallet possible! That's why we opened a donations channel to sustain and improve our small project.",
                typo = UI.typo.b2.style(
                    color = UI.colors.gray,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        item {
            SpacerVer(height = 24.dp)

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .background(UI.colors.medium, UI.shapes.r4)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                text = "If you want to support us feel free to donate whatever amount you're comfortable with - it all helps! (local taxes may apply)".uppercase(),
                style = UI.typo.c.style(
                    fontWeight = FontWeight.Bold,
                    color = UI.colors.red1Inverse
                )
            )
        }

        item {
            SpacerVer(height = 120.dp) //scroll hack
        }
    }
}

@Composable
private fun BoxWithConstraintsScope.DonateButton(
    onClick: () -> Unit
) {
    IvyButton(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 16.dp)
            .testTag("btn_donate"),
        iconStart = R.drawable.ic_donate_crown,
        wrapContentMode = false,
        iconTint = UI.colors.pure,
        iconEdgePadding = 16.dp,
        text = "Donate",
        backgroundGradient = Gradient.solid(UI.colors.pureInverse),
        textStyle = UI.typo.b1.style(
            fontWeight = FontWeight.Bold,
            color = UI.colors.pure
        )
    ) {
        onClick()
    }
}

@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        UI(onEvent = {})
    }
}