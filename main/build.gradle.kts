import com.ivy.buildsrc.EventBus

apply<com.ivy.buildsrc.IvyComposePlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    implementation(project(":ui-common"))
    implementation(project(":data-model"))
    implementation(project(":temp-domain"))
    implementation(project(":screens"))
    implementation(project(":app-base"))
    implementation(project(":ui-components-old"))


    EventBus()

    implementation(project(":home"))
    implementation(project(":accounts"))
}