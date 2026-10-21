package com.example.search

import androidx.compose.ui.graphics.Color
import com.example.data.model.EducationChapter
import com.example.data.model.ElementItem

enum class SearchCategoryFilter(val title: String, val iconEmoji: String) {
    ALL("همه", "✨"),
    ELEMENTS("عناصر جدول", "⚛️"),
    FORMULAS("فرمول‌های شیمیایی", "🧪"),
    CHAPTERS("جزوات و مباحث", "📚")
}

sealed class SmartSearchResult {
    abstract val id: String
    abstract val title: String
    abstract val subtitle: String
    abstract val categoryName: String
    abstract val categoryBadge: String

    data class ElementResult(
        val element: ElementItem,
        val matchDescription: String
    ) : SmartSearchResult() {
        override val id: String = "elem_${element.number}"
        override val title: String = "${element.name} (${element.symbol})"
        override val subtitle: String = "عدد اتمی: ${element.number} | جرم اتمی: ${element.mass} | گروه ${element.group}، تناوب ${element.period}"
        override val categoryName: String = "عنصر جدول تناوبی"
        override val categoryBadge: String = element.category
    }

    data class FormulaResult(
        override val id: String,
        val formula: String,
        val chemicalName: String,
        val contextInfo: String,
        val relatedChapterId: String?,
        val grade: Int?,
        val relatedElementSymbols: List<String> = emptyList(),
        val formulaType: FormulaType = FormulaType.COMPOUND
    ) : SmartSearchResult() {
        override val title: String = formula
        override val subtitle: String = "$chemicalName — $contextInfo"
        override val categoryName: String = when (formulaType) {
            FormulaType.COMPOUND -> "فرمول ترکیب شیمیایی"
            FormulaType.EQUATION -> "معادله و رابطه محاسباتی"
            FormulaType.REACTION -> "واکنش شیمیایی"
        }
        override val categoryBadge: String = when (formulaType) {
            FormulaType.COMPOUND -> "ترکیب"
            FormulaType.EQUATION -> "رابطه کنکوری"
            FormulaType.REACTION -> "واکنش"
        }
    }

    data class ChapterResult(
        val chapter: EducationChapter,
        val matchedField: String,
        val matchedSnippet: String
    ) : SmartSearchResult() {
        override val id: String = "chap_${chapter.id}"
        override val title: String = chapter.title
        override val subtitle: String = "$matchedField: $matchedSnippet"
        override val categoryName: String = "جزوه پایه ${chapter.gradeTitle}"
        override val categoryBadge: String = "پایه ${chapter.gradeTitle}"
    }
}

enum class FormulaType {
    COMPOUND,
    EQUATION,
    REACTION
}
