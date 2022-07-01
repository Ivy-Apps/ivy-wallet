apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
}

android {
    compileSdk = com.ivy.buildsrc.Project.compileSdkVersion

    defaultConfig {
        minSdk = com.ivy.buildsrc.Project.minSdk
    }
}

dependencies {

}