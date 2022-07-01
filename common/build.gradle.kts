import com.ivy.buildsrc.Coroutines
import com.ivy.buildsrc.FunctionalProgramming
import com.ivy.buildsrc.IvyFRP
import com.ivy.buildsrc.Kotlin

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    IvyFRP(api = true)
    Kotlin(api = true)
    Coroutines(api = true)
    FunctionalProgramming(api = true)
}