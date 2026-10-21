package com.example.search

import com.example.data.model.EducationChapter
import com.example.data.model.ElementItem

object SmartSearchEngine {

    val curatedFormulas: List<SmartSearchResult.FormulaResult> = listOf(
        // Common Compounds & Molecules
        SmartSearchResult.FormulaResult(
            id = "f_h2o",
            formula = "H₂O (H2O)",
            chemicalName = "آب (هیدروژن اکسید)",
            contextInfo = "مولکول قطبی با زاویه ۱۰۴.۵°، پیوند هیدروژنی بین‌مولکولی و حلال جهانی",
            relatedChapterId = "g10_c3",
            grade = 10,
            relatedElementSymbols = listOf("H", "O"),
            formulaType = FormulaType.COMPOUND
        ),
        SmartSearchResult.FormulaResult(
            id = "f_nacl",
            formula = "NaCl",
            chemicalName = "سدیم کلرید (نمک خوراکی)",
            contextInfo = "ترکیب یونی با شبکه بلوری مکعبی، پیوند یونی Na⁺ و Cl⁻، الکترولیت قوی",
            relatedChapterId = "g10_c3",
            grade = 10,
            relatedElementSymbols = listOf("Na", "Cl"),
            formulaType = FormulaType.COMPOUND
        ),
        SmartSearchResult.FormulaResult(
            id = "f_co2",
            formula = "CO₂ (CO2)",
            chemicalName = "کربن دی‌اکسید",
            contextInfo = "گاز گلخانه‌ای، مولکول خطی ناقطبی با پیوند دوگانه کربن-اکسیژن",
            relatedChapterId = "g10_c2",
            grade = 10,
            relatedElementSymbols = listOf("C", "O"),
            formulaType = FormulaType.COMPOUND
        ),
        SmartSearchResult.FormulaResult(
            id = "f_ch4",
            formula = "CH₄ (CH4)",
            chemicalName = "متان (گاز طبیعی)",
            contextInfo = "ساده‌ترین هیدروکربن آلکان، ساختار چهاروجهی منتظم با زاویه ۱۰۹.۵°",
            relatedChapterId = "g11_c1",
            grade = 11,
            relatedElementSymbols = listOf("C", "H"),
            formulaType = FormulaType.COMPOUND
        ),
        SmartSearchResult.FormulaResult(
            id = "f_nh3",
            formula = "NH₃ (NH3)",
            chemicalName = "آمونیاک",
            contextInfo = "تولید به روش هابر، ساختار هرمی با پیوند هیدروژنی و رفتار بازی",
            relatedChapterId = "g10_c2",
            grade = 10,
            relatedElementSymbols = listOf("N", "H"),
            formulaType = FormulaType.COMPOUND
        ),
        SmartSearchResult.FormulaResult(
            id = "f_h2so4",
            formula = "H₂SO₄ (H2SO4)",
            chemicalName = "سولفوریک اسید (جوهر گوگرد)",
            contextInfo = "اسید قوی دپروتونه، عامل اصلی باران‌های اسیدی صنعتی و باتری خودرو",
            relatedChapterId = "g12_c1",
            grade = 12,
            relatedElementSymbols = listOf("H", "S", "O"),
            formulaType = FormulaType.COMPOUND
        ),
        SmartSearchResult.FormulaResult(
            id = "f_hcl",
            formula = "HCl",
            chemicalName = "هیدروکلریک اسید (جوهر نمک)",
            contextInfo = "اسید قوی تک‌پروتونه، اسید شیره معده و رسوب‌زدای قوی",
            relatedChapterId = "g12_c1",
            grade = 12,
            relatedElementSymbols = listOf("H", "Cl"),
            formulaType = FormulaType.COMPOUND
        ),
        SmartSearchResult.FormulaResult(
            id = "f_naoh",
            formula = "NaOH",
            chemicalName = "سدیم هیدروکسید (سود سوزآور)",
            contextInfo = "باز قوی، الکترولیت یونی و ماده اولیه تولید صابون سنتی و صنعتی",
            relatedChapterId = "g12_c1",
            grade = 12,
            relatedElementSymbols = listOf("Na", "O", "H"),
            formulaType = FormulaType.COMPOUND
        ),
        SmartSearchResult.FormulaResult(
            id = "f_caco3",
            formula = "CaCO₃ (CaCO3)",
            chemicalName = "کلسیم کربنات (سنگ آهک)",
            contextInfo = "رسوب سفید، پوسته تخم‌مرغ و رسوب لوله‌ها در اثر سختی آب",
            relatedChapterId = "g10_c3",
            grade = 10,
            relatedElementSymbols = listOf("Ca", "C", "O"),
            formulaType = FormulaType.COMPOUND
        ),
        SmartSearchResult.FormulaResult(
            id = "f_fe2o3",
            formula = "Fe₂O₃ (Fe2O3)",
            chemicalName = "آهن(III) اکسید (هماتیت / زنگ آهن)",
            contextInfo = "سنگ معدن آهن، ماده اولیه کوره بلند برای استخراج آهن و فولاد",
            relatedChapterId = "g11_c1",
            grade = 11,
            relatedElementSymbols = listOf("Fe", "O"),
            formulaType = FormulaType.COMPOUND
        ),
        SmartSearchResult.FormulaResult(
            id = "f_al2o3",
            formula = "Al₂O₃ (Al2O3)",
            chemicalName = "آلومینیم اکسید (بوکسیت / یاقوت)",
            contextInfo = "الکترولیت فرآیند هال با کریولیت Na₃AlF₆ برای تولید فلز آلومینیم",
            relatedChapterId = "g12_c2",
            grade = 12,
            relatedElementSymbols = listOf("Al", "O"),
            formulaType = FormulaType.COMPOUND
        ),
        SmartSearchResult.FormulaResult(
            id = "f_c6h12o6",
            formula = "C₆H₁₂O₆ (C6H12O6)",
            chemicalName = "گلوکز (قند خون)",
            contextInfo = "مونوساکارید، فرآورده فتوسنتز و منبع اصلی تأمین انرژی سلولی با ΔH = -2808 kJ",
            relatedChapterId = "g11_c2",
            grade = 11,
            relatedElementSymbols = listOf("C", "H", "O"),
            formulaType = FormulaType.COMPOUND
        ),
        SmartSearchResult.FormulaResult(
            id = "f_c2h5oh",
            formula = "C₂H₅OH (C2H5OH)",
            chemicalName = "اتانول (الکل سفید)",
            contextInfo = "محلول در آب با هر نسبتی به علت پیوند هیدروژنی، ضدعفونی‌کننده و سوخت زیستی",
            relatedChapterId = "g11_c3",
            grade = 11,
            relatedElementSymbols = listOf("C", "H", "O"),
            formulaType = FormulaType.COMPOUND
        ),
        SmartSearchResult.FormulaResult(
            id = "f_ch3cooh",
            formula = "CH₃COOH (سرکه)",
            chemicalName = "استیک اسید (اتانوئیک اسید)",
            contextInfo = "اسید آلی کربوکسیلیک ضعیف، ثابت یونش Ka = 1.8 × 10⁻⁵",
            relatedChapterId = "g12_c1",
            grade = 12,
            relatedElementSymbols = listOf("C", "H", "O"),
            formulaType = FormulaType.COMPOUND
        ),
        SmartSearchResult.FormulaResult(
            id = "f_kmno4",
            formula = "KMnO₄ (KMnO4)",
            chemicalName = "پتاسیم پرمنگنات",
            contextInfo = "اکسنده قوی بنفش رنگ کنکور، عدد اکسایش منگنز در آن برابر +۷ است",
            relatedChapterId = "g12_c2",
            grade = 12,
            relatedElementSymbols = listOf("K", "Mn", "O"),
            formulaType = FormulaType.COMPOUND
        ),
        SmartSearchResult.FormulaResult(
            id = "f_agcl",
            formula = "AgCl",
            chemicalName = "نقره کلرید",
            contextInfo = "رسوب سفید نامحلول در آب در حضور یون‌های Ag⁺ و Cl⁻",
            relatedChapterId = "g10_c3",
            grade = 10,
            relatedElementSymbols = listOf("Ag", "Cl"),
            formulaType = FormulaType.COMPOUND
        ),
        SmartSearchResult.FormulaResult(
            id = "f_pbi2",
            formula = "PbI₂ (PbI2)",
            chemicalName = "سرب(II) یدید",
            contextInfo = "رسوب زرد طلایی در واکنش محلول پتاسیم یدید KI و سرب(II) نیترات",
            relatedChapterId = "g10_c3",
            grade = 10,
            relatedElementSymbols = listOf("Pb", "I"),
            formulaType = FormulaType.COMPOUND
        ),

        // Core Mathematical & Chemical Equations
        SmartSearchResult.FormulaResult(
            id = "eq_pvnrt",
            formula = "PV = nRT",
            chemicalName = "معادله حالت گاز ایده‌آل",
            contextInfo = "P بر حسب atm، V بر حسب لیتر، n تعداد مول، R = ۰.۰۸۲۱ و T بر حسب کلوین (K = °C + ۲۷۳)",
            relatedChapterId = "g10_c2",
            grade = 10,
            formulaType = FormulaType.EQUATION
        ),
        SmartSearchResult.FormulaResult(
            id = "eq_m1v1",
            formula = "M₁V₁ = M₂V₂ (M1V1 = M2V2)",
            chemicalName = "فرمول طلایی رقیق‌سازی محلول‌ها",
            contextInfo = "با افزودن آب مقطر به محلول، تعداد مول ماده حل‌شونده بدون تغییر باقی می‌ماند",
            relatedChapterId = "g10_c3",
            grade = 10,
            formulaType = FormulaType.EQUATION
        ),
        SmartSearchResult.FormulaResult(
            id = "eq_ph",
            formula = "pH = -log[H⁺] و pOH = -log[OH⁻]",
            chemicalName = "فرمول محاسبه مقیاس pH",
            contextInfo = "در دمای ۲۵°C همواره pH + pOH = ۱۴ و [H⁺][OH⁻] = ۱۰⁻¹⁴ است",
            relatedChapterId = "g12_c1",
            grade = 12,
            relatedElementSymbols = listOf("H"),
            formulaType = FormulaType.EQUATION
        ),
        SmartSearchResult.FormulaResult(
            id = "eq_stp",
            formula = "حجم گاز در STP = n × ۲۲.۴ L",
            chemicalName = "حجم مولی در شرایط استاندارد STP",
            contextInfo = "دمای ۰°C (۲۷۳K) و فشار 1atm؛ حجم هر مول از هر گاز ایده‌آل دقیقاً ۲۲.۴ لیتر است",
            relatedChapterId = "g10_c2",
            grade = 10,
            formulaType = FormulaType.EQUATION
        ),
        SmartSearchResult.FormulaResult(
            id = "eq_boyle",
            formula = "P₁V₁ = P₂V₂ (قانون بویل)",
            chemicalName = "قانون بویل برای گازها",
            contextInfo = "در دمای ثابت، فشار و حجم گاز با یکدیگر رابطه عکس دارند",
            relatedChapterId = "g10_c2",
            grade = 10,
            formulaType = FormulaType.EQUATION
        ),
        SmartSearchResult.FormulaResult(
            id = "eq_charles",
            formula = "V₁/T₁ = V₂/T₂ (قانون چارلز)",
            chemicalName = "قانون چارلز برای گازها",
            contextInfo = "در فشار ثابت، حجم گاز با دمای مطلق (کلوین) رابطه مستقیم دارد",
            relatedChapterId = "g10_c2",
            grade = 10,
            formulaType = FormulaType.EQUATION
        ),
        SmartSearchResult.FormulaResult(
            id = "eq_molarity",
            formula = "M = n / V (مولاریته)",
            chemicalName = "غلظت مولی (مول بر لیتر)",
            contextInfo = "n تعداد مول حل‌شونده و V حجم کل محلول بر حسب لیتر است",
            relatedChapterId = "g10_c3",
            grade = 10,
            formulaType = FormulaType.EQUATION
        ),
        SmartSearchResult.FormulaResult(
            id = "eq_ppm",
            formula = "ppm = (جرم حل‌شونده / جرم محلول) × ۱۰⁶",
            chemicalName = "غلظت قسمت در میلیون (ppm)",
            contextInfo = "معیار غلظت محلول‌های بسیار رقیق مانند آلاینده‌های آب و هوا",
            relatedChapterId = "g10_c3",
            grade = 10,
            formulaType = FormulaType.EQUATION
        ),
        SmartSearchResult.FormulaResult(
            id = "eq_deltah",
            formula = "ΔH = ΣH(محصولات) - ΣH(واکنش‌دهنده‌ها)",
            chemicalName = "آنتالپی و گرمای واکنش (قانون هس)",
            contextInfo = "ΔH < 0 گرماده و گرما آزاد می‌شود، ΔH > 0 گرماگیر و گرما جذب می‌شود",
            relatedChapterId = "g11_c2",
            grade = 11,
            formulaType = FormulaType.EQUATION
        ),
        SmartSearchResult.FormulaResult(
            id = "eq_q_mcat",
            formula = "Q = m · c · ΔT",
            chemicalName = "فرمول گرمای مبادله شده با ظرفیت گرمایی ویژه",
            contextInfo = "m جرم بر حسب گرم، c ظرفیت گرمایی ویژه و ΔT تغییر دما بر حسب °C یا K است",
            relatedChapterId = "g11_c2",
            grade = 11,
            formulaType = FormulaType.EQUATION
        ),
        SmartSearchResult.FormulaResult(
            id = "eq_cell_potential",
            formula = "E°cell = E°کاتد - E°آند",
            chemicalName = "نیروی محرکه الکتریکی سلول گالوانی",
            contextInfo = "اگر E°cell مثبت باشد واکنش خودبه‌خودی است. کاتد قطب مثبت و آند قطب منفی در گالوانی است",
            relatedChapterId = "g12_c2",
            grade = 12,
            formulaType = FormulaType.EQUATION
        ),
        SmartSearchResult.FormulaResult(
            id = "eq_equilibrium",
            formula = "K = [محصولات]ᵖ / [واکنش‌دهنده‌ها]ʳ",
            chemicalName = "ثابت تعادل شیمیایی (K)",
            contextInfo = "تنها عامل تغییردهنده مقدار عددی ثابت تعادل K، تغییر دما است (اصل لوشاتلیه)",
            relatedChapterId = "g12_c4",
            grade = 12,
            formulaType = FormulaType.EQUATION
        ),
        SmartSearchResult.FormulaResult(
            id = "eq_speed_rate",
            formula = "R = Δn / (ضریب استوکیومتری × Δt)",
            chemicalName = "سرعت متوسط واکنش در سینتیک",
            contextInfo = "سرعت مصرف واکنش‌دهنده‌ها با علامت منفی و سرعت تولید فرآورده‌ها با علامت مثبت بیان می‌شود",
            relatedChapterId = "g11_c2",
            grade = 11,
            formulaType = FormulaType.EQUATION
        ),
        SmartSearchResult.FormulaResult(
            id = "eq_neutron",
            formula = "N = A - Z",
            chemicalName = "محاسبه تعداد نوترون‌های هسته اتم",
            contextInfo = "A عدد جرمی (پروتون + نوترون) و Z عدد اتمی (تعداد پروتون‌ها) است",
            relatedChapterId = "g10_c1",
            grade = 10,
            formulaType = FormulaType.EQUATION
        ),
        SmartSearchResult.FormulaResult(
            id = "eq_layer_capacity",
            formula = "گنجایش لایه الکترونی = 2n²",
            chemicalName = "حداکثر گنجایش الکترون در لایه اصلی n",
            contextInfo = "لایه ۱ (n=1) دارای ۲ الکترون، لایه ۲ دارای ۸ الکترون، لایه ۳ دارای ۱۸ الکترون و لایه ۴ دارای ۳۲ الکترون است",
            relatedChapterId = "g10_c1",
            grade = 10,
            formulaType = FormulaType.EQUATION
        )
    )

    val popularSuggestions = listOf(
        "طلا (Au)",
        "آهن (Fe)",
        "H2O",
        "NaCl",
        "PV=nRT",
        "اسید و باز (pH)",
        "استوکیومتری",
        "آرایش الکترونی",
        "الکتروشیمی",
        "پیوند هیدروژنی",
        "قاعده آفبا",
        "سینتیک و سرعت",
        "تعادل شیمیایی",
        "گازها و STP",
        "شیمی آلی و آلکان‌ها"
    )

    fun search(
        query: String,
        elements: List<ElementItem>,
        chapters: List<EducationChapter>,
        filter: SearchCategoryFilter = SearchCategoryFilter.ALL
    ): List<SmartSearchResult> {
        val raw = query.trim()
        if (raw.isBlank()) return emptyList()

        val normalizedQuery = normalize(raw)
        val tokens = normalizedQuery.split(" ").filter { it.isNotBlank() }

        val results = mutableListOf<SmartSearchResult>()

        // 1. Elements Search
        if (filter == SearchCategoryFilter.ALL || filter == SearchCategoryFilter.ELEMENTS) {
            elements.forEach { elem ->
                val nameNorm = normalize(elem.name)
                val symbolNorm = normalize(elem.symbol)
                val numberStr = elem.number.toString()
                val massStr = elem.mass.toString()
                val catNorm = normalize(elem.category)
                val configNorm = normalize(elem.electron_config)

                val isExactSymbol = symbolNorm.equals(normalizedQuery, ignoreCase = true)
                val isExactName = nameNorm.equals(normalizedQuery, ignoreCase = true)
                val isExactNumber = numberStr == normalizedQuery

                val matches = isExactSymbol || isExactName || isExactNumber ||
                        nameNorm.contains(normalizedQuery, ignoreCase = true) ||
                        symbolNorm.contains(normalizedQuery, ignoreCase = true) ||
                        numberStr.contains(normalizedQuery) ||
                        catNorm.contains(normalizedQuery, ignoreCase = true) ||
                        tokens.all { t ->
                            nameNorm.contains(t, ignoreCase = true) ||
                            symbolNorm.contains(t, ignoreCase = true) ||
                            catNorm.contains(t, ignoreCase = true) ||
                            numberStr.contains(t)
                        }

                if (matches) {
                    val reason = when {
                        isExactSymbol -> "تطابق دقیق نماد شیمیایی (${elem.symbol})"
                        isExactName -> "تطابق نام فارسی عنصر (${elem.name})"
                        isExactNumber -> "تطابق عدد اتمی Z = ${elem.number}"
                        symbolNorm.contains(normalizedQuery, ignoreCase = true) -> "تطابق با نماد شیمیایی"
                        catNorm.contains(normalizedQuery, ignoreCase = true) -> "تطابق با دسته ${elem.category}"
                        else -> "یافت‌شده در ویژگی‌های عنصر"
                    }
                    results.add(SmartSearchResult.ElementResult(elem, reason))
                }
            }
        }

        // 2. Formulas Search
        if (filter == SearchCategoryFilter.ALL || filter == SearchCategoryFilter.FORMULAS) {
            curatedFormulas.forEach { f ->
                val fFormulaNorm = normalize(f.formula)
                val fFormulaNoSpaces = fFormulaNorm.replace(" ", "").replace("=", "")
                val qNoSpaces = normalizedQuery.replace(" ", "").replace("=", "")
                val fNameNorm = normalize(f.chemicalName)
                val fContextNorm = normalize(f.contextInfo)
                val fSymbols = f.relatedElementSymbols.map { normalize(it) }

                val exactFormula = fFormulaNorm.contains(normalizedQuery, ignoreCase = true) ||
                        (qNoSpaces.length >= 2 && fFormulaNoSpaces.contains(qNoSpaces, ignoreCase = true))
                val exactName = fNameNorm.contains(normalizedQuery, ignoreCase = true)
                val contextMatch = fContextNorm.contains(normalizedQuery, ignoreCase = true)
                val symbolMatch = fSymbols.any { it.equals(normalizedQuery, ignoreCase = true) }
                val tokenMatch = tokens.all { t ->
                    fFormulaNorm.contains(t, ignoreCase = true) ||
                    fFormulaNoSpaces.contains(t.replace(" ", "").replace("=", ""), ignoreCase = true) ||
                    fNameNorm.contains(t, ignoreCase = true) ||
                    fContextNorm.contains(t, ignoreCase = true)
                }

                if (exactFormula || exactName || contextMatch || symbolMatch || tokenMatch) {
                    results.add(f)
                }
            }
        }

        // 3. Education Chapters Search
        if (filter == SearchCategoryFilter.ALL || filter == SearchCategoryFilter.CHAPTERS) {
            chapters.forEach { ch ->
                val titleNorm = normalize(ch.title)
                val summaryNorm = normalize(ch.summary)
                val formulasNorm = normalize(ch.formulas)
                val goldenTipNorm = normalize(ch.goldenTip)
                val konkurTipNorm = normalize(ch.konkurTip)
                val trickNorm = normalize(ch.memorizeTrick)
                val mistakesNorm = normalize(ch.commonMistakes)
                val exampleNorm = normalize(ch.example)
                val examNorm = normalize(ch.examQuestion)

                var matchedField = ""
                var matchedSnippet = ""

                when {
                    titleNorm.contains(normalizedQuery, ignoreCase = true) -> {
                        matchedField = "عنوان درسنامه"
                        matchedSnippet = extractSnippet(ch.title, normalizedQuery)
                    }
                    formulasNorm.contains(normalizedQuery, ignoreCase = true) -> {
                        matchedField = "فرمول‌های کلیدی فصل"
                        matchedSnippet = extractSnippet(ch.formulas, normalizedQuery)
                    }
                    goldenTipNorm.contains(normalizedQuery, ignoreCase = true) -> {
                        matchedField = "نکته طلایی کنکور"
                        matchedSnippet = extractSnippet(ch.goldenTip, normalizedQuery)
                    }
                    konkurTipNorm.contains(normalizedQuery, ignoreCase = true) -> {
                        matchedField = "تحلیل تستی و تله طراحان"
                        matchedSnippet = extractSnippet(ch.konkurTip, normalizedQuery)
                    }
                    summaryNorm.contains(normalizedQuery, ignoreCase = true) -> {
                        matchedField = "خلاصه مباحث درس"
                        matchedSnippet = extractSnippet(ch.summary, normalizedQuery)
                    }
                    trickNorm.contains(normalizedQuery, ignoreCase = true) -> {
                        matchedField = "رمزگردانی و حفظیات"
                        matchedSnippet = extractSnippet(ch.memorizeTrick, normalizedQuery)
                    }
                    mistakesNorm.contains(normalizedQuery, ignoreCase = true) -> {
                        matchedField = "دام‌های شایع تستی"
                        matchedSnippet = extractSnippet(ch.commonMistakes, normalizedQuery)
                    }
                    exampleNorm.contains(normalizedQuery, ignoreCase = true) -> {
                        matchedField = "مثال حل‌شده تستی"
                        matchedSnippet = extractSnippet(ch.example, normalizedQuery)
                    }
                    examNorm.contains(normalizedQuery, ignoreCase = true) -> {
                        matchedField = "سوالات نهایی و کنکور"
                        matchedSnippet = extractSnippet(ch.examQuestion, normalizedQuery)
                    }
                    tokens.all { t ->
                        titleNorm.contains(t, ignoreCase = true) ||
                        summaryNorm.contains(t, ignoreCase = true) ||
                        formulasNorm.contains(t, ignoreCase = true) ||
                        goldenTipNorm.contains(t, ignoreCase = true)
                    } -> {
                        matchedField = "مبحث منطبق"
                        matchedSnippet = extractSnippet(ch.summary, tokens.firstOrNull() ?: normalizedQuery)
                    }
                }

                if (matchedField.isNotEmpty()) {
                    results.add(
                        SmartSearchResult.ChapterResult(
                            chapter = ch,
                            matchedField = matchedField,
                            matchedSnippet = matchedSnippet
                        )
                    )
                }
            }
        }

        // Sort results: exact element symbol/name first, then formulas, then chapters
        return results.sortedBy { item ->
            when (item) {
                is SmartSearchResult.ElementResult -> {
                    val s = normalize(item.element.symbol)
                    val n = normalize(item.element.name)
                    if (s.equals(normalizedQuery, ignoreCase = true) || n.equals(normalizedQuery, ignoreCase = true)) 0
                    else 1
                }
                is SmartSearchResult.FormulaResult -> {
                    val f = normalize(item.formula)
                    val name = normalize(item.chemicalName)
                    if (f.contains(normalizedQuery, ignoreCase = true) || name.contains(normalizedQuery, ignoreCase = true)) 2
                    else 3
                }
                is SmartSearchResult.ChapterResult -> {
                    if (normalize(item.chapter.title).contains(normalizedQuery, ignoreCase = true)) 4
                    else 5
                }
            }
        }
    }

    private fun normalize(input: String): String {
        return input
            .replace("ي", "ی")
            .replace("ك", "ک")
            .replace("ة", "ه")
            .replace("‌", "") // zero-width non-joiner
            .replace("۱", "1")
            .replace("۲", "2")
            .replace("۳", "3")
            .replace("۴", "4")
            .replace("۵", "5")
            .replace("۶", "6")
            .replace("۷", "7")
            .replace("۸", "8")
            .replace("۹", "9")
            .replace("۰", "0")
            .replace("₁", "1")
            .replace("₂", "2")
            .replace("₃", "3")
            .replace("₄", "4")
            .replace("₅", "5")
            .replace("₆", "6")
            .replace("₇", "7")
            .replace("₈", "8")
            .replace("₉", "9")
            .replace("₀", "0")
            .replace("¹", "1")
            .replace("²", "2")
            .replace("³", "3")
            .replace("⁴", "4")
            .trim()
    }

    private fun extractSnippet(text: String, query: String, maxLength: Int = 110): String {
        val clean = text.replace("\n", " ").trim()
        val index = clean.indexOf(query, ignoreCase = true)
        if (index == -1) {
            return if (clean.length > maxLength) clean.take(maxLength) + "..." else clean
        }

        val start = (index - 30).coerceAtLeast(0)
        val end = (index + query.length + 65).coerceAtMost(clean.length)
        val snippet = clean.substring(start, end).trim()
        val prefix = if (start > 0) "... " else ""
        val suffix = if (end < clean.length) " ..." else ""
        return "$prefix$snippet$suffix"
    }
}
