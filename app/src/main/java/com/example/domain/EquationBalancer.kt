package com.example.domain

import com.example.util.PersianUtils

object EquationBalancer {

    data class BalanceResult(
        val originalEquation: String,
        val balancedEquation: String,
        val reactantCoefficients: List<Pair<Int, String>>,
        val productCoefficients: List<Pair<Int, String>>
    )

    fun balance(equation: String): Result<BalanceResult> {
        val cleaned = equation.trim()
        val delimiter = when {
            cleaned.contains("->") -> "->"
            cleaned.contains("→") -> "→"
            cleaned.contains("=") -> "="
            cleaned.contains("⇌") -> "⇌"
            else -> return Result.failure(IllegalArgumentException("علامت واکنش (مانند = یا ->) یافت نشد"))
        }

        val sides = cleaned.split(delimiter)
        if (sides.size != 2) {
            return Result.failure(IllegalArgumentException("معادله باید دارای دو طرف (واکنش‌دهنده‌ها و فراورده‌ها) باشد"))
        }

        val reactants = sides[0].split("+").map { it.trim() }.filter { it.isNotEmpty() }
        val products = sides[1].split("+").map { it.trim() }.filter { it.isNotEmpty() }

        if (reactants.isEmpty() || products.isEmpty()) {
            return Result.failure(IllegalArgumentException("واکنش‌دهنده یا فراورده نامعتبر است"))
        }

        val allSpecies = reactants + products
        val parsedSpecies = mutableListOf<Map<String, Int>>()

        try {
            for (spec in allSpecies) {
                // Strip any existing leading coefficient if user typed it
                val stripped = spec.replaceFirst(Regex("^\\d+"), "")
                val map = parseSpecies(stripped)
                if (map.isEmpty()) return Result.failure(IllegalArgumentException("ترکیب «$spec» نامعتبر است"))
                parsedSpecies.add(map)
            }
        } catch (e: Exception) {
            return Result.failure(IllegalArgumentException("خطا در پردازش فرمول‌ها: ${e.message}"))
        }

        // Collect all distinct elements
        val elements = mutableSetOf<String>()
        for (map in parsedSpecies) {
            elements.addAll(map.keys)
        }

        val numElements = elements.size
        val numSpecies = allSpecies.size
        val numReactants = reactants.size
        val numProducts = products.size

        // Build matrix: rows = elements, cols = species
        // Reactants positive, products negative: sum(coeff * count) = 0
        val elemList = elements.toList()
        val matrix = Array(numElements) { DoubleArray(numSpecies) }

        for (r in 0 until numElements) {
            val elem = elemList[r]
            for (c in 0 until numSpecies) {
                val count = parsedSpecies[c][elem] ?: 0
                val sign = if (c < numReactants) 1 else -1
                matrix[r][c] = (sign * count).toDouble()
            }
        }

        // Search for smallest positive integer coefficients (c1..ck) between 1 and 30 that satisfy all element equations
        val coeffs = solveCoefficients(matrix, numElements, numSpecies)
            ?: return Result.failure(IllegalArgumentException("موازنه‌پذیر نیست یا ترکیب‌های نامتعادل وارد شده‌اند"))

        val reactantPairs = mutableListOf<Pair<Int, String>>()
        for (i in 0 until numReactants) {
            val stripped = reactants[i].replaceFirst(Regex("^\\d+"), "")
            reactantPairs.add(Pair(coeffs[i], PersianUtils.formatFormula(stripped)))
        }

        val productPairs = mutableListOf<Pair<Int, String>>()
        for (i in 0 until numProducts) {
            val stripped = products[i].replaceFirst(Regex("^\\d+"), "")
            productPairs.add(Pair(coeffs[numReactants + i], PersianUtils.formatFormula(stripped)))
        }

        val leftStr = reactantPairs.joinToString(" + ") { (coeff, name) ->
            if (coeff == 1) name else "${PersianUtils.toPersianDigits(coeff)}$name"
        }
        val rightStr = productPairs.joinToString(" + ") { (coeff, name) ->
            if (coeff == 1) name else "${PersianUtils.toPersianDigits(coeff)}$name"
        }

        val balanced = "$leftStr  ➔  $rightStr"

        return Result.success(
            BalanceResult(
                originalEquation = cleaned,
                balancedEquation = balanced,
                reactantCoefficients = reactantPairs,
                productCoefficients = productPairs
            )
        )
    }

    private fun solveCoefficients(matrix: Array<DoubleArray>, rows: Int, cols: Int): List<Int>? {
        // Fast search with depth-first search / brute check on free variables for chemical equations (cols <= 6)
        // Check if small integer solution exists (most chemistry equations have coefficients 1..14)
        val maxCoeff = 24

        fun isValid(sol: IntArray): Boolean {
            for (r in 0 until rows) {
                var sum = 0.0
                for (c in 0 until cols) {
                    sum += matrix[r][c] * sol[c]
                }
                if (Math.abs(sum) > 1e-6) return false
            }
            return true
        }

        // For 2 reactants and 2 products (most common), or 2 & 1, 1 & 2:
        if (cols <= 4) {
            for (c1 in 1..maxCoeff) {
                for (c2 in 1..maxCoeff) {
                    if (cols == 2) {
                        val sol = intArrayOf(c1, c2)
                        if (isValid(sol)) return sol.toList()
                    } else if (cols == 3) {
                        for (c3 in 1..maxCoeff) {
                            val sol = intArrayOf(c1, c2, c3)
                            if (isValid(sol)) return sol.toList()
                        }
                    } else if (cols == 4) {
                        for (c3 in 1..maxCoeff) {
                            for (c4 in 1..maxCoeff) {
                                val sol = intArrayOf(c1, c2, c3, c4)
                                if (isValid(sol)) return sol.toList()
                            }
                        }
                    }
                }
            }
        } else if (cols == 5) {
            for (c1 in 1..12) {
                for (c2 in 1..12) {
                    for (c3 in 1..12) {
                        for (c4 in 1..12) {
                            for (c5 in 1..12) {
                                val sol = intArrayOf(c1, c2, c3, c4, c5)
                                if (isValid(sol)) return sol.toList()
                            }
                        }
                    }
                }
            }
        }

        return null
    }

    private fun parseSpecies(sub: String): Map<String, Int> {
        val stack = ArrayDeque<MutableMap<String, Int>>()
        stack.addLast(mutableMapOf())

        var i = 0
        val n = sub.length

        while (i < n) {
            val ch = sub[i]
            when {
                ch == '(' || ch == '[' -> {
                    stack.addLast(mutableMapOf())
                    i++
                }
                ch == ')' || ch == ']' -> {
                    i++
                    var count = 0
                    while (i < n && sub[i].isDigit()) {
                        count = count * 10 + (sub[i] - '0')
                        i++
                    }
                    if (count == 0) count = 1
                    val top = stack.removeLast()
                    val target = stack.last()
                    for ((elem, cnt) in top) {
                        target[elem] = (target[elem] ?: 0) + cnt * count
                    }
                }
                ch.isUpperCase() -> {
                    val elem = StringBuilder()
                    elem.append(ch)
                    i++
                    while (i < n && sub[i].isLowerCase()) {
                        elem.append(sub[i])
                        i++
                    }
                    var count = 0
                    while (i < n && sub[i].isDigit()) {
                        count = count * 10 + (sub[i] - '0')
                        i++
                    }
                    if (count == 0) count = 1
                    val symbol = elem.toString()
                    val target = stack.last()
                    target[symbol] = (target[symbol] ?: 0) + count
                }
                else -> i++
            }
        }
        return stack.last()
    }
}
