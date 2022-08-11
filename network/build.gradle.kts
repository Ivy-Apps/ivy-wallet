import com.ivy.buildsrc.Hilt
import com.ivy.buildsrc.Ktor

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    Hilt()
    implementation(project(":common"))
    api(project(":temp-network"))
    Ktor(api = true)
}