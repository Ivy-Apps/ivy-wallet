import com.ivy.buildsrc.Hilt
import com.ivy.buildsrc.Ktor

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    Hilt()
    implementation(project(":common:main"))
    Ktor(api = true)
}