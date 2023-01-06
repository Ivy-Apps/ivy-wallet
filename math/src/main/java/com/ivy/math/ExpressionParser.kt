package com.ivy.math

import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf
import com.ivy.parser.*
import com.ivy.parser.common.number

sealed interface TreeNode {
    fun print(): String
    fun eval(): Double
}

class Add(private val things: NonEmptyList<TreeNode>) : TreeNode {
    override fun print(): String = things.map { "(${it.print()})" }
        .joinToString(separator = "+")

    override fun eval(): Double = things.map { it.eval() }.sum()
}

class Multiply(private val left: TreeNode, private val right: TreeNode) : TreeNode {
    override fun print(): String = "(${left.print()}*${right.print()}.)"

    override fun eval(): Double = left.eval() * right.eval()
}

class Divide(private val left: TreeNode, private val right: TreeNode) : TreeNode {
    override fun print(): String = "(${left.print()}/${right.print()}.)"

    override fun eval(): Double = left.eval() / right.eval()
}

class Percent(private val expr: TreeNode) : TreeNode {
    override fun print(): String = "(${expr.print()})%"

    override fun eval(): Double = expr.eval() / 100.0
}

class Negate(private val node: TreeNode) : TreeNode {
    override fun print(): String = "(-${node.print()})"

    override fun eval(): Double = -(node.eval())
}

class Number(private val decimal: Double) : TreeNode {
    override fun print(): String = decimal.toString()

    override fun eval(): Double = decimal
}


/**
 * Evaluates an arbitrary mathematical expression to double.
 */
fun expressionParser(): Parser<TreeNode> = expr()

private fun expr(): Parser<TreeNode> = term().apply { x ->
    oneOrMany(
        (char('+') or char('-')).apply { sign ->
            term().apply { y ->
                pure(
                    when (sign) {
                        '+' -> y
                        '-' -> Negate(y)
                        else -> error("Impossible")
                    }
                )
            }
        }
    ).apply { ys ->
        pure(Add(nonEmptyListOf(x, *ys.toTypedArray())))
    }
} or term()

private fun term(): Parser<TreeNode> = factor().apply { x ->
    char('*').apply {
        term().apply { y ->
            pure(Multiply(x, y))
        }
    }
} or factor().apply { x ->
    char('/').apply {
        term().apply { y ->
            pure(Divide(x, y))
        }
    }
} or factor()

private fun factor(): Parser<TreeNode> = number().apply { x ->
    char('%').apply {
        pure(Percent(Number(x)))
    }
} or number().apply { num ->
    pure(Number(num))
} or char('(').apply {
    expr().apply { x ->
        string(")%").apply {
            pure(Percent(x))
        }
    }
} or char('(').apply {
    expr().apply { x ->
        char(')').apply {
            pure(x)
        }
    }
} or char('-').apply {
    factor().apply { x ->
        pure(Negate(x))
    }
}