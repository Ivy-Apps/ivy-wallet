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
    SectionUnverified(name = "Files", icons = vueFiles()),
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
    Icon("account", keywords = listOf("acc")),
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
    Icon(
        "work",
        keywords = listOf(
            "work", "case", "job", "occupation", "business", "profession", "hustle",
            "labour", "labor", "career", "assign", "company", "organi"
        )
    ),
    Icon(
        "vehicle",
        keywords = listOf(
            "car", "vehicle", "auto", "transport",
            "commute", "gas", "taxi", "bus", "tram", "subway", "trolley"
        )
    ),
    Icon(
        "atom", keywords = listOf(
            "atom", "science", "lab", "universe", "physics", "fantastic"
        )
    ),
    Icon(
        "bills", keywords = listOf(
            "bill", "acc", "tax", "fee", "book",
            "read", "note", "diary", "organise", "plan", "library",
            "organize"
        )
    ),
    Icon(
        "birthday", keywords = listOf(
            "birth", "cake", "candle", "surprise", "bday",
            "b-day"
        )
    ),
    Icon("calculator", keywords = listOf("calc", "math", "numb", "finance")),
    Icon(
        "camera", keywords = listOf(
            "cam", "video", "edit", "photo", "movie", "record", "direct",
            "ticket", "studio", "show", "tv", "stream", "act", "produce"
        )
    ),
    Icon(
        "chemistry", keywords = listOf(
            "chem", "cone", "science", "potion", "elixir",
            "pharmacy", "lab"
        )
    ),
    Icon(
        "coffee", keywords = listOf(
            "coffee", "caf", "hot", "morning", "wake",
            "energy", "drink", "fun"
        )
    ),
    Icon(
        "connect", keywords = listOf(
            "con", "struct", "tech", "net", "web", "tree",
            "group"
        )
    ),
    Icon(
        "dna", keywords = listOf(
            "dna", "life", "health", "gene", "science", "cell",
            "lab"
        )
    ),
    Icon(
        "doctor", keywords = listOf(
            "doc", "check", "medic", "sick", "ill", "gp",
            "examin", "hosp", "clinic", "prescript", "recipe"
        )
    ),
    Icon(
        "document", keywords = listOf(
            "document", "paper", "list", "note", "text",
            "message", "new", "magazine", "diary", "plan", "task", "organi", "bill", "tax", "fee",
            "acc", "receipt", "recipe", "prescript", "label", "order", "warranty", "insurance",
            "policy", "script", "content", "writ", "cop", "creat", "assign", "to-do", "todo",
            "contract", "library", "test", "exam"
        )
    ),
    Icon(
        "drink", keywords = listOf(
            "drink", "celeb", "party", "beer", "leisure", "spare",
            "free", "glass", "cup", "cheer", "bar", "club", "holiday", "out", "toast", "fun",
            "alcohol", "rest"
        )
    ),
    Icon(
        "farmacy", keywords = listOf(
            "farmacy", "pharm", "pill", "medic", "treat", "cure",
            "prescript", "heal", "recipe", "hosp", "clinic", "sick", "ill"
        )
    ),
    Icon(
        "fingerprint", keywords = listOf(
            "print", "finger", "authenticate", "secure", "policy",
            "sensor", "trace", "examine", "unlock", "identify", "touch", "safe"
        )
    ),
    Icon(
        "fishfood", keywords = listOf(
            "fish", "food", "sea", "ocean", "lake", "dam", "river",
            "hobby", "lunch", "din", "deliver", "rest", "fishfood"
        )
    ),
    Icon(
        "food2", keywords = listOf(
            "food", "deliver", "order", "pizza", "din", "party",
            "lunch", "brunch", "netflix", "fun", "rest"
        )
    ),
    Icon(
        "fooddrink", keywords = listOf(
            "food", "deliver", "order", "pizza", "din",
            "wine", "glass", "cup", "drink", "cheer", "fun", "toast", "bar", "club", "alcohol",
            "holiday", "celeb", "leisure", "spare", "free", "out", "rest"
        )
    ),
    Icon(
        "furniture", keywords = listOf(
            "furniture", "house", "cabinet", "home",
            "cupboard", "wardrobe", "dress", "room", "drawer", "stor", "organi"
        )
    ),
    Icon(
        "gambling", keywords = listOf(
            "gambl", "play", "casino", "game", "bet", "dice",
            "risk", "poker"
        )
    ),
    Icon(
        "game", keywords = listOf(
            "gam", "play", "console", "ps", "pc", "nintendo", "xbox",
            "hobby", "spare", "free", "leisure", "chill"
        )
    ),
    Icon(
        "gears", keywords = listOf(
            "gear", "mainten", "car", "mechan", "repair", "tech",
            "setting", "tune", "adjust"
        )
    ),
    Icon(
        "gift", keywords = listOf(
            "gift", "party", "celeb", "present", "donat", "birth",
            "bday", "b-day", "holiday"
        )
    ),
    Icon(
        "groceries", keywords = listOf(
            "grocer", "market", "supplies", "shop", "trade",
            "business", "franchise", "buy", "store", "order", "sell", "sale"
        )
    ),
    Icon(
        "hairdresser", keywords = listOf(
            "dress", "parlor", "salon", "saloon", "beaut",
            "hair", "style", "dry", "shave", "beard", "dye"
        )
    ),
    Icon(
        "health", keywords = listOf(
            "health", "medic", "doc", "hosp", "pill", "case",
            "pharmacy", "treat", "cure", "prescript", "heal", "recipe", "clinic", "sick", "ill",
            "farmacy"
        )
    ),
    Icon(
        "hike", keywords = listOf(
            "hike", "mountain", "walk", "top", "nature", "hobby",
            "forest", "wood", "tree", "environment"
        )
    ),
    Icon(
        "house", keywords = listOf(
            "hous", "mortgage", "home", "apartment", "build",
            "property", "chores", "estate", "accommod", "rent", "sale", "airbnb", "live", "place",
            "host"
        )
    ),
    Icon(
        "insurance", keywords = listOf(
            "insur", "bill", "protect", "fee", "secur",
            "policy", "safe"
        )
    ),
    Icon(
        "label", keywords = listOf(
            "label", "tag", "pric", "cost", "value", "rate",
            "charge", "worth", "market", "shop", "store", "buy", "ticket"
        )
    ),
    Icon(
        "leaf", keywords = listOf(
            "leaf", "plant", "lawn", "garden", "eco", "bio",
            "grass", "green", "vegetarian", "vegan", "natur", "flower", "tree", "spring", "autumn",
            "fall", "produc", "seed", "environment"
        )
    ),
    Icon("location", keywords = listOf("locat", "gps", "place", "map", "address", "live")),
    Icon(
        "makeup", keywords = listOf(
            "makeup", "parlor", "beaut", "salon", "saloon",
            "lip", "highlight", "paint", "cut", "knife", "diy", "glue", "sharp"
        )
    ),
    Icon(
        "music", keywords = listOf(
            "music", "headset", "headphone", "sound", "spotify",
            "note", "sing", "song", "hear", "fun", "party", "record", "direct", "radio", "produc"
        )
    ),
    Icon(
        "notice", keywords = listOf(
            "notice", "warning", "urgent", "attention", "require",
            "must", "important", "prior", "crucial", "danger"
        )
    ),
    Icon(
        "people", keywords = listOf(
            "people", "gather", "contact", "call", "person",
            "friend", "relative", "family", "communi", "speak", "talk", "group"
        )
    ),
    Icon(
        "plant", keywords = listOf(
            "plant", "tree", "garden", "yard", "lawn", "cactus", "leaf", "eco", "bio", "grass",
            "green", "vegetarian", "vegan", "natur", "flower", "tree", "produc", "seed",
            "environment", "sharp"
        )
    ),
    Icon(
        "programming", keywords = listOf(
            "program", "cod", "software", "logic",
            "engineer", "it", "tech", "librar", "dev"
        )
    ),
    Icon(
        "relationship", keywords = listOf(
            "relationship", "love", "heart", "partner",
            "couple", "health", "wife", "husband", "family", "insur", "boyfriend", "girlfriend",
            "fiance", "fiancee", "home"
        )
    ),
    Icon(
        "rocket", keywords = listOf(
            "rocket", "moon", "space", "yolo", "fly", "universe",
            "achieve", "determin", "brilliant", "success", "business", "idea", "top", "rise"
        )
    ),
    Icon("safe", keywords = listOf("safe", "secur", "protect", "lock", "guard", "insur")),
    Icon(
        "sail",
        keywords = listOf(
            "sail", "anchor", "cruise", "boat", "yacht", "travel", "vacation",
            "ship", "sea", "adventure", "ocean", "river", "fish", "div"
        )
    ),
    Icon(
        "server", keywords = listOf(
            "server", "modem", "communi", "pc", "message", "text", "chat", "bubble", "backend",
            "dev", "program", "machine", "software", "engineer", "cod", "logic", "it", "tech",
            "librar", "host"
        )
    ),
    Icon(
        "shopping2", keywords = listOf(
            "shop", "cart", "grocer", "market", "store", "order",
            "buy", "baby", "child", "mother", "father", "parent", "kid"
        )
    ),
    Icon(
        "shopping", keywords = listOf(
            "shop", "basket", "tote", "bag", "box",
            "drawer", "packet", "deliver", "buy", "stor", "order"
        )
    ),
    Icon("sports", keywords = listOf("sport", "ball", "soccer", "hobby", "play", "game")),
    Icon(
        "stats", keywords = listOf(
            "stat", "chart", "heart", "stock", "health", "hosp", "doc",
            "check", "medic", "sick", "ill", "gp", "examin", "clinic", "prescript", "recipe",
            "measure", "test"
        )
    ),
    Icon(
        "tools", keywords = listOf(
            "tool", "equipment", "hammer", "build", "produce",
            "wood", "progress"
        )
    ),
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
    Icon(
        "ic_vue_transport_bus", keywords = listOf(
            "microbus", "minibus", "minivan", "bus", "public transport",
            "travel", "journey", "utility vehicle"
        )
    ),
    Icon(
        "ic_vue_transport_airplane", keywords = listOf(
            "airliner", "air taxi", "aircraft", "airship", "jet",
            "trijet", "aerospace plane", "rocket plane", "bomber", "warplane",
            "biplane", "lightplane", "tilt-rotor", "triplane", "public transport"
        )
    ),
    Icon(
        "ic_vue_transport_train", keywords = listOf(
            "caravan", "track", "chain", "concatenation", "tail", "trail",
            "rail line", "locomotive", "railcar", "freight train", "railway",
            "cargo", "diesel locomotive", "passenger train", "public transport",
            "electric locomotive", "wagon train"
        )
    ),
    Icon(
        "ic_vue_transport_ship", keywords = listOf(
            "warship", "cargo ship", "ferry", "vessel", "sail", "watercraft", "transport",
            "cruise ship", "troopship", "passenger ship", "fleet", "yacht", "navy"
        )
    ),
    Icon(
        "ic_vue_transport_gas", keywords = listOf(
            "incompressible", "chemical weapon", "compressibility", "intermolecular forces",
            "covalent bond", "kerosine", "octanes", "liquification", "weather", "methane",
            "oxygen", "hydrogen", "gasoline", "petrol", "carbon dioxide", "neon", "plasma"
        )
    ),
    Icon(
        "ic_vue_transport_car", keywords = listOf(
            "motor vehicle", "wheel", "automobile", "van",
            "vehicle", "passenger", "internal combustion engine",
            "jeep", "cab", "sedan", "hatchback", "taxi",
            "air pollution", "climate change", "toyota"
        )
    ),
    Icon(
        "ic_vue_transport_car_wash", keywords = listOf(
            "automobile", "carwashing", "carwasher", "become dirty", "carwash",
            "rinse", "cleanse", "washcloth", "disinfect", "sanitation"
        )
    ),
)
// endregion

// region Type (Vue)
private fun vueType(): List<Icon> = listOf(
    Icon(
        "ic_vue_type_link2", keywords = listOf(
            "connection", "connect", "contact", "tie", "link2",
            "attach", "chain", "interconnect", "hyperlink"
        )
    ),
    Icon(
        "ic_vue_type_text", keywords = listOf(
            "book", "textbook", "passage", "page", "words", "language",
            "word", "paragraph", "chapter", "booklet", "dictionary",
            "reference", "read", "written", "phrase", "passages"
        )
    ),
    Icon(
        "ic_vue_type_paperclip", keywords = listOf("clip", "paper clip", "steel", "paper", "managing files")
    ),
    Icon(
        "ic_vue_type_textalign_left", keywords = listOf(
            "content", "document", "idea", "paragraph", "left",
            "quotation", "formating", "body", "context", "align left"
        )
    ),
    Icon(
        "ic_vue_type_translate", keywords = listOf(
            "paraphrase", "interpret", "understand", "language", "writing",
            "translator", "change", "meaning", "literal translation",
            "english language", "grammar", "dictionary"
        )
    ),
    Icon(
        "ic_vue_type_textalign_right", keywords = listOf(
            "content", "document", "paragraph", "quotation", "right",
            "formating", "body", "context", "align right"
        )
    ),
    Icon(
        "ic_vue_type_link", keywords = listOf(
            "connection", "connect", "contact", "tie", "link",
            "attach", "chain", "interconnect", "hyperlink"
        )
    ),
    Icon(
        "ic_vue_type_textalign_center",
        keywords = listOf("core", "centre", "central", "middle", "rivet", "midpoint", "align center")
    ),
    Icon(
        "ic_vue_type_textalign_justifycenter",
        keywords = listOf("distribute", "inline", "central", "middle", "align justifycenter")
    ),
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