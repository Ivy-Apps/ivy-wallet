import com.ivy.buildsrc.Hilt
import com.ivy.buildsrc.ThirdParty

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    implementation(project(":common"))
    implementation(project(":data-model"))
    implementation(project(":app-base"))
    implementation(project(":exchange"))
    implementation(project(":ui-common"))
    implementation(project(":screens"))
    Hilt()
    ThirdParty()

    implementation(project(":temp-persistence"))
    implementation(project(":temp-network"))
    implementation(project(":android-notifications"))
}