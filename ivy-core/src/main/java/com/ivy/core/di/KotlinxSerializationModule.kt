package com.ivy.core.di

import com.ivy.core.kotlinxserilzation.UUIDSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.util.UUID

@Module
@InstallIn(SingletonComponent::class)
object KotlinxSerializationModule {
    @Provides
    fun provideJson(): Json {
        return Json {
            serializersModule = SerializersModule {
                contextual(UUID::class, UUIDSerializer)
            }
        }
    }
}