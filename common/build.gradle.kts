import com.ivy.buildsrc.Coroutines
import com.ivy.buildsrc.FunctionalProgramming
import com.ivy.buildsrc.IvyFRP
import com.ivy.buildsrc.Kotlin

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
}

android {
    compileSdk = com.ivy.buildsrc.Project.compileSdkVersion
}

dependencies {
    IvyFRP(api = true)
    Kotlin(api = true)
    Coroutines(api = true)
    FunctionalProgramming(api = true)
}