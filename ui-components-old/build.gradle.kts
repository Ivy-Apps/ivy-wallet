import com.ivy.buildsrc.Hilt
import com.ivy.buildsrc.ThirdParty

apply<com.ivy.buildsrc.IvyComposePlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

android {
    buildFeatures {
        compose = true
    }
}

dependencies {
    Hilt()
    implementation(project(":common"))
    implementation(project(":ui-common"))
    implementation(project(":data-model"))
    implementation(project(":app-base"))
    implementation(project(":core:ui"))
    implementation(project(":temp-domain"))
    implementation(project(":screens"))

    ThirdParty()
}