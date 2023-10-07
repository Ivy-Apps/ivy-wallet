package ivy.automate

import arrow.core.Either
import kotlinx.coroutines.runBlocking

interface GitHubService {
    fun a()
}

interface IvyService {
    fun b()
}

context(GitHubService, IvyService)
fun doSth() {

}

fun testFun(ok: Boolean): Either<String, Int> {
    return if(ok) {
        Either.Right(42)
    } else {
        Either.Left("err")
    }
}

fun main() = runBlocking {
    println("Hello, Issue assign - testtt!")
    println("Hello, Issue assign - testtt!")
    println("Hello, Issue assign - testtt!")
    println("Hello, Issue assign - testtt!")
}