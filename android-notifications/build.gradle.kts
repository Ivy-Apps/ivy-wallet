import com.ivy.buildsrc.AndroidX

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    implementation(project(":common"))
    implementation(project(":app-base"))
    AndroidX(api = false)
}