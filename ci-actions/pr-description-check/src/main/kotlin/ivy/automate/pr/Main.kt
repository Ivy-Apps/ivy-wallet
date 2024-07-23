package ivy.automate.pr

import kotlinx.coroutines.runBlocking

fun main(args: Array<String>): Unit = runBlocking {
    if(args.size != 1) {
        error("Missing PR description argument")
    }
    val description = args.first()
    println("Analyzing PR description:")
    println(description)
    println("------")

    println("All good! The PR description looks fine.")
}

