import com.ivy.buildsrc.Hilt

apply<com.ivy.buildsrc.IvyComposePlugin>()

plugins {
    `android-library`
}

dependencies {
    Hilt()
    implementation(project(":common"))
    implementation(project(":ui-common"))
    implementation(project(":data-model"))
    implementation(project(":temp-domain"))
    implementation(project(":screens"))
    implementation(project(":app-base"))
    implementation(project(":core:ui"))
    implementation(project(":temp-persistence"))
    implementation(project(":ui-components-old"))

}