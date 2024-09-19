enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
    }
}

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
include(":ci-actions:base")
include(":ci-actions:compose-stability")
include(":ci-actions:issue-assign")
include(":ci-actions:issue-create-comment")
include(":ci-actions:pr-description-check")
include(":screen:accounts")
include(":screen:attributions")
include(":screen:balance")
include(":screen:budgets")
include(":screen:categories")
include(":screen:contributors")
include(":screen:disclaimer")
include(":screen:edit-transaction")
include(":screen:exchange-rates")
include(":screen:features")
include(":screen:home")
include(":screen:import-data")
include(":screen:loans")
include(":screen:main")
include(":screen:onboarding")
include(":screen:piechart")
include(":screen:planned-payments")
include(":screen:releases")
include(":screen:reports")
include(":screen:search")
include(":screen:settings")
include(":screen:transactions")
include(":shared:base")
include(":shared:common-ui")
include(":shared:data:core")
include(":shared:data:core-testing")
include(":shared:data:model")
include(":shared:data:model-testing")
include(":shared:domain")
include(":shared:ui:core")
include(":shared:ui:navigation")
include(":shared:ui:testing")
include(":temp:legacy-code")
include(":temp:old-design")
include(":widget:add-transaction")
include(":widget:balance")
include(":widget:shared-base")
