package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FlashcardItem
import com.example.ui.components.LeitnerCircularChart
import com.example.ui.components.LeitnerNotificationDialog
import com.example.ui.components.SmartUpgradeBanner
import com.example.ui.components.UrgencyPromoCountdownBanner
import com.example.ui.theme.*
import com.example.ui.viewmodel.ShimiTab
import com.example.ui.viewmodel.ShimiYarViewModel
import com.example.util.PersianUtils

data class LessonTabItem(
    val title: String,
    val shortTitle: String,
    val grade: String,
    val icon: String,
    val matchingCategories: List<String>,
    val relatedEducationChapterId: String? = null,
    val relatedGradeInt: Int = 10
)

@Composable
fun FlashcardScreen(
    viewModel: ShimiYarViewModel
) {
    val allCards = viewModel.flashcards
    val selectedCategory by viewModel.selectedFcCategory.collectAsState()
    val currentIndex by viewModel.currentFcIndex.collectAsState()
    val isFlipped by viewModel.isFcFlipped.collectAsState()
    val knownCount by viewModel.fcKnownCount.collectAsState()
    val learningCount by viewModel.fcLearningCount.collectAsState()
    val todayCount by viewModel.fcTodayCount.collectAsState()
    val deckFinished by viewModel.deckFinished.collectAsState()
    val isPro by viewModel.isPro.collectAsState()

    val unseenCount = (allCards.size - knownCount - learningCount).coerceAtLeast(0)
    var showChartDialog by remember { mutableStateOf(false) }
    var showLeitnerNotifDialog by remember { mutableStateOf(false) }
    var selectedGradeFilter by remember { mutableStateOf("همه") }
    var selectedContentTypeFilter by remember { mutableStateOf("همه") }
    var searchQuery by remember { mutableStateOf("") }
    var showSpeedQuiz by remember { mutableStateOf(false) }

    val allLessonTabs = remember {
        listOf(
            // دهم
            LessonTabItem("فصل ۱: کیهان زادگاه الفبای هستی", "فصل ۱ دهم", "دهم", "🌌", listOf("شیمی دهم (کیهان و اتم)", "ساختار اتم", "نور و طیف"), "g10_c1", 10),
            LessonTabItem("فصل ۲: ردپای گازها در زندگی", "فصل ۲ دهم", "دهم", "💨", listOf("شیمی دهم (هواکره و گازها)", "گازها"), "g10_c2", 10),
            LessonTabItem("فصل ۳: آب، آهنگ زندگی", "فصل ۳ دهم", "دهم", "💧", listOf("شیمی دهم (آب و محلول‌ها)", "آب و محلول"), "g10_c3", 10),

            // یازدهم
            LessonTabItem("فصل ۱: قدر هدایای زمینی را بدانیم", "فصل ۱ یازدهم", "یازدهم", "🌍", listOf("شیمی یازدهم (هدایای زمینی و نفت)", "خواص دوره‌ای"), "g11_c1", 11),
            LessonTabItem("فصل ۲: در پی غذای سالم و گرماشیمی", "فصل ۲ یازدهم", "یازدهم", "🍲", listOf("شیمی یازدهم (غذا و گرماشیمی)", "شیمی آلی"), "g11_c2", 11),
            LessonTabItem("فصل ۳: پوشاک، نیازی پایان‌ناپذیر", "فصل ۳ یازدهم", "یازدهم", "🧵", listOf("شیمی یازدهم (پلیمرها و سنتز)"), "g11_c3", 11),

            // دوازدهم
            LessonTabItem("فصل ۱: مولکول‌ها در خدمت تندرستی", "فصل ۱ دوازدهم", "دوازدهم", "🧼", listOf("شیمی دوازدهم (اسید و باز و پاک‌کننده‌ها)", "اسید و باز"), "g12_c1", 12),
            LessonTabItem("فصل ۲: آسایش و رفاه در سایه شیمی", "فصل ۲ دوازدهم", "دوازدهم", "⚡", listOf("شیمی دوازدهم (الکتروشیمی و خوردگی)", "الکتروشیمی"), "g12_c2", 12),
            LessonTabItem("فصل ۳: شیمی جلوه‌ای از هنر، زیبایی و ماندگاری", "فصل ۳ دوازدهم", "دوازدهم", "💎", listOf("شیمی دوازدهم (جامدات، مواد و آینده)"), "g12_c3", 12),
            LessonTabItem("فصل ۴: شیمی، راهی به سوی آینده روشن", "فصل ۴ دوازدهم", "دوازدهم", "🏭", listOf("شیمی دوازدهم (تعادل و سرعت و هابر)"), "g12_c4", 12),
            LessonTabItem("مرورنامه ۶۰ عبارت درست/نادرست کنکور", "درست/نادرست کنکور", "دوازدهم", "⚖️", listOf("مرورنامه دوازدهم (درست/نادرست)"), "g12_c1", 12)
        )
    }

    val visibleLessons = remember(selectedGradeFilter) {
        if (selectedGradeFilter == "همه") {
            allLessonTabs
        } else {
            allLessonTabs.filter { it.grade == selectedGradeFilter }
        }
    }

    val filteredCards = remember(allCards, selectedGradeFilter, selectedCategory, searchQuery, selectedContentTypeFilter) {
        var list = when (selectedGradeFilter) {
            "دهم" -> allCards.filter { it.category.contains("دهم") || it.category in listOf("ساختار اتم", "گازها", "آب و محلول", "نور و طیف") }
            "یازدهم" -> allCards.filter { it.category.contains("یازدهم") || it.category in listOf("شیمی آلی", "خواص دوره‌ای", "شیمی یازدهم (پلیمرها و سنتز)") }
            "دوازدهم" -> allCards.filter { it.category.contains("دوازدهم") || it.category in listOf("اسید و باز", "الکتروشیمی") }
            else -> allCards
        }

        val matchedLesson = allLessonTabs.firstOrNull { it.title == selectedCategory || it.shortTitle == selectedCategory }
        if (matchedLesson != null) {
            list = list.filter { it.category in matchedLesson.matchingCategories }
        } else if (selectedCategory != "همه" && !selectedCategory.startsWith("همه ")) {
            list = list.filter { it.category == selectedCategory }
        }

        if (selectedContentTypeFilter != "همه") {
            list = when (selectedContentTypeFilter) {
                "نکات کنکور" -> list.filter {
                    it.question.contains("نکته") || it.answer.contains("نکته") ||
                    it.question.contains("دام") || it.answer.contains("دام") ||
                    it.question.contains("کنکور") || it.answer.contains("کنکور") ||
                    it.question.contains("استثنا") || it.answer.contains("استثنا") ||
                    it.question.contains("مهم") || it.answer.contains("مهم")
                }
                "فرمول‌ها" -> list.filter {
                    it.category.contains("فرمول") || it.question.contains("فرمول") || it.answer.contains("فرمول") ||
                    it.question.contains("محاسبه") || it.answer.contains("محاسبه") ||
                    it.answer.contains("=") || it.answer.contains("pH") || it.answer.contains("STP") ||
                    it.answer.contains("ΔH") || it.answer.contains("E°")
                }
                "رنگ‌ها" -> list.filter {
                    it.category.contains("رنگ") || it.question.contains("رنگ") || it.answer.contains("رنگ") ||
                    it.question.contains("رسوب") || it.answer.contains("رسوب") ||
                    it.question.contains("شعله") || it.answer.contains("شعله")
                }
                "درست/نادرست" -> list.filter {
                    it.category.contains("درست/نادرست") || it.question.contains("آیا") || it.answer.contains("درست") || it.answer.contains("نادرست")
                }
                else -> list
            }
        }

        if (searchQuery.isNotBlank()) {
            list = list.filter { it.question.contains(searchQuery, ignoreCase = true) || it.answer.contains(searchQuery, ignoreCase = true) }
        }
        list
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("flashcard_screen")
    ) {
        // ۱. فیلتر پایه‌ها (Segmented Row)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurface1)
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            val gradeTabs = listOf(
                "همه" to "همه (۳ پایه)",
                "دهم" to "دهم",
                "یازدهم" to "یازدهم",
                "دوازدهم" to "دوازدهم"
            )
            gradeTabs.forEach { (gKey, gTitle) ->
                val isSel = selectedGradeFilter == gKey
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(
                            if (isSel) DarkSurface3 else Color.Transparent
                        )
                        .clickable {
                            selectedGradeFilter = gKey
                            viewModel.setFcCategory(if (gKey == "همه") "همه" else "همه $gKey")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = gTitle,
                        fontSize = 12.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) TextPrimary else TextSecondary
                    )
                }
            }
        }

        // ۲. نوار پیشرفت دسترسی ۲۵٪ به فلش‌کارت‌ها (فقط نسخه رایگان)
        if (!isPro) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "دسترسی ۲۵٪ نسخه رایگان به فلش‌کارت‌ها",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AppleOrange
                        )
                        Text(
                            text = "۲۵٪",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AppleOrange
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    LinearProgressIndicator(
                        progress = { 0.25f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = AppleOrange,
                        trackColor = DarkSurface3
                    )
                }
            }
        }

        // ۳. بنر ارتقا به نسخه طلایی (فقط نسخه رایگان)
        if (!isPro) {
            UrgencyPromoCountdownBanner(
                title = "👑 عضویت ویژه شیمی‌یار طلایی",
                subtitle = "دسترسی نامحدود به ۴۱۰+ فلش‌کارت درس به درس و جعبه ۵ مرحله‌ای لایتنر",
                onUpgradeClick = { viewModel.openDailyOfferModal() },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )
        }

        // بخش اسکرول‌شونده محتوای فلش‌کارت‌ها (جستجو، درس‌ها، کارت و دکمه‌ها)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {

            // ۲. نوار جستجو و دکمه آزمون سرعتی
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("جستجو در سؤال یا پاسخ فلش‌کارت‌ها...", fontSize = 11.5.sp, color = TextTertiary) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AppleBlue, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "پاک کردن", tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurface1,
                        unfocusedContainerColor = DarkSurface1,
                        focusedBorderColor = AppleBlue,
                        unfocusedBorderColor = DarkSeparator
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                )

                Surface(
                    onClick = { showSpeedQuiz = true },
                    shape = RoundedCornerShape(12.dp),
                    color = AppleOrange.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppleOrange.copy(alpha = 0.5f)),
                    modifier = Modifier.height(46.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⚡ آزمون ۱۰ تایی", color = AppleOrange, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ۳. انتخاب درس به درس (Lesson-by-Lesson Selection)
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📚 آموزش و مرور درس به درس:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Text(
                        text = "۴۱۰ کارت حفظی لایتنر",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppleBlue,
                        fontWeight = FontWeight.Bold
                    )
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val allTitle = if (selectedGradeFilter == "همه") "همه درس‌ها" else "همه $selectedGradeFilter"
                    val isAllSelected = selectedCategory == "همه" || selectedCategory.startsWith("همه ")
                    item {
                        Surface(
                            color = if (isAllSelected) AppleBlue else DarkSurface2,
                            shape = RoundedCornerShape(14.dp),
                            border = if (isAllSelected) null else androidx.compose.foundation.BorderStroke(1.dp, DarkSeparator),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { viewModel.setFcCategory(if (selectedGradeFilter == "همه") "همه" else "همه $selectedGradeFilter") }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Text(text = "📌 ", fontSize = 12.sp)
                                Text(
                                    text = allTitle,
                                    color = if (isAllSelected) Color.White else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    items(visibleLessons) { lesson ->
                        val isSelected = selectedCategory == lesson.title || selectedCategory == lesson.shortTitle
                        val countForLesson = allCards.count { it.category in lesson.matchingCategories }
                        val isSpecial = lesson.title.contains("درست/نادرست")
                        Surface(
                            color = when {
                                isSelected && isSpecial -> MetallicGold
                                isSelected -> AppleBlue
                                isSpecial -> MetallicGold.copy(alpha = 0.15f)
                                else -> DarkSurface2
                            },
                            shape = RoundedCornerShape(14.dp),
                            border = when {
                                isSelected -> null
                                isSpecial -> androidx.compose.foundation.BorderStroke(1.dp, MetallicGold.copy(alpha = 0.4f))
                                else -> androidx.compose.foundation.BorderStroke(1.dp, DarkSeparator)
                            },
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { viewModel.setFcCategory(lesson.title) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Text(text = "${lesson.icon} ", fontSize = 12.sp)
                                Text(
                                    text = "${lesson.title} (${PersianUtils.toPersianDigits(countForLesson)})",
                                    color = when {
                                        isSelected && isSpecial -> Color.Black
                                        isSelected -> Color.White
                                        isSpecial -> MetallicGold
                                        else -> TextSecondary
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected || isSpecial) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // ۴. دکمه ارجاع مستقیم به درسنامه در صورت انتخاب یک فصل
            val activeLesson = allLessonTabs.firstOrNull { it.title == selectedCategory || it.shortTitle == selectedCategory }
            if (activeLesson?.relatedEducationChapterId != null) {
                Surface(
                    color = DarkSurface2,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppleBlue.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable {
                            viewModel.navigateToEducationFromSearch(activeLesson.relatedGradeInt, activeLesson.relatedEducationChapterId)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📖", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "مطالعه درسنامه و فرمول‌های کامل این فصل در بخش آموزش",
                                    color = AppleBlue,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "شامل ۸ بخش تخصصی، تکنیک‌های تستی و حل سوالات سراسری",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Text(text = "مشاهده جزوه ➔", color = AppleBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ۵. فیلتر نوع نکته (Content Type Chips)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val filters = listOf(
                    "همه" to "📌 همه حفظیات",
                    "نکات کنکور" to "💡 نکات کنکور و دام‌ها",
                    "فرمول‌ها" to "🧠 فرمول‌ها و روابط",
                    "رنگ‌ها" to "🌈 رنگ رسوب و شعله",
                    "درست/نادرست" to "⚖️ درست / نادرست"
                )
                items(filters) { (fKey, fTitle) ->
                    val isSel = selectedContentTypeFilter == fKey
                    Surface(
                        color = if (isSel) AppleIndigo else DarkSurface1,
                        shape = RoundedCornerShape(10.dp),
                        border = if (isSel) null else androidx.compose.foundation.BorderStroke(1.dp, DarkSeparator),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedContentTypeFilter = fKey }
                    ) {
                        Text(
                            text = fTitle,
                            color = if (isSel) Color.White else TextTertiary,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Stats Row: امروز / بلد شدم / در حال یادگیری
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatChip(label = "مرور امروز", count = todayCount, color = AppleOrange, modifier = Modifier.weight(1f))
                StatChip(label = "آموخته شده", count = knownCount, color = AppleGreen, modifier = Modifier.weight(1f))
                StatChip(label = "در حال یادگیری", count = learningCount, color = AppleYellow, modifier = Modifier.weight(1f))
                StatChip(label = "کارت‌های دسته", count = filteredCards.size, color = AppleBlue, modifier = Modifier.weight(1f))
            }

            // Circular Leitner Progress Chart (Recharts-style interactive donut chart)
            LeitnerCircularChart(
                masteredCount = knownCount,
                learningCount = learningCount,
                unseenCount = unseenCount,
                totalCards = allCards.size,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                initiallyExpanded = false
            )

            // Leitner Daily Reminder Indicator Card
            val dueInfo by viewModel.dueLeitnerInfo.collectAsState()
            val notifEnabled by viewModel.leitnerNotifEnabled.collectAsState()

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (notifEnabled) MetallicCyan.copy(alpha = 0.35f) else DarkSeparator
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable { showLeitnerNotifDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = if (notifEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                            contentDescription = null,
                            tint = if (notifEnabled) MetallicCyan else TextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "یادآور روزانه لایتنر: نوبت جعبه‌های ${dueInfo.dueBoxes.joinToString(" و ")}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 12.sp
                            )
                            if (dueInfo.dueCategories.isNotEmpty()) {
                                Text(
                                    text = "دسته‌های آماده: ${dueInfo.dueCategories.take(2).joinToString("، ")}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Surface(
                        color = if (notifEnabled) MetallicCyan.copy(alpha = 0.15f) else DarkSurface2,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (notifEnabled) "تنظیمات ⚙️" else "فعال‌سازی 🔔",
                            color = if (notifEnabled) MetallicCyan else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // High-Converting Upgrade Banner for Free Users
            if (!isPro) {
                SmartUpgradeBanner(
                    title = "ماندگاری ۵ برابری مفاهیم با جعبه لایتنر نامحدود پرو",
                    subtitle = "دسترسی به تمام ۵۰۰+ فلش‌کارت طلایی دام‌های کنکور و تکنیک‌های تست‌زنی",
                    remainingTrialDays = viewModel.getRemainingTrialDays(),
                    onUpgradeClick = { viewModel.openDailyOfferModal() },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                )
            }

            if (deckFinished) {
                // Deck Finished Celebration View
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppleGreen.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🎉", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "دسته کارت‌ها با موفقیت مرور شد!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "تمامی کارت‌های بخش «$selectedCategory» را در چرخه جعبه لایتنر مرور کردی.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            // Chart showing updated mastery
                            LeitnerCircularChart(
                                masteredCount = knownCount,
                                learningCount = learningCount,
                                unseenCount = unseenCount,
                                totalCards = allCards.size,
                                initiallyExpanded = true
                            )

                            if (!isPro) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MetallicGold),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.openDailyOfferModal() }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "🎁", fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = "پیشنهاد ویژه: باز کردن تمام دسته‌ها", fontWeight = FontWeight.Bold, color = MetallicGold, fontSize = 13.sp)
                                            Text(text = "فلش‌کارت‌های دام تستی، فرمول‌های حفظی و استثناهای کنکور با اشتراک طلایی", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.restartDeck() },
                                colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text(text = "🔄 مرور مجدد همین دسته", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else if (filteredCards.isNotEmpty()) {
                val safeIndex = currentIndex.coerceIn(0, filteredCards.size - 1)
                val currentCard = filteredCards[safeIndex]
                val cardBox = viewModel.prefs.getCardBox(currentCard.id)

                // Card Counter & Leitner Box Stage
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "کارت ${PersianUtils.toPersianDigits(safeIndex + 1)} از ${PersianUtils.toPersianDigits(filteredCards.size)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextTertiary
                    )
                    Surface(
                        color = AppleIndigo.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "📦 جعبه ${PersianUtils.toPersianDigits(if (cardBox == 0) 1 else cardBox)} لایتنر",
                            color = AppleIndigo,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (!isPro && safeIndex >= 15) {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MetallicGold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "👑", fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "دسترسی به تمام ۴۱۰ فلش‌کارت درس به درس پایه‌های دهم، یازدهم و دوازدهم",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MetallicGold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "شما ۱۵ کارت رایگان این بخش را مرور کردید. برای دسترسی نامحدود به تمام حفظیات درس به درس ۳ پایه، دام‌های طراحان کنکور، اطلس رنگ‌ها، واکنش‌های حفظی و چرخه ۵ مرحله‌ای لایتنر، اشتراک طلایی را فعال کنید.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.activateFreeTrial() },
                                colors = ButtonDefaults.buttonColors(containerColor = MetallicGold),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text(text = "🎁 فعال‌سازی ۳ روز تست رایگان (بدون پرداخت)", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { viewModel.openDailyOfferModal() }) {
                                Text(text = "مشاهده طرح‌های اشتراک ویژه شیمی‌یار", color = AppleYellow, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                } else {
                    // 3D Flip Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ShimiFlipCard(
                            card = currentCard,
                            isFlipped = isFlipped,
                            cardBox = if (cardBox == 0) 1 else cardBox,
                            onCardClick = { viewModel.flipCard() }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Buttons: "🔁 نیاز به مرور (جعبه ۱)" and "✓ بلد شدم (جعبه بعدی)"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                            .padding(bottom = 30.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Button(
                            onClick = { viewModel.onCardRepeat(currentCard, filteredCards.size) },
                            colors = ButtonDefaults.buttonColors(containerColor = AppleOrange),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("repeat_card_button")
                        ) {
                            Text(text = "🔁 مرور مجدد (جعبه ۱)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }

                        Button(
                            onClick = { viewModel.onCardKnown(currentCard, filteredCards.size) },
                            colors = ButtonDefaults.buttonColors(containerColor = AppleGreen),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("known_card_button")
                        ) {
                            Text(text = "✓ بلد شدم (جعبه بعدی)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(96.dp))
        }
    }

    if (showLeitnerNotifDialog) {
        LeitnerNotificationDialog(
            viewModel = viewModel,
            onDismiss = { showLeitnerNotifDialog = false }
        )
    }

    if (showSpeedQuiz) {
        SpeedQuizModal(
            cards = filteredCards,
            onDismiss = { showSpeedQuiz = false },
            onRewardXp = { amount ->
                viewModel.addXp(amount)
                viewModel.triggerAchievement("🏆 آزمون سرعتی با موفقیت انجام شد (+$amount XP)")
            }
        )
    }
}

@Composable
fun StatChip(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface1),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
            Text(
                text = PersianUtils.toPersianDigits(count),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun ShimiFlipCard(
    card: FlashcardItem,
    isFlipped: Boolean,
    cardBox: Int = 1,
    onCardClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 350),
        label = "fcRotation"
    )

    val isTrueFalse = card.category.contains("دوازدهم") ||
            card.question.contains("آیا") ||
            card.answer.contains("[درست]") ||
            card.answer.contains("[نادرست]")

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFlipped) DarkSurface2 else DarkSurface1
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isTrueFalse) AppleYellow.copy(alpha = 0.3f) else DarkSeparator
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 14f * density
            }
            .clickable { onCardClick() }
            .testTag("flashcard_flip_card")
    ) {
        if (rotation <= 90f) {
            // Front (Question)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header tags
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = if (isTrueFalse) AppleYellow.copy(alpha = 0.15f) else AppleBlue.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = card.category,
                                color = if (isTrueFalse) AppleYellow else AppleBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            color = AppleIndigo.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "📦 جعبه ${PersianUtils.toPersianDigits(cardBox)}",
                                color = AppleIndigo,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }

                    if (isTrueFalse) {
                        Surface(
                            color = ApplePurple.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "⚖️ درست یا نادرست؟",
                                color = ApplePurple,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        Surface(
                            color = AppleGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "🔬 تست کنکور",
                                color = AppleGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Question Statement
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = PersianUtils.formatFormula(card.question),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp
                    )
                }

                // If True/False, show quick interactive guess pills
                if (isTrueFalse) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = AppleGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppleGreen.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onCardClick() }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🟢", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "درست است", color = AppleGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        Surface(
                            color = AppleRed.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppleRed.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onCardClick() }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🔴", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "نادرست است", color = AppleRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }

                Text(
                    text = "👈 برای چرخش کارت و دیدن پاسخ ضربه بزنید",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }
        } else {
            // Back (Answer - Inverted 180 degrees)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Banner based on True or False
                val isAnswerTrue = card.answer.contains("[درست]")
                val isAnswerFalse = card.answer.contains("[نادرست]")

                Surface(
                    color = when {
                        isAnswerTrue -> AppleGreen.copy(alpha = 0.2f)
                        isAnswerFalse -> AppleRed.copy(alpha = 0.2f)
                        else -> AppleBlue.copy(alpha = 0.2f)
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when {
                            isAnswerTrue -> AppleGreen
                            isAnswerFalse -> AppleRed
                            else -> AppleBlue
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = when {
                            isAnswerTrue -> "✅ این عبارت کاملاً درست است"
                            isAnswerFalse -> "❌ این عبارت نادرست است"
                            else -> "پاسخ تشریحی"
                        },
                        color = when {
                            isAnswerTrue -> AppleGreen
                            isAnswerFalse -> AppleRed
                            else -> AppleBlue
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // Explanation text
                val explanationText = card.answer
                    .replace("✅ [درست] | ", "")
                    .replace("❌ [نادرست] | ", "")
                    .replace("[درست] | ", "")
                    .replace("[نادرست] | ", "")

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 12.dp)
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = PersianUtils.formatFormula(explanationText),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp
                    )
                }

                Text(
                    text = "💡 بر اساس تسلط خود، یکی از دکمه‌های زیر را انتخاب کنید:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }
        }
    }
}

@Composable
fun SpeedQuizModal(
    cards: List<FlashcardItem>,
    onDismiss: () -> Unit,
    onRewardXp: (Int) -> Unit
) {
    val quizDeck = remember(cards) {
        cards.shuffled().take(10)
    }

    var qIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var isRevealed by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface1,
        modifier = Modifier.padding(16.dp),
        confirmButton = {},
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⚡", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (!isFinished) "آزمون سرعتی ۱۰ تایی" else "کارنامه آزمون سرعتی",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "بستن", tint = TextSecondary)
                }
            }
        },
        text = {
            if (quizDeck.isEmpty()) {
                Text(text = "کارتی برای آزمون وجود ندارد.", color = TextSecondary)
            } else if (!isFinished) {
                val current = quizDeck[qIndex.coerceIn(0, quizDeck.size - 1)]
                val isTrueFalse = current.answer.contains("[درست]") || current.answer.contains("[نادرست]") || current.question.contains("آیا")

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Progress & Counter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "سؤال ${PersianUtils.toPersianDigits(qIndex + 1)} از ۱۰",
                            style = MaterialTheme.typography.labelSmall,
                            color = AppleBlue,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "امتیاز: ${PersianUtils.toPersianDigits(score)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = AppleGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    LinearProgressIndicator(
                        progress = { (qIndex + 1) / 10f },
                        color = AppleBlue,
                        trackColor = DarkSurface3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )

                    // Question Box
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSeparator),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Surface(
                                color = AppleIndigo.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = current.category,
                                    color = AppleIndigo,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = PersianUtils.formatFormula(current.question),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                lineHeight = 24.sp
                            )
                        }
                    }

                    // Revealed Answer or Interactive Choice
                    if (!isRevealed) {
                        if (isTrueFalse) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val isCorrect = current.answer.contains("[درست]")
                                        if (isCorrect) score++
                                        isRevealed = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AppleGreen),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("🟢 درست", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = {
                                        val isCorrect = current.answer.contains("[نادرست]")
                                        if (isCorrect) score++
                                        isRevealed = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AppleRed),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("🔴 نادرست", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        score++
                                        isRevealed = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AppleGreen),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("بلدم بودم ✓", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = {
                                        isRevealed = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurface3),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("فراموش کردم", color = TextPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        // Show Answer Explanation
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = AppleBlue.copy(alpha = 0.12f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppleBlue.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "💡 پاسخ و نکته کنکوری:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AppleBlue
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = PersianUtils.formatFormula(current.answer),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary,
                                    lineHeight = 22.sp
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (qIndex < quizDeck.size - 1) {
                                    qIndex++
                                    isRevealed = false
                                } else {
                                    isFinished = true
                                    onRewardXp(25)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Text(
                                text = if (qIndex < quizDeck.size - 1) "سؤال بعدی ➔" else "مشاهده نتیجه آزمون 🎉",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                // Finished Result View
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "🏆", fontSize = 48.sp)
                    Text(
                        text = "آفرین! آزمون با موفقیت تمام شد",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "نمره شما: ${PersianUtils.toPersianDigits(score)} از ۱۰",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (score >= 7) AppleGreen else AppleOrange
                    )
                    Surface(
                        color = AppleYellow.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "🎁 جایزه: ۲۵ امتیاز علمی (XP) دریافت کردی!",
                            color = AppleYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "بازگشت به فلش‌کارت‌ها", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    )
}
