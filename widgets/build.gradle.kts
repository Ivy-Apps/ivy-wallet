apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    implementation(project(":ui-common"))
    implementation(project(":data-model"))
    implementation(project(":app-base"))
    implementation(project(":temp-domain"))
    implementation(project(":temp-persistence"))
}