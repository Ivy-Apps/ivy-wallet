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
    implementation(project(":common"))
    implementation(project(":design-system"))
    implementation(project(":core:data-model"))
    implementation(project(":temp-persistence"))
}