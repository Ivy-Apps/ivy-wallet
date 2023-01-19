import com.ivy.buildsrc.Hilt
import com.ivy.buildsrc.Testing

apply<com.ivy.buildsrc.IvyComposePlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    Hilt()
    implementation(project(":common:main"))
    implementation(project(":navigation"))
    implementation(project(":core:ui"))
    implementation(project(":core:domain"))
    implementation(project(":design-system"))
    implementation(project(":backup:base"))
    implementation(project(":backup:old"))
    implementation(project(":backup:impl"))
    Testing()
}