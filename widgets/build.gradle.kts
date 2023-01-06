import com.ivy.buildsrc.DataStore
import com.ivy.buildsrc.Glance
import com.ivy.buildsrc.Hilt

apply<com.ivy.buildsrc.IvyComposePlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

android {
    buildFeatures {
        compose = true
    }
}

dependencies {
    Hilt()
    implementation(project(":common:main"))
    implementation(project(":design-system"))
    implementation(project(":core:data-model"))
    implementation(project(":core:ui"))
    Glance()

    DataStore(api = false)
}