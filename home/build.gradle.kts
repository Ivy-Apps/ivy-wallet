apply<com.ivy.buildsrc.IvyComposePlugin>()

plugins {
    `android-library`
}

dependencies {
    implementation(project(":ui-common"))
    implementation(project(":ui-components-old"))
    implementation(project(":app-base"))
    implementation(project(":data-model"))
    implementation(project(":screens"))
    implementation(project(":temp-domain"))

    implementation(project(":more-menu"))
    implementation(project(":customer-journey"))
    implementation(project(":pie-charts"))
}