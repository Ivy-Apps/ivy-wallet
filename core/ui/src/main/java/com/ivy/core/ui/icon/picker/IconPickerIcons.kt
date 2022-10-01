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
    Icon(
        "account", keywords = listOf(
            "accounts", "wallets", "pocket", "notecases", "money",
            "savings", "finances", "cash", "assets", "amounts", "balance"
        )
    ),
    Icon("category", keywords = listOf("category", "jars", "jams", "groups", "sections")),
    Icon(
        "cash", keywords = listOf(
            "cash", "money", "dollars", "usd", "cents", "coins",
            "balance"
        )
    ),
    Icon(
        "bank", keywords = listOf(
            "banks", "cards", "money", "accounts", "savings",
            "finances", "debit", "credit", "assets", "amounts", "balance"
        )
    ),
    Icon(
        "revolut", keywords = listOf(
            "revolut", "accounts", "banks", "cards", "money", "savings",
            "finances", "assets", "amounts", "balance"
        )
    ),
    Icon(
        "clothes2", keywords = listOf(
            "clothes", "appearance", "outfits", "blouses",
            "t-shirts", "apparels", "wardrobe", "shopping", "stores", "closets"
        )
    ),
    Icon(
        "clothes", keywords = listOf(
            "clothes", "wardrobe", "hangers", "appearance", "outfits", "blouses",
            "t-shirts", "apparels", "shopping", "stores", "closets", "storages"
        )
    ),
    Icon(
        "family", keywords = listOf(
            "family", "homes", "couple", "kids", "children",
            "love", "partners", "wifes", "husbands", "boyfriends", "girlfriends", "fiancee",
            "hearts", "relatives", "people"
        )
    ),
    Icon(
        "star", keywords = listOf(
            "stars", "favorites", "favourites", "tops", "reviews",
            "success", "achievements", "christmas", "xmas"
        )
    ),
    Icon(
        "education", keywords = listOf(
            "education", "school", "university", "study",
            "learn", "hats", "academy"
        )
    ),
    Icon(
        "fitness", keywords = listOf(
            "fitness", "gym", "workout", "train", "weights",
            "sport", "lift", "dumbbells", "workout", "work out"
        )
    ),
    Icon(
        "loan", keywords = listOf(
            "loans", "bills", "notes", "receipts", "recipes",
            "reports", "invoices", "fees", "taxes", "expenses"
        )
    ),
    Icon(
        "orderfood", keywords = listOf(
            "orders", "delivery", "boxes", "chinese", "foods",
            "dine", "dining", "lunches", "delivery", "eating"
        )
    ),
    Icon(
        "orderfood2", keywords = listOf(
            "orders", "delivery", "scooters", "takeaways",
            "glovo", "foodpanda"
        )
    ),
    Icon("pet", keywords = listOf("pets", "dogs", "paws", "cats")),
    Icon(
        "restaurant", keywords = listOf(
            "restaurants", "eating", "dinners", "foods", "dine", "dining",
            "lunch", "cutlery", "forks", "knifes", "meals", "diets", "nutritions"
        )
    ),
    Icon(
        "selfdevelopment", keywords = listOf(
            "learn", "improvements", "level up", "grow", "self development", "developing",
            "success", "achievements", "arrows", "person", "faith", "god", "learning", "top", "high"
        )
    ),
    Icon(
        "work",
        keywords = listOf(
            "works", "cases", "jobs", "occupations", "businesses", "professions", "hustles",
            "labours", "labors", "careers", "assignments", "company", "organizations"
        )
    ),
    Icon(
        "vehicle",
        keywords = listOf(
            "cars", "vehicles", "autos", "transports",
            "commutes", "gas", "taxis", "buses", "trams", "subways", "trolleys"
        )
    ),
    Icon(
        "atom", keywords = listOf(
            "atoms", "sciences", "labs", "universes", "physics", "fantastic"
        )
    ),
    Icon(
        "bills", keywords = listOf(
            "bills", "accounts", "taxes", "fees", "books",
            "reads", "notes", "diary", "organise", "plans", "library",
            "organize"
        )
    ),
    Icon(
        "birthday", keywords = listOf(
            "births", "cakes", "candles", "surprises", "bdays",
            "b-day"
        )
    ),
    Icon(
        "calculator", keywords = listOf(
            "calculates", "calculators", "calculations", "maths",
            "numbers", "finances"
        )
    ),
    Icon(
        "camera", keywords = listOf(
            "cameras", "videos", "edits", "photos", "movies", "records", "directing",
            "tickets", "studios", "shows", "tvs", "streams", "acts", "actions", "produces",
            "productions", "acting"
        )
    ),
    Icon(
        "chemistry", keywords = listOf(
            "chemistry", "cones", "sciences", "potions", "elixirs",
            "pharmacy", "labs"
        )
    ),
    Icon(
        "coffee", keywords = listOf(
            "coffees", "cafes", "hot", "mornings", "wake up",
            "energy", "drinks", "fun", "cups", "mugs", "glasses"
        )
    ),
    Icon(
        "connect", keywords = listOf(
            "connections", "structures", "technology", "nets", "webs", "trees",
            "groups", "logistics"
        )
    ),
    Icon(
        "dna", keywords = listOf(
            "dnas", "lifes", "health", "genes", "sciences", "cells",
            "labs"
        )
    ),
    Icon(
        "doctor", keywords = listOf(
            "doctors", "checks", "medics", "sick", "ill", "gp",
            "examinations", "hospitals", "clinics", "prescripts", "recipes"
        )
    ),
    Icon(
        "document", keywords = listOf(
            "documents", "papers", "lists", "notes", "texts",
            "messages", "news", "magazines", "diary", "plans", "tasks", "organise", "organize",
            "bills", "taxes", "fees", "accounts", "reports", "receipts", "recipes", "prescripts",
            "labels", "orders", "warranty", "insurances", "policy", "scripts", "content", "write",
            "copy", "writing", "create", "assignments", "to-do", "todos", "contracts", "library",
            "tests", "exams", "portfolios", "cvs"
        )
    ),
    Icon(
        "drink", keywords = listOf(
            "drinks", "celebrates", "celebrating", "party", "beers", "leisure", "spare",
            "free", "glasses", "cups", "cheers", "bars", "clubs", "holidays", "out", "toasts",
            "fun", "alcohols", "rest"
        )
    ),
    Icon(
        "farmacy", keywords = listOf(
            "farmacy", "pharmacy", "pills", "medics", "treats", "cures",
            "prescripts", "healing", "recipes", "hospitals", "clinics", "sick", "ill"
        )
    ),
    Icon(
        "fingerprint", keywords = listOf(
            "prints", "fingers", "authenticates", "secure", "policy",
            "sensors", "traces", "examines", "unlocks", "identify", "touches", "safe"
        )
    ),
    Icon(
        "fishfood", keywords = listOf(
            "fishes", "foods", "sea", "oceans", "lakes", "dams", "rivers",
            "hobby", "lunches", "dine", "dining", "dinner", "delivery", "rest", "fishfoods"
        )
    ),
    Icon(
        "food2", keywords = listOf(
            "foods", "delivery", "orders", "pizzas", "dine", "dining", "party",
            "lunches", "brunches", "netflix", "fun", "rest"
        )
    ),
    Icon(
        "fooddrink", keywords = listOf(
            "foods", "delivers", "orders", "pizzas", "dine", "dining",
            "wines", "glasses", "cups", "drinks", "cheers", "fun", "toasts", "bars", "clubs",
            "alcohols", "holidays", "celebrates", "leisure", "spare", "free", "out", "rest",
            "celebrations"
        )
    ),
    Icon(
        "furniture", keywords = listOf(
            "furniture", "houses", "cabinets", "homes",
            "cupboards", "wardrobes", "dressing", "rooms", "drawers", "stores", "storages",
            "organize", "organise", "closets"
        )
    ),
    Icon(
        "gambling", keywords = listOf(
            "gambling", "plays", "casinos", "games", "bets", "dices",
            "risks", "poker"
        )
    ),
    Icon(
        "game", keywords = listOf(
            "games", "gaming", "plays", "consoles", "ps", "pc", "nintendos", "xboxes",
            "hobby", "spare", "free", "leisure", "chill", "computers"
        )
    ),
    Icon(
        "gears", keywords = listOf(
            "gears", "maintenance", "maintaining", "cars", "mechanisms", "repairs", "technology",
            "settings", "tunes", "adjusts"
        )
    ),
    Icon(
        "gift", keywords = listOf(
            "gifts", "party", "celebrate", "celebrations", "presents", "donations", "donates",
            "births", "bdays", "b-day", "holidays"
        )
    ),
    Icon(
        "groceries", keywords = listOf(
            "groceries", "grocery", "markets", "supplies", "shops", "trade", "trading",
            "businesses", "franchises", "buy", "stores", "orders", "sells", "sales"
        )
    ),
    Icon(
        "hairdresser", keywords = listOf(
            "hairdressers", "parlor", "parlour", "salon", "saloon", "beauty", "beautify",
            "hairstyles", "haircuts", "dry", "shave", "beards", "dye", "hairdressings"
        )
    ),
    Icon(
        "health", keywords = listOf(
            "health", "medics", "doctors", "hospitals", "pills", "cases",
            "pharmacy", "treats", "cures", "prescripts", "healing", "recipes", "clinics", "sick",
            "ill", "farmacy"
        )
    ),
    Icon(
        "hike", keywords = listOf(
            "hike", "hikings", "mountains", "walks", "tops", "nature", "hobby",
            "forests", "woods", "trees", "environments", "sports"
        )
    ),
    Icon(
        "house", keywords = listOf(
            "houses", "mortgages", "homes", "apartments", "buildings",
            "property", "chores", "estates", "accommodations", "rents", "sales", "airbnb", "lives",
            "places", "hosts", "living"
        )
    ),
    Icon(
        "insurance", keywords = listOf(
            "insurances", "bills", "protections", "fees", "security", "secure",
            "policy", "safety"
        )
    ),
    Icon(
        "label", keywords = listOf(
            "labels", "tags", "prices", "costs", "value", "rates",
            "charges", "worth", "markets", "shops", "stores", "buy", "tickets"
        )
    ),
    Icon(
        "leaf", keywords = listOf(
            "leaf", "leaves", "plants", "lawns", "gardens", "eco", "bio",
            "grasses", "green", "vegetarians", "vegans", "nature", "naturals", "flowers", "trees",
            "spring", "autumn", "fall", "productions", "produce", "seeds", "environments"
        )
    ),
    Icon(
        "location", keywords = listOf(
            "locations", "gps", "places", "maps", "address",
            "live", "delivery"
        )
    ),
    Icon(
        "makeup", keywords = listOf(
            "makeups", "make up", "parlor", "parlour", "beauty", "beautify", "salon", "saloon",
            "lipstick", "lip balm", "highlights", "paints", "cuts", "knifes", "diy", "glues",
            "sharps", "lip gloss", "lipgloss", "makeup artists"
        )
    ),
    Icon(
        "music", keywords = listOf(
            "music", "headsets", "headphones", "sounds", "spotify",
            "notes", "singers", "songs", "hear", "fun", "party", "records", "directing", "radios",
            "produce", "production", "hits"
        )
    ),
    Icon(
        "notice", keywords = listOf(
            "notice", "warnings", "urgents", "attention", "requirements",
            "musts", "important", "prior", "crucial", "dangerous"
        )
    ),
    Icon(
        "people", keywords = listOf(
            "peoples", "gathering", "contacts", "calls", "person",
            "friends", "relatives", "family", "communicate", "communications", "speakings",
            "talkings", "groups"
        )
    ),
    Icon(
        "plant", keywords = listOf(
            "plants", "trees", "gardens", "yards", "lawns", "cactus", "cacti", "leaf", "leaves",
            "eco", "bio", "grasses", "green", "vegetarians", "vegan", "nature", "naturals",
            "flowers", "trees", "produce", "productions", "seeds", "environments", "sharps"
        )
    ),
    Icon(
        "programming", keywords = listOf(
            "programming", "programmer", "code", "coding", "softwares", "logician",
            "engineers", "engineering", "it", "technology", "library", "developers", "programs",
            "development", "developing"
        )
    ),
    Icon(
        "relationship", keywords = listOf(
            "relationships", "love", "hearts", "partners",
            "couples", "health", "wifes", "husbands", "family", "insurances", "boyfriend",
            "girlfriends", "fiancee", "homes"
        )
    ),
    Icon(
        "rocket", keywords = listOf(
            "rockets", "moon", "spaces", "yolo", "fly", "universes",
            "achievements", "determination", "brilliant", "success", "businesses",
            "ideas", "tops", "rise", "high"
        )
    ),
    Icon(
        "safe", keywords = listOf(
            "safety", "secure", "security", "protections", "locks", "guards",
            "insurances", "measures"
        )
    ),
    Icon(
        "sail",
        keywords = listOf(
            "sailing", "sails", "anchors", "cruises", "boats", "yachts", "travels", "travelling",
            "vacations", "ships", "sea", "adventures", "oceans", "rivers", "fishes", "dive",
            "pisces", "diving"
        )
    ),
    Icon(
        "server", keywords = listOf(
            "servers", "modems", "communicate", "communications", "pc", "computers", "messages",
            "texts", "chats", "bubbles", "backends", "developers", "programs", "machines",
            "softwares", "engineers", "code", "logicians", "it", "technology", "library", "hosts",
            "hosting", "engineering", "coding", "programming", "developing", "development"
        )
    ),
    Icon(
        "shopping2", keywords = listOf(
            "shops", "shopping", "carts", "grocery", "groceries", "markets", "stores", "orders",
            "buy", "baby", "children", "motherhood", "fatherhood", "parenthood", "kids"
        )
    ),
    Icon(
        "shopping", keywords = listOf(
            "shopping", "shops", "baskets", "totes", "bags", "boxes",
            "drawers", "packets", "delivery", "buy", "stores", "orders"
        )
    ),
    Icon(
        "sports", keywords = listOf(
            "sports", "balls", "soccers", "hobby", "plays",
            "games"
        )
    ),
    Icon(
        "stats", keywords = listOf(
            "stats", "chart", "hearts", "stocks", "health", "hospitals", "doctors",
            "checks", "medics", "sick", "ill", "gp", "examinations", "clinics", "prescripts",
            "recipes", "measurements", "tests", "measures", "cardiograms"
        )
    ),
    Icon(
        "tools", keywords = listOf(
            "tools", "equipments", "hammers", "buildings", "producing", "produce",
            "wood", "progressings", "builders", "repairments", "repairs", "wip"
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