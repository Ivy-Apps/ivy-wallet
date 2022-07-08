import com.ivy.buildsrc.Hilt

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    implementation(project(":common"))
    implementation(project(":data-model"))
    implementation(project(":app-base"))
    Hilt()

    implementation(project(":temp-persistence"))
}