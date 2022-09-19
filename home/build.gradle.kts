import com.ivy.buildsrc.Hilt
import com.ivy.buildsrc.IvyFRP

apply<com.ivy.buildsrc.IvyComposePlugin>()

plugins {
    `android-library`
}

dependencies {
    Hilt()
    IvyFRP()
    implementation(project(":common"))
    implementation(project(":design-system"))
    implementation(project(":ui-components-old"))
    implementation(project(":app-base"))
    implementation(project(":core:ui"))
    implementation(project(":core:data-model"))
    implementation(project(":screens"))
    implementation(project(":temp-domain"))
    implementation(project(":core:domain"))
    implementation(project(":core:persistence"))

    implementation(project(":more-menu"))
    implementation(project(":customer-journey"))
    implementation(project(":pie-charts"))
}