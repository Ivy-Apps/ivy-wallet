import com.ivy.buildsrc.EventBus

apply<com.ivy.buildsrc.IvyPlugin>()

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
    implementation(project(":temp-persistence"))
    implementation(project(":exchange"))
    EventBus()
}