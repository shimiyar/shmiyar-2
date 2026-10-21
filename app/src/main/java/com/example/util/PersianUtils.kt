package com.example.util

object PersianUtils {
    private val englishDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    private val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    private val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')

    fun toEnglishDigits(input: String?): String {
        if (input == null) return ""
        val builder = java.lang.StringBuilder()
        for (ch in input) {
            val pIdx = persianDigits.indexOf(ch)
            val aIdx = arabicDigits.indexOf(ch)
            if (pIdx != -1) {
                builder.append(englishDigits[pIdx])
            } else if (aIdx != -1) {
                builder.append(englishDigits[aIdx])
            } else if (ch == '٫' || ch == ',') {
                builder.append('.')
            } else {
                builder.append(ch)
            }
        }
        return builder.toString()
    }

    fun parsePersianDouble(input: String?): Double? {
        if (input.isNullOrBlank()) return null
        val eng = toEnglishDigits(input.trim())
        return eng.toDoubleOrNull()
    }

    fun toPersianDigits(input: String?): String {
        if (input == null) return ""
        val builder = java.lang.StringBuilder()
        for (ch in input) {
            val idx = englishDigits.indexOf(ch)
            if (idx != -1) {
                builder.append(persianDigits[idx])
            } else {
                builder.append(ch)
            }
        }
        return builder.toString()
    }

    fun toPersianDigits(number: Number): String {
        return toPersianDigits(number.toString())
    }

    fun formatPriceToman(amount: Long): String {
        val formatted = String.format("%,d", amount)
        return "${toPersianDigits(formatted)} تومان"
    }

    fun toSubscript(input: String): String {
        val normal = "0123456789+-=()"
        val sub = "₀₁₂₃₄₅₆₇₈₉₊₋₌₍₎"
        val sb = StringBuilder()
        for (c in input) {
            val i = normal.indexOf(c)
            if (i >= 0) sb.append(sub[i]) else sb.append(c)
        }
        return sb.toString()
    }

    fun formatFormula(raw: String): String {
        // Replace digit sequences after letters with subscript characters
        val regex = Regex("([a-zA-Z\\)])(\\d+)")
        return regex.replace(raw) { match ->
            val prefix = match.groupValues[1]
            val digits = match.groupValues[2]
            prefix + toSubscript(digits)
        }
    }
}
