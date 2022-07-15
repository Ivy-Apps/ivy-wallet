package com.ivy.wallet.ui.theme.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.IvyWalletPreview
import com.ivy.base.R
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.DividerW
import com.ivy.design.l1_buildingBlocks.IvyText
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.wallet.ui.theme.Ivy
import com.ivy.wallet.ui.theme.components.ItemIconS
import com.ivy.wallet.ui.theme.dynamicContrast
import com.ivy.wallet.utils.hideKeyboard
import com.ivy.wallet.utils.thenIf
import java.util.*

private const val ICON_PICKER_ICONS_PER_ROW = 5

@Composable
fun BoxWithConstraintsScope.ChooseIconModal(
    visible: Boolean,
    initialIcon: String?,
    color: Color,

    id: UUID = UUID.randomUUID(),

    dismiss: () -> Unit,
    onIconChosen: (String?) -> Unit
) {
    var selectedIcon by remember(id) {
        mutableStateOf(initialIcon)
    }

    IvyModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        scrollState = null,
        includeActionsRowPadding = false,
        PrimaryAction = {
            ModalSave(
                modifier = Modifier.testTag("choose_icon_save")
            ) {
                onIconChosen(selectedIcon)
                dismiss()
            }
        }
    ) {
        val view = LocalView.current
        onScreenStart {
            hideKeyboard(view)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Spacer(Modifier.height(32.dp))

                ModalTitle(text = stringResource(R.string.choose_icon))

                Spacer(Modifier.height(4.dp))
            }

            icons(selectedIcon = selectedIcon, color = color) {
                selectedIcon = it
            }

            item {
                Spacer(Modifier.height(160.dp))
            }
        }
    }
}

private fun LazyListScope.icons(
    selectedIcon: String?,
    color: Color,

    onIconSelected: (String) -> Unit
) {
    val icons = ivyIcons()

    iconsR(
        icons = icons,
        iconsPerRow = ICON_PICKER_ICONS_PER_ROW,
        selectedIcon = selectedIcon,
        color = color,
        onIconSelected = onIconSelected
    )
}

private tailrec fun LazyListScope.iconsR(
    icons: List<Any>,
    rowAcc: List<String> = emptyList(),

    iconsPerRow: Int,
    selectedIcon: String?,
    color: Color,

    onIconSelected: (String) -> Unit
) {
    if (icons.isNotEmpty()) {
        //recurse

        when (val currentItem = icons.first()) {
            is IconPickerSection -> {
                addIconsRowIfNotEmpty(
                    rowAcc = rowAcc,
                    selectedIcon = selectedIcon,
                    color = color,
                    onIconSelected = onIconSelected
                )

                item {
                    Section(title = currentItem.title)
                }

                //RECURSE
                iconsR(
                    icons = icons.drop(1),
                    rowAcc = emptyList(),

                    iconsPerRow = iconsPerRow,
                    selectedIcon = selectedIcon,
                    color = color,
                    onIconSelected = onIconSelected

                )
            }
            is String -> {
                //icon

                if (rowAcc.size == iconsPerRow) {
                    //recurse and reset acc

                    addIconsRowIfNotEmpty(
                        rowAcc = rowAcc,
                        selectedIcon = selectedIcon,
                        color = color,
                        onIconSelected = onIconSelected
                    )

                    //RECURSE
                    iconsR(
                        icons = icons.drop(1),
                        rowAcc = emptyList(),

                        iconsPerRow = iconsPerRow,
                        selectedIcon = selectedIcon,
                        color = color,
                        onIconSelected = onIconSelected

                    )
                } else {
                    //recurse by filling acc

                    //RECURSE
                    iconsR(
                        icons = icons.drop(1),
                        rowAcc = rowAcc + currentItem,

                        iconsPerRow = iconsPerRow,
                        selectedIcon = selectedIcon,
                        color = color,
                        onIconSelected = onIconSelected

                    )
                }
            }
        }
    } else {
        //end recursion
        addIconsRowIfNotEmpty(
            rowAcc = rowAcc,
            selectedIcon = selectedIcon,
            color = color,
            onIconSelected = onIconSelected
        )
    }
}

private fun LazyListScope.addIconsRowIfNotEmpty(
    rowAcc: List<String>,

    selectedIcon: String?,
    color: Color,

    onIconSelected: (String) -> Unit
) {
    if (rowAcc.isNotEmpty()) {
        item {
            IconsRow(
                icons = rowAcc,
                selectedIcon = selectedIcon,
                color = color
            ) {
                onIconSelected(it)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun IconsRow(
    icons: List<String>,
    selectedIcon: String?,
    color: Color,

    onIconSelected: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        for ((index, icon) in icons.withIndex()) {
            Icon(
                icon = icon,
                selected = selectedIcon == icon,
                color = color
            ) {
                onIconSelected(icon)
            }

            if (index < icons.lastIndex && icons.size >= 5) {
                Spacer(Modifier.weight(1f))
            } else {
                Spacer(Modifier.width(20.dp))
            }
        }

        Spacer(Modifier.width(24.dp))
    }
}

@Composable
private fun Icon(
    icon: String,
    selected: Boolean,
    color: Color,

    onClick: () -> Unit,
) {
    ItemIconS(
        modifier = Modifier
            .clip(CircleShape)
            .border(2.dp, if (selected) color else UI.colors.medium, CircleShape)
            .thenIf(selected) {
                background(color, CircleShape)
            }
            .clickable {
                onClick()
            }
            .padding(all = 8.dp)
            .testTag(icon),
        iconName = icon,
        tint = if (selected) color.dynamicContrast() else UI.colors.mediumInverse
    )
}

@Composable
private fun Section(
    title: String
) {
    SpacerVer(height = 20.dp)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DividerW()

        SpacerHor(width = 16.dp)

        IvyText(text = title, typo = UI.typo.b1)

        SpacerHor(width = 16.dp)

        DividerW()
    }

    SpacerVer(height = 20.dp)
}

@Preview
@Composable
private fun ChooseIconModal() {
    IvyWalletPreview {
        ChooseIconModal(
            visible = true,
            initialIcon = "gift",
            color = Ivy,
            dismiss = {}
        ) {

        }
    }
}

data class IconPickerSection(val title: String)

fun ivyIcons(): List<Any> = listOf(
    IconPickerSection("Ivy"),
    "account",
    "category",
    "cash",
    "bank",
    "revolut",
    "clothes2",
    "clothes",
    "family",
    "star",
    "education",
    "fitness",
    "loan",
    "orderfood",
    "orderfood2",
    "pet",
    "restaurant",
    "selfdevelopment",
    "work",
    "vehicle",
    "atom",
    "bills",
    "birthday",
    "calculator",
    "camera",
    "chemistry",
    "coffee",
    "connect",
    "dna",
    "doctor",
    "document",
    "drink",
    "farmacy",
    "fingerprint",
    "fishfood",
    "food2",
    "fooddrink",
    "furniture",
    "gambling",
    "game",
    "gears",
    "gift",
    "groceries",
    "hairdresser",
    "health",
    "hike",
    "house",
    "insurance",
    "label",
    "leaf",
    "location",
    "makeup",
    "music",
    "notice",
    "people",
    "plant",
    "programming",
    "relationship",
    "rocket",
    "safe",
    "sail",
    "server",
    "shopping2",
    "shopping",
    "sports",
    "stats",
    "tools",
    "transport",
    "travel",
    "trees",
    "zeus",
    "calendar",
    "crown",
    "diamond",
    "palette",
    IconPickerSection("Brands"),
    "ic_vue_brands_triangle",
    "ic_vue_brands_trello",
    "ic_vue_brands_html5",
    "ic_vue_brands_spotify",
    "ic_vue_brands_bootsrap",
    "ic_vue_brands_dribbble",
    "ic_vue_brands_google_play",
    "ic_vue_brands_dropbox",
    "ic_vue_brands_js",
    "ic_vue_brands_drive",
    "ic_vue_brands_paypal",
    "ic_vue_brands_be",
    "ic_vue_brands_figma",
    "ic_vue_brands_messenger",
    "ic_vue_brands_facebook",
    "ic_vue_brands_framer",
    "ic_vue_brands_whatsapp",
    "ic_vue_brands_html3",
    "ic_vue_brands_zoom",
    "ic_vue_brands_ok",
    "ic_vue_brands_twitch",
    "ic_vue_brands_youtube",
    "ic_vue_brands_apple",
    "ic_vue_brands_android",
    "ic_vue_brands_slack",
    "ic_vue_brands_vuesax",
    "ic_vue_brands_blogger",
    "ic_vue_brands_photoshop",
    "ic_vue_brands_python",
    "ic_vue_brands_google",
    "ic_vue_brands_xd",
    "ic_vue_brands_illustrator",
    "ic_vue_brands_xiaomi",
    "ic_vue_brands_windows",
    "ic_vue_brands_snapchat",
    "ic_vue_brands_ui8",
    IconPickerSection("Building"),
    "ic_vue_building_building1",
    "ic_vue_building_buildings",
    "ic_vue_building_hospital",
    "ic_vue_building_building",
    "ic_vue_building_bank",
    "ic_vue_building_house",
    "ic_vue_building_courthouse",
    IconPickerSection("Chart"),
    "ic_vue_chart_diagram",
    "ic_vue_chart_graph",
    "ic_vue_chart_status_up",
    "ic_vue_chart_chart",
    "ic_vue_chart_trend_up",
    IconPickerSection("Crypto"),
    "ic_vue_crypto_dent",
    "ic_vue_crypto_icon",
    "ic_vue_crypto_decred",
    "ic_vue_crypto_ocean_protocol",
    "ic_vue_crypto_hedera_hashgraph",
    "ic_vue_crypto_binance_usd",
    "ic_vue_crypto_maker",
    "ic_vue_crypto_xrp",
    "ic_vue_crypto_harmony",
    "ic_vue_crypto_theta",
    "ic_vue_crypto_celsius_",
    "ic_vue_crypto_vibe",
    "ic_vue_crypto_augur",
    "ic_vue_crypto_graph",
    "ic_vue_crypto_monero",
    "ic_vue_crypto_aave",
    "ic_vue_crypto_dai",
    "ic_vue_crypto_litecoin",
    "ic_vue_crypto_tether",
    "ic_vue_crypto_thorchain",
    "ic_vue_crypto_nexo",
    "ic_vue_crypto_chainlink",
    "ic_vue_crypto_ethereum_classic",
    "ic_vue_crypto_usd_coin",
    "ic_vue_crypto_nem",
    "ic_vue_crypto_eos",
    "ic_vue_crypto_emercoin",
    "ic_vue_crypto_dash",
    "ic_vue_crypto_ontology",
    "ic_vue_crypto_ftx_token",
    "ic_vue_crypto_educare",
    "ic_vue_crypto_solana",
    "ic_vue_crypto_ethereum",
    "ic_vue_crypto_velas",
    "ic_vue_crypto_hex",
    "ic_vue_crypto_polkadot",
    "ic_vue_crypto_huobi_token",
    "ic_vue_crypto_polyswarm",
    "ic_vue_crypto_ankr",
    "ic_vue_crypto_enjin_coin",
    "ic_vue_crypto_polygon",
    "ic_vue_crypto_wing",
    "ic_vue_crypto_nebulas",
    "ic_vue_crypto_iost",
    "ic_vue_crypto_binance_coin",
    "ic_vue_crypto_kyber_network",
    "ic_vue_crypto_trontron",
    "ic_vue_crypto_stellar",
    "ic_vue_crypto_avalanche",
    "ic_vue_crypto_wanchain",
    "ic_vue_crypto_cardano",
    "ic_vue_crypto_okb",
    "ic_vue_crypto_stacks",
    "ic_vue_crypto_siacoin",
    "ic_vue_crypto_autonio",
    "ic_vue_crypto_civic",
    "ic_vue_crypto_zel",
    "ic_vue_crypto_quant",
    "ic_vue_crypto_tenx",
    "ic_vue_crypto_celo",
    "ic_vue_crypto_bitcoin",
    IconPickerSection("Delivery"),
    "ic_vue_delivery_package",
    "ic_vue_delivery_receive",
    "ic_vue_delivery_box1",
    "ic_vue_delivery_box",
    "ic_vue_delivery_truck",
    IconPickerSection("Design"),
    "ic_vue_design_bezier",
    "ic_vue_design_brush",
    "ic_vue_design_color_swatch",
    "ic_vue_design_scissors",
    "ic_vue_design_magicpen",
    "ic_vue_design_roller",
    "ic_vue_design_tool_pen",
    IconPickerSection("Dev"),
    "ic_vue_dev_code",
    "ic_vue_dev_hierarchy",
    "ic_vue_dev_relation",
    "ic_vue_dev_arrow",
    "ic_vue_dev_data",
    "ic_vue_dev_hashtag",
    IconPickerSection("Education"),
    "ic_vue_edu_planer",
    "ic_vue_edu_briefcase",
    "ic_vue_edu_award",
    "ic_vue_edu_glass",
    "ic_vue_edu_graduate_cap",
    "ic_vue_edu_calculator",
    "ic_vue_edu_note",
    "ic_vue_edu_magazine",
    "ic_vue_edu_pen",
    "ic_vue_edu_telescope",
    "ic_vue_edu_book",
    "ic_vue_edu_ruler_pen",
    "ic_vue_edu_todo",
    "ic_vue_edu_omega",
    "ic_vue_edu_bookmark",
    IconPickerSection("Files"),
    "ic_vue_files_folder_favorite",
    "ic_vue_files_folder",
    "ic_vue_files_folder_cloud",
    IconPickerSection("Location"),
    "ic_vue_location_map1",
    "ic_vue_location_map",
    "ic_vue_location_location",
    "ic_vue_location_global",
    "ic_vue_location_global_search",
    "ic_vue_location_routing",
    "ic_vue_location_discover",
    "ic_vue_location_radar",
    "ic_vue_location_global_edit",
    IconPickerSection("Main"),
    "ic_vue_main_cake",
    "ic_vue_main_reserve",
    "ic_vue_main_archive",
    "ic_vue_main_signpost",
    "ic_vue_main_coffee",
    "ic_vue_main_sport",
    "ic_vue_main_notification",
    "ic_vue_main_lamp_charge",
    "ic_vue_main_home",
    "ic_vue_main_judge",
    "ic_vue_main_timer",
    "ic_vue_main_lamp",
    "ic_vue_main_battery_charging",
    "ic_vue_main_calendar",
    "ic_vue_main_home_wifi",
    "ic_vue_main_tree",
    "ic_vue_main_battery_half",
    "ic_vue_main_send",
    "ic_vue_main_glass",
    "ic_vue_main_emoji_normal",
    "ic_vue_main_share",
    "ic_vue_main_trash",
    "ic_vue_main_milk",
    "ic_vue_main_lifebuoy",
    "ic_vue_main_broom",
    "ic_vue_main_gift",
    "ic_vue_main_clock",
    "ic_vue_main_emoji_happy",
    "ic_vue_main_home_safe",
    "ic_vue_main_crown",
    "ic_vue_main_cup",
    "ic_vue_main_emoji_sad",
    "ic_vue_main_pet",
    "ic_vue_main_flash",
    IconPickerSection("Media"),
    "ic_vue_media_microphone",
    "ic_vue_media_music",
    "ic_vue_media_voice",
    "ic_vue_media_image",
    "ic_vue_media_scissors",
    "ic_vue_media_mountains",
    "ic_vue_media_film",
    "ic_vue_media_photocamera",
    "ic_vue_media_film_play",
    "ic_vue_media_camera",
    "ic_vue_media_screenmirroring",
    "ic_vue_media_speaker",
    "ic_vue_media_play",
    "ic_vue_media_subtitle",
    "ic_vue_media_setting",
    IconPickerSection("Messages"),
    "ic_vue_messages_msg_favorite",
    "ic_vue_messages_direct",
    "ic_vue_messages_msg_notification",
    "ic_vue_messages_device_msg",
    "ic_vue_messages_edit",
    "ic_vue_messages_msgs",
    "ic_vue_messages_msg_text",
    "ic_vue_messages_letter",
    "ic_vue_messages_msg",
    "ic_vue_messages_msg_search",
    IconPickerSection("Money"),
    "ic_vue_money_bitcoin_refresh",
    "ic_vue_money_dollar",
    "ic_vue_money_archive",
    "ic_vue_money_coins",
    "ic_vue_money_discount",
    "ic_vue_money_recive",
    "ic_vue_money_card_send",
    "ic_vue_money_buy_crypto",
    "ic_vue_money_card_bitcoin",
    "ic_vue_money_buy_bitcoin",
    "ic_vue_money_ticket_star",
    "ic_vue_money_wallet",
    "ic_vue_money_send",
    "ic_vue_money_ticket_discount",
    "ic_vue_money_wallet_cards",
    "ic_vue_money_receipt_empty",
    "ic_vue_money_percentage",
    "ic_vue_money_math",
    "ic_vue_money_security_card",
    "ic_vue_money_wallet_money",
    "ic_vue_money_ticket",
    "ic_vue_money_card_receive",
    "ic_vue_money_wallet_empty",
    "ic_vue_money_transfer",
    "ic_vue_money_card_coin",
    "ic_vue_money_receipt_items",
    "ic_vue_money_tag",
    "ic_vue_money_receipt_discount",
    "ic_vue_money_card",
    IconPickerSection("PC"),
    "ic_vue_pc_charging",
    "ic_vue_pc_watch",
    "ic_vue_pc_headphone",
    "ic_vue_pc_gameboy",
    "ic_vue_pc_phone_call",
    "ic_vue_pc_setting",
    "ic_vue_pc_monitor",
    "ic_vue_pc_cpu",
    "ic_vue_pc_printer",
    "ic_vue_pc_bluetooth",
    "ic_vue_pc_wifi",
    "ic_vue_pc_game",
    "ic_vue_pc_speaker",
    "ic_vue_pc_phone",
    IconPickerSection("People"),
    "ic_vue_people_2persons",
    "ic_vue_people_person_tag",
    "ic_vue_people_person_search",
    "ic_vue_people_people",
    "ic_vue_people_person",
    IconPickerSection("Security"),
    "ic_vue_security_eye",
    "ic_vue_security_shield_security",
    "ic_vue_security_key",
    "ic_vue_security_alarm",
    "ic_vue_security_lock",
    "ic_vue_security_password",
    "ic_vue_security_radar",
    "ic_vue_security_shield_person",
    "ic_vue_security_shield",
    IconPickerSection("Shop"),
    "ic_vue_shop_cart",
    "ic_vue_shop_bag",
    "ic_vue_shop_barcode",
    "ic_vue_shop_bag1",
    "ic_vue_shop_shop",
    IconPickerSection("Support"),
    "ic_vue_support_star",
    "ic_vue_support_medal",
    "ic_vue_support_dislike",
    "ic_vue_support_like_dislike",
    "ic_vue_support_smileys",
    "ic_vue_support_heart",
    "ic_vue_support_like",
    IconPickerSection("Transport"),
    "ic_vue_transport_bus",
    "ic_vue_transport_airplane",
    "ic_vue_transport_train",
    "ic_vue_transport_ship",
    "ic_vue_transport_gas",
    "ic_vue_transport_car",
    "ic_vue_transport_car_wash",
    IconPickerSection("Type"),
    "ic_vue_type_link2",
    "ic_vue_type_text",
    "ic_vue_type_paperclip",
    "ic_vue_type_textalign_left",
    "ic_vue_type_translate",
    "ic_vue_type_textalign_right",
    "ic_vue_type_link",
    "ic_vue_type_textalign_center",
    "ic_vue_type_textalign_justifycenter",
    IconPickerSection("Weather"),
    "ic_vue_weather_wind",
    "ic_vue_weather_cloud",
    "ic_vue_weather_flash",
    "ic_vue_weather_moon",
    "ic_vue_weather_drop",
    "ic_vue_weather_cold",
    "ic_vue_weather_sun",
)
