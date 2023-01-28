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
            "success", "achievements", "christmas", "xmas", "premium"
        )
    ),
    Icon(
        "education", keywords = listOf(
            "education", "school", "university", "study", "learning", "hats", "academy",
            "high school"
        )
    ),
    Icon(
        "fitness", keywords = listOf(
            "fitness", "gym", "workouts", "train", "weights", "sports", "lifting", "dumbbells",
            "work out"
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
            "cars", "vehicles", "autos", "transports", "commutes", "gas", "taxis"
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
            "birthdays", "cakes", "candles", "surprises", "bdays",
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
            "cameras", "videos", "editing", "photos", "movies", "records", "directing", "studios",
            "shows", "tvs", "streams", "acts", "actions", "produces", "productions", "acting",
            "films", "vlogs", "vlogging"
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
            "coffees", "cafes", "hot", "mornings", "wake up", "warm",
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
            "documents", "papers", "lists", "notes", "texts", "agenda",
            "messages", "news", "magazines", "diary", "plans", "tasks", "organise", "organize",
            "bills", "taxes", "fees", "accounts", "reports", "receipts", "recipes", "prescripts",
            "labels", "orders", "warranty", "insurances", "policy", "scripts", "content", "write",
            "copy", "writing", "create", "assignments", "to-do", "todos", "contracts", "library",
            "tests", "exams", "portfolios", "cvs", "articles"
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
            "hobby", "spare", "free", "leisure", "chill", "computers", "computers"
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
            "birthdays", "bdays", "b-day", "holidays"
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
            "places", "hosts", "living", "remotely"
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
            "live", "delivery", "geography"
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
            "music", "headsets", "headphones", "sounds", "spotify", "singers", "songs", "hear",
            "fun", "party", "records", "directing", "radios", "produce", "production", "hits",
            "tunes", "performing", "recordings"
        )
    ),
    Icon(
        "notice", keywords = listOf(
            "notice", "warnings", "urgents", "attention", "requirements",
            "musts", "important", "priority", "crucial", "dangerous"
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
            "programming", "programmer", "coder", "coding", "softwares", "logician",
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
            "softwares", "engineers", "coder", "logicians", "it", "technology", "library", "hosts",
            "hosting", "engineering", "coding", "programming", "developing", "development",
            "chatting"
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
    Icon(
        "transport", keywords = listOf(
            "transports", "cars", "buses", "commutes",
            "vehicles", "autos", "transports", "gas", "taxis", "buses", "trams", "subways",
            "trolleys", "trains", "metros", "underground", "gas"
        )
    ),
    Icon(
        "travel", keywords = listOf(
            "traveling", "travels", "trips", "airplanes", "abroad",
            "explore", "vacations", "exploring", "experiences", "flying", "flights"
        )
    ),
    Icon(
        "trees", keywords = listOf(
            "trees", "gardens", "yards", "lawns", "woods",
            "christmas", "xmas", "forests"
        )
    ),
    Icon(
        "zeus", keywords = listOf(
            "zeus", "lightning", "rains", "emergency", "urgents", "sparks",
            "storms", "flash", "thunders", "important", "priority", "thor"
        )
    ),
    Icon(
        "calendar", keywords = listOf(
            "calendars", "plans", "schedules", "memos",
            "planners", "notes", "tasks", "priority", "agenda"
        )
    ),
    Icon(
        "crown", keywords = listOf(
            "crowns", "luxury", "vip", "top", "queens", "kings",
            "the best", "luxe"
        )
    ),
    Icon(
        "diamond", keywords = listOf(
            "diamonds", "luxury", "luxe", "vip", "weddings",
            "rings", "tops", "expensive", "glamorous", "shine", "shining", "sparkling", "brilliant",
            "glory", "sparkle", "premium"
        )
    ),
    Icon(
        "palette", keywords = listOf(
            "palettes", "painters", "artists", "colors", "colours",
            "decorate", "decorations", "dyeing", "painting", "drawing", "pictures", "crayons",
            "brushes"
        )
    ),
)
// endregion

// region Brands (Vue)
private fun vueBrands(): List<Icon> = listOf(
    Icon("ic_vue_brands_triangle", keywords = listOf("triangles")),
    Icon(
        "ic_vue_brands_trello", keywords = listOf(
            "trello", "managements", "projects", "pm", "tasks"
        )
    ),
    Icon(
        "ic_vue_brands_html5", keywords = listOf(
            "html5", "html", "coder", "startup", "projects", "softwares",
            "development", "programming", "programmer", "programs", "developers", "websites"
        )
    ),
    Icon(
        "ic_vue_brands_spotify", keywords = listOf(
            "spotify", "music", "songs", "listening", "streaming", "streams"
        )
    ),
    Icon(
        "ic_vue_brands_bootsrap", keywords = listOf(
            "bootsrap", "startup", "coder",
            "coding", "open-source", "open source", "softwares", "projects",
            "development", "programming", "programmer", "programs", "developers", "websites"
        )
    ),
    Icon(
        "ic_vue_brands_dribbble",
        keywords = listOf(
            "dribbble", "dribble", "designers", "designs"
        )
    ),
    Icon(
        "ic_vue_brands_google_play", keywords = listOf(
            "google play", "apps", "android", "applications", "downloading"
        )
    ),
    Icon(
        "ic_vue_brands_dropbox", keywords = listOf(
            "dropbox", "clouds", "data", "save", "sync"
        )
    ),
    Icon(
        "ic_vue_brands_js", keywords = listOf(
            "js", "javascript", "coder", "coding", "websites",
            "softwares", "projects", "development", "programming", "programmer", "programs",
            "developers"
        )
    ),
    Icon(
        "ic_vue_brands_drive", keywords = listOf(
            "google drive", "save", "files", "clouds"
        )
    ),
    Icon("ic_vue_brands_paypal", keywords = listOf("paypal", "transfer money online")),
    Icon("ic_vue_brands_be", keywords = listOf("be")),
    Icon("ic_vue_brands_figma", keywords = listOf("figma", "designers", "designs")),
    Icon(
        "ic_vue_brands_messenger",
        keywords = listOf(
            "messenger", "messages", "chatting", "chats", "talking", "communication", "communicate",
            "sending", "texting"
        )
    ),
    Icon("ic_vue_brands_facebook", keywords = listOf("facebook", "fb", "social media")),
    Icon("ic_vue_brands_framer", keywords = listOf("framer", "web builder", "websites")),
    Icon(
        "ic_vue_brands_whatsapp", keywords = listOf(
            "whatsapp", "communication", "communicate", "messages", "chatting", "chats", "talking",
            "sending", "texting"
        )
    ),
    Icon(
        "ic_vue_brands_html3", keywords = listOf(
            "html3", "html", "web programming",
            "coder", "coding", "websites", "softwares", "projects", "development", "programming",
            "programmer", "programs", "developers"
        )
    ),
    Icon(
        "ic_vue_brands_zoom", keywords = listOf(
            "zoom", "communication", "communicate", "meetings"
        )
    ),
    Icon("ic_vue_brands_ok", keywords = listOf("ok")),
    Icon(
        "ic_vue_brands_twitch", keywords = listOf(
            "twitch", "streaming", "gaming", "entertainment", "sports", "fun"
        )
    ),
    Icon(
        "ic_vue_brands_youtube", keywords = listOf(
            "youtube", "music", "learning", "videos",
            "streaming", "vlogs", "vlogging", "hits", "fun", "chill", "subscriptions"
        )
    ),
    Icon(
        "ic_vue_brands_apple", keywords = listOf(
            "apple", "iphone", "ipad", "macbook",
            "iwatch", "laptops", "technology", "ios"
        )
    ),
    Icon(
        "ic_vue_brands_android", keywords = listOf(
            "android", "mobile", "apps", "applications", "coder", "coding", "softwares",
            "projects", "development", "programming", "programmer", "programs", "developers"
        )
    ),
    Icon(
        "ic_vue_brands_slack", keywords = listOf(
            "slack", "working", "chatting", "code",
            "developers", "communication", "communicate"
        )
    ),
    Icon("ic_vue_brands_vuesax", keywords = listOf("vuesax", "webs", "code", "developers")),
    Icon(
        "ic_vue_brands_blogger", keywords = listOf(
            "bloggers", "blogging", "hosting",
            "softwares", "publishing", "writing", "content", "writers"
        )
    ),
    Icon(
        "ic_vue_brands_photoshop", keywords = listOf(
            "photoshop", "ps", "designers", "photos", "technology", "software", "editing", "pics",
            "pictures", "images", "photography"
        )
    ),
    Icon(
        "ic_vue_brands_python", keywords = listOf(
            "python", "ai", "coder", "coding", "softwares", "projects",
            "development", "programming", "programmer", "programs", "developers"
        )
    ),
    Icon(
        "ic_vue_brands_google", keywords = listOf(
            "google", "browsers", "browse", "searching", "internet", "software"
        )
    ),
    Icon(
        "ic_vue_brands_xd", keywords = listOf(
            "xd", "adobe", "designers", "creative", "create", "software"
        )
    ),
    Icon(
        "ic_vue_brands_illustrator", keywords = listOf(
            "illustrator", "adobe", "illustrating",
            "illustrations", "designers", "creative", "create", "software"
        )
    ),
    Icon(
        "ic_vue_brands_xiaomi", keywords = listOf(
            "xiaomi", "phones", "technology", "android"
        )
    ),
    Icon("ic_vue_brands_windows", keywords = listOf("windows", "operational system", "os")),
    Icon(
        "ic_vue_brands_snapchat", keywords = listOf(
            "snapchats", "fun", "snaps", "social media"
        )
    ),
    Icon(
        "ic_vue_brands_ui8", keywords = listOf(
            "ui8", "ui", "ux", "designers", "creative", "create", "creators"
        )
    ),
)
// endregion

// region Building (Vue)
private fun vueBuilding(): List<Icon> = listOf(
    Icon(
        "ic_vue_building_building1", keywords = listOf(
            "buildings", "flats", "blocks", "homes", "offices", "company"
        )
    ),
    Icon(
        "ic_vue_building_buildings", keywords = listOf(
            "buildings", "flats", "blocks", "homes", "offices", "company"
        )
    ),
    Icon(
        "ic_vue_building_hospital", keywords = listOf(
            "hospital", "buildings", "health", "church", "hospis"
        )
    ),
    Icon("ic_vue_building_building", keywords = listOf("buildings", "shops", "stores")),
    Icon(
        "ic_vue_building_bank", keywords = listOf(
            "banks", "banking", "money", "finances", "fed", "institutions"
        )
    ),
    Icon(
        "ic_vue_building_house", keywords = listOf(
            "buildings", "houses", "homes", "couples", "love", "live", "remotely"
        )
    ),
    Icon(
        "ic_vue_building_courthouse", keywords = listOf(
            "courthouses", "lawyers", "legal", "businesses", "institutions", "judges"
        )
    ),
)
// endregion

// region Chart (Vue)
private fun vueChart(): List<Icon> = listOf(
    Icon(
        "ic_vue_chart_diagram", keywords = listOf(
            "diagrams", "businesses", "stocks", "investments", "crypto", "portfolios", "graphs",
            "charts"
        )
    ),
    Icon(
        "ic_vue_chart_graph", keywords = listOf(
            "diagrams", "businesses", "stocks", "investments", "crypto", "portfolios", "graphs",
            "charts"
        )
    ),
    Icon(
        "ic_vue_chart_status_up", keywords = listOf(
            "diagrams", "businesses", "stocks", "investments", "crypto", "portfolios", "graphs",
            "charts"
        )
    ),
    Icon(
        "ic_vue_chart_chart", keywords = listOf(
            "diagrams", "businesses", "stocks", "investments", "crypto", "portfolios", "graphs",
            "charts"
        )
    ),
    Icon(
        "ic_vue_chart_trend_up", keywords = listOf(
            "diagrams", "businesses", "stocks", "investments", "crypto", "portfolios", "graphs",
            "charts"
        )
    ),
)
// endregion

// region Crypto (Vue)
private fun vueCrypto(): List<Icon> = listOf(
    Icon("ic_vue_crypto_dent", keywords = listOf("crypto", "blockchain", "currency", "dent")),
    Icon("ic_vue_crypto_icon", keywords = listOf("crypto", "blockchain", "currency")),
    Icon(
        "ic_vue_crypto_decred", keywords = listOf(
            "crypto", "blockchain", "currency", "decred"
        )
    ),
    Icon(
        "ic_vue_crypto_ocean_protocol", keywords = listOf(
            "crypto", "blockchain", "currency", "ocean protocol"
        )
    ),
    Icon(
        "ic_vue_crypto_hedera_hashgraph", keywords = listOf(
            "crypto", "blockchain", "currency", "hedera hashgraph"
        )
    ),
    Icon(
        "ic_vue_crypto_binance_usd", keywords = listOf(
            "crypto", "blockchain", "currency",
            "binance", "busd"
        )
    ),
    Icon(
        "ic_vue_crypto_maker", keywords = listOf(
            "crypto", "blockchain", "currency", "maker", "mkr"
        )
    ),
    Icon(
        "ic_vue_crypto_xrp", keywords = listOf(
            "crypto", "blockchain", "currency", "xrp", "ripple"
        )
    ),
    Icon(
        "ic_vue_crypto_harmony", keywords = listOf(
            "crypto", "blockchain", "currency", "harmony", "one"
        )
    ),
    Icon(
        "ic_vue_crypto_theta", keywords = listOf(
            "crypto", "blockchain", "currency", "theta"
        )
    ),
    Icon(
        "ic_vue_crypto_celsius_", keywords = listOf(
            "crypto", "blockchain", "currency", "celsius"
        )
    ),
    Icon(
        "ic_vue_crypto_vibe", keywords = listOf(
            "crypto", "blockchain", "currency", "vibe"
        )
    ),
    Icon(
        "ic_vue_crypto_augur", keywords = listOf(
            "crypto", "blockchain", "currency", "augur"
        )
    ),
    Icon(
        "ic_vue_crypto_graph", keywords = listOf(
            "crypto", "blockchain", "currency", "hedera", "graph"
        )
    ),
    Icon(
        "ic_vue_crypto_monero", keywords = listOf(
            "crypto", "blockchain", "currency", "monero", "mnr"
        )
    ),
    Icon(
        "ic_vue_crypto_aave", keywords = listOf(
            "crypto", "blockchain", "currency", "aave"
        )
    ),
    Icon(
        "ic_vue_crypto_dai", keywords = listOf(
            "crypto", "blockchain", "currency", "dai"
        )
    ),
    Icon(
        "ic_vue_crypto_litecoin", keywords = listOf(
            "crypto", "blockchain", "currency", "litecoin"
        )
    ),
    Icon(
        "ic_vue_crypto_tether", keywords = listOf(
            "crypto", "blockchain", "currency", "tether", "ust"
        )
    ),
    Icon(
        "ic_vue_crypto_thorchain", keywords = listOf(
            "crypto", "blockchain", "currency", "thorchain"
        )
    ),
    Icon(
        "ic_vue_crypto_nexo", keywords = listOf(
            "crypto", "blockchain", "currency", "nexo"
        )
    ),
    Icon(
        "ic_vue_crypto_chainlink", keywords = listOf(
            "crypto", "blockchain", "currency", "chainlink"
        )
    ),
    Icon(
        "ic_vue_crypto_ethereum_classic", keywords = listOf(
            "crypto", "blockchain", "currency", "ethereum"
        )
    ),
    Icon(
        "ic_vue_crypto_usd_coin", keywords = listOf(
            "crypto", "blockchain", "currency", "usd"
        )
    ),
    Icon(
        "ic_vue_crypto_nem", keywords = listOf(
            "crypto", "blockchain", "currency", "nem"
        )
    ),
    Icon(
        "ic_vue_crypto_eos", keywords = listOf(
            "crypto", "blockchain", "currency", "eos"
        )
    ),
    Icon(
        "ic_vue_crypto_emercoin", keywords = listOf(
            "crypto", "blockchain", "currency", "emercoin"
        )
    ),
    Icon(
        "ic_vue_crypto_dash", keywords = listOf(
            "crypto", "blockchain", "currency", "dash"
        )
    ),
    Icon(
        "ic_vue_crypto_ontology", keywords = listOf(
            "crypto", "blockchain", "currency", "ontology"
        )
    ),
    Icon(
        "ic_vue_crypto_ftx_token", keywords = listOf(
            "crypto", "blockchain", "currency", "ftx", "tokens"
        )
    ),
    Icon(
        "ic_vue_crypto_educare", keywords = listOf(
            "crypto", "blockchain", "currency", "educare"
        )
    ),
    Icon(
        "ic_vue_crypto_solana", keywords = listOf(
            "crypto", "blockchain", "currency", "solana"
        )
    ),
    Icon(
        "ic_vue_crypto_ethereum", keywords = listOf(
            "crypto", "blockchain", "currency", "ethereum"
        )
    ),
    Icon(
        "ic_vue_crypto_velas", keywords = listOf(
            "crypto", "blockchain", "currency", "velas"
        )
    ),
    Icon(
        "ic_vue_crypto_hex", keywords = listOf(
            "crypto", "blockchain", "currency", "hex"
        )
    ),
    Icon(
        "ic_vue_crypto_polkadot", keywords = listOf(
            "crypto", "blockchain", "currency", "polkadot"
        )
    ),
    Icon(
        "ic_vue_crypto_huobi_token", keywords = listOf(
            "crypto", "blockchain", "currency", "huobi token"
        )
    ),
    Icon(
        "ic_vue_crypto_polyswarm", keywords = listOf(
            "crypto", "blockchain", "currency", "polyswarm"
        )
    ),
    Icon(
        "ic_vue_crypto_ankr", keywords = listOf(
            "crypto", "blockchain", "currency", "ankr"
        )
    ),
    Icon(
        "ic_vue_crypto_enjin_coin", keywords = listOf(
            "crypto", "blockchain", "currency", "enjin coin"
        )
    ),
    Icon(
        "ic_vue_crypto_polygon", keywords = listOf(
            "crypto", "blockchain", "currency", "polygon"
        )
    ),
    Icon(
        "ic_vue_crypto_wing", keywords = listOf(
            "crypto", "blockchain", "currency", "wing"
        )
    ),
    Icon(
        "ic_vue_crypto_nebulas", keywords = listOf(
            "crypto", "blockchain", "currency", "nebulas"
        )
    ),
    Icon(
        "ic_vue_crypto_iost", keywords = listOf(
            "crypto", "blockchain", "currency", "iost"
        )
    ),
    Icon(
        "ic_vue_crypto_binance_coin", keywords = listOf(
            "crypto", "blockchain", "currency", "binance", "coins", "bnb"
        )
    ),
    Icon(
        "ic_vue_crypto_kyber_network", keywords = listOf(
            "crypto", "blockchain", "currency", "kyber network"
        )
    ),
    Icon(
        "ic_vue_crypto_trontron", keywords = listOf(
            "crypto", "blockchain", "currency", "tron"
        )
    ),
    Icon(
        "ic_vue_crypto_stellar", keywords = listOf(
            "crypto", "blockchain", "currency", "stellar"
        )
    ),
    Icon(
        "ic_vue_crypto_avalanche", keywords = listOf(
            "crypto", "blockchain", "currency", "avalanche", "avl"
        )
    ),
    Icon(
        "ic_vue_crypto_wanchain", keywords = listOf(
            "crypto", "blockchain", "currency", "wanchain", "wan chain"
        )
    ),
    Icon(
        "ic_vue_crypto_cardano", keywords = listOf(
            "crypto", "blockchain", "currency", "ada", "cardano"
        )
    ),
    Icon(
        "ic_vue_crypto_okb", keywords = listOf(
            "crypto", "blockchain", "currency", "okb"
        )
    ),
    Icon(
        "ic_vue_crypto_stacks", keywords = listOf(
            "crypto", "blockchain", "currency", "stacks"
        )
    ),
    Icon(
        "ic_vue_crypto_siacoin", keywords = listOf(
            "crypto", "blockchain", "currency", "sia coin", "siacoin"
        )
    ),
    Icon(
        "ic_vue_crypto_autonio", keywords = listOf(
            "crypto", "blockchain", "currency", "autonio"
        )
    ),
    Icon(
        "ic_vue_crypto_civic", keywords = listOf(
            "crypto", "blockchain", "currency", "civic"
        )
    ),
    Icon(
        "ic_vue_crypto_zel", keywords = listOf(
            "crypto", "blockchain", "currency", "zel"
        )
    ),
    Icon(
        "ic_vue_crypto_quant", keywords = listOf(
            "crypto", "blockchain", "currency", "quant"
        )
    ),
    Icon(
        "ic_vue_crypto_tenx", keywords = listOf(
            "crypto", "blockchain", "currency", "tenx"
        )
    ),
    Icon(
        "ic_vue_crypto_celo", keywords = listOf(
            "crypto", "blockchain", "currency", "celo"
        )
    ),
    Icon(
        "ic_vue_crypto_bitcoin", keywords = listOf(
            "crypto", "blockchain", "currency", "btc", "bitcoins"
        )
    ),
)
// endregion

// region Delivery (Vue)
private fun vueDelivery(): List<Icon> = listOf(
    Icon(
        "ic_vue_delivery_package", keywords = listOf(
            "delivery", "delivering", "packages", "orders", "give", "take", "buy", "sell", "sales",
            "packets", "boxes", "receiving", "receive", "replacement", "exchange", "swap", "gifts",
            "purchases", "christmas", "xmas"
        )
    ),
    Icon(
        "ic_vue_delivery_receive", keywords = listOf(
            "delivery", "delivering", "packages", "orders", "give", "take", "buy", "sell", "sales",
            "receive", "receiving", "packets", "boxes", "replacement", "exchange", "swap", "gifts",
            "purchases", "christmas", "xmas"
        )
    ),
    Icon(
        "ic_vue_delivery_box1", keywords = listOf(
            "delivery", "delivering", "packages", "orders", "give", "take", "buy", "sell", "sales",
            "receive", "receiving", "packets", "boxes", "gifts", "purchases", "christmas", "xmas"
        )
    ),
    Icon(
        "ic_vue_delivery_box", keywords = listOf(
            "boxes", "cubes", "delivery", "delivering", "orders", "purchases"
        )
    ),
    Icon(
        "ic_vue_delivery_truck", keywords = listOf(
            "truck",
            "delivery",
            "delivering",
            "packages",
            "orders",
            "give",
            "take",
            "buy",
            "sell",
            "sales",
            "packets",
            "cars",
            "vehicles",
            "receiving",
            "receive",
            "replacement",
            "exchange",
            "swap",
            "gifts",
            "purchases",
            "dhl",
            "amazon"
        )
    ),
)
// endregion

// region Design (Vue)
private fun vueDesign(): List<Icon> = listOf(
    Icon(
        "ic_vue_design_bezier", keywords = listOf(
            "bezier", "curves", "graph", "designers", "css", "technology", "tools", "drawings",
            "sketches"
        )
    ),
    Icon(
        "ic_vue_design_brush", keywords = listOf(
            "brushes", "designers", "paintings", "pictures", "art", "decorations", "decorate",
            "decorating"
        )
    ),
    Icon(
        "ic_vue_design_color_swatch", keywords = listOf(
            "swatches", "designers", "fashion", "interiors", "art", "decorations", "decorate",
            "decorating"
        )
    ),
    Icon(
        "ic_vue_design_scissors", keywords = listOf(
            "scissors", "designers", "cutting", "tools", "diy"
        )
    ),
    Icon(
        "ic_vue_design_magicpen", keywords = listOf(
            "magic pen", "pens", "magical", "colorful", "colourful", "fairy", "decorations",
            "decorate", "decorating", "notes"
        )
    ),
    Icon(
        "ic_vue_design_roller", keywords = listOf(
            "rollers", "painters", "designers", "repairs", "repairments", "decorate", "decorating",
            "decorations"
        )
    ),
    Icon(
        "ic_vue_design_tool_pen", keywords = listOf(
            "bezier", "curves", "graph", "designers", "css", "technology", "pens", "tools",
            "drawings", "paintings", "sketches", "notes"
        )
    ),
)
// endregion

// region Dev (Vue)
private fun vueDev(): List<Icon> = listOf(
    Icon(
        "ic_vue_dev_code", keywords = listOf(
            "programming", "programmer", "coder", "coding", "software", "logician", "engineers",
            "engineering", "it", "technology", "developers", "programs", "development", "developing"
        )
    ),
    Icon(
        "ic_vue_dev_hierarchy", keywords = listOf(
            "programming", "programmer", "coder", "coding", "softwares", "logician", "engineers",
            "engineering", "it", "technology", "hierarchy", "developers", "programs", "development",
            "developing", "structures", "relations"
        )
    ),
    Icon(
        "ic_vue_dev_relation", keywords = listOf(
            "programming", "programmer", "coder", "coding", "softwares", "logician", "engineers",
            "engineering", "it", "technology", "hierarchy", "developers", "programs", "development",
            "developing", "structures", "relations"
        )
    ),
    Icon(
        "ic_vue_dev_arrow", keywords = listOf(
            "programming", "programmer", "coder", "coding", "softwares", "logician", "engineers",
            "engineering", "it", "technology", "hierarchy", "developers", "programs", "development",
            "developing", "structures", "relations", "arrows"
        )
    ),
    Icon(
        "ic_vue_dev_data", keywords = listOf(
            "programming", "programmer", "coder", "coding", "softwares", "logician", "engineers",
            "engineering", "it", "technology", "hierarchy", "developers", "programs", "development",
            "developing", "structures", "relations", "data"
        )
    ),
    Icon(
        "ic_vue_dev_hashtag", keywords = listOf(
            "programming", "programmer", "coder", "coding", "softwares", "logician", "engineers",
            "engineering", "it", "technology", "hierarchy", "developers", "programs", "development",
            "developing", "structures", "relations", "social media", "hashtag"
        )
    ),
)
// endregion

// region Education (Vue)
private fun vueEducation(): List<Icon> = listOf(
    Icon(
        "ic_vue_edu_planer", keywords = listOf(
            "planners",
            "logbooks",
            "calendars",
            "organizers",
            "organisers",
            "appointments",
            "diary",
            "notes",
            "notebooks",
            "schedules",
            "agenda"
        )
    ),
    Icon(
        "ic_vue_edu_briefcase", keywords = listOf(
            "briefcases", "suitcases", "working", "careers", "professions", "appointments",
            "occupations"
        )
    ),
    Icon(
        "ic_vue_edu_award", keywords = listOf(
            "awards", "badges", "prizes", "rewards", "premium", "stars"
        )
    ),
    Icon(
        "ic_vue_edu_glass", keywords = listOf(
            "glass", "cones", "vases", "flasks", "chemistry", "cones", "sciences", "potions",
            "elixirs", "pharmacy", "labs", "education", "study", "learning"
        )
    ),
    Icon(
        "ic_vue_edu_graduate_cap", keywords = listOf(
            "education", "graduate", "graduation", "caps", "hats", "students", "graduates", "study",
            "learning", "high school", "academy"
        )
    ),
    Icon(
        "ic_vue_edu_calculator", keywords = listOf(
            "calculates", "calculators", "calculations", "maths", "numbers", "finances"
        )
    ),
    Icon(
        "ic_vue_edu_note", keywords = listOf(
            "notes", "bills", "receipts", "recipes", "reports", "invoices", "fees", "taxes",
            "expenses", "flashcards", "education", "study", "learning"
        )
    ),
    Icon(
        "ic_vue_edu_magazine", keywords = listOf(
            "magazines", "newspapers", "diary", "planners", "notes", "readings",
            "education", "study", "learning", "notebooks", "textbooks", "agenda"
        )
    ),
    Icon(
        "ic_vue_edu_pen", keywords = listOf(
            "pens", "drawings", "notes", "designers", "css", "technology", "pens", "paintings",
            "sketches", "study", "learning", "notes", "pencils"
        )
    ),
    Icon(
        "ic_vue_edu_telescope", keywords = listOf(
            "stars", "telescope", "sky", "planets", "astronomy", "sciences"
        )
    ),
    Icon(
        "ic_vue_edu_book", keywords = listOf(
            "notebooks", "textbooks", "planners", "logbooks", "organizers", "organisers",
            "appointments", "diary", "notes", "agenda"
        )
    ),
    Icon(
        "ic_vue_edu_ruler_pen", keywords = listOf(
            "rulers", "pens", "drawings", "measure", "pencils"
        )
    ),
    Icon(
        "ic_vue_edu_todo", keywords = listOf(
            "todos", "to do", "to-do", "tasks", "check marks", "ticks", "schedules", "plans",
            "agenda"
        )
    ),
    Icon(
        "ic_vue_edu_omega", keywords = listOf(
            "omega", "maths", "symbols", "signs", "letters"
        )
    ),
    Icon(
        "ic_vue_edu_bookmark", keywords = listOf(
            "bookmarks", "save", "favourites", "favorites"
        )
    ),
)
// endregion

// region Files (Vue)
private fun vueFiles(): List<Icon> = listOf(
    Icon(
        "ic_vue_files_folder_favorite", keywords = listOf(
            "bookmarks", "save", "favourites", "favorites", "folders", "files folder", "store",
            "storage"
        )
    ),
    Icon(
        "ic_vue_files_folder", keywords = listOf(
            "bookmarks", "save", "folders", "files folder", "store", "storage"
        )
    ),
    Icon(
        "ic_vue_files_folder_cloud", keywords = listOf(
            "bookmarks", "save", "clouds", "folders", "files folder", "store", "storage"
        )
    ),
)
// endregion

// region Location (Vue)
private fun vueLocation(): List<Icon> = listOf(
    Icon(
        "ic_vue_location_map1", keywords = listOf(
            "maps", "atlas", "geography", "traveling", "world", "locations", "places"
        )
    ),
    Icon(
        "ic_vue_location_map", keywords = listOf(
            "maps", "atlas", "geography", "traveling", "world", "locations", "places"
        )
    ),
    Icon(
        "ic_vue_location_location", keywords = listOf(
            "maps", "atlas", "geography", "traveling", "world", "locations", "gps", "live", "places"
        )
    ),
    Icon(
        "ic_vue_location_global", keywords = listOf(
            "global", "globes", "spheres", "world", "webs", "balls", "basketball"
        )
    ),
    Icon(
        "ic_vue_location_global_search", keywords = listOf(
            "global", "globes", "spheres", "world", "webs", "searching"
        )
    ),
    Icon(
        "ic_vue_location_routing", keywords = listOf(
            "routing", "routs", "locations", "places", "directions", "gps", "maps"
        )
    ),
    Icon(
        "ic_vue_location_discover", keywords = listOf(
            "discovering", "locations", "places"
        )
    ),
    Icon(
        "ic_vue_location_radar", keywords = listOf(
            "radars", "detection", "cars", "speeds"
        )
    ),
    Icon(
        "ic_vue_location_global_edit", keywords = listOf(
            "global", "globes", "spheres", "world", "webs", "editing"
        )
    ),
)
// endregion

// region Main (Vue)
private fun vueMain(): List<Icon> = listOf(
    Icon(
        "ic_vue_main_cake", keywords = listOf(
            "birthdays", "cakes", "candles", "surprises", "bdays", "b-day"
        )
    ),
    Icon(
        "ic_vue_main_reserve", keywords = listOf(
            "reserve", "foods", "reservations", "bells", "hotels", "gourmet", "ringing"
        )
    ),
    Icon("ic_vue_main_archive", keywords = listOf("archives", "history")),
    Icon("ic_vue_main_signpost", keywords = listOf("signposts", "signs", "directions")),
    Icon(
        "ic_vue_main_coffee", keywords = listOf(
            "coffees", "cafes", "hot", "mornings", "wake up", "energy", "drinks", "fun", "cups",
            "mugs", "glasses", "warm"
        )
    ),
    Icon(
        "ic_vue_main_sport", keywords = listOf(
            "fitness", "gym", "workout", "train", "weights", "sports", "lifting", "dumbbells",
            "workouts", "work out"
        )
    ),
    Icon(
        "ic_vue_main_notification", keywords = listOf(
            "bells", "ringing", "churches", "notifications", "news"
        )
    ),
    Icon(
        "ic_vue_main_lamp_charge", keywords = listOf(
            "lamps", "bulbs", "charge", "charging", "electricity", "flashes", "sparks"
        )
    ),
    Icon(
        "ic_vue_main_home", keywords = listOf(
            "homes", "houses", "locations", "live", "remotely"
        )
    ),
    Icon(
        "ic_vue_main_judge", keywords = listOf(
            "judges", "lawyers", "legal", "businesses", "institutions", "courthouses"
        )
    ),
    Icon(
        "ic_vue_main_timer", keywords = listOf(
            "timer", "clocks", "hourglass", "sandglass"
        )
    ),
    Icon(
        "ic_vue_main_lamp", keywords = listOf(
            "lamps", "bulbs", "interiors", "lights", "lighting"
        )
    ),
    Icon(
        "ic_vue_main_battery_charging", keywords = listOf(
            "charge", "charging", "electricity", "flashes", "sparks"
        )
    ),
    Icon(
        "ic_vue_main_calendar", keywords = listOf(
            "planners", "logbooks", "calendars", "organizers", "organisers", "appointments",
            "diary", "notes", "notebooks", "schedules", "agenda"
        )
    ),
    Icon(
        "ic_vue_main_home_wifi", keywords = listOf(
            "wifi", "wi-fi", "homes", "networks", "nets", "webs"
        )
    ),
    Icon(
        "ic_vue_main_tree", keywords = listOf(
            "trees", "gardens", "yards", "lawns", "woods", "christmas", "xmas", "forests"
        )
    ),
    Icon("ic_vue_main_battery_half", keywords = listOf("battery", "half", "charges")),
    Icon(
        "ic_vue_main_send", keywords = listOf(
            "sending", "messages", "communication", "chatting", "chats", "play", "telegram",
            "sharing", "share"
        )
    ),
    Icon(
        "ic_vue_main_glass", keywords = listOf(
            "sunglasses", "eyesight", "vision", "see", "vr", "3d"
        )
    ),
    Icon(
        "ic_vue_main_emoji_normal", keywords = listOf(
            "emojis", "normal", "happy", "chill", "joyful", "cheerful", "happiness", "good",
            "faces", "emoticon", "emotions", "moods", "vibes"
        )
    ),
    Icon(
        "ic_vue_main_share", keywords = listOf(
            "shares", "businesses", "sharing", "company", "structures", "markets", "economy",
            "economics", "relations", "communications", "community", "communicate", "groups",
            "exchanges"
        )
    ),
    Icon(
        "ic_vue_main_trash", keywords = listOf(
            "trashes", "garbages", "junk", "rubbish", "dirt", "useless", "bin", "shit"
        )
    ),
    Icon(
        "ic_vue_main_milk", keywords = listOf(
            "milks", "bottles", "glasses", "plastic", "water", "drinks"
        )
    ),
    Icon(
        "ic_vue_main_lifebuoy", keywords = listOf(
            "lifebuoy",
            "swimming pools",
            "seaside",
            "ocean",
            "save",
            "rescue",
            "rescuing",
            "saveguards",
            "rescuers",
            "life savers",
            "saviors",
            "beaches",
            "safety",
            "safeguards",
            "lifeguards"
        )
    ),
    Icon(
        "ic_vue_main_broom", keywords = listOf(
            "brooms", "cleaning", "dust", "dirt", "trashes", "garbages", "sweeping", "floors",
            "chores", "cleaners", "cleaning woman", "cleaning service", "home duties", "duty",
            "brooming"
        )
    ),
    Icon(
        "ic_vue_main_gift", keywords = listOf(
            "gifts", "party", "celebrate", "celebrations", "presents", "donations", "donates",
            "birthdays", "bdays", "b-day", "holidays"
        )
    ),
    Icon(
        "ic_vue_main_clock", keywords = listOf(
            "timer", "clocks", "appointments", "expiration", "expires", "passes", "quickly",
            "alarms", "watches", "minutes", "hours", "arrows"
        )
    ),
    Icon(
        "ic_vue_main_emoji_happy", keywords = listOf(
            "emojis", "happy", "chill", "joyful", "cheerful", "happiness", "good",
            "faces", "emoticon", "emotions", "moods", "vibes"
        )
    ),
    Icon(
        "ic_vue_main_home_safe", keywords = listOf(
            "safety", "homes", "houses", "insurances"
        )
    ),
    Icon(
        "ic_vue_main_crown", keywords = listOf(
            "crowns", "luxury", "vip", "top", "queens", "kings", "the best", "luxe"
        )
    ),
    Icon(
        "ic_vue_main_cup", keywords = listOf(
            "cups", "champions", "victory", "victories", "win", "prizes", "rewards", "awards"
        )
    ),
    Icon(
        "ic_vue_main_emoji_sad", keywords = listOf(
            "sad", "bad", "faces", "emoticon", "emotions", "moods", "vibes", "sick", "joyless",
            "unhappy"
        )
    ),
    Icon("ic_vue_main_pet", keywords = listOf("pets", "dogs", "paws", "cats")),
    Icon(
        "ic_vue_main_flash", keywords = listOf(
            "zeus", "lightning", "rains", "emergency",
            "urgents", "storms", "flash", "thunders", "important", "priority", "thor", "sparks"
        )
    ),
)
// endregion

// region Media (Vue)
private fun vueMedia(): List<Icon> = listOf(
    Icon(
        "ic_vue_media_microphone", keywords = listOf(
            "microphones", "records", "recordings", "singers", "songs", "singing", "performing",
            "tunes"
        )
    ),
    Icon(
        "ic_vue_media_music", keywords = listOf(
            "music", "sounds", "spotify", "singers", "songs", "hear", "fun", "party", "records",
            "directing", "radios", "produce", "production", "hits", "tunes", "performing", "notes",
            "listening", "recordings"
        )
    ),
    Icon(
        "ic_vue_media_voice", keywords = listOf(
            "music", "sounds", "singers", "songs", "hear", "records", "directing", "radios",
            "produce", "production", "tunes", "performing", "notes", "listening",
            "recordings", "voices", "speaking", "talking", "communication", "communicate"
        )
    ),
    Icon(
        "ic_vue_media_image", keywords = listOf(
            "images", "pics", "pictures", "gallery", "photos", "editing", "images", "photography"
        )
    ),
    Icon(
        "ic_vue_media_scissors", keywords = listOf(
            "scissors", "designers", "cutting", "tools", "cropping", "videos", "editing",
            "video editing"
        )
    ),
    Icon(
        "ic_vue_media_mountains", keywords = listOf(
            "hike", "hikings", "mountains", "walks", "sun", "tops", "nature", "hobby", "forests",
            "woods", "trees", "environments", "sports"
        )
    ),
    Icon(
        "ic_vue_media_film", keywords = listOf(
            "films", "movies", "cameras", "videos", "editing", "records", "directing", "studios",
            "shows", "tvs", "streams", "acts", "actions", "produces", "productions", "acting",
            "vlogs", "vlogging"
        )
    ),
    Icon(
        "ic_vue_media_photocamera", keywords = listOf(
            "cameras", "photos", "photography", "pics", "pictures", "images", "editing"
        )
    ),
    Icon(
        "ic_vue_media_film_play", keywords = listOf(
            "cameras", "videos", "editing", "films", "movies", "play", "records", "directing",
            "studios", "shows", "tvs", "streams", "acts", "actions", "produces", "productions",
            "acting", "vlogs", "vlogging"
        )
    ),
    Icon(
        "ic_vue_media_camera", keywords = listOf(
            "cameras", "videos", "editing", "photos", "movies", "records", "directing", "studios",
            "shows", "tvs", "streams", "acts", "actions", "produces", "productions", "acting",
            "films", "vlogs", "vlogging"
        )
    ),
    Icon(
        "ic_vue_media_screenmirroring", keywords = listOf(
            "screens", "mirrors", "screen mirroring", "remotely", "screen sharing", "share screen",
            "meeting", "presentation"
        )
    ),
    Icon(
        "ic_vue_media_speaker", keywords = listOf(
            "speakers", "volume", "sounds", "drivers", "tunes", "listening", "talking", "speaking"
        )
    ),
    Icon("ic_vue_media_play", keywords = listOf("play", "start", "youtube")),
    Icon(
        "ic_vue_media_subtitle", keywords = listOf(
            "subtitles", "texts", "articles", "chatting", "chats", "texting", "scripts"
        )
    ),
    Icon(
        "ic_vue_media_setting", keywords = listOf(
            "settings", "volume", "brightness", "editing", "contrasts", "changes", "changing"
        )
    ),
)
// endregion

// region Messages (Vue)
private fun vueMessages(): List<Icon> = listOf(
    Icon(
        "ic_vue_messages_msg_favorite", keywords = listOf(
            "messages", "chatting", "chats", "communication", "communicate", "favorites",
            "favourites", "online", "love", "couples", "partnerships", "friendships", "friends",
            "hearts", "partners", "sending", "family", "message box", "text box", "texting",
            "bubbles", "typing", "comments"
        )
    ),
    Icon(
        "ic_vue_messages_direct", keywords = listOf(
            "messages", "chatting", "chats", "communication", "communicate", "online", "direct",
            "couples", "partnerships", "friendships", "friends", "partners", "sending", "family",
            "texting"
        )
    ),
    Icon(
        "ic_vue_messages_msg_notification", keywords = listOf(
            "messages", "chatting", "chats", "communication", "communicate", "online",
            "notifications", "couples", "partnerships", "friendships", "friends", "circles",
            "partners", "sending", "notify", "seen", "news", "missed", "family", "message box",
            "text box", "texting", "bubbles"
        )
    ),
    Icon(
        "ic_vue_messages_device_msg", keywords = listOf(
            "messages", "chatting", "chats", "communication", "communicate", "online",
            "notifications", "couples", "partnerships", "friendships", "friends", "partners",
            "sending", "notify", "seen", "news", "missed", "family", "pc", "computers",
            "message box", "text box", "texting", "bubbles", "devices", "typing", "comments"
        )
    ),
    Icon(
        "ic_vue_messages_edit", keywords = listOf(
            "messages", "chatting", "chats", "communication", "communicate", "online",
            "editing", "pencils", "pens", "redactions", "texting"
        )
    ),
    Icon(
        "ic_vue_messages_msgs", keywords = listOf(
            "messages", "chatting", "chats", "communication", "communicate", "online",
            "notifications", "couples", "partnerships", "friendships", "friends", "partners",
            "sending", "notify", "seen", "news", "missed", "family", "message box", "text box",
            "texting", "bubbles"
        )
    ),
    Icon(
        "ic_vue_messages_msg_text", keywords = listOf(
            "messages", "chatting", "chats", "communication", "communicate", "online",
            "notifications", "couples", "partnerships", "friendships", "friends", "partners",
            "sending", "notify", "seen", "news", "missed", "family", "message box", "text box",
            "texting", "bubbles", "comments"
        )
    ),
    Icon(
        "ic_vue_messages_letter", keywords = listOf(
            "letters", "mails", "watches", "clocks", "ticks", "check marks", "bags", "news",
            "missed", "notifications", "communication", "communicate", "online", "working"
        )
    ),
    Icon(
        "ic_vue_messages_msg", keywords = listOf(
            "messages", "chatting", "chats", "communication", "communicate", "online",
            "notifications", "couples", "partnerships", "friendships", "friends", "partners",
            "sending", "notify", "seen", "news", "missed", "family", "message box", "text box",
            "texting", "bubbles", "typing", "comments"
        )
    ),
    Icon(
        "ic_vue_messages_msg_search", keywords = listOf(
            "messages", "chatting", "chats", "communication", "communicate", "online",
            "notifications", "couples", "partnerships", "friendships", "friends", "partners",
            "news", "missed", "family", "message box", "text box", "texting", "bubbles", "comments",
            "searching"
        )
    ),
)
// endregion

// region Money (Vue)
private fun vueMoney(): List<Icon> = listOf(
    Icon("ic_vue_money_bitcoin_refresh",keywords=listOf("refresh","bitcoin","crypto","money")),
    Icon("ic_vue_money_dollar",keywords=listOf("dollar","currency","money","symbol","usa")),
    Icon("ic_vue_money_archive",keywords=listOf("archive","save","preserve","money")),
    Icon("ic_vue_money_coins",keywords=listOf("money","coins")),
    Icon("ic_vue_money_discount",keywords=listOf("discount","money","offer")),
    Icon("ic_vue_money_receive",keywords=listOf("receive","cash-in","income","money")),
    Icon("ic_vue_money_card_send",keywords=listOf("send","card","card payment","transfer","digital transfer")),
    Icon("ic_vue_money_buy_crypto",keywords=listOf("buy","purchase","crypto","money","trade")),
    Icon("ic_vue_money_card_bitcoin",keywords=listOf("card","crypto","bitcoin","money","crypto card")),
    Icon("ic_vue_money_buy_bitcoin",keywords=listOf("purchase","bitcoin","buy bitcoin","trading","money")),
    Icon("ic_vue_money_ticket_star",keywords=listOf("star","ticket","money")),
    Icon("ic_vue_money_wallet",keywords=listOf("wallet","money","purse")),
    Icon("ic_vue_money_send",keywords=listOf("send","money transfer","money")),
    Icon("ic_vue_money_ticket_discount",keywords=listOf("discount","ticket","offer")),
    Icon("ic_vue_money_wallet_cards",keywords=listOf("card holder","wallet","money")),
    Icon("ic_vue_money_receipt_empty",keywords=listOf("receipt","empty","no items")),
    Icon("ic_vue_money_percentage",keywords=listOf("percentage","proportion","rate","money")),
    Icon("ic_vue_money_math",keywords=listOf("money","math","calculation","calculate")),
    Icon("ic_vue_money_security_card",keywords=listOf("secure","shield","security","money")),
    Icon("ic_vue_money_wallet_money",keywords=listOf("money","wallet","wallet coins")),
    Icon("ic_vue_money_ticket",keywords=listOf("ticket","money")),
    Icon("ic_vue_money_card_receive",keywords=listOf("money","receive","card")),
    Icon("ic_vue_money_wallet_empty", keywords = listOf("empty","wallet","money","broke")),
    Icon("ic_vue_money_transfer",keywords=listOf("transfer","remittance","money","exchange")),
    Icon("ic_vue_money_card_coin",keywords=listOf("card","coin","exchange","card exchange")),
    Icon("ic_vue_money_receipt_items",keywords=listOf("receipt","receipt items","list","items")),
    Icon("ic_vue_money_tag", keywords = listOf("price tag","tag","money tag")),
    Icon("ic_vue_money_receipt_discount",keywords= listOf("receipt","discount","coupon","money")),
    Icon("ic_vue_money_card",keywords=listOf("card","debit card","credit card","money")),
)
// endregion

// region PC (Vue)
private fun vuePC(): List<Icon> = listOf(
    Icon("ic_vue_pc_charging" ,keywords = listOf("charging","plug","charge","pc")),
    Icon("ic_vue_pc_watch", keywords = listOf("watch","time","wristband","wristwatch")),
    Icon("ic_vue_pc_headphone", keywords = listOf("headphone","music","listen","hear","sound")),
    Icon("ic_vue_pc_gameboy", keywords=listOf("gaming","gameboy","player")),
    Icon("ic_vue_pc_phone_call",keywords= listOf("telephone","call","ringing","incoming","phone")),
    Icon("ic_vue_pc_setting",keywords=listOf("settings","configuration","setup","adjustments")),
    Icon("ic_vue_pc_monitor",keywords=listOf("monitor","screen","display")),
    Icon("ic_vue_pc_cpu",keywords=listOf("cpu","processor","microprocessor","computing")),
    Icon("ic_vue_pc_printer",keywords=listOf("printer","copy","print")),
    Icon("ic_vue_pc_bluetooth",keywords=listOf("connectivity","bluetooth","wireless","sharing")),
    Icon("ic_vue_pc_wifi",keywords=listOf("wireless","wifi","signal","connectivity","network")),
    Icon("ic_vue_pc_game",keywords=listOf("game","gamepad","joystick")),
    Icon("ic_vue_pc_speaker",keywords=listOf("speaker","sound","audio","music")),
    Icon("ic_vue_pc_phone",keywords=listOf("phone","telephone","communication")),
)
// endregion

// region People (Vue)
private fun vuePeople(): List<Icon> = listOf(
    Icon("ic_vue_people_2persons",keywords=listOf("duo","pair","people")),
    Icon("ic_vue_people_person_tag",keywords=listOf("tag","person tag","profile","profile tag","people")),
    Icon("ic_vue_people_person_search",keywords=listOf("find","search","locate","lookup","person search")),
    Icon("ic_vue_people_people",keywords=listOf("people","community","group","team")),
    Icon("ic_vue_people_person",keywords=listOf("person","profile","individual")),
    )
// endregion

// region Security (Vue)
private fun vueSecurity(): List<Icon> = listOf(
    Icon("ic_vue_security_eye", keywords=listOf("security", "eye", "vision", "sensor")),
    Icon("ic_vue_security_shield_security", keywords=listOf("security", "shield", "protect", "sensor")),
    Icon("ic_vue_security_key", keywords=listOf("security", "key")),
    Icon("ic_vue_security_alarm", keywords=listOf("security", "alarm", "sensor")),
    Icon("ic_vue_security_lock", keywords=listOf("security", "lock", "sensor")),
    Icon("ic_vue_security_password", keywords=listOf("security", "password", "pass")),
    Icon("ic_vue_security_radar", keywords=listOf("security", "radar", "sensor")),
    Icon("ic_vue_security_shield_person", keywords=listOf("security", "shield", "person")),
    Icon("ic_vue_security_shield", keywords=listOf("security", "shield", "sensor")),
)
// endregion

// region Shop (Vue)
private fun vueShop(): List<Icon> = listOf(
    Icon("ic_vue_shop_cart", keywords=listOf("shop", "cart")),
    Icon("ic_vue_shop_bag", keywords=listOf("shop", "bag")),
    Icon("ic_vue_shop_barcode", keywords=listOf("shop", "barcode", "code")),
    Icon("ic_vue_shop_bag1", keywords=listOf("shop", "bag")),
    Icon("ic_vue_shop_shop", keywords=listOf("shop", "options")),
)
// endregion

// region Support (Vue)
private fun vueSupport(): List<Icon> = listOf(
    Icon("ic_vue_support_star", keywords=listOf("support", "star", "support reaction", "reaction")),
    Icon("ic_vue_support_medal", keywords=listOf("support", "medal", "support reaction", "reaction")),
    Icon("ic_vue_support_dislike", keywords=listOf("support", "dislike", "support reaction", "reaction")),
    Icon("ic_vue_support_like_dislike", keywords=listOf("support", "like", "dislike", "like dislike", "support reaction", "reaction")),
    Icon("ic_vue_support_smileys", keywords=listOf("support", "smileys", "support reaction", "reaction")),
    Icon("ic_vue_support_heart", keywords=listOf("support", "heart", "support reaction", "reaction")),
    Icon("ic_vue_support_like", keywords=listOf("support", "like", "support reaction", "reaction")),
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
        "ic_vue_type_paperclip",
        keywords = listOf("clip", "paper clip", "steel", "paper", "managing files")
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
        keywords = listOf(
            "core",
            "centre",
            "central",
            "middle",
            "rivet",
            "midpoint",
            "align center"
        )
    ),
    Icon(
        "ic_vue_type_textalign_justifycenter",
        keywords = listOf("distribute", "inline", "central", "middle", "align justifycenter")
    ),
)
// endregion

// region Weather (Vue)
private fun vueWeather(): List<Icon> = listOf(
    Icon(
        "ic_vue_weather_wind",
        keywords = listOf(
            "blow", "gale", "gust", "headwind", "tailwind", "tempest", "wind",
            "tornado", "windstorm", "breath", "breeze", "air", "typhoon"
        )
    ),
    Icon(
        "ic_vue_weather_cloud",
        keywords = listOf("vapor", "cloud", "veil", "fogginess", "frost", "thunderhead")
    ),
    Icon(
        "ic_vue_weather_flash",
        keywords = listOf(
            "binge", "jag", "boost", "increase", "pickup", "upswing", "epidemic",
            "eruption", "explosion", "flood", "rush", "surge", "uproar", "flash"
        )
    ),
    Icon(
        "ic_vue_weather_moon", keywords = listOf(
            "moment", "crescent", "half-moon", "celestial body", "full moon",
            "heavenly body", "new moon", "old moon", "orb of night"
        )
    ),
    Icon(
        "ic_vue_weather_drop", keywords = listOf(
            "bead", "bit", "bubble", "dash", "dewdrop",
            "driblet", "drip", "droplet", "pearl",
            "splash", "tear", "teardrop"
        )
    ),
    Icon(
        "ic_vue_weather_cold", keywords = listOf(
            "bleak", "chilled", "cool", "crisp", "frosty", "frozen", "icy", "intense", "snowy",
            "wintry", "Siberian", "arctic", "chill", "icebox", "stinging", "below freezing",
            "sharp", "below zero", "glacial", "have goose bumps", "numbing", "shivery"
        )
    ),
    Icon(
        "ic_vue_weather_sun", keywords = listOf(
            "star", "sunlight", "bask", "daylight", "tan",
            "flare", "shine", "sol", "sunrise", "aubade"
        )
    ),
)
// endregion