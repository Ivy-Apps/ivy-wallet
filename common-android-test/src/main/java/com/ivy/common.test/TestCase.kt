package com.ivy.common.test

import kotlinx.coroutines.runBlocking

fun <Ctx, Result> testCase(
    context: Ctx,
    given: suspend (Ctx) -> Unit = {},
    test: suspend (Ctx) -> Result,
    verifyResult: suspend Result.(Ctx) -> Unit
): Unit = runBlocking {
    given(context)
    val res = test(context)
    res.verifyResult(context)
}