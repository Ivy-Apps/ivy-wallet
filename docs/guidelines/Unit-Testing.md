# Unit Testing

Unit tests are the proof that your code works. While often seen as boring and a waste of time, 
automated tests are the things that guarantee correctness under the test cases and
assumptions that you've setup.

A good unit test is short and simple. If you're test case doesn't fit 
on half the screen then it's likely bad. Also, if you can't understand what's happening
in a test at a glance, then it's bad again.

## Unit test structure

Most good unit tests share a similar structure/pattern. They start with a simple
name that reads like a sentence and tells you what's being tested. Then inside
the test functions body, they're split into three parts.

```kotlin
class CurrencyConverterTest {
    // we mock it because we're interested only in testing
    // the CurrencyConverter code paths
    private val exchangeRatesRepo = mockk<ExchangeRatesRepository>()

    private lateinit var converter: CurrencyConverter

    @Before
    fun setup() {
        // before each test always create a new instance
        // to ensure that you're testing a fresh state of the class
        converter = CurrencyConverter()
    }

    @Test
    fun `converts BTC to USD, happy path`() = runTest {
        // given
        coEvery {
            exchangeRatesRepo.findRate(BTC, USD)
        } returns PositiveDouble.unsafe(50_000.0)
        val btcHolding = value(2.0, BTC)

        // when
        val usdMoney = converter.convert(
            from = btcHolding,
            to = USD
        )

        // then
        usdMoney shouldBe value(100_000.0, USD)
    }

    // ... other tests for the unhappy paths

    private fun value(amount: Double, asset: AssetCode): Value {
        return Value(PositiveDouble.unsafe(amount), asset)
    }

    companion object {
        val USD = AssetCode.unsafe("USD")
        val BTC = AssetCode.unsafe("BTC")
    }
}
```

### Given (optional)

Here we do the required setup for our test case. This usually involves:
- mocking stuff (e.g. `every { x.something() } returns Y`).
- creating instances of data classes required for the test.
- other preparatory work that we need to set the **sut (system under test)**
to the correct state to test our **code path under test** (e.g. happy path, unhappy path, edge-case, etc).

### When

In the `// when` section we execute the code path under test or i.e. just call the function we want to test with the arguments needed.
It's also good practice to save the result of the function in a `val res = ...` that you'll verify in the `// then` section.

### Then

Lastly in the `// then` section, we verify the result of the test and make assertions. We can assert that:

- the returned result is what we expect using `res shouldBe X`
- the side effects that we expected happened (e.g. `coVerify(exactly = 1) { repo.save(item) }`)
- side effects that shouldn't happen didn't happen (`coVerify(exactly = 0) { repo.delete(any()) }`)
- other affected systems state is what we expect (e.g. `fakeUserDao.getCurrentUser shouldBe user1`)

Here you should be smart and be careful to assert only the things that you care about.
Too many assertions and the test becomes a pain in the ass to maintain.
Too few assertions and the test becomes useless.

> As a rule of thumb, test the function as if it's a black box and you don't know its implementation.
> Assert only the things that you expect from its signature and responsibility no matter the implementation
