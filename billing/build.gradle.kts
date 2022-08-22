import com.ivy.buildsrc.Billing
import com.ivy.buildsrc.Hilt

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    Hilt()

    implementation(project(":common"))
    implementation(project(":data-model"))
    implementation(project(":app-base"))
    implementation(project(":core:ui"))

    Billing(api = true)
}