package com.ivy.domain.usecase

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivy.base.TestDispatchersProvider
import com.ivy.base.di.KotlinxSerializationModule
import com.ivy.data.db.IvyRoomDatabase
import com.ivy.data.di.KtorClientModule
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.remote.impl.RemoteExchangeRatesDataSourceImpl
import com.ivy.data.repository.ExchangeRatesRepository
import com.ivy.data.repository.impl.ExchangeRatesRepositoryImpl
import com.ivy.data.repository.mapper.ExchangeRateMapper
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SyncExchangeRatesUseCaseTest {
    private lateinit var useCase: SyncExchangeRatesUseCase
    private lateinit var repository: ExchangeRatesRepository
    private lateinit var db: IvyRoomDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, IvyRoomDatabase::class.java).build()

        repository = ExchangeRatesRepositoryImpl(
            exchangeRatesDao = db.exchangeRatesDao,
            writeExchangeRatesDao = db.writeExchangeRatesDao,
            mapper = ExchangeRateMapper(),
            remoteExchangeRatesDataSource = RemoteExchangeRatesDataSourceImpl(
                ktorClient = {
                    KtorClientModule.provideKtorClient(KotlinxSerializationModule.provideJson())
                },
            ),
            dispatchers = TestDispatchersProvider,
        )

        useCase = SyncExchangeRatesUseCase(repository)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun syncsExchangeRates(): Unit = runBlocking {
        // given
        val exchangeRatesDao = db.exchangeRatesDao

        // when
        useCase.sync(AssetCode.unsafe("USD"))

        // then
        val savedRates = exchangeRatesDao.findAll().first()
        savedRates.shouldNotBeEmpty()
        println("Saved ${savedRates.size} exchange rates")
        val eurRate = savedRates.firstOrNull { it.currency == "EUR" }?.rate
        eurRate.shouldNotBeNull() shouldBeGreaterThan 0.0
    }
}
