package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.NoteEntity
import com.example.data.local.UserStatsEntity
import com.example.ui.components.AppHeader
import com.example.ui.components.ProBadge
import com.example.ui.components.StatPill
import com.example.ui.components.shareChemistryTip
import com.example.ui.theme.OrangeAccent
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.MainViewModel
import com.example.util.PersianUtils

@Composable
fun HomeScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val userStats by viewModel.userStats.collectAsState(initial = null)
    val flashcards by viewModel.allFlashcards.collectAsState(initial = emptyList())
    val questions by viewModel.allQuestions.collectAsState(initial = emptyList())
    val notes by viewModel.allNotes.collectAsState(initial = emptyList())
    val isPro by viewModel.isPro.collectAsState()

    val dueCardsCount = remember(flashcards) {
        val now = System.currentTimeMillis()
        flashcards.count { it.nextReviewTimestamp <= now && !it.isMastered }
    }

    val dailyTip = remember(notes) {
        notes.firstOrNull() ?: NoteEntity(
            grade = 10,
            chapter = "کیهان زادگاه الفبای هستی",
            title = "استثنای آرایش مس و کروم",
            content = "در دوره چهارم، مس ²⁹Cu با آرایش [Ar] 3d¹⁰ 4s¹ و کروم ²⁴Cr با آرایش [Ar] 3d⁵ 4s¹ استثنا هستند و نیمه‌پر و پر پایدارتری دارند.",
            formulaOrRule = "پایداری نیمه‌پر و تمام‌پر در زیرلایه d"
        )
    }

    var showReminderDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // App Header with Profile & Pro Status
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "شیمی‌یار",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (isPro) {
                            Spacer(modifier = Modifier.width(6.dp))
                            ProBadge()
                        }
                    }
                    Text(
                        text = "پایه ${userStats?.selectedGrade ?: "دوازدهم"} • رشته ${userStats?.selectedMajor ?: "تجربی"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Reminder bell action
                IconButton(
                    onClick = { showReminderDialog = true },
                    modifier = Modifier.testTag("reminder_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = "تنظیم یادآور روزانه",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Daily Streak & Stats Banner
        item {
            StreakAndLevelBanner(
                userStats = userStats,
                onProClick = { viewModel.openPaywall("دسترسی به تمام امکانات اختصاصی") }
            )
        }

        // Leitner Daily Review Card
        item {
            LeitnerTodayCard(
                dueCardsCount = dueCardsCount,
                onReviewClick = { viewModel.setTab(AppTab.FLASHCARDS) }
            )
        }

        // Quick Actions Grid
        item {
            Text(
                text = "دسترسی سریع",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            QuickActionsRow(
                onPeriodicClick = { viewModel.setTab(AppTab.PERIODIC_TABLE) },
                onBalancerClick = { viewModel.setTab(AppTab.TOOLS) },
                onQuizClick = { viewModel.setTab(AppTab.QUIZZES) },
                onNotesClick = { viewModel.setShowNotesScreen(true) }
            )
        }

        // Random Chemistry Golden Tip Card
        item {
            Text(
                text = "نکته طلایی امروز",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            DailyTipCard(
                tip = dailyTip,
                onBookmarkToggle = { viewModel.toggleNoteBookmark(dailyTip) },
                onShare = {
                    shareChemistryTip(
                        context = context,
                        title = dailyTip.title,
                        content = "${dailyTip.content}\nفرمول کلیدی: ${dailyTip.formulaOrRule}"
                    )
                }
            )
        }

        // Continue Last Exam or Practice
        item {
            ContinueExamCard(
                questionsCount = questions.size,
                onStartQuickQuiz = {
                    viewModel.startExam(questions.take(10), isTimedExam = false)
                    viewModel.setTab(AppTab.QUIZZES)
                }
            )
        }

        // Content Packages Banner
        item {
            PackagesShortcutCard(
                onOpenPackages = { viewModel.setShowPackagesScreen(true) }
            )
        }
    }

    if (showReminderDialog) {
        ReminderSettingsDialog(
            currentHour = userStats?.reminderHour ?: 20,
            currentMinute = userStats?.reminderMinute ?: 0,
            isEnabled = userStats?.isReminderEnabled ?: true,
            onDismiss = { showReminderDialog = false },
            onSave = { h, m, en ->
                viewModel.updateReminderSettings(h, m, en)
                showReminderDialog = false
            }
        )
    }
}

@Composable
fun StreakAndLevelBanner(
    userStats: UserStatsEntity?,
    onProClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Streak
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(OrangeAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "زنجیره مطالعه",
                            tint = OrangeAccent,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = PersianUtils.toPersianDigits(userStats?.streakDays ?: 1),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = OrangeAccent
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "روز پیاپی",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "آفرین! زنجیره رو حفظ کن 🔥",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Level / XP
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "سطح ${PersianUtils.toPersianDigits(userStats?.level ?: 1)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )
                    Text(
                        text = "${PersianUtils.toPersianDigits(userStats?.xp ?: 50)} امتیاز علمی (XP)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // XP Progress Bar
            val xp = userStats?.xp ?: 50
            val progressInLevel = (xp % 150) / 150f
            LinearProgressIndicator(
                progress = { progressInLevel },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = TealPrimary,
                trackColor = TealPrimary.copy(alpha = 0.15f)
            )
        }
    }
}

@Composable
fun LeitnerTodayCard(
    dueCardsCount: Int,
    onReviewClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = TealPrimary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onReviewClick() }
            .testTag("leitner_review_card")
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Style,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "مرور امروز لایتنر",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (dueCardsCount > 0)
                        "${PersianUtils.toPersianDigits(dueCardsCount)} کارت آماده مرور در حافظه داری"
                    else
                        "همه کارت‌های امروز را مرور کردی! عالیه",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun QuickActionsRow(
    onPeriodicClick: () -> Unit,
    onBalancerClick: () -> Unit,
    onQuizClick: () -> Unit,
    onNotesClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val actions = listOf(
            Triple("جدول تناوبی", Icons.Default.ViewModule, onPeriodicClick),
            Triple("موازنه واکنش", Icons.Default.CompareArrows, onBalancerClick),
            Triple("آزمون تستی", Icons.Default.FactCheck, onQuizClick),
            Triple("نکات کنکوری", Icons.Default.BookmarkBorder, onNotesClick)
        )

        actions.forEach { (title, icon, action) ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .clickable { action() }
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun DailyTipCard(
    tip: NoteEntity,
    onBookmarkToggle: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = OrangeAccent.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = tip.chapter,
                        color = OrangeAccent,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row {
                    IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "اشتراک‌گذاری",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onBookmarkToggle, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (tip.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "نشان‌کردن",
                            tint = if (tip.isBookmarked) OrangeAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = tip.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = tip.content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            if (tip.formulaOrRule.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = PersianUtils.formatFormula(tip.formulaOrRule),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ContinueExamCard(
    questionsCount: Int,
    onStartQuickQuiz: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onStartQuickQuiz() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(OrangeAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = OrangeAccent,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "آزمون سریع ۱۰ سوالی کنکور",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "شبیه‌ساز سوالات کنکور با پاسخنامه تشریحی و درصد",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun PackagesShortcutCard(
    onOpenPackages: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onOpenPackages() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CloudDownload,
                contentDescription = null,
                tint = TealPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "پکیج‌های آموزشی دانلودی",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "دانلود بانک سوالات المپیاد و جمع‌بندی طلایی برای مطالعه آفلاین",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun ReminderSettingsDialog(
    currentHour: Int,
    currentMinute: Int,
    isEnabled: Boolean,
    onDismiss: () -> Unit,
    onSave: (hour: Int, minute: Int, enabled: Boolean) -> Unit
) {
    var hour by remember { mutableStateOf(currentHour) }
    var minute by remember { mutableStateOf(currentMinute) }
    var enabled by remember { mutableStateOf(isEnabled) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "یادآور روزانه مطالعه شیمی", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    text = "برای تداوم زنجیره یادگیری و حفظ مطالب در حافظه، شیمی‌یار سر این ساعت به شما پیام یادآوری می‌فرستد.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "فعال بودن اعلان")
                    Switch(checked = enabled, onCheckedChange = { enabled = it })
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ساعت یادآوری: ${PersianUtils.toPersianDigits("%02d:%02d".format(hour, minute))}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(hour, minute, enabled) }) {
                Text("ذخیره تنظیمات")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    )
}
