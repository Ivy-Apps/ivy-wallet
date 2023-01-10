import com.ivy.buildsrc.Billing
import com.ivy.buildsrc.Hilt

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    Hilt()
    implementation(project(":common:main"))

    implementation(project(":core:data-model"))
    implementation(project(":core:ui"))

    Billing(api = true)
}