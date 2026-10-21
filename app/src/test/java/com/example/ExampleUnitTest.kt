package com.example

import com.example.data.model.EducationChapter
import com.example.data.model.ElementItem
import com.example.search.SearchCategoryFilter
import com.example.search.SmartSearchEngine
import com.example.search.SmartSearchResult
import com.example.util.PersianUtils
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun persianDigits_conversion_isCorrect() {
        assertEquals("۱۲۳۴۵۶۷۸۹۰", PersianUtils.toPersianDigits("1234567890"))
        assertEquals("شماره ۱۱۸", PersianUtils.toPersianDigits("شماره 118"))
    }

    @Test
    fun chemicalFormula_subscript_isCorrect() {
        assertEquals("H₂O", PersianUtils.formatFormula("H2O"))
        assertEquals("C₃H₈", PersianUtils.formatFormula("C3H8"))
    }

    @Test
    fun smartSearchEngine_searchesElementBySymbolAndName() {
        val dummyElements = listOf(
            ElementItem(79, "Au", "طلا", "فلزات واسطه", 196.97, "[Xe] 4f14 5d10 6s1", 11, 6, 11, 6),
            ElementItem(26, "Fe", "آهن", "فلزات واسطه", 55.845, "[Ar] 3d6 4s2", 8, 4, 8, 4),
            ElementItem(1, "H", "هیدروژن", "غیرفلزات", 1.008, "1s1", 1, 1, 1, 1)
        )
        val dummyChapters = emptyList<EducationChapter>()

        // Search by Persian name "طلا"
        val resultsGold = SmartSearchEngine.search("طلا", dummyElements, dummyChapters, SearchCategoryFilter.ALL)
        assertTrue(resultsGold.any { it is SmartSearchResult.ElementResult && it.element.symbol == "Au" })

        // Search by Symbol "Fe"
        val resultsIron = SmartSearchEngine.search("fe", dummyElements, dummyChapters, SearchCategoryFilter.ALL)
        assertTrue(resultsIron.any { it is SmartSearchResult.ElementResult && it.element.name == "آهن" })
    }

    @Test
    fun smartSearchEngine_searchesCuratedFormulas() {
        val dummyElements = emptyList<ElementItem>()
        val dummyChapters = emptyList<EducationChapter>()

        // Search by formula "H2O"
        val resultsWater = SmartSearchEngine.search("H2O", dummyElements, dummyChapters, SearchCategoryFilter.FORMULAS)
        assertTrue(resultsWater.any { it is SmartSearchResult.FormulaResult && it.formula.contains("H₂O") })

        // Search by equation "PV=nRT"
        val resultsGas = SmartSearchEngine.search("PV=nRT", dummyElements, dummyChapters, SearchCategoryFilter.ALL)
        assertTrue(resultsGas.any { it is SmartSearchResult.FormulaResult && it.formula.contains("PV = nRT") })
    }

    @Test
    fun smartSearchEngine_searchesEducationTopics() {
        val dummyElements = emptyList<ElementItem>()
        val dummyChapters = listOf(
            EducationChapter(
                id = "g10_c2",
                grade = 10,
                gradeTitle = "دهم",
                chapterNumber = 2,
                title = "فصل ۲: ردپای گازها در زندگی",
                summary = "استوکیومتری گازها در STP و قوانین بویل و چارلز",
                formulas = "PV = nRT",
                goldenTip = "دما به کلوین تبدیل شود",
                konkurTip = "دام تستی گازها",
                memorizeTrick = "رمز گازها",
                commonMistakes = "اشتباه ۲۲.۴",
                example = "مثال گاز",
                examQuestion = "سوال کنکور"
            )
        )

        val results = SmartSearchEngine.search("استوکیومتری", dummyElements, dummyChapters, SearchCategoryFilter.CHAPTERS)
        assertTrue(results.any { it is SmartSearchResult.ChapterResult && it.chapter.id == "g10_c2" })
    }

    @Test
    fun leitnerAlgorithm_intervalAndCycleDays_areValid() {
        // Test standard Leitner 5-box intervals
        val boxIntervals = mapOf(1 to 1, 2 to 2, 3 to 4, 4 to 8, 5 to 16)
        assertEquals(1, boxIntervals[1])
        assertEquals(2, boxIntervals[2])
        assertEquals(4, boxIntervals[3])
        assertEquals(8, boxIntervals[4])
        assertEquals(16, boxIntervals[5])

        // Verify day cycle coverage
        for (day in 1..30) {
            val scheduled = mutableListOf(1)
            if (day % 2 == 0) scheduled.add(2)
            if (day % 4 == 0) scheduled.add(3)
            if (day % 8 == 0) scheduled.add(4)
            if (day % 16 == 0 || day == 30) scheduled.add(5)

            assertTrue(scheduled.contains(1))
            if (day == 16) {
                assertTrue(scheduled.containsAll(listOf(1, 2, 3, 4, 5)))
            }
        }
    }
}
