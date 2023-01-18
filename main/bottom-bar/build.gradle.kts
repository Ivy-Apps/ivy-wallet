import com.ivy.buildsrc.Hilt
import com.ivy.buildsrc.Testing

apply<com.ivy.buildsrc.IvyComposePlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    Hilt()
    implementation(project(":design-system"))
    implementation(project(":core:ui"))
    implementation(project(":navigation"))
    implementation(project(":resources"))
    Testing()
}