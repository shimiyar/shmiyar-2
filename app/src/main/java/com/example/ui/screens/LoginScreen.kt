package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.viewmodel.LoginViewModel

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext

/**
 * صفحه ورود (Login Screen) اختصاصی شیمی‌یار با تم تیره سرمه‌ای/بنفش
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToMain: () -> Unit
) {
    val context = LocalContext.current

    // لانچر دریافت نتیجه احراز هویت از اکتیویتی کافه‌بازار
    val bazaarAuthLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleBazaarResult(
            resultCode = result.resultCode,
            data = result.data,
            onSuccess = onNavigateToMain
        )
    }

    // رنگ‌های شاخص صفحه بر اساس تم سرمه‌ای و بنفش تیره
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF140E34), // بنفش تیره بالایی
            Color(0xFF0D0B22), // سرمه‌ای نیمه‌شب
            Color(0xFF070514)  // تاریکی خالص پایینی
        )
    )

    val logoPurpleGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF8B5CF6), // بنفش روشن
            Color(0xFF6D28D9), // بنفش متالیک
            Color(0xFFF59E0B)  // رگه‌های طلایی
        )
    )

    val goldAccent = Color(0xFFFBBF24)
    val cardBackground = Color(0xFF181338)
    val cardBorder = Color(0xFF2E245E)
    val checkGreen = Color(0xFF22C55E) // سبز تیک‌ها
    val bazaarGreen = Color(0xFF16A34A) // سبز دکمه کافه‌بازار

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("login_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // بخش بالایی: لوگو، نام و شعار
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // آیکون و لوگوی رسمی و معتمد شیمی‌یار
                Box(
                    modifier = Modifier
                        .size(104.dp)
                        .shadow(
                            elevation = 20.dp,
                            shape = RoundedCornerShape(26.dp),
                            ambientColor = Color(0xFFF59E0B),
                            spotColor = Color(0xFF00C2CB)
                        )
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF38BDF8), Color(0xFFF59E0B), Color(0xFF8B5CF6))
                            )
                        )
                        .padding(2.5.dp)
                        .testTag("login_logo_circle"),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_icon_fg),
                        contentDescription = "لوگوی رسمی شیمی یار",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(24.dp))
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // نام برنامه
                Text(
                    text = "شیمی یار",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.testTag("login_app_title")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // شعار برنامه
                Text(
                    text = "با شیمی یار شیمی رو بفهم نه حفظ کن",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFC4B5FD), // بنفش روشن ملایم
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("login_slogan_text")
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // بخش مزایا (کادر با گوشه‌های گرد و آیکون سپر)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_benefits_card"),
                shape = RoundedCornerShape(22.dp),
                color = cardBackground,
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // هدر کادر مزایا با آیکون سپر
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF261D52)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "سپر امنیت",
                                tint = goldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "مزایای حساب کاربری",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // ۳ مزیت با آیکون تیک سبز
                    BenefitItemRow(
                        title = "همگام‌سازی امن اشتراک ویژه",
                        description = "دسترسی مادام‌العمر به خریدهای پیشین حتی پس از تغییر گوشی",
                        checkColor = checkGreen
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    BenefitItemRow(
                        title = "اشتراک آزمایشی رسمی کافه‌بازار",
                        description = "امکان فعال‌سازی اشتراک آزمایشی رسمی ثبت‌شده در حساب بازار",
                        checkColor = checkGreen
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    BenefitItemRow(
                        title = "حفظ حریم خصوصی کامل (Local-First)",
                        description = "تمامی یادداشت‌ها، پیشرفت لایتنر و کارنامه آزمون‌ها فقط در حافظه گوشی شما می‌ماند",
                        checkColor = checkGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // بخش دکمه‌ها و قوانین
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ۱. دکمه اصلی (سبز رنگ): "ورود با کافه‌بازار" با آیکون کاربر
                Button(
                    onClick = {
                        viewModel.loginWithBazaar(context = context) { intent ->
                            bazaarAuthLauncher.launch(intent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("login_bazaar_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = bazaarGreen,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "آیکون کاربر",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "ورود با کافه‌بازار",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ۲. دکمه فرعی (دور سفید): "ادامه با نسخه رایگان (بدون شروع تست)"
                OutlinedButton(
                    onClick = {
                        viewModel.continueAsGuest(onSuccess = onNavigateToMain)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("login_guest_button"),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.2.dp, Color.White.copy(alpha = 0.85f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "ادامه با نسخه رایگان (بدون شروع تست)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ۳. متن قوانین و شرایط حریم خصوصی
                Text(
                    text = "با ورود به برنامه، قوانین حریم خصوصی شیمی یار را می‌پذیرم.",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8), // طوسی ملایم
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .testTag("login_privacy_terms_text")
                )

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

/**
 * ردیف نمایش تک تک مزایا به همراه آیکون تیک سبز
 */
@Composable
private fun BenefitItemRow(
    title: String,
    description: String,
    checkColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = checkColor,
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = description,
                fontSize = 12.sp,
                color = Color(0xFFA5B4FC), // اسطوخودوسی ملایم
                lineHeight = 18.sp
            )
        }
    }
}
