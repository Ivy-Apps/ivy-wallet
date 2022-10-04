import com.ivy.buildsrc.Hilt

apply<com.ivy.buildsrc.IvyComposePlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    Hilt()
    implementation(project(":common:main"))

    implementation(project(":design-system"))
    implementation(project(":app-base"))
    implementation(project(":core:ui"))
    implementation(project(":ui-components-old"))
    implementation(project(":core:data-model"))
}