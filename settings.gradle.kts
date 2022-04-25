import java.net.URI

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = URI.create("https://jitpack.io")
        }
    }
}
rootProject.name = "Ivy Wallet"
include(":app")
include(":ivy-design")
include(":ivy-fp")

 