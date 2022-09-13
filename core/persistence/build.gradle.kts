import com.ivy.buildsrc.Hilt
import com.ivy.buildsrc.RoomDB
import com.ivy.buildsrc.SerializationJson
import com.ivy.buildsrc.Testing

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
    `kotlin-kapt` // for Room DB
}

android {
    defaultConfig {
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/../schemas-new")
            }
        }
    }
}

dependencies {
    Hilt()
    implementation(project(":common"))
    RoomDB(api = false)
    SerializationJson()

    Testing()
}