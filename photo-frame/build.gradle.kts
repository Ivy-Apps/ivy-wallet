import com.ivy.buildsrc.Hilt
import com.ivy.buildsrc.Testing

apply<com.ivy.buildsrc.IvyComposePlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    Hilt()
    implementation(project(":common:main"))
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))
    implementation(project(":android:file-system"))
    implementation(project(":design-system"))
    Testing()
}