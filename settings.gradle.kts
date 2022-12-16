dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}
rootProject.name = "Ivy Wallet"
include(":app")
include(":common:main")
include(":common:android-test")
include(":common:test")
include(":design-system")
//include(":reports")
include(":accounts")
include(":categories")
include(":home:tab")
include(":home:more-menu")
include(":home:customer-journey")
include(":transaction")
//include(":planned-payments")
//include(":pie-charts")
//include(":budgets")
//include(":loans")
//include(":settings")
include(":onboarding")
//include(":item-transactions")
//include(":search-transactions")
//include(":donate")
include(":main:impl")
include(":main:base")
include(":app-base")
include(":ui-components-old")
include(":temp-domain")
include(":temp-persistence")
include(":widgets")
include(":app-locked")
//include(":balance-prediction")
//include(":import-csv-backup")
include(":temp-network")
include(":billing")
include(":web-view")
include(":android-notifications")
include(":core:data-model")
include(":core:domain")
include(":core:exchange-provider")
include(":core:ui")
include(":core:persistence")
include(":sync:public")
include(":sync:base")
//include(":sync:ivy-server")
include(":network")
include(":resources")
include(":navigation")
include(":debug")
include(":formula:domain")
include(":formula:persistence")
include(":formula:ui")
include(":parser")
include(":math")