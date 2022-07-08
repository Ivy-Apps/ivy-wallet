import com.ivy.buildsrc.Hilt
import com.ivy.buildsrc.RoomDB

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
}

dependencies {
    implementation(project(":common"))
    implementation(project(":network-common"))
    implementation(project(":data-model"))
    Hilt()
    RoomDB()
}