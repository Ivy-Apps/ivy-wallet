import com.ivy.buildsrc.Hilt

apply<com.ivy.buildsrc.IvyComposePlugin>()

plugins {
    `android-library`
}

dependencies {
    Hilt()
    implementation(project(":common:main"))
    implementation(project(":design-system"))
    implementation(project(":core:ui"))
    implementation(project(":core:domain"))
    implementation(project(":core:persistence"))
    implementation(project(":navigation"))

}