import com.ivy.buildsrc.Hilt
import com.ivy.buildsrc.Testing

apply<com.ivy.buildsrc.IvyComposePlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    Hilt()
    implementation(project(":common"))
    implementation(project(":design-system"))
    implementation(project(":core:domain"))
    implementation(project(":screens"))
    implementation(project(":app-base")) // TODO: temp dependency, remove later

    Testing()
}