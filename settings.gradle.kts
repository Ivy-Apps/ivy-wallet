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
include(":ivy-design")
include(":common")
include(":reports")
include(":accounts")
include(":categories")
include(":home")
include(":more-menu")
include(":planned-payments")
include(":transaction-details")
include(":pie-charts")
include(":budgets")
include(":loans")
include(":settings")
include(":onboarding")
include(":item-transactions")
include(":account-transactions")
include(":category-transactions")
include(":search-transactions")