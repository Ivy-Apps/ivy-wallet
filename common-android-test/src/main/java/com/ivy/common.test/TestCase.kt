package com.ivy.common.test

import kotlinx.coroutines.runBlocking

fun <Ctx, Result> testCase(
    context: Ctx,
    given: suspend (Ctx) -> Unit = {},
    executeTest: suspend (Ctx) -> Result,
    verifyResult: suspend Result.(Ctx) -> Unit
): Unit = runBlocking {
    given(context)
    val res = executeTest(context)
    res.verifyResult(context)
}