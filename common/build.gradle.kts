import com.ivy.buildsrc.*
import com.ivy.buildsrc.Hilt

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    Hilt()
    IvyFRP(api = true)
    Kotlin(api = true)
    Coroutines(api = true)
    FunctionalProgramming(api = true)
    Timber(api = true)
}