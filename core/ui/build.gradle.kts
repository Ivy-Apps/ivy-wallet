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
    implementation(project(":design-system"))
    implementation(project(":core:domain"))
    implementation(project(":navigation"))
    implementation(project(":app-base")) // TODO: temp dependency, remove later
    implementation(project(":math"))
    implementation("com.github.Vishwa-Raghavendra:colorpicker-compose:v1.1.2")

    Testing()
}