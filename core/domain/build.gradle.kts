import com.ivy.buildsrc.ComposeTesting
import com.ivy.buildsrc.Hilt
import com.ivy.buildsrc.Lifecycle
import com.ivy.buildsrc.Testing

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    Hilt()
    implementation(project(":common:main"))
    implementation(project(":core:persistence"))
    implementation(project(":core:exchange-provider"))

    Lifecycle(api = false)
    ComposeTesting(api = false) // for IdlingResource
    Testing()
}