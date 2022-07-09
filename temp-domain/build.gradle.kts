import com.ivy.buildsrc.Hilt

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    implementation(project(":common"))
    implementation(project(":data-model"))
    implementation(project(":app-base"))
    implementation(project(mapOf("path" to ":exchange")))
    Hilt()

    implementation(project(":temp-persistence"))
}