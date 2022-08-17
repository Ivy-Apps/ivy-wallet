import com.ivy.buildsrc.Hilt

apply<com.ivy.buildsrc.IvyComposePlugin>()

plugins {
    `android-library`
}

dependencies {
    Hilt()
    implementation(project(":common"))
    implementation(project(":ui-common"))
    implementation(project(":ui-components-old"))
    implementation(project(":app-base"))
    implementation(project(":core:ui"))
    implementation(project(":data-model"))
    implementation(project(":screens"))
    implementation(project(":temp-domain"))
    implementation(project(":temp-persistence"))
    implementation(project(":temp-network"))
    implementation(project(":core:exchange-provider"))
    implementation(project(":widgets"))
}