import com.ivy.buildsrc.DataStore

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
    implementation(project(":app-base"))
    implementation(project(":temp-domain"))
    implementation(project(":temp-persistence"))

    DataStore(api = false)
}