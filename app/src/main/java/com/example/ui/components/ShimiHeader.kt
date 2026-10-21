package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.ShimiTab
import com.example.ui.viewmodel.ShimiYarViewModel
import com.example.util.PersianUtils

@Composable
fun ShimiHeader(
    viewModel: ShimiYarViewModel,
    onMenuClick: () -> Unit
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val xp by viewModel.xp.collectAsState()
    val level by viewModel.level.collectAsState()
    val isPro by viewModel.isPro.collectAsState()
    val achievementMsg by viewModel.achievementMessage.collectAsState()

    val levelProgress = (xp % 100) / 100f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBackground)
            .statusBarsPadding()
    ) {
        // Achievement Toast Banner from Top
        AnimatedVisibility(
            visible = achievementMsg != null,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(AppleOrange, AppleYellow)
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = achievementMsg ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.testTag("menu_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "منو",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Image(
                    painter = painterResource(id = R.drawable.app_icon_fg),
                    contentDescription = "لوگوی شیمی یار",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onMenuClick() }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = when (currentTab) {
                        ShimiTab.TABLE -> "⚛️ جدول تناوبی عناصر"
                        ShimiTab.EDUCATION -> "📚 درسنامه شیمی کنکور"
                        ShimiTab.FLASHCARDS -> "🗂️ جعبه لایتنر شیمی"
                        ShimiTab.QUIZ -> "🎯 آزمون شبیه‌ساز کنکور"
                        ShimiTab.TOOLS -> "🧮 جعبه‌ابزار محاسباتی"
                        ShimiTab.ORGANIC -> "🧪 شیمی آلی کنکور"
                        ShimiTab.GLOSSARY -> "🔬 واژه‌نامه تخصصی"
                        ShimiTab.PROFILE -> "🏆 کارنامه و رتبه"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Smart Search Button
                Surface(
                    color = MetallicCyan.copy(alpha = 0.15f),
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MetallicCyan.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .clip(CircleShape)
                        .testTag("smart_search_button")
                ) {
                    IconButton(
                        onClick = { viewModel.openSmartSearch() },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "جستجوی هوشمند",
                            tint = MetallicCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Gold Membership Chip for free users
                if (!isPro) {
                    Surface(
                        color = MetallicGold.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MetallicGold.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .clickable { viewModel.openDailyOfferModal() }
                            .testTag("header_daily_offer_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "👑", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "اشتراک طلایی",
                                color = MetallicGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                // Crown button (Premium modal)
                Surface(
                    color = if (isPro) AppleYellow.copy(alpha = 0.2f) else DarkSurface2,
                    shape = CircleShape,
                    modifier = Modifier
                        .clip(CircleShape)
                        .testTag("crown_button")
                ) {
                    IconButton(
                        onClick = { viewModel.openPremiumModal() },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Text(
                            text = "👑",
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }

        // XP Progress Bar below header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "سطح ${PersianUtils.toPersianDigits(level)}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppleBlue
                    )
                    if (isPro) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = AppleOrange,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "PRO",
                                color = Color.Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "${PersianUtils.toPersianDigits(xp)} XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { levelProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = AppleBlue,
                trackColor = DarkSurface3
            )
        }
    }
}
