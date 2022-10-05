import com.ivy.buildsrc.Coroutines
import com.ivy.buildsrc.Hilt
import com.ivy.buildsrc.Kotlin
import com.ivy.buildsrc.Testing

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    Hilt()
    implementation(project(":common:main"))
    Testing(
        // Prevent circular dependency
        commonTest = false,
        commonAndroidTest = false
    )
    Kotlin(api = false)
    Coroutines(api = false)
}