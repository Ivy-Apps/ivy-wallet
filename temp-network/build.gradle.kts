import com.ivy.buildsrc.Hilt
import com.ivy.buildsrc.Networking

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    Hilt()
    implementation(project(":common"))
    implementation(project(":core:data-model"))
    implementation(project(":temp-persistence"))
    Networking(api = true)

}