import com.ivy.buildsrc.Networking

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
}

dependencies {
    implementation(project(":common"))
    Networking(api = true)
}