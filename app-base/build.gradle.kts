import com.ivy.buildsrc.Hilt

apply<com.ivy.buildsrc.IvyPlugin>()

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
    implementation(project(":ui-common"))
    implementation(project(":data-model"))
    implementation(project(":temp-persistence"))

    Hilt()
}