package com.ivy.domain.usecase

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import arrow.core.left
import arrow.core.right
import com.ivy.base.TestDispatchersProvider
import com.ivy.data.db.IvyRoomDatabase
import com.ivy.data.db.entity.ExchangeRateEntity
import com.ivy.data.remote.RemoteExchangeRatesDataSource
import com.ivy.data.remote.responses.ExchangeRatesResponse
import com.ivy.data.repository.ExchangeRatesRepository
import com.ivy.data.repository.impl.ExchangeRatesRepositoryImpl
import com.ivy.data.repository.mapper.ExchangeRateMapper
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.take
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

    private val dataSource = mockk<RemoteExchangeRatesDataSource> {
        every { urls } returns listOf(
            "www.exampleurl.com",
            "www.exampleurl2.com",
            "www.exampleurl3.com"
        )
    }
    private val mockSuccessfulNetworkResponse =
        ExchangeRatesResponse(
            date = "3/3/2024",
            rates =
                mapOf(
                    "tone" to 774.71625493,
                    "top" to 2.57214941,
                    "topia" to 14.29070496,
                    "tor" to 0.19979103,
                    "trac" to 0.86330152,
                    "trb" to 0.0093354176,
                    "tribe" to 2.21640575,
                    "trl" to 34278449.41257124,
                    "tru" to 14.17009138,
                    "trump" to 0.11268264,
                    "trx" to 7.73424282,
                    "try" to 34.27844941,
                    "ttd" to 7.36013449,
                    "ttt" to 280.05157688,
                    "tusd" to 1.08325956,
                    "tvd" to 1.66719095,
                    "tvk" to 6.02514016,
                    "twd" to 34.23998943,
                    "twt" to 0.74876888,
                    "tzs" to 2762.30654389,
                    "uah" to 41.59998531,
                    "ugx" to 4257.67143978,
                    "uma" to 0.24889452,
                    "unfi" to 0.1342522,
                    "uni" to 0.086953727,
                    "uos" to 4.27326213,
                    "upi" to 2087.73100357,
                    "uqc" to 0.11401793,
                    "usd" to 1.08545239,
                    "usdc" to 1.08558632,
                    "usdd" to 1.08516708,
                    "usde" to 1.08538602,
                    "usdp" to 1.08566689,
                    "usdt" to 1.08431852,
                    "ust" to 30.16612281,
                    "ustc" to 30.06513032,
                    "uyu" to 42.27182963,
                    "uzs" to 13531.32932644,
                    "val" to 1936.27000005,
                    "vara" to 10.18799512,
                    "veb" to 3912982812.1479034,
                    "ved" to 39.12852274,
                    "vef" to 3912852.27396198,
                    "velo" to 11.7470671,
                    "ves" to 39.12852274,
                    "vet" to 22.31738091,
                    "vgx" to 8.50489193,
                    "vnd" to 26808.84977315,
                    "vnst" to 27852.48970999,
                    "voxel" to 3.56170304,
                    "vr" to 23.8164718,
                    "vtho" to 225.63745972,
                    "vuv" to 130.84665403,
                    "wampl" to 0.08683432,
                    "waves" to 0.33300871,
                    "waxl" to 0.49417424,
                    "waxp" to 12.96806893,
                    "wbeth" to 0.00029154536,
                    "wbt" to 0.13023256,
                    "wbtc" to 0.000015929973,
                    "wcfg" to 1.44147278,
                    "wemix" to 0.41000026,
                    "whrh" to 247193.99623485,
                    "wif" to 0.73084395,
                    "wld" to 0.1471992,
                    "wluna" to 17743.19126635,
                    "woo" to 1.87365872,
                    "wst" to 2.98585956,
                    "xaf" to 655.95700002,
                    "xag" to 0.045545644,
                    "xai" to 0.86212909,
                    "xau" to 0.00051287376,
                    "xaut" to 0.00051390972,
                    "xbt" to 0.000015936612,
                    "xcd" to 2.93190243,
                    "xch" to 0.021554961,
                    "xcn" to 448.52438017,
                    "xdc" to 23.27666807,
                    "xdr" to 0.81719664,
                    "xec" to 14044.58413064,
                    "xem" to 21.84709917,
                    "xlm" to 7.27476819,
                    "xmon" to 0.0011939743,
                    "xmr" to 0.0072300761,
                    "xof" to 655.95700002,
                    "xpd" to 0.0011277654,
                    "xpf" to 119.33174225,
                    "xpt" to 0.0012095396,
                    "xrd" to 24.11419775,
                    "xrp" to 1.65167259,
                    "xtz" to 0.77913247,
                    "xvs" to 0.084198776,
                    "xyo" to 108.16432486,
                    "yer" to 271.75115573,
                    "yfi" to 0.0001143126,
                    "yfii" to 0.0015461102,
                    "zar" to 20.68009295,
                    "zec" to 0.03305513,
                    "zen" to 0.089446148,
                    "zeta" to 0.50977058,
                    "zil" to 34.19831077,
                ),
        )

    private val expectedSavedEntities =
        listOf(
            ExchangeRateEntity("USD", "TONE", 713.7266102753711),
            ExchangeRateEntity("USD", "TOP", 2.3696565908339844),
            ExchangeRateEntity("USD", "TOPIA", 13.16566722931072),
            ExchangeRateEntity("USD", "TOR", 0.1840624534439507),
            ExchangeRateEntity("USD", "TRAC", 0.7953379880622863),
            ExchangeRateEntity("USD", "TRB", 0.008600485554230528),
            ExchangeRateEntity("USD", "TRIBE", 2.041918899823879),
            ExchangeRateEntity("USD", "TRL", 3.157987372672443E7),
            ExchangeRateEntity("USD", "TRU", 13.054548970130327),
            ExchangeRateEntity("USD", "TRUMP", 0.10381168353224594),
            ExchangeRateEntity("USD", "TRX", 7.125363480935356),
            ExchangeRateEntity("USD", "TRY", 31.579873724355615),
            ExchangeRateEntity("USD", "TTD", 6.780706881118941),
            ExchangeRateEntity("USD", "TTT", 258.0044776353572),
            ExchangeRateEntity("USD", "TUSD", 0.9979798008459864),
            ExchangeRateEntity("USD", "TVD", 1.5359411111527426),
            ExchangeRateEntity("USD", "TVK", 5.550810164967254),
            ExchangeRateEntity("USD", "TWD", 31.54444151161711),
            ExchangeRateEntity("USD", "TWT", 0.689821946036712),
            ExchangeRateEntity("USD", "TZS", 2544.843577975815),
            ExchangeRateEntity("USD", "UAH", 38.32502069482753),
            ExchangeRateEntity("USD", "UGX", 3922.485664967765),
            ExchangeRateEntity("USD", "UMA", 0.22930026438101078),
            ExchangeRateEntity("USD", "UNFI", 0.12368317692865367),
            ExchangeRateEntity("USD", "UNI", 0.08010828277783791),
            ExchangeRateEntity("USD", "UOS", 3.936848975937121),
            ExchangeRateEntity("USD", "UPI", 1923.3740906591033),
            ExchangeRateEntity("USD", "UQC", 0.10504185264173586),
            ExchangeRateEntity("USD", "USD", 1.0),
            ExchangeRateEntity("USD", "USDC", 1.0001233863421684),
            ExchangeRateEntity("USD", "USDD", 0.9997371510693344),
            ExchangeRateEntity("USD", "USDE", 0.9999388549874584),
            ExchangeRateEntity("USD", "USDP", 1.00019761345774),
            ExchangeRateEntity("USD", "USDT", 0.9989553940730649),
            ExchangeRateEntity("USD", "UST", 27.791290606490815),
            ExchangeRateEntity("USD", "USTC", 27.69824876427791),
            ExchangeRateEntity("USD", "UYU", 38.943973977522866),
            ExchangeRateEntity("USD", "UZS", 12466.073547859618),
            ExchangeRateEntity("USD", "VAL", 1783.8368756551358),
            ExchangeRateEntity("USD", "VARA", 9.385943790680678),
            ExchangeRateEntity("USD", "VEB", 3.6049326973686094E9),
            ExchangeRateEntity("USD", "VED", 36.04812435854511),
            ExchangeRateEntity("USD", "VEF", 3604812.435819484),
            ExchangeRateEntity("USD", "VELO", 10.822277612747254),
            ExchangeRateEntity("USD", "VES", 36.04812435854511),
            ExchangeRateEntity("USD", "VET", 20.56044200151423),
            ExchangeRateEntity("USD", "VGX", 7.835343132829621),
            ExchangeRateEntity("USD", "VND", 24698.31935525979),
            ExchangeRateEntity("USD", "VNST", 25659.798593275937),
            ExchangeRateEntity("USD", "VOXEL", 3.2813074740201182),
            ExchangeRateEntity("USD", "VR", 21.941516753212916),
            ExchangeRateEntity("USD", "VTHO", 207.87411939827228),
            ExchangeRateEntity("USD", "VUV", 120.54573303763236),
            ExchangeRateEntity("USD", "WAMPL", 0.07999827611047962),
            ExchangeRateEntity("USD", "WAVES", 0.3067925531031352),
            ExchangeRateEntity("USD", "WAXL", 0.45527030439354416),
            ExchangeRateEntity("USD", "WAXP", 11.947155904276926),
            ExchangeRateEntity("USD", "WBETH", 2.68593411084571E-4),
            ExchangeRateEntity("USD", "WBT", 0.11997998364534442),
            ExchangeRateEntity("USD", "WBTC", 1.467588366542728E-5),
            ExchangeRateEntity("USD", "WCFG", 1.3279926354024612),
            ExchangeRateEntity("USD", "WEMIX", 0.3777229326474651),
            ExchangeRateEntity("USD", "WHRH", 227733.61458520536),
            ExchangeRateEntity("USD", "WIF", 0.673308158637893),
            ExchangeRateEntity("USD", "WLD", 0.135610922557368),
            ExchangeRateEntity("USD", "WLUNA", 16346.356072190325),
            ExchangeRateEntity("USD", "WOO", 1.7261546773138527),
            ExchangeRateEntity("USD", "WST", 2.750797351876484),
            ExchangeRateEntity("USD", "XAF", 604.3166941849933),
            ExchangeRateEntity("USD", "XAG", 0.04196005685703083),
            ExchangeRateEntity("USD", "XAI", 0.7942578577767009),
            ExchangeRateEntity("USD", "XAU", 4.724977020871454E-4),
            ExchangeRateEntity("USD", "XAUT", 4.734521059924149E-4),
            ExchangeRateEntity("USD", "XBT", 1.4682000009231174E-5),
            ExchangeRateEntity("USD", "XCD", 2.7010880044218246),
            ExchangeRateEntity("USD", "XCH", 0.019858043704708228),
            ExchangeRateEntity("USD", "XCN", 413.21423611218916),
            ExchangeRateEntity("USD", "XDC", 21.444209146750325),
            ExchangeRateEntity("USD", "XDR", 0.7528627211369446),
            ExchangeRateEntity("USD", "XEC", 12938.922296389252),
            ExchangeRateEntity("USD", "XEM", 20.1271832567433),
            ExchangeRateEntity("USD", "XLM", 6.702061054930286),
            ExchangeRateEntity("USD", "XMON", 0.001099978507578762),
            ExchangeRateEntity("USD", "XMR", 0.006660887355916182),
            ExchangeRateEntity("USD", "XOF", 604.3166941849933),
            ExchangeRateEntity("USD", "XPD", 0.001038981912417181),
            ExchangeRateEntity("USD", "XPF", 109.93733428510855),
            ExchangeRateEntity("USD", "XPT", 0.0011143184271767091),
            ExchangeRateEntity("USD", "XRD", 22.21580418649223),
            ExchangeRateEntity("USD", "XRP", 1.5216444361967825),
            ExchangeRateEntity("USD", "XTZ", 0.7177951582012732),
            ExchangeRateEntity("USD", "XVS", 0.07757021567753884),
            ExchangeRateEntity("USD", "XYO", 99.64907337852009),
            ExchangeRateEntity("USD", "YER", 250.3575082920035),
            ExchangeRateEntity("USD", "YFI", 1.0531332470510292E-4),
            ExchangeRateEntity("USD", "YFII", 0.0014243924599954127),
            ExchangeRateEntity("USD", "ZAR", 19.05204976332495),
            ExchangeRateEntity("USD", "ZEC", 0.030452860304632987),
            ExchangeRateEntity("USD", "ZEN", 0.08240448758881079),
            ExchangeRateEntity("USD", "ZETA", 0.46963882036318516),
            ExchangeRateEntity("USD", "ZIL", 31.506044009908166),
        )

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, IvyRoomDatabase::class.java).build()

        val mapper = ExchangeRateMapper()
        repository =
            ExchangeRatesRepositoryImpl(
                exchangeRatesDao = db.exchangeRatesDao,
                writeExchangeRatesDao = db.writeExchangeRatesDao,
                mapper = mapper,
                remoteExchangeRatesDataSource = dataSource,
                dispatchersProvider = TestDispatchersProvider,
            )

        useCase = SyncExchangeRatesUseCase(repository)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun successfulNetworkResponse(): Unit =
        runBlocking {
            successfulNetworkResponseTestCase()
        }

    @Test
    fun unSuccessfulNetworkResponse(): Unit =
        runBlocking {
            unSuccessfulNetworkResponseTestCase()
        }

    private suspend fun successfulNetworkResponseTestCase() {
        closeDb()
        createDb()
        mockSuccessfulNetworkResponses()

        useCase.sync("USD")

        val flow = db.exchangeRatesDao.findAll()
        flow.take(1).collect { savedEntities ->
            savedEntities shouldBe expectedSavedEntities
        }
    }

    private suspend fun unSuccessfulNetworkResponseTestCase() {
        closeDb()
        createDb()
        mockUnSuccessfulNetworkResponses()

        useCase.sync("USD")

        val flow = db.exchangeRatesDao.findAll()
        flow.take(1).collect { savedEntities ->
            savedEntities shouldBe emptyList()
        }
    }

    private suspend fun mockSuccessfulNetworkResponses() {
        val mockNetworkResponse = mockSuccessfulNetworkResponse.right()

        coEvery { dataSource.fetchEurExchangeRates(any()) } returns mockNetworkResponse
    }

    private suspend fun mockUnSuccessfulNetworkResponses() {
        val mockNetworkResponse = "Network Failure".left()

        coEvery { dataSource.fetchEurExchangeRates(any()) } returns mockNetworkResponse
    }
}
