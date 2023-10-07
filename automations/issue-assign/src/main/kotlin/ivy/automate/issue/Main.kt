package ivy.automate.issue

import arrow.core.Either
import io.ktor.client.call.body
import io.ktor.client.request.get
import ivy.automate.base.ktor.ktorClientScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

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
    return if (ok) {
        Either.Right(42)
    } else {
        Either.Left("err")
    }
}

fun main(args: Array<String>) = runBlocking {
    ktorClientScope {
        println("Args: ${args.toList()}")
    }
}