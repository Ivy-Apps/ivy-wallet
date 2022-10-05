package com.ivy.formula.domain.pure.parse

import com.ivy.common.toNonEmptyList
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class CompileFunctionTest : StringSpec({
    fun args(vararg numbers: Double) = flowOf(numbers.toList().toNonEmptyList())

    "compile just value" {
        val f = compileFunction("=$1")

        val res = f(args(20_000.0))

        res.first() shouldBe 20_000.0
    }

    "compile addition" {
        val f = compileFunction("=$1+$2")

        val res = f(args(10.0, 15.0))

        res.first() shouldBe 25.0
    }

    "compile 10% of 1,000" {
        val f = compileFunction("=$1*$2%")

        val res = f(args(1_000.0, 10.0))

        res.first() shouldBe 100.0
    }

    "compile bracketed expression" {
        val f = compileFunction("=($1+$2)/$3")

        val res = f(args(25.0, 35.0, 3.0))

        res.first() shouldBe 20.0
    }
})