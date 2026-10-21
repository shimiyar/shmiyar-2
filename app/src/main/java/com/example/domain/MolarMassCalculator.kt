package com.example.domain

import com.example.data.model.PeriodicTableData

object MolarMassCalculator {

    data class ElementComposition(
        val symbol: String,
        val persianName: String,
        val count: Int,
        val atomicMass: Double,
        val totalMass: Double,
        val percentage: Double
    )

    data class CalculationResult(
        val formula: String,
        val formattedFormula: String,
        val molarMass: Double,
        val compositions: List<ElementComposition>
    )

    private val atomicWeights: Map<String, Double> by lazy {
        val map = mutableMapOf<String, Double>()
        for (el in PeriodicTableData.elements) {
            map[el.symbol] = el.atomicMass
        }
        // Additional common elements if needed
        map.putIfAbsent("H", 1.008)
        map.putIfAbsent("C", 12.011)
        map.putIfAbsent("N", 14.007)
        map.putIfAbsent("O", 15.999)
        map.putIfAbsent("Na", 22.990)
        map.putIfAbsent("Cl", 35.45)
        map.putIfAbsent("S", 32.06)
        map.putIfAbsent("Fe", 55.845)
        map.putIfAbsent("Ca", 40.078)
        map.putIfAbsent("K", 39.098)
        map.putIfAbsent("Cu", 63.546)
        map.putIfAbsent("Al", 26.982)
        map.putIfAbsent("Mg", 24.305)
        map.putIfAbsent("P", 30.974)
        map.putIfAbsent("Br", 79.904)
        map.putIfAbsent("I", 126.90)
        map.putIfAbsent("Ba", 137.33)
        map.putIfAbsent("Ag", 107.87)
        map.putIfAbsent("Zn", 65.38)
        map.putIfAbsent("Pb", 207.2)
        map
    }

    private val persianNames: Map<String, String> by lazy {
        val map = mutableMapOf<String, String>()
        for (el in PeriodicTableData.elements) {
            map[el.symbol] = el.persianName
        }
        map
    }

    /**
     * Parses a chemical formula with optional parentheses and hydrates like CuSO4.5H2O or Fe2(SO4)3
     */
    fun calculate(input: String): Result<CalculationResult> {
        val cleaned = input.trim().replace(" ", "").replace("·", ".").replace("*", ".")
        if (cleaned.isEmpty()) {
            return Result.failure(IllegalArgumentException("فرمول شیمیایی خالی است"))
        }

        return try {
            val elementCounts = parseFormula(cleaned)
            if (elementCounts.isEmpty()) {
                return Result.failure(IllegalArgumentException("فرمول نامعتبر است"))
            }

            var totalMass = 0.0
            val compositions = mutableListOf<ElementComposition>()

            for ((symbol, count) in elementCounts) {
                val weight = atomicWeights[symbol]
                    ?: return Result.failure(IllegalArgumentException("عنصر «$symbol» در جدول تناوبی یافت نشد"))
                val elementTotal = weight * count
                totalMass += elementTotal
            }

            for ((symbol, count) in elementCounts) {
                val weight = atomicWeights[symbol] ?: 0.0
                val elementTotal = weight * count
                val percentage = if (totalMass > 0) (elementTotal / totalMass) * 100.0 else 0.0
                compositions.add(
                    ElementComposition(
                        symbol = symbol,
                        persianName = persianNames[symbol] ?: symbol,
                        count = count,
                        atomicMass = weight,
                        totalMass = elementTotal,
                        percentage = percentage
                    )
                )
            }

            // Sort by mass percentage descending
            compositions.sortByDescending { it.percentage }

            Result.success(
                CalculationResult(
                    formula = cleaned,
                    formattedFormula = com.example.util.PersianUtils.formatFormula(cleaned),
                    molarMass = totalMass,
                    compositions = compositions
                )
            )
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("خطا در پردازش فرمول: ${e.message}"))
        }
    }

    private fun parseFormula(formula: String): Map<String, Int> {
        // Handle hydrates like CuSO4.5H2O
        if (formula.contains(".")) {
            val parts = formula.split(".")
            val baseCounts = parseSubFormula(parts[0])
            val result = baseCounts.toMutableMap()

            for (i in 1 until parts.size) {
                val hydratePart = parts[i]
                var multiplier = 1
                var hydrateFormula = hydratePart

                val match = Regex("^(\\d+)(.+)").find(hydratePart)
                if (match != null) {
                    multiplier = match.groupValues[1].toInt()
                    hydrateFormula = match.groupValues[2]
                }
                val partCounts = parseSubFormula(hydrateFormula)
                for ((sym, cnt) in partCounts) {
                    result[sym] = (result[sym] ?: 0) + (cnt * multiplier)
                }
            }
            return result
        }

        return parseSubFormula(formula)
    }

    private fun parseSubFormula(sub: String): Map<String, Int> {
        val stack = ArrayDeque<MutableMap<String, Int>>()
        stack.addLast(mutableMapOf())

        var i = 0
        val n = sub.length

        while (i < n) {
            val ch = sub[i]
            when {
                ch == '(' || ch == '[' || ch == '{' -> {
                    stack.addLast(mutableMapOf())
                    i++
                }
                ch == ')' || ch == ']' || ch == '}' -> {
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
                else -> {
                    i++
                }
            }
        }

        return stack.last()
    }
}
