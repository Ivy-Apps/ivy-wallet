import com.ivy.buildsrc.Hilt

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    Hilt()
    implementation(project(":common"))
    implementation(project(":temp-persistence"))
    implementation(project(":sync:base"))
    implementation(project(":sync:ivy-server"))
}