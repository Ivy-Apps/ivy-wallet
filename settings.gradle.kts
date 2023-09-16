enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        gradlePluginPortal()
    }
}

rootProject.name = "IvyWallet"
include(":app")
include(":ivy-design")
include(":ivy-core")
include(":ivy-navigation")
include(":ivy-resources")
include(":ivy-widget-base")
include(":widget-add-transaction")
include(":widget-balance")
include(":screen-home")
include(":screen-settings")
include(":screen-accounts")
include(":screen-categories")
include(":screen-transaction")
include(":screen-reports")
include(":screen-budgets")
include(":screen-loans")
include(":screen-planned-payments")
include(":screen-import-data")
include(":screen-onboarding")
include(":screen-transactions")
include(":screen-piechart")
include(":temp-legacy-code")
include(":screen-exchange-rates")
include(":screen-search")
include(":screen-test")
include(":screen-balance")
