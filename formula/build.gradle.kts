import com.ivy.buildsrc.Hilt

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    Hilt()
    implementation(project(":common"))
    implementation(project(":core:domain"))
}