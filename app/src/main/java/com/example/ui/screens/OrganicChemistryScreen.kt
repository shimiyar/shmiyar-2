package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.UrgencyPromoCountdownBanner
import com.example.ui.theme.*
import com.example.ui.viewmodel.ShimiYarViewModel
import com.example.util.PersianUtils

/**
 * صفحه جامع آموزش شیمی آلی کنکور سراسری
 * شامل درسنامه‌ها، اطلس گروه‌های عاملی، فرمول‌های سرعتی و کوئیز تستی
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganicChemistryScreen(
    viewModel: ShimiYarViewModel
) {
    val isPro by viewModel.isPro.collectAsState()
    var selectedSection by remember { mutableIntStateOf(0) } // 0: درسنامه, 1: گروه‌های عاملی, 2: فرمول‌های سرعتی, 3: آزمون تستی
    var searchQuery by remember { mutableStateOf("") }

    val chapters = OrganicChemistryData.chapters
    val functionalGroups = OrganicChemistryData.functionalGroups
    val techniques = OrganicChemistryData.techniques
    val quizQuestions = OrganicChemistryData.quizQuestions

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("organic_chemistry_screen")
    ) {
        // ۱. ردیف ۴ تب اصلی بخش‌های شیمی آلی (کامپکت و مرتب)
        val sectionTabs = listOf(
            0 to "📖 درسنامه جامع",
            1 to "⚗️ گروه‌های عاملی",
            2 to "⚡ فرمول‌های سرعتی",
            3 to "🎯 آزمون تستی"
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(sectionTabs) { (idx, title) ->
                val isSelected = selectedSection == idx
                Surface(
                    onClick = { selectedSection = idx },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) AppleBlue else DarkSurface2,
                    border = BorderStroke(1.dp, if (isSelected) AppleBlue else DarkSeparator)
                ) {
                    Text(
                        text = title,
                        fontSize = 11.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else TextSecondary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }

        // ۲. نوار پیشرفت دسترسی ۲۵٪ به شیمی آلی (فقط نسخه رایگان)
        if (!isPro) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "دسترسی ۲۵٪ نسخه رایگان به شیمی آلی",
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

        // ۳. بنر ارتقا به عضویت طلایی
        if (!isPro) {
            UrgencyPromoCountdownBanner(
                title = "👑 عضویت ویژه شیمی‌یار طلایی",
                subtitle = "دسترسی نامحدود به ساختارها، گروه‌های عاملی، استرها و آزمون تستی",
                onUpgradeClick = { viewModel.openDailyOfferModal() },
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
            )
        }

        // بدنه اصلی محتوای انتخابی
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (selectedSection) {
                0 -> OrganicChaptersSection(
                    chapters = chapters,
                    searchQuery = searchQuery
                )
                1 -> FunctionalGroupsSection(
                    groups = functionalGroups,
                    searchQuery = searchQuery
                )
                2 -> ShortcutTechniquesSection(
                    techniques = techniques,
                    searchQuery = searchQuery
                )
                3 -> OrganicQuizSection(questions = quizQuestions)
            }
        }
    }
}

/**
 * بخش درسنامه‌های جامع شیمی آلی
 */
@Composable
fun OrganicChaptersSection(
    chapters: List<OrganicChapter>,
    searchQuery: String
) {
    var expandedChapterId by remember { mutableStateOf<String?>("org_c1") }

    val filteredChapters = remember(chapters, searchQuery) {
        if (searchQuery.isBlank()) chapters else {
            chapters.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.subtitle.contains(searchQuery, ignoreCase = true) ||
                it.fullContent.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(filteredChapters) { ch ->
            val isExpanded = expandedChapterId == ch.id

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isExpanded) MetallicCyan.copy(alpha = 0.5f) else DarkSeparator
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // هدر فصل
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedChapterId = if (isExpanded) null else ch.id }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = ch.icon, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = MetallicCyan.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "فصل ${PersianUtils.toPersianDigits(ch.number)}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MetallicCyan,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = ch.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = ch.subtitle,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    maxLines = 1
                                )
                            }
                        }

                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = if (isExpanded) MetallicCyan else TextTertiary
                        )
                    }

                    // محتوای باز شده
                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp)
                                .padding(bottom = 14.dp)
                        ) {
                            Divider(color = DarkSeparator, modifier = Modifier.padding(bottom = 10.dp))

                            // نکات کلیدی و اصلی
                            Text(
                                text = "📌 نکات کلیدی این فصل:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppleYellow
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            ch.keyPoints.forEach { pt ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(text = "•", color = MetallicCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = pt, fontSize = 12.sp, color = TextPrimary, lineHeight = 18.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // متن کامل درسنامه
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DarkSurface2,
                                border = BorderStroke(1.dp, DarkSeparator),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = ch.fullContent,
                                        fontSize = 12.sp,
                                        color = TextPrimary,
                                        lineHeight = 20.sp
                                    )
                                }
                            }

                            // جعبه نکات طلایی کنکور
                            if (ch.konkurTips.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = AppleYellow.copy(alpha = 0.1f),
                                    border = BorderStroke(1.dp, AppleYellow.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = "💡", fontSize = 14.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "نکات طلایی کنکور سراسری:",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AppleYellow
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        ch.konkurTips.forEach { tip ->
                                            Text(
                                                text = "⚡ $tip",
                                                fontSize = 11.sp,
                                                color = TextPrimary,
                                                modifier = Modifier.padding(vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // جعبه تله‌های تستی
                            if (ch.commonTraps.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = AppleRed.copy(alpha = 0.1f),
                                    border = BorderStroke(1.dp, AppleRed.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = "⚠️", fontSize = 14.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "دام‌های تستی طراحان کنکور:",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AppleRed
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        ch.commonTraps.forEach { trap ->
                                            Text(
                                                text = "❌ $trap",
                                                fontSize = 11.sp,
                                                color = TextPrimary,
                                                modifier = Modifier.padding(vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * بخش اطلس تعاملی گروه‌های عاملی
 */
@Composable
fun FunctionalGroupsSection(
    groups: List<FunctionalGroupInfo>,
    searchQuery: String
) {
    val filteredGroups = remember(groups, searchQuery) {
        if (searchQuery.isBlank()) groups else {
            groups.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.englishName.contains(searchQuery, ignoreCase = true) ||
                it.representativeExample.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(filteredGroups) { fg ->
            val color = Color(fg.colorHex)

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // ردیف عنوان گروه عاملی
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = fg.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "(${fg.englishName})",
                                fontSize = 11.sp,
                                color = TextTertiary
                            )
                        }

                        Surface(
                            color = color.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = fg.generalFormula,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = color,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // فرمول ساختاری و پسوند
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkSurface2,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(text = "ساختار پیوندی", fontSize = 10.sp, color = TextTertiary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = fg.structuralFormula, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkSurface2,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(text = "نام‌گذاری آیوپاک", fontSize = 10.sp, color = TextTertiary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = fg.suffixOrPrefix, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // مثال و منبع طبیعی/کاربرد
                    Text(
                        text = "🔬 مثال و کاربرد: ${fg.representativeExample}",
                        fontSize = 11.5.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "🌿 منشأ یا عطر: ${fg.naturalSourceOrUse}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "💧 انحلال‌پذیری: ${fg.polarityAndSolubility}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = color.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "⭐ نکته کلیدی: ${fg.keyProperties}",
                            fontSize = 11.sp,
                            color = color,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * بخش فرمول‌های سرعتی و تکنیک‌های حل تست کنکور
 */
@Composable
fun ShortcutTechniquesSection(
    techniques: List<OrganicShortcutTechnique>,
    searchQuery: String
) {
    val filteredTechniques = remember(techniques, searchQuery) {
        if (searchQuery.isBlank()) techniques else {
            techniques.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.explanation.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(filteredTechniques) { tech ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(1.dp, AppleOrange.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = tech.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AppleOrange
                        )
                        Text(text = "⚡ فرمول کنکوری", fontSize = 10.sp, color = AppleOrange)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // کادر فرمول
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DarkSurface2,
                        border = BorderStroke(1.dp, AppleOrange.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = tech.formulaOrRule,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MetallicCyan,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = tech.explanation, fontSize = 11.5.sp, color = TextPrimary, lineHeight = 18.sp)

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AppleGreen.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(text = "📝 مثال عینی:", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = AppleGreen)
                            Text(text = tech.example, fontSize = 11.sp, color = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "🎯 کاربرد در کنکور: ${tech.konkurApplication}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppleYellow
                    )
                }
            }
        }
    }
}

/**
 * بخش کوئیز و آزمون تستی هوشمند شیمی آلی
 */
@Composable
fun OrganicQuizSection(
    questions: List<OrganicQuizQuestion>
) {
    var selectedAnswers by remember { mutableStateOf(mapOf<Int, Int>()) }
    var score by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // نوار وضعیت امتیاز
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(1.dp, MetallicCyan.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "آزمون ارزیابی شیمی آلی کنکور",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "پاسخ صحیح به تست‌ها با بازخورد آنی تشریحی",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    val answeredCount = selectedAnswers.size
                    Surface(
                        color = AppleGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "$answeredCount از ${questions.size} پاسخ داده شده",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppleGreen,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        items(questions) { q ->
            val selectedOption = selectedAnswers[q.id]
            val isAnswered = selectedOption != null

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(1.dp, DarkSeparator),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "سوال ${PersianUtils.toPersianDigits(q.id)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MetallicCyan
                        )
                        Text(text = q.source, fontSize = 10.sp, color = TextTertiary)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = q.question,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // گزینه‌ها
                    q.options.forEachIndexed { optIdx, optText ->
                        val isThisSelected = selectedOption == optIdx
                        val isCorrect = optIdx == q.correctIndex

                        val containerColor = when {
                            !isAnswered -> DarkSurface2
                            isThisSelected && isCorrect -> AppleGreen.copy(alpha = 0.25f)
                            isThisSelected && !isCorrect -> AppleRed.copy(alpha = 0.25f)
                            isCorrect -> AppleGreen.copy(alpha = 0.15f)
                            else -> DarkSurface2
                        }

                        val borderColor = when {
                            !isAnswered -> DarkSeparator
                            isThisSelected && isCorrect -> AppleGreen
                            isThisSelected && !isCorrect -> AppleRed
                            isCorrect -> AppleGreen.copy(alpha = 0.6f)
                            else -> DarkSeparator
                        }

                        Surface(
                            onClick = {
                                if (!isAnswered) {
                                    val newMap = selectedAnswers.toMutableMap()
                                    newMap[q.id] = optIdx
                                    selectedAnswers = newMap
                                    if (optIdx == q.correctIndex) {
                                        score++
                                    }
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = containerColor,
                            border = BorderStroke(1.dp, borderColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${PersianUtils.toPersianDigits(optIdx + 1)})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isThisSelected) TextPrimary else TextSecondary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = optText,
                                    fontSize = 12.sp,
                                    color = TextPrimary,
                                    modifier = Modifier.weight(1f)
                                )

                                if (isAnswered) {
                                    if (isCorrect) {
                                        Text(text = "✓", color = AppleGreen, fontWeight = FontWeight.Bold)
                                    } else if (isThisSelected) {
                                        Text(text = "✕", color = AppleRed, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // پاسخ تشریحی پس از پاسخ دادن
                    if (isAnswered) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedOption == q.correctIndex) AppleGreen.copy(alpha = 0.1f) else AppleYellow.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, if (selectedOption == q.correctIndex) AppleGreen.copy(alpha = 0.35f) else AppleYellow.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (selectedOption == q.correctIndex) "✅ پاسخ شما صحیح است!" else "💡 پاسخ تشریحی و نکته تستی:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedOption == q.correctIndex) AppleGreen else AppleYellow
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = q.explanation,
                                    fontSize = 11.5.sp,
                                    color = TextPrimary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
