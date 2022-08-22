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
    implementation(project(":core:functions"))
    implementation(project(":core:exchange-provider"))
    implementation(project(":sync:public"))
    implementation(project(":app-base")) // TODO: migrate to :resources
}