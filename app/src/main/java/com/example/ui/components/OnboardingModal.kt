package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
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
import com.example.ui.theme.*

@Composable
fun OnboardingModal(
    onFinish: () -> Unit
) {
    var currentSlide by remember { mutableStateOf(0) }

    val slides = listOf(
        Triple(
            "⚛️",
            "به شیمی یار خوش آمدی",
            "جدول تناوبی تعاملی کامل شامل تمام ۱۱۸ عنصر، شعاع اتمی، الکترونگاتیوی، آرایش الکترونی و تحلیل‌های کنکوری در جیب شما."
        ),
        Triple(
            "📚",
            "آموزش کامل دبیرستان",
            "۱۰ فصل استاندارد پایه دهم، یازدهم و دوازدهم همراه با خلاصه درس، فرمول‌ها، نکات طلایی، تکنیک‌های حفظی و سوالات واقعی کنکور."
        ),
        Triple(
            "🗂️",
            "فلشکارت جامع",
            "بیش از ۱۳۰ فلش‌کارت مرور سریع در ۱۰ دسته‌بندی با انیمیشن سه‌بعدی و طبقه‌بندی تخصصی مفاهیم پرتکرار کنکور سراسری."
        ),
        Triple(
            "🎁",
            "۷ روز هدیه بگیر",
            "به مناسبت شروع، ۷ روز دسترسی کامل به تمامی امکانات پرو شیمی‌یار به صورت کاملاً رایگان برای شما فعال شد!"
        )
    )

    val slide = slides[currentSlide]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .testTag("onboarding_view"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                if (currentSlide < 3) {
                    TextButton(
                        onClick = onFinish,
                        modifier = Modifier.testTag("skip_onboarding_button")
                    ) {
                        Text(
                            text = "رد کردن",
                            color = TextSecondary,
                            fontSize = 15.sp
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            // Slide Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(AppleBlue.copy(alpha = 0.3f), ApplePurple.copy(alpha = 0.3f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (currentSlide == 0) {
                        Image(
                            painter = painterResource(id = R.drawable.app_icon_fg),
                            contentDescription = "آیکون شیمی یار",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(26.dp))
                        )
                    } else {
                        Text(text = slide.first, fontSize = 52.sp)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = slide.second,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = slide.third,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp
                )
            }

            // Bottom Controls (Dots & Next/Finish)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Progress dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 28.dp)
                ) {
                    for (i in 0..3) {
                        val isCurrent = i == currentSlide
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(if (isCurrent) 24.dp else 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isCurrent) AppleBlue else DarkSurface3)
                        )
                    }
                }

                Button(
                    onClick = {
                        if (currentSlide < 3) {
                            currentSlide++
                        } else {
                            onFinish()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentSlide == 3) AppleGreen else AppleBlue
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("onboarding_action_button")
                ) {
                    Text(
                        text = if (currentSlide == 3) "شروع با ۷ روز هدیه رایگان" else "بعدی",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
