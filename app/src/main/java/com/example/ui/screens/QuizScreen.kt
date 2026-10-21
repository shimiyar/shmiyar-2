package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExamQuestion
import com.example.data.model.KonkurExamData
import com.example.ui.components.UrgencyPromoCountdownBanner
import com.example.ui.theme.*
import com.example.ui.viewmodel.ShimiYarViewModel
import com.example.util.PersianUtils

@Composable
fun QuizScreen(
    viewModel: ShimiYarViewModel
) {
    val examSession by viewModel.examSession.collectAsState()
    val isPro by viewModel.isPro.collectAsState()
    val mistakeIds by viewModel.mistakeIds.collectAsState()

    if (examSession.isActive) {
        if (examSession.isFinished) {
            ExamResultView(
                viewModel = viewModel,
                isPro = isPro
            )
        } else {
            ActiveExamView(
                viewModel = viewModel,
                isPro = isPro
            )
        }
    } else {
        QuizHomeView(
            viewModel = viewModel,
            isPro = isPro,
            mistakeIds = mistakeIds
        )
    }
}

/**
 * صفحه اصلی شبیه‌ساز آزمون‌های تستی کنکور
 */
@Composable
fun QuizHomeView(
    viewModel: ShimiYarViewModel,
    isPro: Boolean,
    mistakeIds: Set<Int>
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: آزمون‌ها, 1: دفترچه خطای من

    val allQuestions = KonkurExamData.allQuestions
    val mistakeQuestions = remember(mistakeIds) {
        allQuestions.filter { it.id in mistakeIds }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("quiz_screen")
    ) {
        // ۱. سوئیچر تب: آزمون‌های کنکور / دفترچه خطای من
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurface1)
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            val tabs = listOf(
                0 to "🎯 آزمون‌های شبیه‌ساز کنکور",
                1 to "⚠️ دفترچه خطای من (${PersianUtils.toPersianDigits(mistakeQuestions.size)})"
            )
            tabs.forEach { (idx, title) ->
                val isSel = selectedTab == idx
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (isSel) DarkSurface3 else Color.Transparent)
                        .clickable { selectedTab = idx },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) TextPrimary else TextSecondary
                    )
                }
            }
        }

        // ۲. نوار پیشرفت دسترسی ۲۵٪ به آزمون‌ها (فقط نسخه رایگان)
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
                            text = "دسترسی ۲۵٪ نسخه رایگان به آزمون‌ها",
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
                subtitle = "دسترسی به پاسخنامه تشریحی، تراز دقیق کنکور و آزمون‌های جامع سراسری",
                onUpgradeClick = { viewModel.openDailyOfferModal() },
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
            )
        }

        // محتوای اسکرول‌شونده
        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    // آزمون سریع ۱۰ سوالی
                    QuizPackageCard(
                        title = "⚡ آزمون استاندارد ۱۰ سوالی شیمی کنکور",
                        subtitle = "ترکیب ۱۰ تست از پایه‌های دهم، یازدهم و دوازدهم با نمره منفی و محاسبه تراز",
                        questionCount = 10,
                        durationMinutes = 12,
                        badgeColor = AppleOrange,
                        badgeText = "پیشنهادی امروز",
                        onStart = {
                            viewModel.startExam("آزمون ۱۰ سوالی کنکور سراسری", allQuestions.take(10), 12)
                        }
                    )
                }

                item {
                    // آزمون پایه دهم
                    val g10Questions = allQuestions.filter { it.grade == 10 }
                    QuizPackageCard(
                        title = "⚛️ آزمون اختصاصی شیمی پایه دهم",
                        subtitle = "مباحث ساختار اتم، استوکیومتری، گازهای STP و محلول‌ها",
                        questionCount = g10Questions.size,
                        durationMinutes = 6,
                        badgeColor = AppleBlue,
                        badgeText = "شیمی ۱",
                        onStart = {
                            viewModel.startExam("آزمون شیمی پایه دهم", g10Questions, 6)
                        }
                    )
                }

                item {
                    // آزمون شیمی ۱۱ و ۱۲
                    val upperQuestions = allQuestions.filter { it.grade >= 11 }
                    QuizPackageCard(
                        title = "🧪 آزمون جامع شیمی پایه‌های یازدهم و دوازدهم",
                        subtitle = "گرماشیمی، سرعت، هیدروکربن‌ها، اسید و باز، الکتروشیمی و تعادل",
                        questionCount = upperQuestions.size,
                        durationMinutes = 10,
                        badgeColor = MetallicCyan,
                        badgeText = "شیمی ۲ و ۳",
                        onStart = {
                            if (isPro) {
                                viewModel.startExam("آزمون شیمی یازدهم و دوازدهم", upperQuestions, 10)
                            } else {
                                viewModel.openDailyOfferModal()
                            }
                        },
                        isLocked = !isPro
                    )
                }
            }
        } else {
            // دفترچه خطای من (Mistake Notebook)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (mistakeQuestions.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "🎉", fontSize = 42.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "دفترچه خطای شما خالی است!",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "هر تستی که در آزمون‌ها اشتباه پاسخ دهید، به طور خودکار به این بخش منتقل می‌شود تا قبل از کنکور آن را مرور کنید.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(mistakeQuestions) { q ->
                        MistakeQuestionCard(question = q)
                    }
                }
            }
        }
    }
}

@Composable
fun QuizPackageCard(
    title: String,
    subtitle: String,
    questionCount: Int,
    durationMinutes: Int,
    badgeColor: Color,
    badgeText: String,
    onStart: () -> Unit,
    isLocked: Boolean = false
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface1),
        border = BorderStroke(1.dp, if (isLocked) DarkSeparator else badgeColor.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = badgeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⏱️ ${PersianUtils.toPersianDigits(durationMinutes)} دقیقه",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "📝 ${PersianUtils.toPersianDigits(questionCount)} سوال",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.5.sp)

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(containerColor = if (isLocked) DarkSurface3 else badgeColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLocked) {
                    Text(text = "👑 باز کردن در اشتراک طلایی (پرو)", color = AppleYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                } else {
                    Text(text = "شروع آزمون ➔", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

/**
 * نمای آزمون در حال برگزاری
 */
@Composable
fun ActiveExamView(
    viewModel: ShimiYarViewModel,
    isPro: Boolean
) {
    val session by viewModel.examSession.collectAsState()
    val questions = session.questions
    val currentIndex = session.currentIndex
    val currentQ = questions.getOrNull(currentIndex) ?: return

    val remainingMin = session.remainingSeconds / 60
    val remainingSec = session.remainingSeconds % 60
    val isBookmarked = session.bookmarkedIds.contains(currentQ.id)
    val userSelectedOption = session.answers[currentQ.id]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(14.dp)
    ) {
        // نوار هدر آزمون: دکمه خروج، تایمر و شماره سوال
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.closeExam() }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "بستن آزمون", tint = TextSecondary)
            }

            // تایمر معکوس
            Surface(
                color = if (session.remainingSeconds < 120) AppleRed.copy(alpha = 0.2f) else DarkSurface2,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, if (session.remainingSeconds < 120) AppleRed else DarkSeparator)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "⏱️ ", fontSize = 12.sp)
                    Text(
                        text = String.format("%02d:%02d", remainingMin, remainingSec),
                        fontWeight = FontWeight.Bold,
                        color = if (session.remainingSeconds < 120) AppleRed else TextPrimary,
                        fontSize = 13.sp
                    )
                }
            }

            // دکمه نشان‌گذاری
            IconButton(onClick = { viewModel.toggleExamBookmark(currentQ.id) }) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "نشان‌گذاری",
                    tint = if (isBookmarked) AppleYellow else TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // شماره سوال و مبحث
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "سوال ${PersianUtils.toPersianDigits(currentIndex + 1)} از ${PersianUtils.toPersianDigits(questions.size)}",
                fontWeight = FontWeight.Bold,
                color = AppleBlue,
                fontSize = 13.sp
            )
            Surface(color = DarkSurface2, shape = RoundedCornerShape(6.dp)) {
                Text(
                    text = currentQ.examSource,
                    color = TextTertiary,
                    fontSize = 10.5.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // متن سوال
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface1),
            border = BorderStroke(1.dp, DarkSeparator),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = currentQ.questionText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(currentQ.options.mapIndexed { idx, opt -> idx to opt }) { (idx, optText) ->
                    val isSelected = userSelectedOption == idx
                    Surface(
                        onClick = { viewModel.answerExamQuestion(currentQ.id, idx) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) AppleBlue.copy(alpha = 0.2f) else DarkSurface2,
                        border = BorderStroke(1.5.dp, if (isSelected) AppleBlue else DarkSeparator),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) AppleBlue else DarkSurface3),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = PersianUtils.toPersianDigits(idx + 1),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = optText, fontSize = 12.sp, color = TextPrimary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // دکمه‌های ناوبری: قبلی / بعدی / پایان آزمون
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.prevExamQuestion() },
                enabled = currentIndex > 0,
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface2),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "قبلی ➔", color = TextPrimary, fontSize = 12.sp)
            }

            if (currentIndex < questions.size - 1) {
                Button(
                    onClick = { viewModel.nextExamQuestion() },
                    colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "بعدی ➔", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            } else {
                Button(
                    onClick = { viewModel.finishExam() },
                    colors = ButtonDefaults.buttonColors(containerColor = AppleGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "پایان آزمون ✓", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

/**
 * نمای کارنامه تحلیلی نتایج آزمون کنکور
 */
@Composable
fun ExamResultView(
    viewModel: ShimiYarViewModel,
    isPro: Boolean
) {
    val session by viewModel.examSession.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(1.5.dp, AppleYellow),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "📊 کارنامه تحلیلی کنکور سراسری", fontWeight = FontWeight.Bold, color = AppleYellow, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "درصد کنکور (با نمره منفی)", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text(
                                text = "${PersianUtils.toPersianDigits(String.format("%.1f", session.scorePercent))}٪",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (session.scorePercent >= 50) AppleGreen else if (session.scorePercent >= 20) AppleOrange else AppleRed
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "تراز تخمینی کنکور", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text(
                                text = PersianUtils.toPersianDigits(session.estimatedTaraz),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MetallicCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = DarkSeparator)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "✅ درست: ${PersianUtils.toPersianDigits(session.correctCount)}", color = AppleGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = "❌ نادرست: ${PersianUtils.toPersianDigits(session.wrongCount)}", color = AppleRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = "⚪ نزده: ${PersianUtils.toPersianDigits(session.unansweredCount)}", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Text(text = "📖 تحلیل تشریحی و کلید سوالات:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        items(session.questions.mapIndexed { idx, q -> idx to q }) { (idx, q) ->
            val userAns = session.answers[q.id]
            val isCorrect = userAns == q.correctOptionIndex
            val isUnanswered = userAns == null

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = BorderStroke(
                    1.dp,
                    when {
                        isUnanswered -> DarkSeparator
                        isCorrect -> AppleGreen.copy(alpha = 0.5f)
                        else -> AppleRed.copy(alpha = 0.5f)
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "سوال ${PersianUtils.toPersianDigits(idx + 1)}",
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isUnanswered -> TextSecondary
                                isCorrect -> AppleGreen
                                else -> AppleRed
                            },
                            fontSize = 12.sp
                        )
                        Text(
                            text = when {
                                isUnanswered -> "⚪ پاسخ داده نشده"
                                isCorrect -> "✅ درست"
                                else -> "❌ نادرست"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isUnanswered -> TextSecondary
                                isCorrect -> AppleGreen
                                else -> AppleRed
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = q.questionText, fontSize = 12.sp, color = TextPrimary, lineHeight = 20.sp)

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = DarkSurface2,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "💡 پاسخ تشریحی:", fontWeight = FontWeight.Bold, color = AppleYellow, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(text = q.explanation, fontSize = 11.5.sp, color = TextSecondary, lineHeight = 19.sp)
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = { viewModel.closeExam() },
                colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(text = "بازگشت به فهرست آزمون‌ها ➔", fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MistakeQuestionCard(question: ExamQuestion) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface1),
        border = BorderStroke(1.dp, AppleRed.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = AppleRed.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(text = "⚠️ اشتباه پرتکرار", color = AppleRed, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
                Text(text = question.chapterTitle, color = TextTertiary, fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = question.questionText, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = DarkSurface2,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "گزینه صحیح: گزینه ${PersianUtils.toPersianDigits(question.correctOptionIndex + 1)}", color = AppleGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = question.explanation, color = TextSecondary, fontSize = 11.sp, lineHeight = 18.sp)
                }
            }
        }
    }
}
