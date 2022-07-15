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
    implementation(project(":ui-common"))
    implementation(project(":data-model"))
    implementation(project(":temp-persistence"))
}