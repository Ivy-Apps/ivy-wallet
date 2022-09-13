import com.ivy.buildsrc.*

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    Hilt()

    Testing(commonTest = false)
    Kotlin(api = false)
    Coroutines(api = false)
    AndroidXTest(dependency = { dep ->
        api(dep)
    })
}