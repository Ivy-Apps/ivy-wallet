import com.ivy.buildsrc.AndroidX
import com.ivy.buildsrc.Hilt


apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    Hilt()
    implementation(project(":common:main"))
    implementation(project(":core:ui"))
    AndroidX(api = false)
}