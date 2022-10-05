import com.ivy.buildsrc.Hilt

apply<com.ivy.buildsrc.IvyComposePlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    implementation(project(":common:main"))
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))
    implementation(project(":navigation"))
    implementation(project(":design-system"))
    Hilt()
}