import com.ivy.buildsrc.*

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    api(project(":state"))
    api(project(":data-model"))
    api(project(":resources"))

    Hilt()
    IvyFRP(api = true)
    Kotlin(api = true)
    Coroutines(api = true)
    FunctionalProgramming(api = true)
    Timber(api = true)
}