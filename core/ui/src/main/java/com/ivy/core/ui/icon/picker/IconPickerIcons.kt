package com.ivy.core.ui.icon.picker

import com.ivy.core.ui.icon.picker.data.Icon
import com.ivy.core.ui.icon.picker.data.SectionUnverified

// region Icons
internal fun pickerItems(): List<SectionUnverified> = listOf(
    SectionUnverified(name = "Ivy", icons = ivyIcons()),
    SectionUnverified(name = "Brands", icons = vueBrands()),
    SectionUnverified(name = "Building", icons = vueBuilding()),
    SectionUnverified(name = "Chart", icons = vueChart()),
    SectionUnverified(name = "Crypto", icons = vueCrypto()),
    SectionUnverified(name = "Delivery", icons = vueDelivery()),
    SectionUnverified(name = "Design", icons = vueDesign()),
    SectionUnverified(name = "Dev", icons = vueDev()),
    SectionUnverified(name = "Education", icons = vueEducation()),
    SectionUnverified(name = "Location", icons = vueLocation()),
    SectionUnverified(name = "Main", icons = vueMain()),
    SectionUnverified(name = "Media", icons = vueMedia()),
    SectionUnverified(name = "Messages", icons = vueMessages()),
    SectionUnverified(name = "Money", icons = vueMoney()),
    SectionUnverified(name = "PC", icons = vuePC()),
    SectionUnverified(name = "People", icons = vuePeople()),
    SectionUnverified(name = "Security", icons = vueSecurity()),
    SectionUnverified(name = "Shop", icons = vueShop()),
    SectionUnverified(name = "Support", icons = vueSupport()),
    SectionUnverified(name = "Transport", icons = vueTransport()),
    SectionUnverified(name = "Type", icons = vueType()),
    SectionUnverified(name = "Weather", icons = vueWeather())
)
// endregion

// region Ivy Icons
private fun ivyIcons(): List<Icon> = listOf(
    Icon("account", keywords = listOf("account", "acc")),
    Icon("category", keywords = listOf("category", "cat")),
    Icon("cash", keywords = listOf("cash", "money")),
    Icon("bank", keywords = listOf("bank")),
    Icon("revolut", keywords = listOf("revolut", "rev")),
    Icon("clothes2", keywords = listOf("clothes")),
    Icon("clothes", keywords = listOf("clothes", "wardrobe")),
    Icon("family", keywords = listOf("family", "home", "couple", "kids")),
    Icon("star", keywords = listOf("star", "favorite")),
    Icon("education", keywords = listOf("education", "school", "university")),
    Icon("fitness", keywords = listOf("fitness", "gym", "workout", "train", "weights")),
    Icon("loan", keywords = listOf("loan", "bill", "note")),
    Icon("orderfood", keywords = listOf("order", "delivery", "box")),
    Icon("orderfood2", keywords = listOf("order", "delivery", "scouter")),
    Icon("pet", keywords = listOf("pet", "dog", "paw")),
    Icon("restaurant", keywords = listOf("restaurant", "eat", "dinner", "food")),
    Icon("selfdevelopment", keywords = listOf("learn", "improve", "level", "grow")),
    Icon("work", keywords = listOf("work", "job")),
    Icon("vehicle", keywords = listOf("vehicle", "car", "transport")),
    Icon("atom", keywords = listOf("atom")),
    Icon("bills", keywords = listOf("bills", "payment")),
    Icon("birthday", keywords = listOf("birthday", "celebration", "party", "gathering")),
    Icon("calculator", keywords = listOf("calculator", "count")),
    Icon("camera", keywords = listOf("camera", "picture", "photo", "album")),
    Icon("chemistry", keywords = listOf("chemistry", "chemical", "lab", "laboratory")),
    Icon("coffee", keywords = listOf("coffee", "cafe")),
    Icon("connect", keywords = listOf("connect")),
    Icon("dna", keywords = listOf("dna", "laboratory")),
    Icon("doctor", keywords = listOf("doctor", "checkup", "medicine", "sick", "ill")),
    Icon("document", keywords = listOf("document", "paper", "paper")),
    Icon("drink", keywords = listOf("drink", "celebration", "party")),
    Icon("farmacy", keywords = listOf("farmacy", "pharmacy", "pills", "medicine")),
    Icon("fingerprint", keywords = listOf("fingerprint", "authentication", "security")),
    Icon("fishfood", keywords = listOf("fishfood", "fish", "food", "seafood")),
    Icon("food2", keywords = listOf("food", "delivery", "order", "pizza", "dine")),
    Icon("fooddrink", keywords = listOf("food", "delivery", "order", "pizza", "dine")),
    Icon("furniture", keywords = listOf("furniture", "household", "house", "cabinet", "home")),
    Icon("gambling", keywords = listOf("gambling", "play", "casino", "game")),
    Icon("game", keywords = listOf("game", "play", "console", "pc")),
    Icon("gears", keywords = listOf("gears", "maintenance", "car")),
    Icon("gift", keywords = listOf("gift", "party", "celebration")),
    Icon("groceries", keywords = listOf("groceries", "market", "supplies")),
    Icon("hairdresser", keywords = listOf("hairdresser", "parlor", "beauty", "hair")),
    Icon("health", keywords = listOf("health", "medicine", "doctor")),
    Icon("hike", keywords = listOf("hike", "mountain", "walk")),
    Icon("house", keywords = listOf("house", "mortgage", "home")),
    Icon("insurance", keywords = listOf("insurance", "bills", "protection")),
    Icon("label", keywords = listOf("label", "tag")),
    Icon("leaf", keywords = listOf("leaf", "plant", "lawn", "garden")),
    Icon("location", keywords = listOf("location", "gps")),
    Icon("makeup", keywords = listOf("makeup", "parlor", "beauty")),
    Icon("music", keywords = listOf("music", "headset", "headphones", "sound", "spotify")),
    Icon("notice", keywords = listOf("notice", "warning", "urgent")),
    Icon("people", keywords = listOf("people", "gathering", "contact")),
    Icon("plant", keywords = listOf("plant", "tree", "garden", "yard", "lawn")),
    Icon("programming", keywords = listOf("programming", "code", "software")),
    Icon("relationship", keywords = listOf("relationship", "love", "heart", "partner")),
    Icon("rocket", keywords = listOf("rocket", "moon", "space", "yolo")),
    Icon("safe", keywords = listOf("safe", "security", "protection", "lock")),
    Icon(
        "sail",
        keywords = listOf("sail", "anchor", "cruise", "boat", "yacht", "travel", "vacation")
    ),
    Icon("server", keywords = listOf("server", "modem", "communication")),
    Icon("shopping2", keywords = listOf("shopping", "cart", "groceries")),
    Icon("shopping", keywords = listOf("shopping", "basket", "tote", "bag", "box")),
    Icon("sports", keywords = listOf("sports", "basketball", "soccer", "football")),
    Icon("stats", keywords = listOf("stats", "chart", "stocks")),
    Icon("tools", keywords = listOf("tools", "equipment", "hammer")),
    Icon("transport", keywords = listOf("transport", "car", "bus", "commute")),
    Icon("travel", keywords = listOf("travel", "plane", "abroad", "explore", "vacation")),
    Icon("trees", keywords = listOf("trees", "garden", "yard", "lawn")),
    Icon("zeus", keywords = listOf("zeus", "lightning", "rain", "emergency", "urgent")),
    Icon("calendar", keywords = listOf("calendar", "plan", "schedule", "memo")),
    Icon("crown", keywords = listOf("crown", "luxury", "vip")),
    Icon("diamond", keywords = listOf("diamond", "luxury", "vip", "wedding")),
    Icon("palette", keywords = listOf("palette", "paint", "art")),
)
// endregion

// region Brands (Vue)
private fun vueBrands(): List<Icon> = listOf(
    Icon("ic_vue_brands_triangle"),
    Icon("ic_vue_brands_trello"),
    Icon("ic_vue_brands_html5"),
    Icon("ic_vue_brands_spotify"),
    Icon("ic_vue_brands_bootsrap"),
    Icon("ic_vue_brands_dribbble"),
    Icon("ic_vue_brands_google_play"),
    Icon("ic_vue_brands_dropbox"),
    Icon("ic_vue_brands_js"),
    Icon("ic_vue_brands_drive"),
    Icon("ic_vue_brands_paypal"),
    Icon("ic_vue_brands_be"),
    Icon("ic_vue_brands_figma"),
    Icon("ic_vue_brands_messenger"),
    Icon("ic_vue_brands_facebook"),
    Icon("ic_vue_brands_framer"),
    Icon("ic_vue_brands_whatsapp"),
    Icon("ic_vue_brands_html3"),
    Icon("ic_vue_brands_zoom"),
    Icon("ic_vue_brands_ok"),
    Icon("ic_vue_brands_twitch"),
    Icon("ic_vue_brands_youtube"),
    Icon("ic_vue_brands_apple"),
    Icon("ic_vue_brands_android"),
    Icon("ic_vue_brands_slack"),
    Icon("ic_vue_brands_vuesax"),
    Icon("ic_vue_brands_blogger"),
    Icon("ic_vue_brands_photoshop"),
    Icon("ic_vue_brands_python"),
    Icon("ic_vue_brands_google"),
    Icon("ic_vue_brands_xd"),
    Icon("ic_vue_brands_illustrator"),
    Icon("ic_vue_brands_xiaomi"),
    Icon("ic_vue_brands_windows"),
    Icon("ic_vue_brands_snapchat"),
    Icon("ic_vue_brands_ui8"),
)
// endregion

// region Building (Vue)
private fun vueBuilding(): List<Icon> = listOf(
    Icon("ic_vue_building_building1"),
    Icon("ic_vue_building_buildings"),
    Icon("ic_vue_building_hospital"),
    Icon("ic_vue_building_building"),
    Icon("ic_vue_building_bank"),
    Icon("ic_vue_building_house"),
    Icon("ic_vue_building_courthouse"),
)
// endregion

// region Chart (Vue)
private fun vueChart(): List<Icon> = listOf(
    Icon("ic_vue_chart_diagram"),
    Icon("ic_vue_chart_graph"),
    Icon("ic_vue_chart_status_up"),
    Icon("ic_vue_chart_chart"),
    Icon("ic_vue_chart_trend_up"),
)
// endregion

// region Crypto (Vue)
private fun vueCrypto(): List<Icon> = listOf(
    Icon("ic_vue_crypto_dent"),
    Icon("ic_vue_crypto_icon"),
    Icon("ic_vue_crypto_decred"),
    Icon("ic_vue_crypto_ocean_protocol"),
    Icon("ic_vue_crypto_hedera_hashgraph"),
    Icon("ic_vue_crypto_binance_usd"),
    Icon("ic_vue_crypto_maker"),
    Icon("ic_vue_crypto_xrp"),
    Icon("ic_vue_crypto_harmony"),
    Icon("ic_vue_crypto_theta"),
    Icon("ic_vue_crypto_celsius_"),
    Icon("ic_vue_crypto_vibe"),
    Icon("ic_vue_crypto_augur"),
    Icon("ic_vue_crypto_graph"),
    Icon("ic_vue_crypto_monero"),
    Icon("ic_vue_crypto_aave"),
    Icon("ic_vue_crypto_dai"),
    Icon("ic_vue_crypto_litecoin"),
    Icon("ic_vue_crypto_tether"),
    Icon("ic_vue_crypto_thorchain"),
    Icon("ic_vue_crypto_nexo"),
    Icon("ic_vue_crypto_chainlink"),
    Icon("ic_vue_crypto_ethereum_classic"),
    Icon("ic_vue_crypto_usd_coin"),
    Icon("ic_vue_crypto_nem"),
    Icon("ic_vue_crypto_eos"),
    Icon("ic_vue_crypto_emercoin"),
    Icon("ic_vue_crypto_dash"),
    Icon("ic_vue_crypto_ontology"),
    Icon("ic_vue_crypto_ftx_token"),
    Icon("ic_vue_crypto_educare"),
    Icon("ic_vue_crypto_solana"),
    Icon("ic_vue_crypto_ethereum"),
    Icon("ic_vue_crypto_velas"),
    Icon("ic_vue_crypto_hex"),
    Icon("ic_vue_crypto_polkadot"),
    Icon("ic_vue_crypto_huobi_token"),
    Icon("ic_vue_crypto_polyswarm"),
    Icon("ic_vue_crypto_ankr"),
    Icon("ic_vue_crypto_enjin_coin"),
    Icon("ic_vue_crypto_polygon"),
    Icon("ic_vue_crypto_wing"),
    Icon("ic_vue_crypto_nebulas"),
    Icon("ic_vue_crypto_iost"),
    Icon("ic_vue_crypto_binance_coin"),
    Icon("ic_vue_crypto_kyber_network"),
    Icon("ic_vue_crypto_trontron"),
    Icon("ic_vue_crypto_stellar"),
    Icon("ic_vue_crypto_avalanche"),
    Icon("ic_vue_crypto_wanchain"),
    Icon("ic_vue_crypto_cardano"),
    Icon("ic_vue_crypto_okb"),
    Icon("ic_vue_crypto_stacks"),
    Icon("ic_vue_crypto_siacoin"),
    Icon("ic_vue_crypto_autonio"),
    Icon("ic_vue_crypto_civic"),
    Icon("ic_vue_crypto_zel"),
    Icon("ic_vue_crypto_quant"),
    Icon("ic_vue_crypto_tenx"),
    Icon("ic_vue_crypto_celo"),
    Icon("ic_vue_crypto_bitcoin"),
)
// endregion

// region Delivery (Vue)
private fun vueDelivery(): List<Icon> = listOf(
    Icon("ic_vue_delivery_package"),
    Icon("ic_vue_delivery_receive"),
    Icon("ic_vue_delivery_box1"),
    Icon("ic_vue_delivery_box"),
    Icon("ic_vue_delivery_truck"),
)
// endregion

// region Design (Vue)
private fun vueDesign(): List<Icon> = listOf(
    Icon("ic_vue_design_bezier"),
    Icon("ic_vue_design_brush"),
    Icon("ic_vue_design_color_swatch"),
    Icon("ic_vue_design_scissors"),
    Icon("ic_vue_design_magicpen"),
    Icon("ic_vue_design_roller"),
    Icon("ic_vue_design_tool_pen"),
)
// endregion

// region Dev (Vue)
private fun vueDev(): List<Icon> = listOf(
    Icon("ic_vue_dev_code"),
    Icon("ic_vue_dev_hierarchy"),
    Icon("ic_vue_dev_relation"),
    Icon("ic_vue_dev_arrow"),
    Icon("ic_vue_dev_data"),
    Icon("ic_vue_dev_hashtag"),
)
// endregion

// region Education (Vue)
private fun vueEducation(): List<Icon> = listOf(
    Icon("ic_vue_edu_planer"),
    Icon("ic_vue_edu_briefcase"),
    Icon("ic_vue_edu_award"),
    Icon("ic_vue_edu_glass"),
    Icon("ic_vue_edu_graduate_cap"),
    Icon("ic_vue_edu_calculator"),
    Icon("ic_vue_edu_note"),
    Icon("ic_vue_edu_magazine"),
    Icon("ic_vue_edu_pen"),
    Icon("ic_vue_edu_telescope"),
    Icon("ic_vue_edu_book"),
    Icon("ic_vue_edu_ruler_pen"),
    Icon("ic_vue_edu_todo"),
    Icon("ic_vue_edu_omega"),
    Icon("ic_vue_edu_bookmark"),
)
// endregion

// region Files (Vue)
private fun vueFiles(): List<Icon> = listOf(
    Icon("ic_vue_files_folder_favorite"),
    Icon("ic_vue_files_folder"),
    Icon("ic_vue_files_folder_cloud"),
)
// endregion

// region Location (Vue)
private fun vueLocation(): List<Icon> = listOf(
    Icon("ic_vue_location_map1"),
    Icon("ic_vue_location_map"),
    Icon("ic_vue_location_location"),
    Icon("ic_vue_location_global"),
    Icon("ic_vue_location_global_search"),
    Icon("ic_vue_location_routing"),
    Icon("ic_vue_location_discover"),
    Icon("ic_vue_location_radar"),
    Icon("ic_vue_location_global_edit"),
)
// endregion

// region Main (Vue)
private fun vueMain(): List<Icon> = listOf(
    Icon("ic_vue_main_cake"),
    Icon("ic_vue_main_reserve"),
    Icon("ic_vue_main_archive"),
    Icon("ic_vue_main_signpost"),
    Icon("ic_vue_main_coffee"),
    Icon("ic_vue_main_sport"),
    Icon("ic_vue_main_notification"),
    Icon("ic_vue_main_lamp_charge"),
    Icon("ic_vue_main_home"),
    Icon("ic_vue_main_judge"),
    Icon("ic_vue_main_timer"),
    Icon("ic_vue_main_lamp"),
    Icon("ic_vue_main_battery_charging"),
    Icon("ic_vue_main_calendar"),
    Icon("ic_vue_main_home_wifi"),
    Icon("ic_vue_main_tree"),
    Icon("ic_vue_main_battery_half"),
    Icon("ic_vue_main_send"),
    Icon("ic_vue_main_glass"),
    Icon("ic_vue_main_emoji_normal"),
    Icon("ic_vue_main_share"),
    Icon("ic_vue_main_trash"),
    Icon("ic_vue_main_milk"),
    Icon("ic_vue_main_lifebuoy"),
    Icon("ic_vue_main_broom"),
    Icon("ic_vue_main_gift"),
    Icon("ic_vue_main_clock"),
    Icon("ic_vue_main_emoji_happy"),
    Icon("ic_vue_main_home_safe"),
    Icon("ic_vue_main_crown"),
    Icon("ic_vue_main_cup"),
    Icon("ic_vue_main_emoji_sad"),
    Icon("ic_vue_main_pet"),
    Icon("ic_vue_main_flash"),
)
// endregion

// region Media (Vue)
private fun vueMedia(): List<Icon> = listOf(
    Icon("ic_vue_media_microphone"),
    Icon("ic_vue_media_music"),
    Icon("ic_vue_media_voice"),
    Icon("ic_vue_media_image"),
    Icon("ic_vue_media_scissors"),
    Icon("ic_vue_media_mountains"),
    Icon("ic_vue_media_film"),
    Icon("ic_vue_media_photocamera"),
    Icon("ic_vue_media_film_play"),
    Icon("ic_vue_media_camera"),
    Icon("ic_vue_media_screenmirroring"),
    Icon("ic_vue_media_speaker"),
    Icon("ic_vue_media_play"),
    Icon("ic_vue_media_subtitle"),
    Icon("ic_vue_media_setting"),
)
// endregion

// region Messages (Vue)
private fun vueMessages(): List<Icon> = listOf(
    Icon("ic_vue_messages_msg_favorite"),
    Icon("ic_vue_messages_direct"),
    Icon("ic_vue_messages_msg_notification"),
    Icon("ic_vue_messages_device_msg"),
    Icon("ic_vue_messages_edit"),
    Icon("ic_vue_messages_msgs"),
    Icon("ic_vue_messages_msg_text"),
    Icon("ic_vue_messages_letter"),
    Icon("ic_vue_messages_msg"),
    Icon("ic_vue_messages_msg_search"),
)
// endregion

// region Money (Vue)
private fun vueMoney(): List<Icon> = listOf(
    Icon("ic_vue_money_bitcoin_refresh"),
    Icon("ic_vue_money_dollar"),
    Icon("ic_vue_money_archive"),
    Icon("ic_vue_money_coins"),
    Icon("ic_vue_money_discount"),
    Icon("ic_vue_money_recive"),
    Icon("ic_vue_money_card_send"),
    Icon("ic_vue_money_buy_crypto"),
    Icon("ic_vue_money_card_bitcoin"),
    Icon("ic_vue_money_buy_bitcoin"),
    Icon("ic_vue_money_ticket_star"),
    Icon("ic_vue_money_wallet"),
    Icon("ic_vue_money_send"),
    Icon("ic_vue_money_ticket_discount"),
    Icon("ic_vue_money_wallet_cards"),
    Icon("ic_vue_money_receipt_empty"),
    Icon("ic_vue_money_percentage"),
    Icon("ic_vue_money_math"),
    Icon("ic_vue_money_security_card"),
    Icon("ic_vue_money_wallet_money"),
    Icon("ic_vue_money_ticket"),
    Icon("ic_vue_money_card_receive"),
    Icon("ic_vue_money_wallet_empty"),
    Icon("ic_vue_money_transfer"),
    Icon("ic_vue_money_card_coin"),
    Icon("ic_vue_money_receipt_items"),
    Icon("ic_vue_money_tag"),
    Icon("ic_vue_money_receipt_discount"),
    Icon("ic_vue_money_card"),
)
// endregion

// region PC (Vue)
private fun vuePC(): List<Icon> = listOf(
    Icon("ic_vue_pc_charging"),
    Icon("ic_vue_pc_watch"),
    Icon("ic_vue_pc_headphone"),
    Icon("ic_vue_pc_gameboy"),
    Icon("ic_vue_pc_phone_call"),
    Icon("ic_vue_pc_setting"),
    Icon("ic_vue_pc_monitor"),
    Icon("ic_vue_pc_cpu"),
    Icon("ic_vue_pc_printer"),
    Icon("ic_vue_pc_bluetooth"),
    Icon("ic_vue_pc_wifi"),
    Icon("ic_vue_pc_game"),
    Icon("ic_vue_pc_speaker"),
    Icon("ic_vue_pc_phone"),
)
// endregion

// region People (Vue)
private fun vuePeople(): List<Icon> = listOf(
    Icon("ic_vue_people_2persons"),
    Icon("ic_vue_people_person_tag"),
    Icon("ic_vue_people_person_search"),
    Icon("ic_vue_people_people"),
    Icon("ic_vue_people_person"),
)
// endregion

// region Security (Vue)
private fun vueSecurity(): List<Icon> = listOf(
    Icon("ic_vue_security_eye"),
    Icon("ic_vue_security_shield_security"),
    Icon("ic_vue_security_key"),
    Icon("ic_vue_security_alarm"),
    Icon("ic_vue_security_lock"),
    Icon("ic_vue_security_password"),
    Icon("ic_vue_security_radar"),
    Icon("ic_vue_security_shield_person"),
    Icon("ic_vue_security_shield"),
)
// endregion

// region Shop (Vue)
private fun vueShop(): List<Icon> = listOf(
    Icon("ic_vue_shop_cart"),
    Icon("ic_vue_shop_bag"),
    Icon("ic_vue_shop_barcode"),
    Icon("ic_vue_shop_bag1"),
    Icon("ic_vue_shop_shop"),
)
// endregion

// region Support (Vue)
private fun vueSupport(): List<Icon> = listOf(
    Icon("ic_vue_support_star"),
    Icon("ic_vue_support_medal"),
    Icon("ic_vue_support_dislike"),
    Icon("ic_vue_support_like_dislike"),
    Icon("ic_vue_support_smileys"),
    Icon("ic_vue_support_heart"),
    Icon("ic_vue_support_like"),
)
// endregion

// region Transport (Vue)
private fun vueTransport(): List<Icon> = listOf(
    Icon("ic_vue_transport_bus"),
    Icon("ic_vue_transport_airplane"),
    Icon("ic_vue_transport_train"),
    Icon("ic_vue_transport_ship"),
    Icon("ic_vue_transport_gas"),
    Icon("ic_vue_transport_car"),
    Icon("ic_vue_transport_car_wash"),
)
// endregion

// region Type (Vue)
private fun vueType(): List<Icon> = listOf(
    Icon("ic_vue_type_link2"),
    Icon("ic_vue_type_text"),
    Icon("ic_vue_type_paperclip"),
    Icon("ic_vue_type_textalign_left"),
    Icon("ic_vue_type_translate"),
    Icon("ic_vue_type_textalign_right"),
    Icon("ic_vue_type_link"),
    Icon("ic_vue_type_textalign_center"),
    Icon("ic_vue_type_textalign_justifycenter"),
)
// endregion

// region Weather (Vue)
private fun vueWeather(): List<Icon> = listOf(
    Icon("ic_vue_weather_wind"),
    Icon("ic_vue_weather_cloud"),
    Icon("ic_vue_weather_flash"),
    Icon("ic_vue_weather_moon"),
    Icon("ic_vue_weather_drop"),
    Icon("ic_vue_weather_cold"),
    Icon("ic_vue_weather_sun"),
)
// endregion