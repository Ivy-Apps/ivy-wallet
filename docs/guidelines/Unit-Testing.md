# Unit Testing

Unit tests are the proof that your code works. While often seen as boring and waste of time, 
automated tests are the thing that guarantee correctness under the test cases and
assumptions that you've setup.

A good unit test short and simple. If you're unit test doesn't fit 
on half the screen then it's likely bad. Also, if you can't understand what's happening
in a test in a glance, then it's bad again.

## Unit test structure

Most good unit tests share a similar structure/pattern. They start with a simple
name that reads like a sentence and tells you what's being tested. Then inside
the test functions body they're split into three parts.

```kotlin
class CurrencyConverterTest {
  private val exchangeRatesRepo = mockk<ExchangeRatesRepository>

  private lateinit var converter: CurrencyConverter

  @Before
  fun setup() {
    // before each test always create a new instance
    // to ensure that you're testing a fresh state of the class
    converter = CurrencyConverter()
  }

  @Test
  fun `converts BTC to USD`() = runTest {
    // given
    every { exchangeRatesRepo.findRate(from = AssetCode.

 
    // when
    // then
  }
}

}
```

### Given (optional)

### When

### Then

