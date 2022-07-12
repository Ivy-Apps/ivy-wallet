import com.ivy.buildsrc.Networking
import com.ivy.buildsrc.Hilt

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    Hilt()
    implementation(project(":common"))
    implementation(project(":data-model"))
    implementation(project(":temp-persistence"))
    Networking(api = true)

}