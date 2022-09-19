import com.ivy.buildsrc.EventBus
import com.ivy.buildsrc.Hilt

apply<com.ivy.buildsrc.IvyComposePlugin>()

plugins {
    `android-library`
}

dependencies {
    Hilt()
    implementation(project(":common"))
    implementation(project(":design-system"))
    implementation(project(":ui-components-old"))
    implementation(project(":app-base"))
    implementation(project(":core:ui"))
    implementation(project(":core:data-model"))
    implementation(project(":screens"))
    implementation(project(":temp-domain"))
    implementation(project(":temp-persistence"))
    implementation(project(":core:exchange-provider"))
    implementation(project(":widgets"))
    EventBus()
}