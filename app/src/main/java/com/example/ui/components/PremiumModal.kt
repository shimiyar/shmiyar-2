package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.PersianUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumModal(
    isPro: Boolean = false,
    onDismiss: () -> Unit,
    onActivateTrial: () -> Unit,
    onPurchase: (variant: String) -> Unit,
    onRestorePurchases: () -> Unit = {}
) {
    var selectedPlan by remember { mutableStateOf("1_year") } // Decoy effect + Anchoring default

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = DarkSurface1,
        dragHandle = { BottomSheetDefaults.DragHandle(color = DarkSeparator) },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier.testTag("premium_modal")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "بستن", tint = TextSecondary)
                }
            }

            if (isPro) {
                // Active VIP Status Card
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(MetallicGold.copy(alpha = 0.35f), MetallicCopper.copy(alpha = 0.35f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👑", fontSize = 40.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "اشتراک شیمی‌یار طلایی شما فعال است",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MetallicGold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "شما به تمامی امکانات تخصصی کنکور، فرمول‌ها، درسنامه‌ها و لایتنر دسترسی ۱۰۰٪ نامحدود دارید.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MetallicGold.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(
                            "✅ دسترسی ۱۰۰٪ به تمامی ۴۱۰+ فلش‌کارت و جعبه ۵ مرحله‌ای لایتنر",
                            "✅ دسترسی کامل به تمامی ۱۰ فصل درسنامه ۳ پایه دهم، یازدهم و دوازدهم",
                            "✅ دسترسی نامحدود به بانک فرمول‌های طلایی و میانبرهای کنکور",
                            "✅ ماشین‌حساب‌های پیشرفته استوکیومتری، pH و جرم مولی با حل تشریحی",
                            "✅ تایمر ۳۵ ثانیه‌ای تست سرعتی و تمرکز پومودورو بدون محدودیت",
                            "✅ شبیه‌ساز آزمون‌های جامع سراسری با کارنامه تحلیلی و تراز تخمینی",
                            "✅ پشتیبان‌گیری خودکار و دسترسی آفلاین دائمی بدون اینترنت"
                        ).forEach { perk ->
                            Text(
                                text = perk,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MetallicGold),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "بستن و ادامه یادگیری",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            } else {
                // 1. Hero: Crown + Title + Description
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(MetallicGold.copy(alpha = 0.25f), MetallicCopper.copy(alpha = 0.25f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👑", fontSize = 36.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "با شیمی‌یار پرو، شیمی کنکور را ۱۰۰ بزن!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "دسترسی نامحدود به تحلیل تستی عناصر، دام‌های طراحان، درسنامه جامع و لایتنر",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Free vs Pro Comparison Table
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface2),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "مقایسه امکانات نسخه رایگان و پرو:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Surface(
                            color = MetallicGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "VIP",
                                color = MetallicGold,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val comparisonRows = listOf(
                        Triple("فلش‌کارت‌های حفظی درس به درس ۳ پایه", "۱۵ کارت", "۴۱۰+ کارت کامل با لایتنر"),
                        Triple("درسنامه جامع، فرمول‌ها و دام‌های تستی", "فقط خلاصه", "تمامی ۱۰ فصل ۳ پایه ۱۰۰٪"),
                        Triple("بانک فرمول‌های طلایی و ترفندهای تست‌زنی زیر ۴۰ ثانیه", "فقط ۲ فرمول", "تمامی روابط و دام‌های طراحان"),
                        Triple("محاسبه‌گر پیشرفته pH و شبیه‌ساز شناساگرها", "محدود به آب", "کامل با ۱۱ ترکیب پرتکرار"),
                        Triple("تایمر ۳۵ ثانیه‌ای تست سرعتی و تمرکز پومودورو", "قفل", "نامحدود با ثبت رکورد و XP"),
                        Triple("آزمون شبیه‌ساز سرعتی کنکور و تخمین درصد", "قفل", "نامحدود با پاسخنامه تشریحی"),
                        Triple("اطلس رنگ‌ها، واکنش‌های کلیدی و نام‌های تجاری", "قفل", "دسترسی کامل و دائمی"),
                        Triple("دانلود آفلاین پکیج‌ها بدون اینترنت", "غیرفعال", "فعال بدون نیاز به اینترنت")
                    )

                    comparisonRows.forEach { (feature, free, pro) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = feature, style = MaterialTheme.typography.bodySmall, color = TextPrimary, modifier = Modifier.weight(1.4f))
                            Text(text = free, style = MaterialTheme.typography.labelSmall, color = TextTertiary, modifier = Modifier.weight(0.9f), textAlign = TextAlign.Center)
                            Text(text = pro, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MetallicEmerald, modifier = Modifier.weight(1.3f), textAlign = TextAlign.End)
                        }
                        Divider(color = DarkSeparator, modifier = Modifier.padding(vertical = 2.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Subscription Plans Selection (Decoy Effect + Anchoring)
            Text(
                text = "انتخاب دوره اشتراک:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 1 Year Plan (Featured with Decoy Pricing)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedPlan == "1_year") DarkSurface3 else DarkSurface2
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (selectedPlan == "1_year") 2.dp else 1.dp,
                    color = if (selectedPlan == "1_year") MetallicGold else DarkSeparator
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedPlan = "1_year" }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedPlan == "1_year",
                            onClick = { selectedPlan = "1_year" },
                            colors = RadioButtonDefaults.colors(selectedColor = MetallicGold)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "اشتراک ۱ ساله (طلایی)",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = MetallicGold,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "طرح طلایی پیشنهادی",
                                        color = Color.Black,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "دسترسی کامل تا کنکور • روزانه کمتر از ۱,۳۰۰ تومان",
                                style = MaterialTheme.typography.labelSmall,
                                color = MetallicEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "۴۸۵,۰۰۰ تومان",
                            fontWeight = FontWeight.ExtraBold,
                            color = MetallicGold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "سالانه",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 6 Months Plan
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedPlan == "6_months") DarkSurface3 else DarkSurface2
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (selectedPlan == "6_months") 2.dp else 1.dp,
                    color = if (selectedPlan == "6_months") MetallicCyan else DarkSeparator
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedPlan = "6_months" }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedPlan == "6_months",
                            onClick = { selectedPlan = "6_months" },
                            colors = RadioButtonDefaults.colors(selectedColor = MetallicCyan)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "اشتراک ۶ ماهه",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "مناسب برای یک نیم‌سال تحصیلی و جمع‌بندی",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Text(
                        text = "۳۸۵,۰۰۰ تومان",
                        fontWeight = FontWeight.ExtraBold,
                        color = MetallicCyan,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 1 Month Plan
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedPlan == "1_month") DarkSurface3 else DarkSurface2
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (selectedPlan == "1_month") 2.dp else 1.dp,
                    color = if (selectedPlan == "1_month") MetallicCopper else DarkSeparator
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedPlan = "1_month" }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedPlan == "1_month",
                            onClick = { selectedPlan = "1_month" },
                            colors = RadioButtonDefaults.colors(selectedColor = MetallicCopper)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "اشتراک ۱ ماهه",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "دسترسی ۳۰ روزه جهت بررسی و یادگیری",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Text(
                        text = "۹۹,۰۰۰ تومان",
                        fontWeight = FontWeight.ExtraBold,
                        color = MetallicCopper,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Purchase Button for selected plan
            Button(
                onClick = { onPurchase(selectedPlan) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = when (selectedPlan) {
                        "1_year" -> MetallicGold
                        "6_months" -> MetallicCyan
                        else -> MetallicCopper
                    }
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("purchase_selected_plan_button")
            ) {
                val priceLabel = when (selectedPlan) {
                    "1_year" -> "۴۸۵,۰۰۰ تومان"
                    "6_months" -> "۳۸۵,۰۰۰ تومان"
                    else -> "۹۹,۰۰۰ تومان"
                }
                Text(
                    text = "خرید اشتراک و فعال‌سازی آنی ($priceLabel)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3-Day Free Trial CTA Button (Exclusive for Bazaar)
            OutlinedButton(
                onClick = onActivateTrial,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MetallicEmerald),
                border = androidx.compose.foundation.BorderStroke(1.dp, MetallicEmerald),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("activate_trial_button")
            ) {
                Text(
                    text = "🎁 ۳ روز دسترسی رایگان (ویژه ورود با کافه‌بازار)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Restore Purchases CTA Button (Mandatory for Cafe Bazaar review)
            TextButton(
                onClick = onRestorePurchases,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("restore_purchases_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = null,
                        tint = MetallicCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "قبلاً خریده‌اید؟ بازیابی خرید از کافه‌بازار",
                        fontSize = 12.sp,
                        color = MetallicCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Risk-Free Guarantee Note
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = MetallicEmerald,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "پرداخت امن از طریق درگاه رسمی کافه‌بازار با گارانتی بازگشت وجه تا ۷ روز",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}
