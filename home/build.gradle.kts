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
    implementation(project(":ui-common"))
    implementation(project(":ui-components-old"))
    implementation(project(":app-base"))
    implementation(project(":core:ui"))
    implementation(project(":data-model"))
    implementation(project(":screens"))
    implementation(project(":temp-domain"))

    implementation(project(":more-menu"))
    implementation(project(":customer-journey"))
    implementation(project(":pie-charts"))
}