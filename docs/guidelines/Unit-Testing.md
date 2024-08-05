# Unit Testing

Unit tests are the proof that your code works. While often seen as boring and a waste of time, 
automated tests are the things that guarantee correctness under the test cases and
assumptions that you've set up.

A good unit test is short and simple. If your test case doesn't fit 
on half the screen then, it's likely bad. Also, if you can't understand what's happening
in a test at a glance, then it's bad again.

## Unit test structure

Most good unit tests share a similar structure/pattern. They start with a simple
name that reads like a sentence and tells you what's being tested. Then inside
the test function's body, they're split into three parts: Given-When-Then.

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
        // Given
        coEvery {
            exchangeRatesRepo.findRate(BTC, USD)
        } returns PositiveDouble.unsafe(50_000.0)
        val btcHolding = value(2.0, BTC)

        // When
        val usdMoney = converter.convert(
            from = btcHolding,
            to = USD
        )

        // Then
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
- mocking stuff (e.g., `every { x.something() } returns Y`).
- creating instances of data classes required for the test.
- other preparatory work that we need to set the **sut (system under test)**
to the correct state to test our **code path under test** (e.g. happy path, unhappy path, edge case, etc.).

### When

In the `// when` section, we execute the code path under test, i.e., just call the function we want to test with the arguments needed.
It's also good practice to save the result of the function in a `val res = ...` that you'll verify in the `// then` section.

### Then

Lastly, in the `// then` section, we verify the result of the test and make assertions. We can assert that:

- The returned result is what we expect using `res shouldBe X`.
- The side effects that we expected happened (e.g., `coVerify(exactly = 1) { repo.save(item) }`).
- Side effects that shouldn't happen didn't happen (e.g., `coVerify(exactly = 0) { repo.delete(any()) }`).
- Other affected systems' state is what we expect (e.g., `fakeUserDao.getCurrentUser shouldBe user1`).

Here, you should be smart and be careful to assert only the things that you care about.
Too many assertions, and the test becomes a pain in the ass to maintain.
Too few assertions, and the test becomes useless.

> As a rule of thumb, test the function as if it's a black box, and you don't know its implementation.
> Assert only the things that you expect from its signature and responsibility without looking at the implementation.

## When to mock and when not?

That solely depends on whether you want to execute the code paths in a dependency.
It's common in practice for some code paths to be impossible to execute in JVM unit tests - for example, using real Room DB DAOs and SQLite database.
In those cases, we have two options:
- Create fake implementations (e.g., `FakeDao`, `FakeDataStore` that stores stuff in-memory only)
- Mock them using [MockK](https://mockk.io/)

There's not a single rule but I'll give you some questions to help you decide:
1. Do I want to test and execute the code path (implementation) of this dependency?
2. Will my test become simpler if I mock it?
3. Will my test become simpler if I use the real implementation?
4. Will my test become simpler if I create and use a fake implementation?

It's all about making your tests short, simple, and valuable.

## Tips for making unit tests simpler

- Extract complex `data class` and model creation to a `companion object { val something = Something(...) } ` at the bottom of the test class.
- Extract complex mocking and setup in a `private fun` at the bottom of the test class.
- To create similar `data classes` and models to the already extracted ones use `.copy()` or create those extracted objects using a `fun` that accepts default arguments.
- If the same helper `vals` and util functions are needed in different test classes, extract them in a new `object SomethingFixtures` file so they can be reused.
- If your test is still not fitting half the screen, maybe you're testing too much in a single test case. Split the test into multiple smaller tests that verify only a single portion of the original large one.
- Sometimes when the `data class` you want to create is too hard to create, you can even mock it like `val transaction = mockk<Transaction>(relaxed = true) { every { settled } returns true }` to simplify your test.
