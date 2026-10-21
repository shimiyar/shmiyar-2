package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================================
// Scientific Metallic Dark Theme (طراحی ارگونومیک برای مطالعه طولانی‌مدت کنکور)
// ============================================================================

// Base Metallic Obsidian & Titanium Dark Palette
val DarkBackground = Color(0xFF0C101A)       // Deep Obsidian Navy - ضد خستگی چشم، عمق علمی
val DarkSurface1 = Color(0xFF141C2B)          // Metallic Titanium Charcoal - پس‌زمینه کارت‌ها و پانل‌ها
val DarkSurface2 = Color(0xFF1E2A3E)          // Steel Metallic Slate - کانتینر عناصر تعاملی
val DarkSurface3 = Color(0xFF283852)          // Polished Titanium - سطوح برجسته و هدرها
val DarkSeparator = Color(0xFF233249)         // Metallic Divider - حاشیه‌های ظریف متالیک
val MetallicBorder = Color(0xFF2D405D)        // Metallic Stroke - کادربندی‌های ابزارهای آزمایشگاهی

// Scientific Metallic Accent Lights
val MetallicCyan = Color(0xFF38BDF8)         // سیان علمی و کوانتومی (لیزر و آنالیز)
val MetallicBlue = Color(0xFF2563EB)         // کبالت متالیک
val MetallicEmerald = Color(0xFF10B981)      // سبز فسفری آزمایشگاهی و تسلط
val MetallicAmber = Color(0xFFF59E0B)        // کهربای متالیک و رتبه کنکور
val MetallicCopper = Color(0xFFFB923C)       // مس متالیک و حرارت واکنش
val MetallicGold = Color(0xFFFBBF24)         // طلای متالیک و اشتراک پرو
val MetallicViolet = Color(0xFFA855F7)       // بنفش کوانتومی و اوربیتال‌ها
val MetallicRose = Color(0xFFF43F5E)         // قرمز واکنش گرمازا و دام‌های تستی
val MetallicTeal = Color(0xFF2DD4BF)         // فیروزه‌ای متالیک آزمایشگاهی
val MetallicPlatinum = Color(0xFFCBD5E1)     // پلاتین متالیک

// Ergonomic Text Palette (کاهش سردرد و خیرگی ناشی از سفیدی مطلق در مطالعه شبانه)
val TextPrimary = Color(0xFFF1F5F9)          // Titanium Platinum - خوانایی ۹۵٪ ارگونومیک
val TextSecondary = Color(0xFF94A3B8)        // Metallic Slate - توضیحات و نکات علمی
val TextTertiary = Color(0xFF64748B)         // Muted Titanium - زیرنویس‌ها و پانویس‌ها

// Eye-Care Warm Study Mode (فیلتر نوری ویژه کاهش خستگی چشم در شب)
val EyeCareWarmBackground = Color(0xFF13110E)
val EyeCareWarmSurface1 = Color(0xFF1C1914)
val EyeCareWarmSurface2 = Color(0xFF29241D)
val EyeCareWarmTextPrimary = Color(0xFFF5EBE1)
val EyeCareWarmTextSecondary = Color(0xFFC7B8A5)

// Aliases for Apple palette compatibility across codebase
val AppleBlue = MetallicCyan
val AppleGreen = MetallicEmerald
val AppleRed = MetallicRose
val AppleOrange = MetallicCopper
val AppleYellow = MetallicAmber
val ApplePurple = MetallicViolet
val ApplePink = Color(0xFFF43F5E)
val AppleTeal = MetallicTeal
val AppleIndigo = Color(0xFF6366F1)
val AppleGray = MetallicPlatinum

// Periodic Table Category Colors (Metallic Scientific Spectrum)
val CatNonmetal = Color(0xFFFB923C)          // نارنجی مس
val CatAlkali = Color(0xFFF43F5E)            // قرمز واکنش‌پذیر
val CatAlkaline = Color(0xFF38BDF8)          // سیان قلیایی خاکی
val CatTransition = Color(0xFFFBBF24)        // زرد طلایی فلزات واسطه
val CatHalogen = Color(0xFFA855F7)           // بنفش هالوژن
val CatNoble = Color(0xFF10B981)             // زمرد گازهای نجیب
val CatMetalloid = Color(0xFF6366F1)         // ایندیگو شبه‌فلزات
val CatPostTransition = Color(0xFF94A3B8)    // پلاتین فلزات پس‌واسطه
val CatLanthanide = Color(0xFFF97316)        // کهربای تیره لانتانیدها
val CatActinide = Color(0xFFEC4899)          // صورتی رادیواکتیو آکتینیدها

// Category color aliases
val CatAlkaliMetal = CatAlkali
val CatAlkalineEarth = CatAlkaline
val CatTransitionMetal = CatTransition
val CatPostTransitionMetal = CatPostTransition
val CatReactiveNonmetal = CatNonmetal
val CatNobleGas = CatNoble
val CatUnknown = MetallicPlatinum

// Compatibility aliases
val TealPrimary = MetallicCyan
val OrangeAccent = MetallicCopper
val DarkTealPrimary = MetallicTeal
val TealContainer = DarkSurface2
val TealOnContainer = MetallicCyan
val OrangeContainer = DarkSurface2
val OrangeOnContainer = MetallicCopper

fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "nonmetal" -> CatNonmetal
        "alkali" -> CatAlkali
        "alkaline" -> CatAlkaline
        "transition" -> CatTransition
        "halogen" -> CatHalogen
        "noble" -> CatNoble
        "metalloid" -> CatMetalloid
        "post-transition" -> CatPostTransition
        "lanthanide" -> CatLanthanide
        "actinide" -> CatActinide
        else -> MetallicPlatinum
    }
}

fun getCategoryPersian(category: String): String {
    return when (category.lowercase()) {
        "nonmetal" -> "نافلز"
        "alkali" -> "فلز قلیایی"
        "alkaline" -> "قلیایی خاکی"
        "transition" -> "فلز واسطه"
        "halogen" -> "هالوژن"
        "noble" -> "گاز نجیب"
        "metalloid" -> "شبه‌فلز"
        "post-transition" -> "فلز پس‌واسطه"
        "lanthanide" -> "لانتانید"
        "actinide" -> "آکتینید"
        else -> "سایر"
    }
}
