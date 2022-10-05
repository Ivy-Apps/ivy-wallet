import com.ivy.buildsrc.EventBus
import com.ivy.buildsrc.Hilt

apply<com.ivy.buildsrc.IvyComposePlugin>()

plugins {
    `android-library`
}

dependencies {
    Hilt()
    implementation(project(":common:main"))
    implementation(project(":design-system"))
    implementation(project(":core:data-model"))
    implementation(project(":core:ui"))
    implementation(project(":core:domain"))
    implementation(project(":navigation"))

    // TODO: Remove these
    implementation(project(":temp-domain"))
    implementation(project(":app-base"))
    implementation(project(":temp-persistence"))
    implementation(project(":ui-components-old"))

    EventBus()
}