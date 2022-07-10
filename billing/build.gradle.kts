import com.ivy.buildsrc.Billing
import com.ivy.buildsrc.Google

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    implementation(project(":common"))
    implementation(project(":data-model"))
    implementation(project(":app-base"))
    Google()

    Billing(api = true)
}