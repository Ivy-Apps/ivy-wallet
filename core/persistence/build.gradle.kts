import com.ivy.buildsrc.DataStore
import com.ivy.buildsrc.Hilt
import com.ivy.buildsrc.RoomDB
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
                arg("room.schemaLocation", "$projectDir/../room-db-schemas")
            }
        }
    }
}

dependencies {
    Hilt()
    implementation(project(":common:main"))
    RoomDB(api = false)
    DataStore(api = true)

    Testing()
}