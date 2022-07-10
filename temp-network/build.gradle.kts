import com.ivy.buildsrc.Firebase
import com.ivy.buildsrc.Google
import com.ivy.buildsrc.Networking

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
    id("com.google.gms.google-services")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":data-model"))
    implementation(project(":temp-persistence"))
    Networking(api = true)

    Google()
    Firebase()
}