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

    Kotlin(api = false)
    Coroutines(api = false)
    AndroidXTest(dependency = { api(it) })


    Testing(
        // :common:test needs to be added as implementation dep
        // else won't work
        commonTest = false,
        // Prevent circular dependency
        commonAndroidTest = false
    )
    api(project(":common:test")) // expose :common:test classes to all androidTest
}