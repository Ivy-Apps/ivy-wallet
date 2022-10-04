import com.ivy.buildsrc.*

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    Hilt()
    HiltTesting(
        dependency = { api(it) },
        kaptProcessor = { kapt(it) }
    )

    Testing(
        // Prevent circular dependency
        commonAndroidTest = false
    )
    Kotlin(api = false)
    Coroutines(api = false)
    AndroidXTest(dependency = { api(it) })
}