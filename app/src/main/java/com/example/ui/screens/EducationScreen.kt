package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EducationChapter
import com.example.ui.components.SmartUpgradeBanner
import com.example.ui.components.UrgencyPromoCountdownBanner
import com.example.ui.theme.*
import com.example.ui.viewmodel.ShimiYarViewModel
import com.example.util.PersianUtils

@Composable
fun EducationScreen(
    viewModel: ShimiYarViewModel
) {
    val chapters = viewModel.educationChapters
    val selectedGrade by viewModel.selectedGrade.collectAsState()
    val isPro by viewModel.isPro.collectAsState()
    val targetChapterId by viewModel.targetEducationChapterId.collectAsState()

    val filteredChapters = remember(chapters, selectedGrade) {
        chapters.filter { it.grade == selectedGrade }
    }

    var expandedChapterId by remember { mutableStateOf<String?>("g${selectedGrade}_c1") }
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    LaunchedEffect(targetChapterId, filteredChapters) {
        if (targetChapterId != null) {
            expandedChapterId = targetChapterId
            val index = filteredChapters.indexOfFirst { it.id == targetChapterId }
            if (index != -1) {
                listState.animateScrollToItem(index + 2) // +2 for organic and search header items
            }
            viewModel.clearTargetEducationChapterId()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("education_screen")
    ) {
        // Segmented Control: دهم / یازدهم / دوازدهم
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(DarkSurface1)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val grades = listOf(
                10 to "دهم",
                11 to "یازدهم",
                12 to "دوازدهم"
            )
            grades.forEach { (g, title) ->
                val isSelected = selectedGrade == g
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) DarkSurface3 else Color.Transparent)
                        .clickable {
                            viewModel.setGrade(g)
                            expandedChapterId = "g${g}_c1"
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Unlock Progress Bar (دسترسی ۲۵٪ فقط برای نسخه رایگان)
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
                            text = "دسترسی ۲۵٪ نسخه رایگان به آموزش",
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

        // Upgrade Banner for Free Users
        if (!isPro) {
            UrgencyPromoCountdownBanner(
                title = "👑 عضویت ویژه شیمی‌یار طلایی",
                subtitle = "دسترسی کامل به تمام فرمول‌ها، نکات طلایی، تکنیک‌های مفهومی و دام‌های طراحان",
                onUpgradeClick = { viewModel.openDailyOfferModal() },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )
        }

        // Chapters Accordion List - scrollable area
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Featured Organic Chemistry Banner inside scrollable content
            item {
                Surface(
                    color = DarkSurface1,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppleYellow.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setTab(com.example.ui.viewmodel.ShimiTab.ORGANIC) }
                        .testTag("education_organic_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🧪", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "یادگیری تخصصی شیمی آلی کنکور",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AppleYellow
                                )
                                Text(
                                    text = "هیدروکربن‌ها، گروه‌های عاملی، پلیمرها، استرها و ترفندهای تستی",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Text(text = "ورود ➔", color = AppleYellow, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                    }
                }
            }

            // Quick Smart Search Banner inside scrollable content
            item {
                Surface(
                    color = DarkSurface1,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MetallicCyan.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openSmartSearch() }
                        .testTag("education_smart_search_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MetallicCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "جستجوی هوشمند در مباحث، فرمول‌ها و نکات طلایی...",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Text(text = "🔍", fontSize = 14.sp)
                    }
                }
            }

            items(filteredChapters) { ch ->
                val isExpanded = expandedChapterId == ch.id
                ChapterAccordionItem(
                    chapter = ch,
                    isExpanded = isExpanded,
                    isPro = isPro,
                    onToggle = {
                        expandedChapterId = if (isExpanded) null else ch.id
                    },
                    onLockedClick = { viewModel.onLockedContentClicked() },
                    onOpenFlashcards = {
                        val category = mapChapterToFlashcardCategory(ch)
                        viewModel.navigateToFlashcardsForLesson(ch.grade, category)
                    },
                    onOpenQuiz = {
                        viewModel.setTab(com.example.ui.viewmodel.ShimiTab.QUIZ)
                    }
                )
            }
        }
    }
}

fun mapChapterToFlashcardCategory(chapter: EducationChapter): String = when (chapter.id) {
    "g10_c1" -> "فصل ۱: کیهان زادگاه الفبای هستی"
    "g10_c2" -> "فصل ۲: ردپای گازها در زندگی"
    "g10_c3" -> "فصل ۳: آب، آهنگ زندگی"
    "g11_c1" -> "فصل ۱: قدر هدایای زمینی را بدانیم"
    "g11_c2" -> "فصل ۲: در پی غذای سالم و گرماشیمی"
    "g11_c3" -> "فصل ۳: پوشاک، نیازی پایان‌ناپذیر"
    "g12_c1" -> "فصل ۱: مولکول‌ها در خدمت تندرستی"
    "g12_c2" -> "فصل ۲: آسایش و رفاه در سایه شیمی"
    "g12_c3" -> "فصل ۳: شیمی جلوه‌ای از هنر، زیبایی و ماندگاری"
    "g12_c4" -> "فصل ۴: شیمی، راهی به سوی آینده روشن"
    else -> chapter.title
}

@Composable
fun ChapterAccordionItem(
    chapter: EducationChapter,
    isExpanded: Boolean,
    isPro: Boolean,
    onToggle: () -> Unit,
    onLockedClick: () -> Unit,
    onOpenFlashcards: () -> Unit = {},
    onOpenQuiz: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface1),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isExpanded) AppleBlue.copy(alpha = 0.5f) else DarkSeparator
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = chapter.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "شیمی پایه ${chapter.gradeTitle} • شامل ۸ بخش تخصصی",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = AppleBlue
                )
            }

            // Expandable Content with 8 Sections
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Divider(color = DarkSeparator)

                    // دکمه‌های ورود مستقیم به مرور لایتنر و شبیه‌ساز آزمون تستی این فصل
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = AppleBlue.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppleBlue.copy(alpha = 0.45f)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onOpenFlashcards() }
                                .testTag("open_flashcards_lesson_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🎴", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "فلش‌کارت‌های فصل",
                                        fontWeight = FontWeight.Bold,
                                        color = AppleBlue,
                                        fontSize = 11.5.sp
                                    )
                                    Text(
                                        text = "مرور لایتنر",
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        Surface(
                            color = AppleYellow.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppleYellow.copy(alpha = 0.45f)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onOpenQuiz() }
                                .testTag("open_quiz_lesson_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🎯", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "آزمون تستی فصل",
                                        fontWeight = FontWeight.Bold,
                                        color = AppleYellow,
                                        fontSize = 11.5.sp
                                    )
                                    Text(
                                        text = "شبیه‌ساز کنکور",
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    // 1. خلاصه درس (Free)
                    SectionBox(
                        title = "📋 خلاصه درس",
                        content = chapter.summary,
                        containerColor = DarkSurface2,
                        textColor = TextSecondary
                    )

                    // 2. فرمول‌ها (Free - green background, monospace, LTR)
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E382B)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppleGreen.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "📐 فرمول‌ها و روابط کلیدی", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = AppleGreen)
                            Spacer(modifier = Modifier.height(6.dp))
                            CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Ltr) {
                                Text(
                                    text = PersianUtils.formatFormula(chapter.formulas),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    color = Color(0xFFA5D6A7),
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }

                    // Sections 3-8: Locked for Free users with blur overlay
                    LockedContentWrapper(
                        isPro = isPro,
                        onLockedClick = onLockedClick
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // 3. نکته طلایی کنکور (yellow box)
                            SectionBox(
                                title = "💡 نکته طلایی کنکور",
                                content = chapter.goldenTip,
                                containerColor = Color(0xFF38331A),
                                textColor = Color(0xFFFFF176),
                                borderColor = AppleYellow.copy(alpha = 0.5f)
                            )

                            // 4. نکته کنکوری (red box)
                            SectionBox(
                                title = "🎯 نکته کنکوری و تستی",
                                content = chapter.konkurTip,
                                containerColor = Color(0xFF3B1E1E),
                                textColor = Color(0xFFFF8A80),
                                borderColor = AppleRed.copy(alpha = 0.5f)
                            )

                            // 5. تکنیک حفظی (purple box)
                            SectionBox(
                                title = "🧠 تکنیک حفظی و کدگذاری",
                                content = chapter.memorizeTrick,
                                containerColor = Color(0xFF2E1E38),
                                textColor = Color(0xFFCE93D8),
                                borderColor = ApplePurple.copy(alpha = 0.5f)
                            )

                            // 6. اشتباهات رایج (orange box)
                            SectionBox(
                                title = "⚠️ اشتباهات رایج داوطلبان",
                                content = chapter.commonMistakes,
                                containerColor = Color(0xFF38271A),
                                textColor = Color(0xFFFFB74D),
                                borderColor = AppleOrange.copy(alpha = 0.5f)
                            )

                            // 7. مثال‌های حل‌شده
                            SectionBox(
                                title = "📝 مثال حل‌شده تشریحی",
                                content = chapter.example,
                                containerColor = DarkSurface2,
                                textColor = TextSecondary
                            )

                            // 8. سوالات واقعی کنکور
                            SectionBox(
                                title = "🎓 سوالات واقعی کنکور و نهایی",
                                content = chapter.examQuestion,
                                containerColor = DarkSurface2,
                                textColor = TextPrimary,
                                borderColor = AppleBlue.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionBox(
    title: String,
    content: String,
    containerColor: Color,
    textColor: Color,
    borderColor: Color? = null
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = borderColor?.let { androidx.compose.foundation.BorderStroke(1.dp, it) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = textColor)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = PersianUtils.formatFormula(content), style = MaterialTheme.typography.bodySmall, color = textColor.copy(alpha = 0.9f), lineHeight = 22.sp)
        }
    }
}

@Composable
fun LockedContentWrapper(
    isPro: Boolean,
    onLockedClick: () -> Unit,
    content: @Composable () -> Unit
) {
    if (isPro) {
        content()
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLockedClick() }
        ) {
            Box(modifier = Modifier.blur(8.dp)) {
                content()
            }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(DarkBackground.copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Surface(
                        color = MetallicRose.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MetallicRose.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "🔒 نکات ترکیبی و دام‌های تستی قفل است",
                            color = MetallicRose,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onLockedClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MetallicGold),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "🔓 باز کردن نکات طلایی (تست رایگان)", fontWeight = FontWeight.ExtraBold, color = Color.Black, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
