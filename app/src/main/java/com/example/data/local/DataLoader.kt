package com.example.data.local

import android.content.Context
import com.example.data.model.*
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

object DataLoader {

    fun loadElements(context: Context): List<ElementItem> {
        val list = mutableListOf<ElementItem>()
        try {
            val jsonStr = readAsset(context, "elements.json")
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ElementItem(
                        number = obj.getInt("number"),
                        symbol = obj.getString("symbol"),
                        name = obj.getString("name"),
                        category = obj.getString("category"),
                        mass = obj.getDouble("mass"),
                        electron_config = obj.getString("electron_config"),
                        group = obj.getInt("group"),
                        period = obj.getInt("period"),
                        x = obj.getInt("x"),
                        y = obj.getInt("y"),
                        konkur_tip = obj.optString("konkur_tip", "")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun loadElementEntities(context: Context): List<ElementEntity> {
        val items = loadElements(context)
        val detailedMap = PeriodicTableData.elements.associateBy { it.atomicNumber }
        return items.map { item ->
            val detailed = detailedMap[item.number]
            val resolvedKonkurTip = detailed?.konkurTipsAndUses?.takeIf { it.isNotBlank() }
                ?: item.konkur_tip.takeIf { it.isNotBlank() }
                ?: "عنصر شماره ${item.number} جدول تناوبی (${item.name})"
            ElementEntity(
                atomicNumber = item.number,
                symbol = item.symbol,
                persianName = item.name,
                englishName = detailed?.englishName ?: item.name,
                atomicMass = item.mass,
                category = detailed?.category?.persianTitle ?: mapCategoryToPersian(item.category),
                period = item.period,
                group = item.group,
                block = detailed?.block ?: "s",
                electronConfiguration = detailed?.electronConfiguration ?: item.electron_config,
                electronegativity = detailed?.electronegativity,
                atomicRadiusPm = detailed?.atomicRadiusPm,
                meltingPointK = detailed?.meltingPointK,
                boilingPointK = detailed?.boilingPointK,
                stateAt298K = detailed?.stateAt298K?.persianTitle ?: "جامد",
                valenceElectrons = detailed?.valenceElectrons ?: 1,
                oxidationStates = detailed?.oxidationStates ?: "",
                konkurTipsAndUses = resolvedKonkurTip,
                gridX = item.x,
                gridY = item.y
            )
        }
    }

    private fun mapCategoryToPersian(category: String): String = when (category.lowercase()) {
        "alkali" -> "فلز قلیایی"
        "alkaline" -> "فلز قلیایی خاکی"
        "transition" -> "فلز واسطه"
        "post-transition" -> "فلز پس‌واسطه"
        "metalloid" -> "شبه‌فلز"
        "nonmetal" -> "نافلز واکنش‌پذیر"
        "halogen" -> "هالوژن"
        "noble" -> "گاز نجیب"
        "lanthanide" -> "لانتانید"
        "actinide" -> "آکتینید"
        else -> "فلز"
    }

    fun loadFlashcards(context: Context): List<FlashcardItem> {
        val list = mutableListOf<FlashcardItem>()
        try {
            val jsonStr = readAsset(context, "flashcards.json")
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    FlashcardItem(
                        id = obj.getInt("id"),
                        category = obj.getString("category"),
                        question = obj.getString("question"),
                        answer = obj.getString("answer")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun loadEducation(context: Context): List<EducationChapter> {
        val list = mutableListOf<EducationChapter>()
        try {
            val jsonStr = readAsset(context, "education.json")
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    EducationChapter(
                        id = obj.getString("id"),
                        grade = obj.getInt("grade"),
                        gradeTitle = obj.getString("gradeTitle"),
                        chapterNumber = obj.getInt("chapterNumber"),
                        title = obj.getString("title"),
                        summary = obj.getString("summary"),
                        formulas = obj.getString("formulas"),
                        goldenTip = obj.getString("goldenTip"),
                        konkurTip = obj.getString("konkurTip"),
                        memorizeTrick = obj.getString("memorizeTrick"),
                        commonMistakes = obj.getString("commonMistakes"),
                        example = obj.getString("example"),
                        examQuestion = obj.getString("examQuestion")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun loadGlossary(context: Context): List<GlossaryItem> {
        val list = mutableListOf<GlossaryItem>()
        try {
            val jsonStr = readAsset(context, "glossary.json")
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    GlossaryItem(
                        id = obj.getInt("id"),
                        category = obj.getString("category"),
                        term = obj.getString("term"),
                        definition = obj.getString("definition")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    val dailyTips = listOf(
        DailyTip(1, "استثنای آرایش Cr و Cu", "عناصر کروم (۲۴Cr) و مس (۲۹Cu) به دلیل پایداری آرایش‌های نیمه‌پر و پر زیرلایه 3d به ترتیب به 3d⁵ 4s¹ و 3d¹⁰ 4s¹ ختم می‌شوند."),
        DailyTip(2, "کلید پیوند هیدروژنی FON", "پیوند هیدروژنی بین‌مولکولی فقط بین اتم H متصل به یکی از سه اتم پرقدرت فلوئور، اکسیژن یا نیتروژن ایجاد می‌شود."),
        DailyTip(3, "فرمول طلایی STP", "در شرایط استاندارد (دما ۰°C و فشار 1atm)، حجم هر مول گاز ایده‌آل دقیقاً ۲۲.۴ لیتر یا ۲۲۴۰۰ میلی‌لیتر است."),
        DailyTip(4, "نیروی بین‌مولکولی و نقطه جوش", "ترتیب قدرت نیروهای بین‌مولکولی: پیوند هیدروژنی > دوقطبی-دوقطبی > نیروهای لاندن (واندروالس)."),
        DailyTip(5, "آند و کاتد در الکتروشیمی", "یادت باشه: آنُد = اکسایش (هر دو با الف)، کاتُد = کاهش (هر دو با ک). این قاعده در تمام سلول‌ها ثابت است!"),
        DailyTip(6, "واکنش‌دهنده محدودکننده", "همیشه مقدار مول هر ماده را تقسیم بر ضریب موازنه آن کن (n/ضریب)؛ کمترین مقدار، واکنش‌دهنده محدودکننده است."),
        DailyTip(7, "محاسبه pH اسیدهای قوی", "برای اسید قوی تک‌پروتونه مانند HCl، غلظت یون [H⁺] دقیقاً برابر غلظت اولیه اسید است: pH = -log[H⁺]."),
        DailyTip(8, "اصل لوشاتلیه در تعادل گازها", "افزایش فشار، تعادل را به سمت تعداد مول‌های گازی کمتر هدایت می‌کند تا تغییر فشار وارده تعدیل شود."),
        DailyTip(9, "رنگ رسوب‌های مشهور کنکور", "رسوب AgCl سفید، AgI زرد کم‌رنگ، PbI2 زرد طلایی، Fe(OH)2 سبز و Fe(OH)3 قهوه‌ای مایل به قرمز است."),
        DailyTip(10, "رابطه رقیق‌سازی محلول‌ها", "با اضافه کردن آب مقطر به محلول، تعداد مول حل‌شونده تغییری نمی‌کند؛ پس همیشه M₁V₁ = M₂V₂ است.")
    )

    val defaultBadges = listOf(
        BadgeItem("streak_3", "مداومت ۳ روزه", "۳ روز مطالعه پیوسته شیمی", "fire", false),
        BadgeItem("streak_7", "آتشین ۷ روزه", "۷ روز زنجیره کامل مطالعه", "local_fire_department", false),
        BadgeItem("cards_25", "استاد فلش‌کارت", "مرور ۲۵ فلش‌کارت در لایتنر", "style", false),
        BadgeItem("cards_100", "حافظه فولادی", "یادگیری ۱۰۰ کارت مفهومی", "psychology", false),
        BadgeItem("periodic_explorer", "کاشف ۱۱۸ عنصر", "بررسی حداقل ۲۰ عنصر جدول", "view_module", false),
        BadgeItem("notes_master", "خلاصه‌نویس برتر", "ثبت ۵ یادداشت شخصی روی عناصر", "edit_note", false),
        BadgeItem("pro_member", "قهرمان کنکور", "فعال‌سازی اشتراک شیمی‌یار پرو", "workspace_premium", false),
        BadgeItem("top_scorer", "تراز بالای ۷۰۰۰", "کسب بیش از ۵۰۰ امتیاز XP", "military_tech", false)
    )

    val dailyMissions = listOf(
        DailyMission("m1", "مشاهده ۵ عنصر از جدول تناوبی", 20, false),
        DailyMission("m2", "مرور ۱۰ فلش‌کارت با جعبه لایتنر", 30, false),
        DailyMission("m3", "مطالعه یک فصل از بخش آموزش", 40, false),
        DailyMission("m4", "مطالعه نکته طلایی روز", 10, false)
    )

    private fun readAsset(context: Context, fileName: String): String {
        val stream = context.assets.open(fileName)
        val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
        val sb = StringBuilder()
        var line: String? = reader.readLine()
        while (line != null) {
            sb.append(line)
            line = reader.readLine()
        }
        reader.close()
        return sb.toString()
    }
}
