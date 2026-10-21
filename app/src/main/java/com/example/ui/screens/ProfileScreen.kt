package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.data.local.DataLoader
import com.example.data.model.BadgeItem
import com.example.ui.components.LeitnerCircularChart
import com.example.ui.components.LeitnerNotificationDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.ShimiYarViewModel
import com.example.util.PersianUtils

@Composable
fun ProfileScreen(
    viewModel: ShimiYarViewModel
) {
    val context = LocalContext.current
    val xp by viewModel.xp.collectAsState()
    val level by viewModel.level.collectAsState()
    val streak by viewModel.streak.collectAsState()
    val isPro by viewModel.isPro.collectAsState()
    val knownCount by viewModel.fcKnownCount.collectAsState()
    val learningCount by viewModel.fcLearningCount.collectAsState()
    val allCards = viewModel.flashcards
    val unseenCount = (allCards.size - knownCount - learningCount).coerceAtLeast(0)

    var showAboutDialog by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showLeitnerNotifDialog by remember { mutableStateOf(false) }

    val badges = DataLoader.defaultBadges.map {
        it.copy(isUnlocked = viewModel.prefs.isBadgeUnlocked(it.id) || (it.id == "pro_member" && isPro) || (it.id == "streak_3" && streak >= 3))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Avatar & Status
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isPro) AppleYellow else DarkSeparator),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(
                                if (isPro) {
                                    Brush.linearGradient(colors = listOf(AppleYellow, AppleOrange))
                                } else {
                                    Brush.linearGradient(colors = listOf(AppleBlue, AppleIndigo))
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = if (isPro) "👑" else "🔬", fontSize = 32.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "دانش‌آموز کوشا کنکور",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = if (isPro) AppleYellow.copy(alpha = 0.2f) else DarkSurface2,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isPro) "👑 کاربر شیمی‌یار پرو" else "عضویت رایگان",
                                color = if (isPro) AppleYellow else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // 3 Stat Cards: XP, Level, Badges
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileStatCard(label = "مجموع امتیاز", value = "${PersianUtils.toPersianDigits(xp)} XP", icon = "⚡", color = AppleYellow, modifier = Modifier.weight(1f))
                ProfileStatCard(label = "سطح علمی", value = "سطح ${PersianUtils.toPersianDigits(level)}", icon = "🎯", color = AppleBlue, modifier = Modifier.weight(1f))
                ProfileStatCard(label = "نشان‌های کسب‌شده", value = "${PersianUtils.toPersianDigits(badges.count { it.isUnlocked })} از ۸", icon = "🏅", color = ApplePurple, modifier = Modifier.weight(1f))
            }
        }

        // Leitner Box Learning Status Chart (Recharts-style circular progress)
        item {
            LeitnerCircularChart(
                masteredCount = knownCount,
                learningCount = learningCount,
                unseenCount = unseenCount,
                totalCards = allCards.size,
                initiallyExpanded = true
            )
        }

        // Leitner Daily Reminder Quick Card
        item {
            val dueInfo by viewModel.dueLeitnerInfo.collectAsState()
            val notifEnabled by viewModel.leitnerNotifEnabled.collectAsState()
            val notifHour by viewModel.leitnerNotifHour.collectAsState()
            val notifMinute by viewModel.leitnerNotifMinute.collectAsState()

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (notifEnabled) MetallicCyan.copy(alpha = 0.5f) else DarkSeparator
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLeitnerNotifDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (notifEnabled) MetallicCyan.copy(alpha = 0.15f) else DarkSurface3),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (notifEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                            contentDescription = null,
                            tint = if (notifEnabled) MetallicCyan else TextTertiary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "یادآور روزانه لایتنر",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = if (notifEnabled) MetallicEmerald.copy(alpha = 0.2f) else DarkSurface3,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (notifEnabled) "فعال (${String.format("%02d:%02d", notifHour, notifMinute)})" else "غیرفعال",
                                    color = if (notifEnabled) MetallicEmerald else TextTertiary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "مرور امروز: جعبه ${dueInfo.dueBoxes.joinToString(" و ")} (${dueInfo.dueCardsCount} کارت) • تنظیمات و تست فوری",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Daily Missions List
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎯 ماموریت‌های روزانه (کسب XP)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    DataLoader.dailyMissions.forEach { mission ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "•", color = AppleBlue, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = mission.title, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                            Surface(
                                color = AppleOrange.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "+${PersianUtils.toPersianDigits(mission.xpReward)} XP",
                                    color = AppleOrange,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 8 Achievement Badges Grid
        item {
            Text(
                text = "🏅 نشان‌های افتخار (Badges)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        items(badges.chunked(2)) { badgePair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                badgePair.forEach { badge ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (badge.isUnlocked) DarkSurface2 else DarkSurface1
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = if (badge.isUnlocked) AppleYellow.copy(alpha = 0.5f) else DarkSeparator
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = if (badge.isUnlocked) "🏆" else "🔒", fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = badge.title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (badge.isUnlocked) TextPrimary else TextTertiary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = badge.description,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        if (isPro) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MetallicGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                color = MetallicGold.copy(alpha = 0.2f),
                                shape = CircleShape,
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "👑", fontSize = 24.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "عضویت شیمی‌یار طلایی (VIP)",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MetallicGold,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = MetallicEmerald,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "فعال",
                                            color = Color.Black,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "دسترسی ۱۰۰٪ به تمامی درسنامه‌ها، فلش‌کارت‌ها و ابزارها برای شما فعال است.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // High-Converting Pro Upgrade Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "👑 ارتقای عضویت شیمی‌یار پرو",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppleYellow
                    )
                    Surface(
                        color = MetallicRose.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable { viewModel.openDailyOfferModal() }
                    ) {
                        Text(
                            text = "⚡ تخفیف امروز",
                            color = MetallicRose,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // 1 Year Plan (Featured with Price Anchoring & Decoy Pricing)
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                        border = androidx.compose.foundation.BorderStroke(2.dp, MetallicGold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openDailyOfferModal() }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "اشتراک ۱ ساله طلایی (ویژه کنکور)", fontWeight = FontWeight.Bold, color = MetallicGold, fontSize = 15.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(color = MetallicGold, shape = RoundedCornerShape(6.dp)) {
                                            Text(text = "پیشنهادی", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                    Text(text = "روزی فقط ۸۰۰ تومان • دسترسی به تمامی ۱۱۸ عنصر و درسنامه‌ها", style = MaterialTheme.typography.labelSmall, color = MetallicEmerald, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "۴۸۵,۰۰۰ تومان", fontWeight = FontWeight.ExtraBold, color = MetallicGold, fontSize = 15.sp)
                                    Text(text = "سالانه", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = { viewModel.openPremiumModal() },
                                colors = ButtonDefaults.buttonColors(containerColor = MetallicGold),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                            ) {
                                Text(text = "🎁 فعال‌سازی اشتراک (یا ۳ روز رایگان کافه‌بازار)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 6 Months
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppleBlue.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.openPremiumModal() }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(text = "اشتراک ۶ ماهه", fontWeight = FontWeight.Bold, color = TextPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "۳۸۵,۰۰۰ تومان", fontWeight = FontWeight.Bold, color = AppleBlue)
                                Text(text = "یک نیم‌سال تحصیلی", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                            }
                        }

                        // 1 Month
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppleOrange.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.openPremiumModal() }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(text = "اشتراک ۱ ماهه", fontWeight = FontWeight.Bold, color = TextPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "۹۹,۰۰۰ تومان", fontWeight = FontWeight.Bold, color = AppleOrange)
                                Text(text = "دسترسی ۳۰ روزه", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                            }
                        }
                    }
                }
            }
        }

        // Settings / Menu rows
        item {
            val isEyeCareMode by viewModel.eyeCareMode.collectAsState()

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface1),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    // Scientific Metallic Dark Mode & Eye Care Mode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DarkMode,
                                contentDescription = null,
                                tint = MetallicCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "تم تیره علمی متالیک (ضد خستگی چشم)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (isEyeCareMode) "حالت محافظ چشم شبانه (فیلتر نور آبی) فعال است" else "پالت تیتانیوم و ابسیدین ارگونومیک (فعال)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isEyeCareMode) MetallicAmber else TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Switch(
                            checked = isEyeCareMode,
                            onCheckedChange = { viewModel.toggleEyeCareMode() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = MetallicAmber,
                                uncheckedThumbColor = MetallicCyan,
                                uncheckedTrackColor = DarkSurface3
                            )
                        )
                    }
                    Divider(color = DarkSeparator, modifier = Modifier.padding(horizontal = 8.dp))

                    MenuRow(title = "ماشین‌حساب و محاسبات استوکیومتری", icon = Icons.Default.Calculate) {
                        viewModel.setTab(com.example.ui.viewmodel.ShimiTab.TOOLS)
                    }
                    Divider(color = DarkSeparator, modifier = Modifier.padding(horizontal = 8.dp))
                    MenuRow(title = "نکات و آموزش‌های تخصصی کنکور", icon = Icons.Default.MenuBook) {
                        viewModel.setTab(com.example.ui.viewmodel.ShimiTab.EDUCATION)
                    }
                    Divider(color = DarkSeparator, modifier = Modifier.padding(horizontal = 8.dp))
                    MenuRow(title = "🔔 یادآور هوشمند جعبه لایتنر (اعلان روزانه)", icon = Icons.Default.NotificationsActive) {
                        showLeitnerNotifDialog = true
                    }
                    Divider(color = DarkSeparator, modifier = Modifier.padding(horizontal = 8.dp))
                    MenuRow(title = "⭐ ثبت نظر و امتیاز در کافه‌بازار", icon = Icons.Default.Star) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("bazaar://details?id=com.shimiyar.app"))
                            intent.setPackage("com.farsitel.bazaar")
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            try {
                                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://cafebazaar.ir/app/com.shimiyar.app"))
                                context.startActivity(webIntent)
                            } catch (e2: Exception) {
                                e2.printStackTrace()
                            }
                        }
                    }
                    Divider(color = DarkSeparator, modifier = Modifier.padding(horizontal = 8.dp))
                    MenuRow(title = "📩 پشتیبانی و ارتباط با توسعه‌دهنده", icon = Icons.Default.SupportAgent) {
                        showSupportDialog = true
                    }
                    Divider(color = DarkSeparator, modifier = Modifier.padding(horizontal = 8.dp))
                    MenuRow(title = "🔒 سیاست حفظ حریم خصوصی", icon = Icons.Default.PrivacyTip) {
                        showPrivacyDialog = true
                    }
                    Divider(color = DarkSeparator, modifier = Modifier.padding(horizontal = 8.dp))
                    MenuRow(title = "ℹ️ درباره شیمی‌یار کنکور (نسخه ۱.۰)", icon = Icons.Default.Info) {
                        showAboutDialog = true
                    }
                }
            }
        }
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = DarkSurface1,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.app_icon_fg),
                        contentDescription = "آیکون شیمی یار",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "درباره شیمی‌یار کنکور",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "شیمی‌یار، کامل‌ترین اپلیکیشن و دستیار تحصیلی برای داوطلبان کنکور سراسری تجربی و ریاضی و امتحانات نهایی است.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 22.sp
                    )
                    Text(
                        text = "• جدول تناوبی ۱۱۸ عنصر با تحلیل کامل تستی\n• جزوات خلاصه و نکات طلایی فصول دهم، یازدهم و دوازدهم\n• جعبه لایتنر هوشمند با بیش از ۲۰۰ فلش‌کارت کنکوری\n• واژه‌نامه تخصصی و محاسبات استوکیومتری\n• نگارش ۱.۰.۰ - آماده انتشار در کافه‌بازار",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary,
                        lineHeight = 22.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "متوجه شدم", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showSupportDialog) {
        AlertDialog(
            onDismissRequest = { showSupportDialog = false },
            containerColor = DarkSurface1,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = null,
                        tint = MetallicCyan,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "پشتیبانی و ارتباط با توسعه‌دهنده",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "داوطلب گرامی، برای گزارش هرگونه مشکل در عملکرد اپلیکیشن، پیشنهاد، یا سوال درباره اشتراک کافه‌بازار می‌توانید از راه‌های زیر با ما در ارتباط باشید:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 22.sp
                    )
                    Surface(
                        color = DarkSurface2,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "📧 ایمیل پشتیبانی:", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "shimiyar.app@gmail.com", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MetallicCyan)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "⏱️ زمان پاسخگویی:", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "حداکثر ۲۴ ساعت کاری", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:shimiyar.app@gmail.com")
                                putExtra(Intent.EXTRA_SUBJECT, "پشتیبانی اپلیکیشن شیمی‌یار")
                            }
                            context.startActivity(Intent.createChooser(emailIntent, "ارسال ایمیل پشتیبانی"))
                        } catch (_: Exception) {}
                        showSupportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MetallicCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "ارسال ایمیل", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSupportDialog = false }) {
                    Text(text = "بستن", color = TextTertiary)
                }
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            containerColor = DarkSurface1,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = AppleGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "سیاست حفظ حریم خصوصی",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "شیمی‌یار به امنیت و حریم خصوصی داوطلبان متعهد است:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "۱. معماری محلی (Local-First): تمامی یادداشت‌ها، کارت‌های مرورشده لایتنر و امتیازات صرفاً در حافظه محلی گوشی شما نگهداری می‌شوند و به هیچ سرور خارجی ارسال نمی‌گردند.\n" +
                                "۲. عدم دسترسی به داده‌های حساس: این برنامه به دفترچه تلفن، پیامک‌ها، تصاویر شخصی یا موقعیت مکانی دسترسی ندارد.\n" +
                                "۳. پرداخت امن: تمامی امور خرید و اشتراک منحصراً از طریق زیرساخت رسمی کافه‌بازار انجام می‌پذیرد.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 22.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = AppleGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "تایید و بازگشت", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showLeitnerNotifDialog) {
        LeitnerNotificationDialog(
            viewModel = viewModel,
            onDismiss = { showLeitnerNotifDialog = false }
        )
    }
}

@Composable
fun ProfileStatCard(
    label: String,
    value: String,
    icon: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface1),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSeparator),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextTertiary, fontSize = 10.sp)
        }
    }
}

@Composable
fun MenuRow(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = AppleBlue, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        }
        Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(14.dp))
    }
}
